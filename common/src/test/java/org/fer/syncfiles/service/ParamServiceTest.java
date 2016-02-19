package org.fer.syncfiles.service;

import org.apache.log4j.Logger;
import org.fer.syncfiles.model.FileInfo;
import org.fer.syncfiles.model.OriginFile;
import org.fer.syncfiles.model.Param;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.query.SearchManager;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.Node;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

/**
 * Created by fer on 15/10/2014.
 */
public class ParamServiceTest {
    private static final Logger log = Logger.getLogger(ParamServiceTest.class);

    //@Test
    public void test_queryCache() throws IOException, ParseException {
        ParamServiceImpl paramService = new ParamServiceImpl();

        paramService.initService();

        Param param1 = new Param();
        param1.setName("param 1");
        param1.setMasterDir("masterDir");
        param1.setSlaveDir("slaveDir");
        paramService.save(param1);
        Param param2 = new Param();
        param2.setName("param 2");
        param2.setMasterDir("masterDir 2");
        param2.setSlaveDir("slaveDir 2");
        paramService.save(param2);

        Param res = paramService.findOne(param1.getKey());
        log.info("Get in cache param : " + res);
        Param res2 = paramService.findOne(param2.getKey());
        log.info("Get in cache param : " + res2);

        for(Param param : paramService.findAll().getParams()) {
            log.info("Find All param in cache : " + param.getName());
        }
    }

    final String relativePathString = "/dev/test/file11234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

    //@Test
    public void test_createCacheWithLink() throws IOException, ParseException {
        ParamServiceImpl paramService = new ParamServiceImpl();

        paramService.initService();

        Param param1 = new Param();
        param1.setName("param 1");
        param1.setMasterDir("masterDir");
        param1.setSlaveDir("slaveDir");
        paramService.save(param1);
        Param param2 = new Param();
        param2.setName("param 2");
        param2.setMasterDir("masterDir 2");
        param2.setSlaveDir("slaveDir 2");
        paramService.save(param2);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginFile(OriginFile.SOURCE);
        fileInfo.setParamKey(param1.getKey());
        fileInfo.setRelativePathString(relativePathString);
        fileInfo.setHash("Hash1");
        paramService.saveFileInfo(fileInfo);

        fileInfo = new FileInfo();
        fileInfo.setOriginFile(OriginFile.TARGET);
        fileInfo.setParamKey(param2.getKey());
        fileInfo.setRelativePathString("/dev/test/file2");
        fileInfo.setHash("Hash2");
        paramService.saveFileInfo(fileInfo);

    }

    //@Test
    public void test_loadCache() throws IOException, ParseException {
        ParamServiceImpl paramService = new ParamServiceImpl();

        paramService.initService();

        for(Param param : paramService.findAll().getParams()) {
            log.info("Find All param in cache : " + param.getKey() + " : " + param.getName());
        }
    }

    //@Test
    public void test_AllValues() throws IOException, ParseException {
        ParamServiceImpl paramService = new ParamServiceImpl();

//        System.setProperty("jgroups.udp.mcast_addr", "228.21.21.21");
//        System.setProperty("jgroups.udp.mcast_port", "8821");

        paramService.initService();

        while (true) {
            log.info("-----------------------------------------------------------------------------------");
            for(Object param : paramService.loadAllObjectFromCache()) {
                log.info("Find All param in cache : " + param);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //@Test
    public void test_Cache2() throws IOException {
        ParamServiceImpl paramService = new ParamServiceImpl();

        System.setProperty("jgroups.udp.mcast_addr", "228.21.21.21");
        System.setProperty("jgroups.udp.mcast_port", "8821");

        paramService.initService2();

        Param param1 = new Param();
        param1.setName("param 1");
        param1.setMasterDir("masterDir");
        param1.setSlaveDir("slaveDir");
        paramService.save(param1);
        Param param2 = new Param();
        param2.setName("param 2");
        param2.setMasterDir("masterDir 2");
        param2.setSlaveDir("slaveDir 2");
        paramService.save(param2);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginFile(OriginFile.SOURCE);
        fileInfo.setParamKey(param1.getKey());
        fileInfo.setRelativePathString(relativePathString);
        fileInfo.setHash("Hash1");
        paramService.saveFileInfo(fileInfo);

        fileInfo = new FileInfo();
        fileInfo.setOriginFile(OriginFile.TARGET);
        fileInfo.setParamKey(param2.getKey());
        fileInfo.setRelativePathString("/dev/test/file2");
        fileInfo.setHash("Hash2");
        paramService.saveFileInfo(fileInfo);

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //@Test
    public void test_CacheTree() throws IOException {
        ParamServiceImpl paramService = new ParamServiceImpl();

        System.setProperty("jgroups.udp.mcast_addr", "228.21.21.21");
        System.setProperty("jgroups.udp.mcast_port", "8821");

        DefaultCacheManager manager = new DefaultCacheManager("infinispan.xml");
        paramService.createLevelDbTreeCache(manager);

        TreeCache<Object, Object> treeCache = new TreeCacheFactory().createTreeCache(paramService.getTreeCacheManager());

        treeCache.put("debut/test", "key1", "Value1");
        treeCache.put("debut/test", "key2", "Value2");
        treeCache.put("debut/test2", "key1", "Value1Bis");

        Map<Object, Object> res = treeCache.getData(Fqn.fromString("debut/test"));
        Map<Object, Object> res2 = treeCache.getData(Fqn.fromString("debut/test2"));

        log.info("res:" + res);
        log.info("res2:" + res2);

        Node<Object, Object> root = treeCache.getRoot();
        log.info("root:" + root);
        Set<Node<Object, Object>> fils = root.getChildren();
        log.info("fils:" + fils);
        for(Node<Object, Object> f : fils) {
            log.info("Data Fils : " + f.getData() + " -> " + f.getData().getClass().toString());
            Set<Node<Object, Object>> petitFils = f.getChildren();
            log.info("fils de " + fils + " -> " + petitFils);
            for(Node<Object, Object> pf : petitFils) {
                log.info("Data : " + pf.getData() + " -> " + f.getData().getClass().toString());
            }
        }
    }
}
