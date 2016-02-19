package org.fer.syncfiles.bus.tree;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.FileInfoAction;
import org.fer.syncfiles.model.Param;
import org.fer.syncfiles.service.ParamService;
import org.infinispan.tree.Fqn;

/**
 * Created by fer on 02/11/2014.
 */
public class UpdateParamHubicTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(UpdateParamHubicTreeCacheVisitor.class);
    private final ParamService paramService;

    private Param param;
    private final Fqn fqnParam;
    private final Fqn fqnPrefixeMaster;
    private int prefixeSizeToRemove;
    private String prefixeSlaveDir;

    public UpdateParamHubicTreeCacheVisitor(Param param, ParamService paramService) {
        this.param = param;
        this.paramService = paramService;
        this.fqnParam = Fqn.fromString("/" + param.getKey());
        this.fqnPrefixeMaster = Fqn.fromString(param.getSlaveDir());
        prefixeSizeToRemove = fqnPrefixeMaster.size() + 1;
        prefixeSlaveDir = param.getSlaveDir();
        if (prefixeSlaveDir.startsWith("/")) {
            prefixeSlaveDir = prefixeSlaveDir.substring(1);
        }
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        // fqn = relativePathString
        FileInfo fileInfo = (FileInfo) o;
        if (fileInfo.getRelativePathString().startsWith(prefixeSlaveDir)) {
            Fqn relativeFqn = Fqn.fromRelativeFqn(fqnParam, fqn.fromString(fileInfo.getRelativePathString().substring(prefixeSlaveDir.length())));
            FileInfo fileInfoExist = paramService.getTreeCache(relativeFqn, key);
            if (fileInfoExist == null) {
                fileInfoExist = new FileInfo(fileInfo.getOriginFile(), fileInfo);
                fileInfoExist.setLastAccessTime(fileInfo.getLastAccessTime());
                fileInfoExist.setLastModifiedTime(fileInfo.getLastModifiedTime());
                fileInfoExist.setSize(fileInfo.getSize());
                fileInfoExist.setHash(fileInfo.getHash());
                fileInfoExist.setParamKey(param.getKey());
                fileInfoExist.setFileInfoAction(FileInfoAction.CREATE);
                try {
                    fileInfoExist.setRelativePathString(fileInfo.getRelativePathString().substring(prefixeSlaveDir.length() + 1));
                } catch (StringIndexOutOfBoundsException e) {
                    log.error("Error : skipping file = " + fileInfo);
                    log.error(e);
                    return ;
                }
            } else {
                if (fileInfo.getHash().equals(fileInfoExist.getHash())) {
                    fileInfoExist.setFileInfoAction(FileInfoAction.NOTHING);
                } else {
                    initPreviousData(fileInfoExist);
                    fileInfoExist.setLastAccessTime(fileInfo.getLastAccessTime());
                    fileInfoExist.setLastModifiedTime(fileInfo.getLastModifiedTime());
                    fileInfoExist.setSize(fileInfo.getSize());
                    fileInfoExist.setHash(fileInfo.getHash());
                    fileInfoExist.setFileInfoAction(FileInfoAction.UPDATE);
                }
            }
            paramService.putTreeCache(param.getKey(), fileInfoExist);
        }
    }

    private void initPreviousData(FileInfo fileInfo) {
        fileInfo.setPreviousHash(fileInfo.getHash());
        fileInfo.setPreviousLastAccessTime(fileInfo.getLastAccessTime());
        fileInfo.setPreviousLastModifiedTime(fileInfo.getLastModifiedTime());
        fileInfo.setPreviousSize(fileInfo.getSize());
    }

}
