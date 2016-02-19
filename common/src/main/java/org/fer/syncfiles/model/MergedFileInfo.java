package org.fer.syncfiles.model;

import javafx.beans.property.SimpleStringProperty;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.naming.NamingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fer on 22/10/2014.
 */
public class MergedFileInfo implements Comparable<MergedFileInfo> {
    private FileInfo sourceFileInfo;
    private FileInfo targetFileInfo;

    
    private SimpleStringProperty nameProperty;
    
    private SimpleStringProperty originProperty;
    
    private SimpleStringProperty actionSourceProperty;
    
    private SimpleStringProperty actionTargetProperty;
    
    private SimpleStringProperty sourceSizeProperty;
    
    private SimpleStringProperty targetSizeProperty;
    
    private SimpleStringProperty sourceLastModifiedProperty;
    
    private SimpleStringProperty targetLastModifiedProperty;
    
    private NamingException internalActionSource;

    public MergedFileInfo(MergedFileInfoJson mergedFileInfoJson) {
        this.sourceFileInfo = mergedFileInfoJson.getSourceFileInfo();
        this.targetFileInfo = mergedFileInfoJson.getTargetFileInfo();
    }


    public SimpleStringProperty actionSourceProperty() {
        if (actionSourceProperty == null) {
            actionSourceProperty = new SimpleStringProperty(this, "actionSource");
        }
        return actionSourceProperty;
    }
    
    public SimpleStringProperty actionTargetProperty() {
        if (actionTargetProperty == null) {
            actionTargetProperty = new SimpleStringProperty(this, "actionTarget");
        }
        return actionTargetProperty;
    }
    
