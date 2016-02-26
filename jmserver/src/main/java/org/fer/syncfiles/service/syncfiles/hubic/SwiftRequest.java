package org.fer.syncfiles.service.syncfiles.hubic;

import org.apache.catalina.util.URLEncoder;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.fer.syncfiles.service.syncfiles.hubic.Consumer.ObjectConsumer;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ObjectDetailInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Created by fensm on 22/02/2016.
 */
public class SwiftRequest implements Closeable {
    private final Logger log = LoggerFactory.getLogger(SwiftRequest.class);


    private SwiftAccess swiftAccess = null;
    private CloseableHttpClient httpClient = HttpClients.createDefault();

    public SwiftRequest(SwiftAccess swiftAccess) {
        this.swiftAccess = swiftAccess;
    }

    public SwiftRequest() {
    }

    public <T> T get(String url, Header[] headers, List<NameValuePair> params, ResponseHandler<? extends T> responseHandler) throws IOException {
        URLEncoder urlEncoder = new URLEncoder();
        final String paramString;
        if (params!=null) {
            paramString = "?" + URLEncodedUtils.format(params, "utf-8");
        } else {
            paramString = "";
        }
        HttpGet httpGet = new HttpGet(swiftAccess.getEndpoint()+urlEncoder.encode(url)+paramString);
        if (headers!=null) {
            httpGet.setHeaders(headers);
        }
        httpGet.setHeader("X-Auth-Token", swiftAccess.getToken());
        httpGet.setHeader("Accept", "application/json; charset=utf-8");
        return httpClient.execute(httpGet, responseHandler);
    }

    public <T> T getHead(String url, Header[] headers, List<NameValuePair> params, ResponseHandler<? extends T> responseHandler) throws IOException {
        // URLEncodedUtils
        URLEncoder urlEncoder = new URLEncoder();
        final String paramString;
        if (params!=null) {
            paramString = "?" + URLEncodedUtils.format(params, "utf-8");
        } else {
            paramString = "";
        }
        HttpHead httpHead = new HttpHead(swiftAccess.getEndpoint()+urlEncoder.encode(url)+paramString);
        if (headers!=null) {
            httpHead.setHeaders(headers);
        }
        httpHead.setHeader("X-Auth-Token", swiftAccess.getToken());
        httpHead.setHeader("Accept", "application/json; charset=utf-8");
        return httpClient.execute(httpHead, responseHandler);
    }

    public CloseableHttpResponse get(String url, Header[] headers, List<NameValuePair> params) throws IOException {
        URLEncoder urlEncoder = new URLEncoder();
        final String paramString;
        if (params!=null) {
            paramString = "?" + URLEncodedUtils.format(params, "utf-8");
        } else {
            paramString = "";
        }
        HttpGet httpGet = new HttpGet(swiftAccess.getEndpoint()+urlEncoder.encode(url)+paramString);
        if (headers!=null) {
            httpGet.setHeaders(headers);
        }
        httpGet.setHeader("X-Auth-Token", swiftAccess.getToken());
        httpGet.setHeader("Accept", "application/json; charset=utf-8");
        return httpClient.execute(httpGet);
    }

    public StatusLine listContainers(Consumer<InputStream> consumer) throws IOException {
        return get("", null, null, httpResponse -> {
            if (httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                try (InputStream inputStream = httpResponse.getEntity().getContent()) {
                    consumer.accept(inputStream);
                }
            }
            return httpResponse.getStatusLine();
        });
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    public void setSwiftAccess(SwiftAccess swiftAccess) {
        this.swiftAccess = swiftAccess;
    }

    public StatusLine listObjects(String container, Integer limit, String marker, ObjectConsumer objectConsumer) throws IOException {
        List<NameValuePair> params = new ArrayList<>();
        if (limit!=null) {
            params.add(new BasicNameValuePair("limit", limit.toString()));
        }
        if (marker!=null) {
            params.add(new BasicNameValuePair("marker", marker));
        }
        return get("/" + container, null, params, httpResponse -> {
            Integer nbObjects = new Integer(httpResponse.getFirstHeader("X-Container-Object-Count").getValue());
            if (httpResponse.getStatusLine().getStatusCode()== HttpStatus.SC_OK) {
                try (InputStream inputStream = httpResponse.getEntity().getContent()) {
                    objectConsumer.accept(inputStream);
                }
            }
            if (nbObjects==0) {
                return new BasicStatusLine(null, 204, "Normal end readings objects");
            } else {
                return httpResponse.getStatusLine();
            }
        });
    }

    public ObjectDetailInfo loadObjectMetaData(String container, String fileName) throws IOException {
        final String url = "/" + container + "/" + fileName;
        return getHead(url, null, null, httpResponse -> {
            if (httpResponse.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
                throw new RuntimeException("Unable to access to header objects of : " + url);
            }
            EntityUtils.consume(httpResponse.getEntity());
            return getObjectDetailInfo(httpResponse);
        });
    }

    private ObjectDetailInfo getObjectDetailInfo(HttpResponse httpResponse) {
        ObjectDetailInfo objectDetailInfo = new ObjectDetailInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
        for(Header header : httpResponse.getAllHeaders()) {
            log.info(header.getName() + " -> " + header.getValue());
            switch (header.getName()) {
                case "Content-Type":
                    objectDetailInfo.setContentType(header.getValue());
                    break;
                case "Content-Length":
                    objectDetailInfo.setContentLength(Long.parseLong(header.getValue()));
                    break;
                case "Last-Modified":
                    try {
                        objectDetailInfo.setLastModified(sdf.parse(header.getValue()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    break;
                case "Etag":
                    objectDetailInfo.setHashMd5(header.getValue());
                    break;
                case "X-Delete-At":
                    objectDetailInfo.setDeleteAt(new Date(new Long(header.getValue())));
                    break;
                case "X-Object-Manifest":
                    objectDetailInfo.setManifestPrefix(header.getValue());
                    break;
                case "X-Static-Large-Object":
                    objectDetailInfo.setManifest(new Boolean(header.getValue()));
                    break;
                default:
                    // skip header
            }
        }
        return objectDetailInfo;
    }

}
