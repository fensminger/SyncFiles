package org.fer.syncfiles.service.syncfiles;

import org.fer.syncfiles.domain.syncfiles.ParamSyncFiles;
import org.fer.syncfiles.domain.syncfiles.FileValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;

/**
 * Created by fensm on 14/02/2016.
 */
@Service
public class FileUtils {

    public boolean match(ParamSyncFiles param, Path relaExp) {
        String regExpIncludeExcludePattern = (param==null)?null:param.getRegExpIncludeExcludePattern();
        if (regExpIncludeExcludePattern==null) {
            return true;
        }

        try {
            String strMatch = ""+relaExp.toString();
            if (strMatch.matches(regExpIncludeExcludePattern)) {
                return param.isIncludeDir();
            }
            return !param.isIncludeDir();
        } catch (Throwable e) {
            throw e;
        }
    }

    public Optional<FileValue> createFile(ParamSyncFiles param, Path prefix, Path file, BasicFileAttributes attrs) {
        Path relaFile = prefix.relativize(file);
        if (match(param, relaFile)) {
            final FileValue fileValue = new FileValue(relaFile, attrs);
            return Optional.of(fileValue);
        } else {
            return Optional.empty();
        }
    }

    public Optional<FileValue> createDir(ParamSyncFiles param, Path prefix, Path dir, BasicFileAttributes attrs) {
        Path relaDir = prefix.relativize(dir);
        if (relaDir.toString().equals("")) {
            return Optional.empty();
        }
        if (match(param, relaDir)) {
            final FileValue fileValue = new FileValue(relaDir, attrs);
            return Optional.of(fileValue);
        } else {
            return Optional.empty();
        }
    }

}
