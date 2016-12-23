package org.fer.syncfiles.converter;

import org.fer.syncfiles.domain.IncludeExcludeInfoType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by fensm on 22/12/2016.
 */
@Component
public class IncludeExcludeInfoType2String implements Converter<String, IncludeExcludeInfoType> {

    @Override
    public IncludeExcludeInfoType convert(String s) {
        return IncludeExcludeInfoType.valueOf(s);
    }
}
