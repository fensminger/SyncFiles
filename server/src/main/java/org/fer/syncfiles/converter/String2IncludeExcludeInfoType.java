package org.fer.syncfiles.converter;

import org.fer.syncfiles.domain.IncludeExcludeInfoType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by fensm on 22/12/2016.
 */
@Component
public class String2IncludeExcludeInfoType implements Converter<IncludeExcludeInfoType, String> {

    @Override
    public String convert(IncludeExcludeInfoType includeExcludeInfoType) {
        if (includeExcludeInfoType==null) return null;
        return includeExcludeInfoType.toString();
    }
}