    public SimpleStringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(this, "name");
        }
        return nameProperty;
    }
    
    public SimpleStringProperty originProperty() {
        if (originProperty == null) {
            originProperty = new SimpleStringProperty(this, "origin");
        }
        return originProperty;
    }
    
    public SimpleStringProperty sourceSizeProperty() {
        if (sourceSizeProperty == null) {
            sourceSizeProperty = new SimpleStringProperty(this, "sourceSize");
        }
        return sourceSizeProperty;
    }
    
    public SimpleStringProperty targetSizeProperty() {
        if (targetSizeProperty == null) {
            targetSizeProperty = new SimpleStringProperty(this, "targetSize");
        }
        return targetSizeProperty;
    }
    
    public SimpleStringProperty sourceLastModifiedProperty() {
        if (sourceLastModifiedProperty == null) {
            sourceLastModifiedProperty = new SimpleStringProperty(this, "sourceLastModified");
        }
        return sourceLastModifiedProperty;
    }
    
    public SimpleStringProperty targetLastModifiedProperty() {
        if (targetLastModifiedProperty == null) {
            targetLastModifiedProperty = new SimpleStringProperty(this, "targetLastModified");
        }
        return targetLastModifiedProperty;
    }

    public MergedFileInfo() {
        super();
    }

    public MergedFileInfo(FileInfo sourceFileInfo, FileInfo targetFileInfo) {
        this.sourceFileInfo = sourceFileInfo;
        this.targetFileInfo = targetFileInfo;
    }

    public FileInfo getSourceFileInfo() {
        return sourceFileInfo;
    }

    public void setSourceFileInfo(FileInfo sourceFileInfo) {
        this.sourceFileInfo = sourceFileInfo;
    }

    public FileInfo getTargetFileInfo() {
        return targetFileInfo;
    }

    public void setTargetFileInfo(FileInfo targetFileInfo) {
        this.targetFileInfo = targetFileInfo;
    }

    public void initSwing() {
        nameProperty();
        actionSourceProperty();
        actionTargetProperty();
        originProperty();
        sourceLastModifiedProperty();
        sourceSizeProperty();
        targetLastModifiedProperty();
        targetSizeProperty();

        final String internalRelativePathString = getRelativePathString();
        final int beginIndex = internalRelativePathString.lastIndexOf('/');
        if (beginIndex < 0) {
            setName(internalRelativePathString);
        } else {
            setName(internalRelativePathString.substring(beginIndex + 1));
        }
        setOrigin(getInternalOrigin().toString());
        setSourceSize((getInternalSourceSize() == null) ? "N/A" : getInternalSourceSize().toString());
        setTargetSize((getInternalTargetSize() == null) ? "N/A" : getInternalTargetSize().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        setSourceLastModifiedTime((getInternalSourceLastModifiedTime() == null) ? "N/A" : sdf.format(getInternalSourceLastModifiedTime()));
        setTargetLastModifiedTime((getInternalTargetLastModifiedTime() == null) ? "N/A" : sdf.format(getInternalTargetLastModifiedTime()));
        setActionSource((getInternalActionSource() == null) ? "N/A" : getInternalActionSource().toString());
        setActionTarget((getInternalActionTarget() == null) ? "N/A" : getInternalActionTarget().toString());
    }

    
    public String getName() {
        return nameProperty().get();
    }
    
    public void setName(String value) {
        nameProperty().set(value);
    }

    
    public String getActionSource() {
        return actionSourceProperty().get();
    }
    
    public void setActionSource(String value) {
        actionSourceProperty().set(value);
    }

    
    public String getActionTarget() {
        return actionTargetProperty().get();
    }
    
    public void setActionTarget(String value) {
        actionTargetProperty().set(value);
    }

    
    public String getRelativePathString() {
        if (sourceFileInfo!=null) {
            return sourceFileInfo.getRelativePathString();
        } else {
            return targetFileInfo.getRelativePathString();
        }
    }

    
    public String getOrigin() {
        return originProperty.get();
    }
    
    public void setOrigin(String value) {
        originProperty.set(value);
    }

    
    public OriginFile getInternalOrigin() {
        if (sourceFileInfo!=null && targetFileInfo!=null) {
            return OriginFile.SOURCE_AND_TARGET;
        } else if (sourceFileInfo!=null) {
            return OriginFile.SOURCE;
        } else {
            return OriginFile.TARGET;
        }
    }

    
    public String getSourceSize() {
        return sourceSizeProperty.get();
    }
    
    public void setSourceSize(String value) {
        sourceSizeProperty.set(value);
    }

    
    public Long getInternalSourceSize() {
        if (sourceFileInfo==null) {
            return null;
        } else {
            return sourceFileInfo.getSize();
        }
    }

    
    public String getTargetSize() {
        return targetSizeProperty.get();
    }
    
    public void setTargetSize(String value) {
        targetSizeProperty.set(value);
    }

    
    public Long getInternalTargetSize() {
        if (targetFileInfo==null) {
            return null;
        } else {
            return targetFileInfo.getSize();
        }
    }

    
    public String getSourceLastModifiedTime() {
        return sourceLastModifiedProperty.get();
    }
    
    public void setSourceLastModifiedTime(String value) {
        sourceLastModifiedProperty.set(value);
    }

    
    public Date getInternalSourceLastModifiedTime() {
        if (sourceFileInfo==null) {
            return null;
        } else {
            return sourceFileInfo.getLastModifiedTime();
        }
    }

    
    public String getTargetLastModifiedTime() {
        return targetLastModifiedProperty.get();
    }
    
    public void setTargetLastModifiedTime(String value) {
        targetLastModifiedProperty.set(value);
    }

    
    public Date getInternalTargetLastModifiedTime() {
        if (targetFileInfo == null) {
            return null;
        } else {
            return targetFileInfo.getLastModifiedTime();
        }
    }

    
    public boolean isDirectory() {
        if (targetFileInfo==null) {
            return sourceFileInfo.isDirectory();
        } else {
            return targetFileInfo.isDirectory();
        }
    }

    
    public String getPath() {
        final String relativePathString = getRelativePathString();
        if (relativePathString == null) {
            return "";
        }  else {
            return relativePathString;
        }
    }

    
    public String getParentPath() {
        final String relativePathString = getRelativePathString();
        if (relativePathString==null) {
            return "";
        }
        Path path = Paths.get(relativePathString);
        if (path==null) {
            return "";
        } else {
            final Path parent = path.getParent();
            if (parent==null) {
                return "";
            } else {
                return parent.toString();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MergedFileInfo)) return false;

        MergedFileInfo that = (MergedFileInfo) o;

        return getRelativePathString().equals(that.getRelativePathString());
    }

    @Override
    public int hashCode() {
        int result = getRelativePathString() != null ? getRelativePathString().hashCode() : 0;
        return result;
    }

    @Override
    public int compareTo(MergedFileInfo o) {
        if (this == o) return 0;
        if (!(o instanceof MergedFileInfo)) return -1;

        MergedFileInfo that = (MergedFileInfo) o;

//        if (isDirectory() && that.isDirectory()) {
//            return getRelativePathString().compareTo(that.getRelativePathString());
//        } else if (isDirectory()) {
//            return 1;
//        } else if (that.isDirectory()) {
//            return 1;
//        }

        return getRelativePathString().compareTo(that.getRelativePathString());
    }

    
    public FileInfoAction getInternalActionTarget() {
        if (getTargetFileInfo()==null) {
            return FileInfoAction.NOTHING;
        }
        final FileInfoAction fileInfoAction = getTargetFileInfo().getFileInfoAction();
        return (fileInfoAction==null)?FileInfoAction.NOTHING:fileInfoAction;
    }
    
    public FileInfoAction getInternalActionSource() {
        if (getSourceFileInfo()==null) {
            return FileInfoAction.NOTHING;
        }
        final FileInfoAction fileInfoAction = getSourceFileInfo().getFileInfoAction();
        return (fileInfoAction==null)?FileInfoAction.NOTHING:fileInfoAction;
    }
}
