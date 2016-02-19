package org.fer.syncfiles.bus.tree;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.log4j.Logger;
import org.fer.syncfiles.model.FileInfo;
import org.infinispan.tree.Fqn;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by fer on 02/11/2014.
 */
public class JsonFileTreeCacheVisitor implements TreeCacheVisitor {
    private static final Logger log = Logger.getLogger(UpdateActionTreeCacheVisitor.class);
    private final PrintWriter writer;
    private JsonGenerator jsonGenerator;

    public static  final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public JsonFileTreeCacheVisitor(PrintWriter writer) {
        this.writer = writer;
        JsonFactory jsonFactory = new JsonFactory();
        try {
            jsonGenerator = jsonFactory.createGenerator(writer);
            final DefaultPrettyPrinter defaultPrettyPrinter = new DefaultPrettyPrinter();
            defaultPrettyPrinter.indentObjectsWith(new DefaultPrettyPrinter.Lf2SpacesIndenter());
            jsonGenerator.setPrettyPrinter(defaultPrettyPrinter);
            jsonGenerator.writeStartArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(Fqn fqn, Object key, Object o) {
        try {
            if (o!=null && o instanceof FileInfo) {
                FileInfo fileInfo = (FileInfo) o;
                writeFileInfo(fileInfo);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void writeFileInfo(FileInfo fileInfo) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("relativePathString", getNullString(fileInfo.getRelativePathString()));
        jsonGenerator.writeStringField("treeKey", getNullString(fileInfo.getTreeKey()));
        jsonGenerator.writeNumberField("size", fileInfo.getSize());
        jsonGenerator.writeStringField("fileInfoAction", getNullString(fileInfo.getFileInfoAction()));
        jsonGenerator.writeStringField("hash", getNullString(fileInfo.getHash()));
        jsonGenerator.writeStringField("creationTime", getNullDate(fileInfo.getCreationTime()));
        jsonGenerator.writeStringField("lastModifiedTime", getNullDate(fileInfo.getLastModifiedTime()));
        jsonGenerator.writeStringField("lastAccessTime", getNullDate(fileInfo.getLastAccessTime()));
        jsonGenerator.writeNumberField("previousSize", fileInfo.getPreviousSize());
        jsonGenerator.writeNumberField("version", fileInfo.getVersion());
        jsonGenerator.writeStringField("action", getNullString(fileInfo.getAction()));
        jsonGenerator.writeStringField("key", getNullString(fileInfo.getKey()));
        jsonGenerator.writeStringField("paramKey", getNullString(fileInfo.getParamKey()));
        jsonGenerator.writeStringField("originFile", getNullString(fileInfo.getOriginFile()));
//                jsonGenerator.writeStringField("sourceKey", getNullString(fileInfo.getSourceKey()));
        jsonGenerator.writeStringField("previousLastAccessTime", getNullDate(fileInfo.getPreviousLastAccessTime()));
        jsonGenerator.writeStringField("previousLastModifiedTime", getNullDate(fileInfo.getPreviousLastModifiedTime()));
        jsonGenerator.writeBooleanField("isDirectory", fileInfo.isDirectory());
        jsonGenerator.writeBooleanField("isOther", fileInfo.isOther());
        jsonGenerator.writeBooleanField("isRegularFile", fileInfo.isRegularFile());
        jsonGenerator.writeBooleanField("isSymbolicLink", fileInfo.isSymbolicLink());
        jsonGenerator.writeEndObject();
    }

    private SimpleDateFormat sdf = new SimpleDateFormat(JSON_DATE_FORMAT);
    private String getNullDate(Date o) {
        if (o==null) {
            return null;
        } else {
            return sdf.format(o);
        }
    }

    private String getNullString(Object o) {
        if (o==null) {
            return null;
        } else {
            return o.toString();
        }
    }

    public void endWrite() {
        try {
            jsonGenerator.writeEndArray();
            jsonGenerator.flush();
            jsonGenerator.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
