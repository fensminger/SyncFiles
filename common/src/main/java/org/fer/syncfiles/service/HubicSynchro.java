package org.fer.syncfiles.service;

import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.Param;
import org.javaswift.joss.model.Container;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by fer on 17/11/14.
 */
public interface HubicSynchro {
    Container getMainContainer();

    void loadAllHubicFilesIntoCache(String prefix, ParamService paramService) throws IOException;

    void updateFileToHubic(Param param, FileInfo fileInfo, String msg);

    void deleteHubicObject(String targetFileName);
}
