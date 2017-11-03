package org.fer.syncfiles.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.fer.syncfiles.converter.IncludeExcludeInfoType2String;
import org.fer.syncfiles.converter.String2IncludeExcludeInfoType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
    private List<Converter<?,?>> converters = new ArrayList<Converter<?,?>>();

    @Override
    protected String getDatabaseName() {
        return "syncfiles";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient("127.0.0.1", 27017);
    }

    @Override
    public CustomConversions customConversions() {
//        converters.add(new String2IncludeExcludeInfoType());
//        converters.add(new IncludeExcludeInfoType2String());
        return new CustomConversions(converters);
    }
}
