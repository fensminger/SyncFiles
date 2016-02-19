package org.fer.syncfiles.service;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.Param;
import org.javaswift.joss.headers.Header;
import org.javaswift.joss.model.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * Created by fer on 01/10/2014.
 */
@Service("HubicSynchroMock")
@Scope("singleton")
public class HubicSynchroMockImpl implements HubicSynchro {
    private static final Logger log = Logger.getLogger(HubicSynchroMockImpl.class);

    Container mainContainer;

    @Override
    public Container getMainContainer() {
        if (mainContainer==null) {
            mainContainer = new Container() {
                @Override
                public void makePublic() {

                }

                @Override
                public void makePrivate() {

                }

                @Override
                public Container create() {
                    return null;
                }

                @Override
                public void delete() {

                }

                @Override
                public boolean exists() {
                    return false;
                }

                @Override
                public StoredObject getObject(String name) {
                    return null;
                }

                @Override
                public StoredObject getObjectSegment(String name, int part) {
                    return null;
                }

                @Override
                public boolean isInfoRetrieved() {
                    return false;
                }

                @Override
                public void reload() {

                }

                @Override
                public int getCount() {
                    return 0;
                }

                @Override
                public long getBytesUsed() {
                    return 0;
                }

                @Override
                public FormPost getFormPost(String redirect, long maxFileSize, long maxFileCount, long seconds) {
                    return null;
                }

                @Override
                public Collection<DirectoryOrObject> listDirectory(String prefix, Character delimiter, String marker, int pageSize) {
                    return null;
                }

                @Override
                public Collection<DirectoryOrObject> listDirectory(Directory directory) {
                    return null;
                }

                @Override
                public Collection<DirectoryOrObject> listDirectory() {
                    return null;
                }

                @Override
                public boolean isPublic() {
                    return false;
                }

                @Override
                public void setCount(int count) {

                }

                @Override
                public void setBytesUsed(long bytesUsed) {

                }

                @Override
                public Account getAccount() {
                    return null;
                }

                @Override
                public void setContainerRights(String writePermissions, String readPermissions) {

                }

                @Override
                public String getContainerReadPermission() {
                    return null;
                }

                @Override
                public String getcontainerWritePermission() {
                    return null;
                }

                @Override
                public void setCustomHeaders(Collection<Header> headers) {

                }

                @Override
                public Collection<Header> getCustomHeaders() {
                    return null;
                }

                @Override
                public int compareTo(Container o) {
                    return 0;
                }

                @Override
                public Collection<StoredObject> list() {
                    return null;
                }

                @Override
                public Collection<StoredObject> list(String prefix, String marker, int pageSize) {
                    return null;
                }

                @Override
                public Collection<StoredObject> list(PaginationMap paginationMap, int page) {
                    return null;
                }

                @Override
                public PaginationMap getPaginationMap(int pageSize) {
                    return null;
                }

                @Override
                public PaginationMap getPaginationMap(String prefix, int pageSize) {
                    return null;
                }

                @Override
                public int getMaxPageSize() {
                    return 0;
                }

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public void metadataSetFromHeaders() {

                }

                @Override
                public void setMetadata(Map<String, Object> metadata) {

                }

                @Override
                public void setAndSaveMetadata(String key, Object value) {

                }

                @Override
                public void setAndDoNotSaveMetadata(String key, Object value) {

                }

                @Override
                public void removeAndSaveMetadata(String key) {

                }

                @Override
                public void removeAndDoNotSaveMetadata(String key) {

                }

                @Override
                public Object getMetadata(String key) {
                    return null;
                }

                @Override
                public void saveMetadata() {

                }

                @Override
                public Map<String, Object> getMetadata() {
                    return null;
                }

                @Override
                public String getPath() {
                    return null;
                }
            };
        }
        return mainContainer;
    }

    @Override
    public void loadAllHubicFilesIntoCache(String prefix, ParamService paramService) {
    }

    @Override
    public void updateFileToHubic(Param param, FileInfo fileInfo, String msg) {
        log.warn("Mock updateFileToHubic : " + fileInfo);
    }

    @Override
    public void deleteHubicObject(String targetFileName) {
        log.warn("Mock deleteHubicObject : " + targetFileName);
    }


}
