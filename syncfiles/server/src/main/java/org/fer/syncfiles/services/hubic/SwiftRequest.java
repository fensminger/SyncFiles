package org.fer.syncfiles.services.hubic;

import org.apache.catalina.util.URLEncoder;
import org.apache.http.*;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.fer.syncfiles.domain.ObjectDetailInfo;
import org.fer.syncfiles.services.hubic.Consumer.ObjectConsumer;
import org.fer.syncfiles.services.hubic.domain.StringStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        HttpGet httpGet = getHttpGet(url, headers, params);
        return httpClient.execute(httpGet, responseHandler);
    }

    private HttpGet getHttpGet(String url, Header[] headers, List<NameValuePair> params) {
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
        return httpGet;
    }

    public StringStatusLine getStringResponse(String url, Header[] headers, List<NameValuePair> params) {
        HttpGet httpGet = getHttpGet(url, headers, params);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK) {
                throw new RuntimeException("Error on getWithStringResult" + response.getStatusLine().getStatusCode() + " : " + response.getStatusLine().getReasonPhrase());
            }
            return new StringStatusLine(EntityUtils.toString(response.getEntity(), "utf-8"), response.getStatusLine());
        } catch (ClientProtocolException e) {
            throw new RuntimeException("Error on getWithStringResult" + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error on getWithStringResult" + e.getMessage(), e);
        }
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

    public CloseableHttpResponse delete(String url, Header[] headers, List<NameValuePair> params) throws IOException {
        // URLEncodedUtils
        final String paramString;
        if (params!=null) {
            paramString = "?" + URLEncodedUtils.format(params, "utf-8");
        } else {
            paramString = "";
        }
        HttpDelete httpDelete = new HttpDelete(swiftAccess.getEndpoint()+url+paramString);
        if (headers!=null) {
            httpDelete.setHeaders(headers);
        }
        httpDelete.setHeader("X-Auth-Token", swiftAccess.getToken());
//        httpDelete.setHeader("Accept", "application/json; charset=utf-8");
        return httpClient.execute(httpDelete);
    }

    public CloseableHttpResponse get(String url, Header[] headers, List<NameValuePair> params) throws IOException {
        HttpGet httpGet = getHttpGet(url, headers, params);
        return httpClient.execute(httpGet);
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }

    public void setSwiftAccess(SwiftAccess swiftAccess) {
        this.swiftAccess = swiftAccess;
    }

    public StatusLine listObjects(String container, Integer limit, String marker, String prefix, ObjectConsumer objectConsumer) throws IOException {
        List<NameValuePair> params = new ArrayList<>();
        if (limit!=null) {
            params.add(new BasicNameValuePair("limit", limit.toString()));
        }
        if (marker!=null) {
            params.add(new BasicNameValuePair("marker", marker));
        }
        if (prefix!=null) {
            params.add(new BasicNameValuePair("prefix", prefix));
        }
        StringStatusLine response = getStringResponse("/" + container, null, params);
        String body = response.getContent();
        if (body==null || "".equals(body)) {
            return new BasicStatusLine(response.getStatusLine().getProtocolVersion(), 204, "Normal end readings objects");
        } else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes("utf-8"));
            try (InputStream inputStream = byteArrayInputStream) {
                objectConsumer.accept(inputStream);
            }
            return response.getStatusLine();
        }
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
            log.trace(header.getName() + " -> " + header.getValue());
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
                    log.info("X-Object-Manifest" + header.getName() + " -> " + header.getValue());
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

    public CloseableHttpResponse loadObject(String container, String fileName, boolean loadManifest) throws IOException {
        final String url = "/" + container + "/" + fileName;
        List<NameValuePair> params = new ArrayList<>();
        if (loadManifest) {
            params.add(new BasicNameValuePair("multipart-manifest", "get"));
        }
        return get(url, null, params);
    }

    public void uploadObject(String container, String fileName, String md5, File fileToUpload) throws IOException {
        URLEncoder urlEncoder = new URLEncoder();
        String s = "?temp_url_expires=68000&temp_url_sig=fdsfdsfssfds";
        HttpPut httpPut = new HttpPut(swiftAccess.getEndpoint()+"/"+container+"/"+urlEncoder.encode(fileName));
        httpPut.setHeader("X-Auth-Token", swiftAccess.getToken());
//        httpPut.setHeader("Accept", "application/json; charset=utf-8");
        if (md5!=null) {
//            httpPut.setHeader("ETag", md5);
        }
        FileEntity entity = new FileEntity(fileToUpload);
        httpPut.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPut);
        log.info("Response of upload : " + response.getStatusLine() + " of file : " + fileToUpload);
    }

    public void deleteObject(String container, String fileName) throws IOException {
        URLEncoder urlEncoder = new URLEncoder();
        String url = "/"+container + "/" + urlEncoder.encode(fileName);
        try (CloseableHttpResponse response = delete(url, null, null)) {
            EntityUtils.consume(response.getEntity());
            Header firstHeader = response.getFirstHeader("Content-Length");
            if (!"0".equals(firstHeader.getValue())) {
                throw new IOException("Unable to delete the file " + fileName + ", length="+firstHeader);
            }
            if (response.getStatusLine().getStatusCode()!=204) {
                throw new IOException("Unable to delete the file " + fileName + ", statusCode="+response.getStatusLine().getStatusCode()
                + " -> " + response.getStatusLine().getReasonPhrase());
            }
        }

    }

    public SwiftAccess getSwiftAccess() {
        return swiftAccess;
    }
}
