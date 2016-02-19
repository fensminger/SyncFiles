package org.fer.syncfiles.bus.tree;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileNode extends Node<FileValue> {

	public FileNode(Path rootPath, Path path) {
		super();
		FileValue value = new FileValue(path);
		setValue(value);
	}
	
	public FileNode(Path rootPath, Path path, BasicFileAttributes attrs) {
		super();
		FileValue value = new FileValue(path, attrs);
		//value.init(rootPath);
		setValue(value);
	}

	public FileNode(FileValue value) {
		super(value);
	}

	@Override
	public String toString() {
		FileValue value = getValue();
		if (value==null) {
			return null;
		} else {
			return value.getRelativePathString();
		}
	}
	
}
