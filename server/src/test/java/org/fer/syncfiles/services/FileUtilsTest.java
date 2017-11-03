package org.fer.syncfiles.services;

import org.fer.syncfiles.domain.IncludeExcludeInfo;
import org.fer.syncfiles.domain.IncludeExcludeInfoType;
import org.fer.syncfiles.domain.ParamSyncFiles;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fensm on 15/12/2016.
 */
public class FileUtilsTest {
    @Test
    public void patternTest() {
        FileUtils fileUtils = new FileUtils();

        ParamSyncFiles param = new ParamSyncFiles();
        param.setIncludeDir(true);
        List<IncludeExcludeInfo> excludePatterns = new ArrayList<>();
        excludePatterns.add(new IncludeExcludeInfo(IncludeExcludeInfoType.START, ".DS_Store"));
        excludePatterns.add(new IncludeExcludeInfo(IncludeExcludeInfoType.END, "End"));
        excludePatterns.add(new IncludeExcludeInfo(IncludeExcludeInfoType.CONTAIN, "orren"));
        excludePatterns.add(new IncludeExcludeInfo(IncludeExcludeInfoType.REGEXP, "^http://yahoo.com$"));
        param.setIncludeExcludePatterns(excludePatterns);

        Assert.assertEquals(true, fileUtils.match(param, ".DS_Store"));
        Assert.assertEquals(false, fileUtils.match(param, "x.DS_Store"));
        Assert.assertEquals(true, fileUtils.match(param, "x.DS_StoreEnd"));
        Assert.assertEquals(false, fileUtils.match(param, "x.DS_Storend"));
        Assert.assertEquals(true, fileUtils.match(param, "x.DS_Storren!!!"));
        Assert.assertEquals(true, fileUtils.match(param, "http://yahoo.com"));
        Assert.assertEquals(false, fileUtils.match(param, "https://yahoo.com"));
        Assert.assertEquals(false, fileUtils.match(param, "hhttp://yahoo.com"));

    }
}
