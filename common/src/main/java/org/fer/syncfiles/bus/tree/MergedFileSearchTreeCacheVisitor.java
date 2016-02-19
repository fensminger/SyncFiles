package org.fer.syncfiles.bus.tree;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.util.Comparators;
import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.MergedFileInfoJson;
import org.fer.syncfiles.model.OriginFile;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fer on 02/11/2014.
 */
public class MergedFileSearchTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(MergedFileSearchTreeCacheVisitor.class);
    private final String nameFilter;
    private final boolean fileToSync;

    private List<MergedFileInfoJson> mergedFileInfoList;


    public MergedFileSearchTreeCacheVisitor(String nameFilter, boolean fileToSync) {
        mergedFileInfoList = new ArrayList<>();
        this.nameFilter = nameFilter;
        this.fileToSync = fileToSync;
    }

    public List<MergedFileInfoJson> getMergedFileInfoList() {
        return mergedFileInfoList;
    }

    @Override
    public void visit(Node<Object, Object> value) {
        Map<Object, Object> dataMap = value.getData();
        MergedFileInfoJson mergedFileInfo = new MergedFileInfoJson();
        for(Object object: dataMap.values()) {
            FileInfo fileInfo = (FileInfo) object;
            if (nameFilter==null ||
                    (fileInfo!=null && fileInfo.getRelativePathString()!=null && fileInfo.getRelativePathString().toLowerCase().contains(nameFilter))) {
                if (OriginFile.SOURCE.equals(fileInfo.getOriginFile())) {
                    mergedFileInfo.setSourceFileInfo(fileInfo);
                } else if (OriginFile.TARGET.equals(fileInfo.getOriginFile())) {
                    mergedFileInfo.setTargetFileInfo(fileInfo);
                } else {
                    log.error("Problème dans la construction des fichiers à afficher : " + fileInfo);
                }
            }
        }
        final FileInfo sourceFileInfo = mergedFileInfo.getSourceFileInfo();
        final FileInfo targetFileInfo = mergedFileInfo.getTargetFileInfo();
        if (sourceFileInfo !=null || targetFileInfo !=null) {
            if (!fileToSync) {
                mergedFileInfoList.add(mergedFileInfo);
            } else {
                boolean isDirectory = (sourceFileInfo==null)?false:sourceFileInfo.isDirectory();
                isDirectory = (targetFileInfo==null)?isDirectory:targetFileInfo.isDirectory();
                if (!isDirectory) {
                    if (sourceFileInfo != null && targetFileInfo == null) {
                        mergedFileInfoList.add(mergedFileInfo);
                    } else if (targetFileInfo != null && sourceFileInfo == null) {
                        mergedFileInfoList.add(mergedFileInfo);
                    } else {
                        final String sourceFileInfoHash = sourceFileInfo.getHash();
                        final String targetFileInfoHash = targetFileInfo.getHash();
                        if (sourceFileInfoHash != null && targetFileInfoHash == null) {
                            mergedFileInfoList.add(mergedFileInfo);
                        } else if (targetFileInfoHash != null && sourceFileInfoHash == null) {
                            mergedFileInfoList.add(mergedFileInfo);
                        } else if (!sourceFileInfoHash.equals(targetFileInfoHash)) {
                            mergedFileInfoList.add(mergedFileInfo);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        throw new RuntimeException("Not implemented here");
    }
}
