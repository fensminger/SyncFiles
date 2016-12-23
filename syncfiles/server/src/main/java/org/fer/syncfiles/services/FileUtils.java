package org.fer.syncfiles.services;

import org.fer.syncfiles.domain.FileValue;
import org.fer.syncfiles.domain.IncludeExcludeInfo;
import org.fer.syncfiles.domain.ParamSyncFiles;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;

/**
 * Created by fensm on 14/02/2016.
 */
@Service
public class FileUtils {

    public boolean match(ParamSyncFiles param, String strMatchParam) {
        List<IncludeExcludeInfo> includeExcludeList = param.getIncludeExcludePatterns();
        boolean matches = false;
        if (includeExcludeList==null) {
            matches = false;
        } else {
            final String strMatch = strMatchParam.replace('\\', '/');
            for(IncludeExcludeInfo includeExcludeInfo : includeExcludeList) {
                final String toCheck = includeExcludeInfo.getValue();
                switch (includeExcludeInfo.getType()) {
                    case START:
                        matches = strMatch.startsWith(toCheck);
                        break;
                    case END:
                        matches = strMatch.endsWith(toCheck);
                        break;
                    case CONTAIN:
                        matches = strMatch.contains(toCheck);
                        break;
                    case REGEXP:
                    default:
                        matches = strMatch.matches(toCheck);
                }
                if (matches) {
                    break;
                }
            }
        }

        if (matches) {
            return param.isIncludeDir();
        } else {
            return !param.isIncludeDir();
        }
    }

    public Optional<FileValue> createFile(ParamSyncFiles param, Path prefix, Path file, BasicFileAttributes attrs) {
        Path relaFile = prefix.relativize(file);
        if (match(param, relaFile.toString())) {
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
        if (match(param, relaDir.toString())) {
            final FileValue fileValue = new FileValue(relaDir, attrs);
            return Optional.of(fileValue);
        } else {
            return Optional.empty();
        }
    }

}
