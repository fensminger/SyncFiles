package org.fer.syncfiles.service;

import org.fer.syncfiles.bus.tree.JsonFileTreeCacheVisitor;
import org.fer.syncfiles.model.FileInfo;

import java.io.*;

/**
 * Created by fer on 11/12/14.
 */
public class LocalFileJsonWriter implements Closeable {

    PrintWriter printWriter;
    JsonFileTreeCacheVisitor serializeCache;

    public LocalFileJsonWriter(String paramKey) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(new File(getTreeParamFileName(paramKey)));
        serializeCache = new JsonFileTreeCacheVisitor(printWriter);
    }

    public void writeObject(FileInfo fileInfo) throws IOException {
        serializeCache.writeFileInfo(fileInfo);
    }

    public void close() {
        serializeCache.endWrite();
    }

    private String getTreeParamFileName(String paramKey) {
        return "param_"+paramKey+".json";
    }


}
