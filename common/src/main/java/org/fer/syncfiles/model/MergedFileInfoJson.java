package org.fer.syncfiles.model;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by fer on 22/10/2014.
 */
public class MergedFileInfoJson {
    private FileInfo sourceFileInfo;
    private FileInfo targetFileInfo;
    private String fileName;
    private String fullName;
    private boolean directory;
    private boolean synchronize;


    public MergedFileInfoJson() {
        super();
    }

    public FileInfo getSourceFileInfo() {
        return sourceFileInfo;
    }

    public void setSourceFileInfo(FileInfo sourceFileInfo) {
        this.sourceFileInfo = sourceFileInfo;
        if (sourceFileInfo!=null) {
            fullName = sourceFileInfo.getRelativePathString();
            fileName = getFileName(fullName);
            directory = sourceFileInfo.isDirectory();
            if (directory) {
                synchronize = false;
            } else {
                if (targetFileInfo==null) {
                    synchronize = true;
                } else {
                    synchronize = sourceFileInfo.getHash().equals(targetFileInfo.getHash());
                }
            }
        }
    }

    public FileInfo getTargetFileInfo() {
        return targetFileInfo;
    }

    public void setTargetFileInfo(FileInfo targetFileInfo) {
        this.targetFileInfo = targetFileInfo;
        if (targetFileInfo!=null) {
            fullName = targetFileInfo.getRelativePathString();
            fileName = getFileName(fullName);
            directory = targetFileInfo.isDirectory();
            if (directory) {
                synchronize = false;
            } else {
                if (sourceFileInfo==null) {
                    synchronize = true;
                } else {
                    synchronize = targetFileInfo.getHash().equals(sourceFileInfo.getHash());
                }
            }
        }
    }

    private String getFileName(String fullPath) {
        return Paths.get(fullPath).getFileName().toString();
    }

    public String getPath() {
        if (fileName==null) {
            return "";
        }
        final Path parent = Paths.get(fullName).getParent();
        if (parent==null) {
            return "";
        }
        return parent.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isSynchronize() {
        return synchronize;
    }

    public void setSynchronize(boolean synchronize) {
        this.synchronize = synchronize;
    }
}
