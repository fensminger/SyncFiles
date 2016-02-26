package org.fer.syncfiles.service.syncfiles.hubic.Consumer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.fer.syncfiles.service.syncfiles.hubic.domain.ContainerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Created by fensm on 23/02/2016.
 */
public class ContainerConsumer implements Consumer<InputStream> {
    private final Logger log = LoggerFactory.getLogger(ContainerConsumer.class);

    private final Consumer<ContainerInfo> service;

    public ContainerConsumer(Consumer<ContainerInfo> service) {
        this.service = service;
    }

    @Override
    public void accept(InputStream inputStream) {
        try {
            try {
                JsonFactory jsonFactory = new JsonFactory();
                try (JsonParser jsonParser = jsonFactory.createJsonParser(inputStream)) {
                    String nameValue = null;
                    Long countValue = null;
                    Long sizeValue = null;
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
                                if (countNbField == 3) {
                                    ContainerInfo containerInfo = new ContainerInfo(nameValue, countValue, sizeValue);
                                    service.accept(containerInfo);
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
                                    case "count":
                                        countValue = jsonParser.getLongValue();
                                        break;
                                    case "bytes":
                                        sizeValue = jsonParser.getLongValue();
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
}
