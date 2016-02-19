package org.fer.syncfiles.bus.tree;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.*;
import org.infinispan.tree.*;
import org.infinispan.tree.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by fer on 02/11/2014.
 */
public class MergedFileTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(MergedFileTreeCacheVisitor.class);

    private List<MergedFileInfoJson> mergedFileInfoList;

    public MergedFileTreeCacheVisitor() {
        mergedFileInfoList = new ArrayList<>();
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
            if (fileInfo.getRelativePathString().equals("01 - Janvier/2014-01-08")) {
                log.info("Yep -> " + fileInfo);
            }
            if (OriginFile.SOURCE.equals(fileInfo.getOriginFile())) {
                mergedFileInfo.setSourceFileInfo(fileInfo);
            } else if (OriginFile.TARGET.equals(fileInfo.getOriginFile())) {
                mergedFileInfo.setTargetFileInfo(fileInfo);
            } else {
                log.error("Problème dans la construction des fichiers à afficher : " + fileInfo);
            }
        }
        if (mergedFileInfo.getSourceFileInfo()!=null || mergedFileInfo.getTargetFileInfo()!=null) {
            mergedFileInfoList.add(mergedFileInfo);
        }
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        throw new RuntimeException("Not implemented here");
    }
}
