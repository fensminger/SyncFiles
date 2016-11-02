package org.fer.syncfiles.services.hubic;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.fer.syncfiles.domain.ContainerInfo;
import org.fer.syncfiles.domain.ObjectDetailInfo;
import org.fer.syncfiles.domain.ObjectInfo;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by fensm on 26/09/2016.
 */
public interface HubicService {
    void consumeObjects(String container, String prefix, Consumer<ObjectInfo> objectConsumer) throws IOException;

    ObjectDetailInfo loadObjectMetaData(String container, String fileName) throws IOException;

    CloseableHttpResponse loadObject(String container, String fileName, boolean loadManifest) throws IOException;

    void uploadObject(String container, String fileName, String md5, File fileToUpload) throws IOException;

    void refreshTokenIfExpired();

    void deleteObject(String container, String fileName) throws IOException;

    SwiftAccess authenticate(String clientId, String clientSecret, int port, String user, String pwd) throws IOException, InterruptedException;
}
