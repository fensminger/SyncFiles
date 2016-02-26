package org.fer.syncfiles.service.syncfiles.hubic.Consumer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ContainerInfo;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ObjectInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Created by fensm on 23/02/2016.
 */
public class ObjectConsumer implements Consumer<InputStream> {
    private final Logger log = LoggerFactory.getLogger(ContainerConsumer.class);

    private final Consumer<ObjectInfo> service;

    private ObjectInfo marker;

    public ObjectConsumer(Consumer<ObjectInfo> service) {
        this.service = service;
        this.marker = null;
    }

    //        "bytes": 3231182,
    //        "content_type": "application/octet-stream",
    //        "hash": "35c38a8ead88fbd5a0e756642716af21",
    //        "last_modified": "2014-01-02T18:24:58.532960",
    //        "name": "ff3afa83-f159-4cef-8cc9-6c13b82eecbb/1388686123/108088782/00000001"
    @Override
    public void accept(InputStream inputStream) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            try {
                JsonFactory jsonFactory = new JsonFactory();
                try (JsonParser jsonParser = jsonFactory.createJsonParser(inputStream)) {
                    Long sizeValue = null;
                    String contentTypeValue = null;
                    String nameValue = null;
                    String hashValue = null;
                    String lastModifiedValue = null;
                    int countNbField = 0;
                    JsonToken jsonToken = jsonParser.nextToken();
                    while (jsonToken != null) {
                        switch (jsonToken) {
                            case START_ARRAY:
                            case END_ARRAY:
                                break;
                            case START_OBJECT:
                                countNbField = 0;
                                break;
                            case END_OBJECT:
                                if (countNbField == 5) {
                                    ObjectInfo objectInfo = new ObjectInfo(sizeValue, contentTypeValue, hashValue, lastModifiedValue, nameValue);
                                    marker = objectInfo;
                                    service.accept(objectInfo);
                                    countNbField = -1;
                                } else {
                                    throw new RuntimeException("Le format du fichier n'est pas au format attendu.");
                                }
                                break;
                            case FIELD_NAME:
                                String name = jsonParser.getCurrentName();
                                jsonParser.nextToken();
                                countNbField++;
                                switch (name) {
                                    case "name":
                                        nameValue = jsonParser.getText();
                                        break;
                                    case "content_type":
                                        contentTypeValue= jsonParser.getText();
                                        break;
                                    case "bytes":
                                        sizeValue = jsonParser.getLongValue();
                                        break;
                                    case "hash":
                                        hashValue = jsonParser.getText();
                                        break;
                                    case "last_modified":
                                        lastModifiedValue = jsonParser.getText();
                                        break;
                                    default:
                                        throw new RuntimeException("Unexpected token : " + name);
                                }
                                break;
                            default:
                                throw new RuntimeException("Unexpected jsonToken error : " + jsonToken);
                        }
                        jsonToken = jsonParser.nextToken();
                    }
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ObjectInfo getMarker() {
        return marker;
    }
}
