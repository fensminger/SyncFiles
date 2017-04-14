package org.fer.syncfiles.services.hubic;

import com.github.scribejava.core.model.Token;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.fer.syncfiles.domain.ContainerInfo;
import org.fer.syncfiles.domain.HubicInfo;
import org.fer.syncfiles.domain.ObjectDetailInfo;
import org.fer.syncfiles.domain.ObjectInfo;
import org.fer.syncfiles.repository.HubicInfoRepository;
import org.fer.syncfiles.services.hubic.Consumer.ObjectConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Created by fensm on 26/09/2016.
 */
@Service("hubicService")
@Profile("test")
public class HubicServiceOffLine implements HubicService {
    private final Logger log = LoggerFactory.getLogger(HubicServiceOffLine.class);

    private HubicInfoRepository hubicInfoRepository;

    @Autowired
    public HubicServiceOffLine(HubicInfoRepository hubicInfoRepository) {
        this.hubicInfoRepository = hubicInfoRepository;
    }

    @Override
    public void consumeObjects(final String container, final String prefix, final Consumer<ObjectInfo> objectConsumer) throws IOException {
        hubicInfoRepository.findByContainer(container).forEach(h -> {
            if (prefix==null || h.getName().startsWith(prefix)) {
                objectConsumer.accept(new ObjectInfo(h, prefix));
            }
        });
    }

    @Override
    public ObjectDetailInfo loadObjectMetaData(String container, String fileName) throws IOException {
        HubicInfo hubicInfo = hubicInfoRepository.findByContainerAndName(container, fileName);
        ObjectDetailInfo objectDetailInfo = new ObjectDetailInfo();
        objectDetailInfo.setContentLength(hubicInfo.getContentLength());
        objectDetailInfo.setContentType(hubicInfo.getContentType());
        objectDetailInfo.setHashMd5(hubicInfo.getHashMd5());
        objectDetailInfo.setLastModified(hubicInfo.getLastModified());
        return objectDetailInfo;
    }

    @Override
    public CloseableHttpResponse loadObject(String container, String fileName, boolean loadManifest) throws IOException {
        return new CloseableHttpResponse() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public StatusLine getStatusLine() {
                return new BasicStatusLine(new ProtocolVersion("http",2,1), 200,"OK");
            }

            @Override
            public void setStatusLine(StatusLine statusLine) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i) {

            }

            @Override
            public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {

            }

            @Override
            public void setStatusCode(int i) throws IllegalStateException {

            }

            @Override
            public void setReasonPhrase(String s) throws IllegalStateException {

            }

            @Override
            public HttpEntity getEntity() {
                return new HttpEntity() {
                    @Override
                    public boolean isRepeatable() {
                        return false;
                    }

                    @Override
                    public boolean isChunked() {
                        return false;
                    }

                    @Override
                    public long getContentLength() {
                        return 0;
                    }

                    @Override
                    public Header getContentType() {
                        return null;
                    }

                    @Override
                    public Header getContentEncoding() {
                        return null;
                    }

                    @Override
                    public InputStream getContent() throws IOException, UnsupportedOperationException {
                        return null;
                    }

                    @Override
                    public void writeTo(OutputStream outputStream) throws IOException {
                        outputStream.write("OK, Test File.".getBytes());
                    }

                    @Override
                    public boolean isStreaming() {
                        return false;
                    }

                    @Override
                    public void consumeContent() throws IOException {

                    }
                };
            }

            @Override
            public void setEntity(HttpEntity httpEntity) {

            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public void setLocale(Locale locale) {

            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                return null;
            }

            @Override
            public boolean containsHeader(String s) {
                return false;
            }

            @Override
            public Header[] getHeaders(String s) {
                return new Header[0];
            }

            @Override
            public Header getFirstHeader(String s) {
                return null;
            }

            @Override
            public Header getLastHeader(String s) {
                return null;
            }

            @Override
            public Header[] getAllHeaders() {
                return new Header[0];
            }

            @Override
            public void addHeader(Header header) {

            }

            @Override
            public void addHeader(String s, String s1) {

            }

            @Override
            public void setHeader(Header header) {

            }

            @Override
            public void setHeader(String s, String s1) {

            }

            @Override
            public void setHeaders(Header[] headers) {

            }

            @Override
            public void removeHeader(Header header) {

            }

            @Override
            public void removeHeaders(String s) {

            }

            @Override
            public HeaderIterator headerIterator() {
                return null;
            }

            @Override
            public HeaderIterator headerIterator(String s) {
                return null;
            }

            @Override
            public HttpParams getParams() {
                return null;
            }

            @Override
            public void setParams(HttpParams httpParams) {

            }
        };
    }

    @Override
    public void uploadObject(String container, String fileName, String md5, File fileToUpload) throws IOException {
        HubicInfo hubicInfo = hubicInfoRepository.findByContainerAndName(container, fileName);
        if (hubicInfo==null) {
            hubicInfo = new HubicInfo();
        }

        hubicInfo.setName(fileName);
        hubicInfo.setHashMd5(md5);
        int beginIndex = fileName.lastIndexOf(".");
        if (beginIndex>=0) {
            hubicInfo.setContentType(fileName.substring(beginIndex).replaceFirst(".", ""));
        } else {
            hubicInfo.setContentType("app/binary");
        }
        hubicInfo.setDirectory(fileToUpload.isDirectory());
        if (hubicInfo.isDirectory()) {
            hubicInfo.setContentType("application/directory");
        }
        hubicInfo.setLastModified(new Date(Files.getLastModifiedTime(Paths.get(fileToUpload.getAbsolutePath())).toMillis()));
        hubicInfo.setContentLength(fileToUpload.length());
        hubicInfo.setSize(fileToUpload.length());
        hubicInfo.setContainer(container);
        hubicInfoRepository.save(hubicInfo);
    }

    @Override
    public void refreshTokenIfExpired() {

    }

    @Override
    public void deleteObject(String container, String fileName) throws IOException {
        HubicInfo hubicInfo = hubicInfoRepository.findByContainerAndName(container, fileName);
        if (hubicInfo!=null) {
            hubicInfoRepository.delete(hubicInfo);
        }
    }

    @Override
    public SwiftAccess authenticate(String clientId, String clientSecret, int port, String user, String pwd) throws IOException, InterruptedException {
        return new SwiftAccess("token", "endpoint", "expires", new Token("token") {
            @Override
            public String getRawResponse() {
                return "rawResponse";
            }

            @Override
            public String getParameter(String parameter) {
                return parameter;
            }
        });
    }
}
