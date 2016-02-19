package org.fer.syncfiles.bus;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.fer.syncfiles.UpdateTaskListener;
import org.fer.syncfiles.bus.tree.FileNode;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.model.ResultSync;
import org.fer.syncfiles.model.ResultSyncAction;
import org.fer.syncfiles.model.ResultSyncDetail;

public class SyncFileMgr {
	private static final Logger log = Logger.getLogger(SyncFileMgr.class);
	
	private Param param;
	private Date executionDate;
	private Date endExecutionDate;
	private Throwable error = null;
	
	private FilesList master;
	private FilesList slave;
	
	private FilesList newfilesToCopy;
	private FilesList filesToDelete;
	private FilesList filesToCopy;
	
	private UpdateTaskListener task = null;
	
	public SyncFileMgr() {
		super();
	}
	
	protected void launchNewFilesToCopy(boolean simulation) throws IOException {
		String msg = "Etape 3/5 : Copie des nouveaux fichiers vers la cible";
		updateMessage(msg+"...");
		newfilesToCopy = new FilesList(slave.getPrefixe());
		
		TreeSet<FileNode> dirToCopy = new TreeSet<>(master.getDirSet());
		dirToCopy.removeAll(slave.getDirSet());
		
		TreeSet<FileNode> fileToCopy = new TreeSet<>(master.getFileSet());
		fileToCopy.removeAll(slave.getFileSet());
		
		newfilesToCopy.setDirSet(dirToCopy);
		newfilesToCopy.setFileSet(fileToCopy);
		
		log.debug("launchNewFilesToCopy : \n"+newfilesToCopy.toString());
		
		if (!simulation) {
			int nbToCopy = dirToCopy.size() + fileToCopy.size();
			Path destPath = slave.getPrefixe();
			Path sourcePath = master.getPrefixe();
			
			int pos = 1;
			for(FileNode dirPath : dirToCopy) {
				Path fullDir = Paths.get(destPath.toString(), dirPath.toString());
				log.debug("Creation du répertoire : " + pos + " - " + fullDir.toString());
				Files.createDirectories(fullDir);
				if (task!=null)	{
					updateMessage(msg+" : "+dirPath);
					updateProgress(pos, nbToCopy);
				}
				pos++;
			}
			for(FileNode filePath : fileToCopy) {
				Path in = Paths.get(sourcePath.toString(), filePath.toString());
				Path target = Paths.get(destPath.toString(), filePath.toString());
				log.debug("Copie du fichier : " + pos + " - " + target.toString());
				Files.copy(in, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				if (task!=null)	{
					updateMessage(msg+" : "+filePath.toString());
					updateProgress(pos, nbToCopy);
				}
				pos++;
			}
		}
	}
	
	protected void launchFilesToDelete(boolean simulation) throws IOException {
		String msg = "Etape 4/5 : Suppression des Fichiers de la cible";
		updateMessage(msg + "...");
		filesToDelete = new FilesList(slave.getPrefixe());
		
		TreeSet<FileNode> dirTodelete = new TreeSet<>(slave.getDirSet());
		dirTodelete.removeAll(master.getDirSet());
		
		TreeSet<FileNode> fileToDelete = new TreeSet<>(slave.getFileSet());
		fileToDelete.removeAll(master.getFileSet());
		
		filesToDelete.setDirSet(dirTodelete);
		filesToDelete.setFileSet(fileToDelete);
		
		log.debug("launchFilesToDelete : \n"+filesToDelete.toString());
		if (!simulation) {
			int nbToCopy = dirTodelete.size() + fileToDelete.size();
			Path destPath = slave.getPrefixe();
			
			int pos = 1;
			for(FileNode filePath : fileToDelete) {
				Path oneFileToDelete = Paths.get(destPath.toString(), filePath.toString());
				log.debug("Suppression du fichier : " + pos + " - " + oneFileToDelete.toString());
				Files.deleteIfExists(oneFileToDelete);
				if (task!=null)	{
					updateMessage(msg+" : "+filePath.toString());
					updateProgress(pos, nbToCopy);
				}
				pos++;
			}
			log.debug("--- Dir ---");
			for(FileNode dirPath : dirTodelete.descendingSet()) {
				Path fullDir = Paths.get(destPath.toString(), dirPath.toString());
				log.debug("Suppression du répertoire : " + pos + " - " + fullDir.toString());
				Files.deleteIfExists(fullDir);
				if (task!=null)	{
					updateMessage(msg+" : "+dirPath);
					updateProgress(pos, nbToCopy);
				}
				pos++;
			}
		}
	}
	
	protected void launchFilesToCopy(boolean simulation) throws IOException {
		String msg = "Etape 5/5 : Copie des fichiers mis à jour";
		updateMessage(msg+"...");
		filesToCopy = new FilesList(slave.getPrefixe());
		
		TreeSet<FileNode> dirToCopy = new TreeSet<>();
		
		TreeSet<FileNode> fileToCopy = new TreeSet<>(master.getFileSet());
		fileToCopy.retainAll(slave.getFileSet());
		
		filesToCopy.setDirSet(dirToCopy);
		TreeSet<FileNode> realfileCopied = new TreeSet<>();
		
		log.debug("launchFilesToCopy : \n"+filesToCopy.toString());
		int nbToCopy = fileToCopy.size();
		Path destPath = slave.getPrefixe();
		Path sourcePath = master.getPrefixe();
		
		int pos = 1;
		for(FileNode filePath : fileToCopy) {
			Path in = Paths.get(sourcePath.toString(), filePath.toString());
			Path target = Paths.get(destPath.toString(), filePath.toString());
			if (Files.size(in)!=Files.size(target) ||
					!Files.getLastModifiedTime(in).equals(Files.getLastModifiedTime(target))) {
				log.debug("Copie du fichier : " + pos + " - " + target.toString());
				if (!simulation) {
					Files.copy(in, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
				}
				realfileCopied.add(filePath);
			}/* else {
				log.debug("On ne copie pas le fichier : " + pos + " - " + target.toString());
			}*/
			if (task!=null && !simulation)	{
				updateMessage(msg+" : "+filePath.toString());
				updateProgress(pos, nbToCopy);
			}
			pos++;
		}
		
		filesToCopy.setFileSet(realfileCopied);
	}
	
	public ResultSync getResultSync() {
		ResultSync res = new ResultSync();
		res.setParam(param);
		List<ResultSyncDetail> resultSyncDetailList = new ArrayList<>();
		res.setActions(resultSyncDetailList);
		res.setExecutionDate(executionDate);
		res.setEndExecutionDate(endExecutionDate);
		res.setReaded(false);
		if (error!=null) {
			res.setMsgError("Erreur de synchronisation : \n" + error.getMessage());
			res.setError(true);
			res.setToolTip(true);
		} else {
			res.setMsgError("La synchronisation s'est effectuée avec succès.");
			res.setError(false);
			res.setToolTip(false);
		}
		
		if (filesToDelete!=null) {
			for(FileNode fileNode : filesToDelete.getFileSet()) {
				addResultSyncDetail(res, resultSyncDetailList, fileNode, ResultSyncAction.DELETE_FILE);
			}
			for(FileNode fileNode : filesToDelete.getDirSet()) {
				addResultSyncDetail(res, resultSyncDetailList, fileNode, ResultSyncAction.DELETE_DIRECTORY);
			}
		}
		if (newfilesToCopy!=null) {
			for(FileNode fileNode : newfilesToCopy.getDirSet()) {
				addResultSyncDetail(res, resultSyncDetailList, fileNode, ResultSyncAction.NEW_DIRECTORY);
			}
			for(FileNode fileNode : newfilesToCopy.getFileSet()) {
				addResultSyncDetail(res, resultSyncDetailList, fileNode, ResultSyncAction.NEW_FILE);
			}
		}
		if (filesToCopy!=null) {
			for(FileNode fileNode : filesToCopy.getFileSet()) {
				addResultSyncDetail(res, resultSyncDetailList, fileNode, ResultSyncAction.COPY_FILE);
			}
		}
		return res;
	}

	private void addResultSyncDetail(ResultSync res, List<ResultSyncDetail> resultSyncDetailList, FileNode fileNode, ResultSyncAction action) {
		ResultSyncDetail detail = new ResultSyncDetail();
		detail.setResultSync(res);
		detail.setAction(action);
		detail.setPath(fileNode.getValue().getRelativePathString());
		resultSyncDetailList.add(detail);
	}

	public void launchSyncFiles(Param param, UpdateTaskListener task, boolean isSimulation) {
		this.param = param;
		this.task = task;
		try {
			this.executionDate = new Date();
			Path masterPath = FileSystems.getDefault().getPath(param.getMasterDir());
			Path slavePath = FileSystems.getDefault().getPath(param.getSlaveDir());
			
			if (!masterPath.toFile().exists()) {
				throw new Exception("Le chemin source n'existe pas : " + masterPath.toString());
			} else if (!slavePath.toFile().exists()) {
				throw new Exception("Le chemin de destination n'existe pas : " + masterPath.toString());
			} else {
				updateMessage("Etape 1/5 : Chargement de la liste des fichiers source...");
				this.master = new FilesList(masterPath);
				Files.walkFileTree(masterPath , new SyncFileVisitor(param, master));
				this.master.initTree();
				//log.debug(master.toString());
				log.debug("============= Fin 1 walkFileTree ==================");
				
				updateMessage("Etape 2/5 : Chargement de la liste des fichiers cible...");
				this.slave = new FilesList(slavePath);
				Files.walkFileTree(slavePath , new SyncFileVisitor(param, slave));
				this.slave.initTree();
				//log.debug(slave.toString());
				log.debug("============= Fin 2 walkFileTree ==================");
				
				launchNewFilesToCopy(isSimulation);
				log.debug("============= Fin launchNewFilesToCopy ==================");
	
				launchFilesToDelete(isSimulation);
				log.debug("============= Fin launchFilesToDelete ==================");
				
				launchFilesToCopy(isSimulation);
				log.debug("============= Fin launchFilesToCopy ==================");
			}
		} catch (Throwable e) {
			error = e;
			log.info("La synchronisation a échoué : " + e.getMessage());
			e.printStackTrace();
		} finally {
			this.endExecutionDate = new Date();
		}
	}


	protected void updateMessage(String msg) {
		if (task !=null) {
			task.updateMessage(msg);
		}
	}
	
	protected void updateProgress(int pos, int nbToCopy) {
		if (task!=null) {
			task.updateProgress(pos, nbToCopy);
		}
	}
	
	public String getMasterTreeStr() {
		if (master==null) {
			return null;
		}
		return master.getTreeStr();
	}
	public String getSlaveTreeStr() {
		if (slave==null) {
			return null;
		}
		return slave.getTreeStr();
	}
	
	public TreeSet<Path> getMasterErrorPath() {
		return master.getErrorPath();
	}
	
	public TreeSet<Path> getSlaveErrorPath() {
		return master.getErrorPath();
	}
	
}
