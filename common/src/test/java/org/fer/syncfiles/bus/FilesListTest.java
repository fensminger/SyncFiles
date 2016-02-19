package org.fer.syncfiles.bus;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.Param;
import org.junit.Test;


public class FilesListTest {
    private static final Logger log = Logger.getLogger(FilesListTest.class);

    @Test
    public void testRegExp() {
        Param param = new Param();
        param.setIncludeDir(false);
        final ArrayList<String> includeExcludePatterns = new ArrayList<>();
        includeExcludePatterns.add("^\\.DS_Store");
        includeExcludePatterns.add(".*/\\.DS_Store");
        includeExcludePatterns.add(".*/\\._*\\..*");
        includeExcludePatterns.add(".*/\\._*.*");
        includeExcludePatterns.add("^\\._*\\..*");
        includeExcludePatterns.add(".*/\\.@.*thumb.*");
        includeExcludePatterns.add("^\\.@.*thumb.*");
        param.setIncludeExcludePatterns(includeExcludePatterns);

        Path prefixe = Paths.get("/home/test");
        FilesList filesList = new FilesList(prefixe);

        String file;
        file = "août/.@_thumb/test.jpg";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
        file = "août/.@__thumb/test.jpg";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
        file = ".@__thumb/test.jpg";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
        file = ".@__thumb";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));

        file = ".DS_Store";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
        file = "/test/toto/.DS_Store";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
        file = "/test/toto/._.DS_Store";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
        file = "._.DS_Store";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));

        file = "/test/toto/.__.fichier";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));

        file = "/test/toto/un_bon-Fichier.jpg";
        log.info(file + " : " + filesList.match(param, Paths.get(file)));
    }
}
