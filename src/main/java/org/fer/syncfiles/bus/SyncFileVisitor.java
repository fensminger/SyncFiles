package org.fer.syncfiles.bus;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.fer.syncfiles.model.Param;

public class SyncFileVisitor extends SimpleFileVisitor<Path> {
	
	private FilesList fileList;
	private Param param;

	public SyncFileVisitor(Param param,  FilesList fileList) {
		super();
		assert fileList!=null : "fileListMaster ne peut être null";
		
		this.fileList = fileList;
		this.param = param;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		if (!(attrs.isSymbolicLink() || attrs.isOther())) {
			fileList.addDir(dir, param, attrs);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		if (!(attrs.isSymbolicLink() || attrs.isOther())) {
			fileList.addFile(file, param, attrs);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		fileList.addError(file);
		return FileVisitResult.CONTINUE;
	}

	
}
