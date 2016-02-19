package org.fer.syncfiles.service;

import org.fer.syncfiles.model.*;

import org.infinispan.tree.Fqn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ParamService {
    public static final String HUBIC_KEY_TREE = "hubic";

    void printHubicCache(String pathname, String paramKey) throws FileNotFoundException;

    void writeTreeToJson(String paramKey) throws FileNotFoundException;

    public abstract Param save(Param param);

    public void delete(String key);

    public abstract ParamList findAll();

    public Param load(String key);


    void prepSynchroSourceTargetHubic(Param param);

    public void synchroHubic(Param param) throws FileNotFoundException;

    Param findOne(String key);

    List<MergedFileInfoJson> loadMergedFileFromParam(String paramKey);

    List<MergedFileInfoJson> loadMergedFileFromParam(String paramKey, String pathNode, String nameFilter, boolean fileToSync);

    List<MergedFileInfoJson> loadFlatMergedFileFromParam(String paramKey, String path);

    MapTreeResult loadMergedFileViewFromParam(String paramKey, String path);

    void initCacheTreeForAllHubicFiles() throws IOException;

    void putTreeCache(String prefix, FileInfo fileInfo);

    void putTreeCache(Fqn fqn, FileInfo fileInfo);

    void putTreeCache(FileInfo fileInfo);

    void removeTreeCache(String prefix, FileInfo fileInfo);

    FileInfo getTreeCache(String prefix, String relativePathString, OriginFile originFile);

    void prepFilesToSynchronize(Param param) throws IOException, ParseException;

    FileInfo getTreeCache(Fqn fqn, Object key);

    FileInfo getTreeCacheByOrigin(Fqn fqn, OriginFile originFile);

    void initPreviousData(FileInfo fileInfo);

    void deleteHubicObject(String targetFileName);

    void updateFileToHubic(Param param, FileInfo fileInfo, String msg);

    void quitApp();

    void removeTreeCache(Fqn fqn, Object key);
}