package org.fer.syncfiles.bus;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.tree.FileNode;
import org.fer.syncfiles.bus.tree.FileValue;
import org.fer.syncfiles.model.Param;

public class FilesList {
	private static final Logger log = Logger.getLogger(FilesList.class);
	
	private Path prefixe;
	private TreeSet<FileNode> dirSet = new TreeSet<>();
	private TreeSet<FileNode> fileSet = new TreeSet<>();
	private TreeSet<Path> errorPath = new TreeSet<>();
	private FileNode root;
	
	public void setDirSet(TreeSet<FileNode> dirSet) {
		this.dirSet = dirSet;
	}

	public void setFileSet(TreeSet<FileNode> fileSet) {
		this.fileSet = fileSet;
	}

	public FilesList(Path prefixe) {
		super();
		assert prefixe!=null : "Le prefixe ne peut être null.";
		this.prefixe = prefixe;
		root = new FileNode(prefixe, prefixe.relativize(prefixe));
		dirSet.add(root);
	}
	
	public void initTree() {
		for(FileNode fileNode : dirSet) {
			Path parentPath= fileNode.getValue().getParentPath();
			if (parentPath==null) {
				log.info("Le parent de " + fileNode + " est null");
				if (!fileNode.getValue().equals(root.getValue())) {
					root.addChild(fileNode);
				}
			} else {
				FileNode parentNode = new FileNode(new FileValue(parentPath));
				parentNode = dirSet.ceiling(parentNode);
				parentNode.addChild(fileNode);
			}
		}
		for(FileNode fileNode : fileSet) {
			Path parentPath= fileNode.getValue().getParentPath();
			if (parentPath==null) {
				root.addChild(fileNode);
			} else {
				FileNode parentNode = new FileNode(new FileValue(parentPath));
				parentNode = dirSet.ceiling(parentNode);
				parentNode.addChild(fileNode);
			}
		}
	}
	
	public void addDir(Path dir, Param param, BasicFileAttributes attrs) {
		Path relaDir = prefixe.relativize(dir);
		if (relaDir.toString().equals("")) {
			return;
		}
		if (match(param, relaDir)) {
			FileNode relaDirNode = new FileNode(prefixe, relaDir, attrs);
			dirSet.add(relaDirNode);
			if (param!=null && param.isIncludeDir()) {
				Path parent = relaDir.getParent();
				while (parent!=null && !parent.toString().equals("")) {
					FileNode relaParentNode = new FileNode(prefixe, parent, attrs);
					dirSet.add(relaParentNode);
					parent = parent.getParent();
				}
			}
		}
	}
	
	public boolean match(Param param, Path relaExp) {
		String regExpIncludeExcludePattern = (param==null)?null:param.getRegExpIncludeExcludePattern();
		if (regExpIncludeExcludePattern==null) {
			return true;
		}
		
		try {
			String strMatch = ""+relaExp.toString();
			if (strMatch.matches(regExpIncludeExcludePattern)) {
				return param.isIncludeDir();
			}
			return !param.isIncludeDir();
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public void addFile(Path file, Param param, BasicFileAttributes attrs) {
		Path relaFile = prefixe.relativize(file);
		if (match(param, relaFile)) {
			FileNode relaFileNode = new FileNode(prefixe, relaFile, attrs);
			fileSet.add(relaFileNode);
		}
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		
		res.append("Préfixe : ").append(prefixe).append("\n")
		.append("Liste des répertoires : \n");
		for(FileNode path : dirSet) {
			res.append(path).append("\n");
		}
		res.append("Liste des fichiers : \n");
		for(FileNode path : fileSet) {
			res.append(path).append("\n");
		}
		
		return res.toString();
	}

	public Path getPrefixe() {
		return prefixe;
	}

	public TreeSet<FileNode> getDirSet() {
		return dirSet;
	}

	public TreeSet<FileNode> getFileSet() {
		return fileSet;
	}
	
	public String getTreeStr() {
		if (root==null) {
			return null;
		} else {
			return root.getTreeStr();
		}
	}
	
	public void addError(Path path) {
		errorPath.add(path);
	}

	public TreeSet<Path> getErrorPath() {
		return errorPath;
	}
	
}
