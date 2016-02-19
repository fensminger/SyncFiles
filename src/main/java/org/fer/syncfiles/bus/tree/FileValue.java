package org.fer.syncfiles.bus.tree;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FileValue implements Comparable<FileValue> {

	private String relativePathString;
	
	private boolean isOther;
	private boolean isDirectory;
	private boolean isRegularFile;
	private boolean isSymbolicLink;
	
	private long creationTime;
	private long lastAccessTime;
	private long lastModifiedTime;
	private long size;
	
	public FileValue() {
		super();
	}
	
	public FileValue(Path relaPath) {
		super();
		this.relativePathString = relaPath.toString();
	}
	
	public FileValue(Path relaPath, BasicFileAttributes attrs) {
		super();
		this.relativePathString = relaPath.toString();
		this.creationTime = attrs.creationTime().toMillis();
		this.isDirectory = attrs.isDirectory();
		this.isOther = attrs.isOther();
		this.isRegularFile = attrs.isRegularFile();
		this.isSymbolicLink = attrs.isSymbolicLink();
		this.lastAccessTime = attrs.lastAccessTime().toMillis();
		this.lastModifiedTime = attrs.lastModifiedTime().toMillis();
		this.size = attrs.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relativePathString == null) ? 0 : relativePathString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileValue other = (FileValue) obj;
		if (relativePathString == null) {
			if (other.relativePathString != null)
				return false;
		} else if (!relativePathString.equals(other.relativePathString))
			return false;
		return true;
	}

	@Override
	public int compareTo(FileValue obj) {
		if (this == obj)
			return 0;
		if (obj == null)
			return -1;
		String p1 = relativePathString;
		String p2 = obj.relativePathString;
		if (p1 == p2)
			return 0;
		if (p2 == null)
			return -1;
		return p1.compareTo(p2);
	}

	@Override
	public String toString() {
		return relativePathString;
	}

	public String getRelativePathString() {
		return relativePathString;
	}

	public void setRelativePathString(String relativePathString) {
		this.relativePathString = relativePathString;
	}

	public Path getParentPath() {
		Path path = Paths.get(relativePathString);
		return path.getParent();
	}
	
	public boolean isOther() {
		return isOther;
	}

	public void setOther(boolean isOther) {
		this.isOther = isOther;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public boolean isRegularFile() {
		return isRegularFile;
	}

	public void setRegularFile(boolean isRegularFile) {
		this.isRegularFile = isRegularFile;
	}

	public boolean isSymbolicLink() {
		return isSymbolicLink;
	}

	public void setSymbolicLink(boolean isSymbolicLink) {
		this.isSymbolicLink = isSymbolicLink;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
