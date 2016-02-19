package org.fer.syncfiles.service;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import org.apache.log4j.Logger;
import org.fer.syncfiles.bus.FilesList;
import org.fer.syncfiles.bus.SyncFileVisitor;
import org.fer.syncfiles.bus.tree.*;
import org.fer.syncfiles.dto.SynchroInfoDto;
import org.fer.syncfiles.model.*;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.Index;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.persistence.leveldb.configuration.LevelDBStoreConfiguration;
import org.infinispan.persistence.leveldb.configuration.LevelDBStoreConfigurationBuilder;
import org.infinispan.tree.Fqn;
import org.infinispan.tree.Node;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;
import org.javaswift.joss.util.FileAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service(value = "paramService")
@Scope("singleton")
public class ParamServiceImpl implements ParamService {
	private static final Logger log = Logger.getLogger(ParamServiceImpl.class);

    private Cache<Object, Object> cacheManager;
    private Cache<Object, Object> treeCacheManager;
    private TreeCache<Object, Object> treeCache;

    @Autowired
    private Environment environment;

    @Autowired()
    @Qualifier("HubicSynchro")
    private HubicSynchro hubicSynchro;

    @Autowired
    FeedbackEvent feedbackEvent;

    private boolean isDebug = false;

    public ParamServiceImpl() {
		super();
	}

    @PostConstruct
    public void initService() throws IOException, ParseException {
//        LogManager.getRootLogger().setLevel(Level.INFO);
//        Logger.getRootLogger().setLevel(Level.INFO);
//        log.setLevel(Level.INFO);

        log.info("==================================== Init Cache =====================================================");
        String cacheEnable = (environment==null)?"true":environment.getProperty("cache.enable");
        isDebug = Boolean.valueOf(System.getProperty("debugLoad"));
        if ("true".equals(cacheEnable)) {
//            cacheManager = new DefaultCacheManager("infinispan_leveldb.xml").getCache("syncfiles");
            DefaultCacheManager manager = new DefaultCacheManager("infinispan.xml");

//            createMongoDbCache(manager);
            createLevelDbCache(manager);
            createLevelDbTreeCache(manager);
            treeCache = new TreeCacheFactory().createTreeCache(treeCacheManager);


//            log.info("Start log HUBIC Files");
//            printHubicCache("./hubicStart.txt",null);
//            log.info("End log HUBIC Files");
//
//            loadAllTreeFromJson();


//            ConfigurationBuilder builder = new ConfigurationBuilder();
//            builder.persistence().addStore(JdbcStringBasedStoreConfigurationBuilder.class)
//                    .fetchPersistentState(true)
//                    .ignoreModifications(false)
//                    .preload(true)
//                    .shared(false)
//                    .async().disable()
//                    .purgeOnStartup(false)
//                    .table()
//                        .dropOnExit(false)
//                        .createOnStart(true)
//                        .tableNamePrefix("CACHE_")
//                        .idColumnName("ID").idColumnType("VARCHAR(255)")
//                        .dataColumnName("DATA").dataColumnType("BLOB")
//                        .timestampColumnName("TIMESTAMP").timestampColumnType("BIGINT")
//                    .connectionPool()
//                        .connectionUrl("jdbc:mysql://localhost:3306/syncfiles")
//                        .username("sync")
//                        .password("sync")
//                        .driverClass("com.mysql.jdbc.Driver")
//                    .indexing().enabled(true).indexLocalOnly(false)
//                        .addProperty("hibernate.search.default.directory_provider", "filesystem");
//            Configuration config = builder.build();
//            manager.defineConfiguration("jdbcsyncfiles", config);
//            cacheManager = manager.getCache("jdbcsyncfiles");
            log.info("==================================== End Init Cache =====================================================");
        }

    }

    private void loadAllTreeFromJson() throws IOException, ParseException {
        ParamList allParam = findAll();
        RemoveActionTreeCacheVisitor removeActionTreeCacheVisitor = new RemoveActionTreeCacheVisitor(this);
        for(Param param : allParam.getParams()) {
            walkTree(param.getKey(), removeActionTreeCacheVisitor);
            loadTreeFromJson(param.getKey(), null);
        }

//        PrintWriter debugPrinter = new PrintWriter(new File("alltree.txt"));
//        LogFileTreeCacheVisitor logFileTreeCacheVisitor = new LogFileTreeCacheVisitor(debugPrinter);
//        walkRootTree(logFileTreeCacheVisitor);
//        debugPrinter.flush();
//        debugPrinter.close();
//        walkTree(HUBIC_KEY_TREE, removeActionTreeCacheVisitor);
//        try {
//            loadTreeFromJson(ParamService.HUBIC_KEY_TREE);
//        } catch (FileNotFoundException e) {
//            log.warn("Les fichiers d'hubic ne sont pas sauvés : " + e.getMessage());
//        }
    }

    @Override
    public void printHubicCache(String pathname, String paramKey) throws FileNotFoundException {
//        PrintWriter printWriter = new PrintWriter(new File(pathname));
//        LogFileTreeCacheVisitor logFileTreeCacheVisitor = new LogFileTreeCacheVisitor(printWriter);
//        walkTree(HUBIC_KEY_TREE, logFileTreeCacheVisitor);
//        printWriter.flush();
//        printWriter.close();

//        if (paramKey!=null) {
//            writeTreeToJson(paramKey);
//            try {
//                loadTreeFromJson(paramKey);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void writeTreeToJson(String paramKey) throws FileNotFoundException {
        try (PrintWriter printWriter1 = new PrintWriter(new File(getTreeParamFileName(paramKey)))) {
            JsonFileTreeCacheVisitor serializeCache = new JsonFileTreeCacheVisitor(printWriter1);
            walkTree(paramKey, serializeCache);
            serializeCache.endWrite();
            printWriter1.flush();
        }
    }

    private String getTreeParamFileName(String paramKey) {
        if (isDebug && !HUBIC_KEY_TREE.equals(paramKey)) {
            return "base_param_"+paramKey+".json";
        } else {
            return "param_"+paramKey+".json";
        }
    }

    public void loadTreeFromJson(String paramKey, TreeCacheVisitor treeCacheVisitor) throws IOException, ParseException {
        try (Reader reader = new BufferedReader(new FileReader(getTreeParamFileName(paramKey)))) {
            JsonFactory jsonFactory = new JsonFactory();
            JsonParser parser = jsonFactory.createParser(reader);

            SimpleDateFormat sdf = new SimpleDateFormat(JsonFileTreeCacheVisitor.JSON_DATE_FORMAT);
            String fieldName = null;
            Object value = null;
            FileInfo fileInfo = null;
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
//                log.info(token);

                boolean isValue = false;
                if (token!=null) {
                    switch (token.id()) {
                        case JsonTokenId.ID_FIELD_NAME:
                            fieldName = parser.getText();
                            break;
                        case JsonTokenId.ID_EMBEDDED_OBJECT:
                            log.error("Unknown embedded Object : " + token + " parse : " + parser.getText());
                            break;
                        case JsonTokenId.ID_START_ARRAY:
                            log.info("start array : " + token + " parse : " + parser.getText());
                            break;
                        case JsonTokenId.ID_END_ARRAY:
                            log.info("end array : " + token + " parse : " + parser.getText());
                            break;
                        case JsonTokenId.ID_START_OBJECT:
                            fileInfo = new FileInfo();
                            break;
                        case JsonTokenId.ID_END_OBJECT:
                            if (treeCacheVisitor==null) {
                                putTreeCache(fileInfo);
                            } else {
                                treeCacheVisitor.visit(Fqn.fromString(fileInfo.getRelativePathString()), fileInfo.getKey(), fileInfo);
                            }
                            fileInfo = null;
                            break;
                        case JsonTokenId.ID_TRUE:
                            isValue = true;
                            value = true;
                            break;
                        case JsonTokenId.ID_FALSE:
                            isValue = true;
                            value = false;
                            break;
                        case JsonTokenId.ID_NULL:
                            isValue = true;
                            value = null;
                            break;
                        case JsonTokenId.ID_STRING:
                            isValue = true;
                            value = parser.getText();
                            break;
                        case JsonTokenId.ID_NUMBER_FLOAT:
                            isValue = true;
                            value = parser.getNumberValue();
                            break;
                        case JsonTokenId.ID_NUMBER_INT:
                            isValue = true;
                            value = parser.getValueAsLong();
                            break;
                        default:
                            log.error("Unknown token : " + token + " parse : " + parser.getText());
                    }
                    if (isValue) {
                        isValue = false;
                        updateFileInfoFromJson(parser, sdf, fieldName, value, fileInfo);
                        fieldName = null;
                        value = null;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            log.error("La liste des fichiers n'existe pas : " + getTreeParamFileName(paramKey) + " -> " + e.getMessage());
        }
    }

    private void updateFileInfoFromJson(JsonParser parser, SimpleDateFormat sdf, String fieldName, Object value, FileInfo fileInfo) throws ParseException, IOException {
        switch (fieldName) {
            case "relativePathString" :
                fileInfo.setRelativePathString((String) value);
                break;
            case "treeKey":
                fileInfo.setTreeKey((String) value);
                break;
            case "size" :
                fileInfo.setSize((Long) value);
                break;
            case "fileInfoAction" :
                if (value==null) {
                    fileInfo.setFileInfoAction(null);
                } else {
                    fileInfo.setFileInfoAction(FileInfoAction.valueOf((String) value));
                }
                break;
            case "hash" :
                fileInfo.setHash((String) value);
                break;
            case "creationTime" :
                fileInfo.setCreationTime(getDateOrNull(sdf, (String) value));
                break;
            case "lastModifiedTime" :
                fileInfo.setLastModifiedTime(getDateOrNull(sdf, (String) value));
                break;
            case "lastAccessTime" :
                fileInfo.setLastAccessTime(getDateOrNull(sdf, (String) value));
                break;
            case "previousSize" :
                fileInfo.setPreviousSize((Long) value);
                break;
            case "version" :
                fileInfo.setVersion((Long) value);
                break;
            case "action" :
                if (value==null) {
                    fileInfo.setAction(null);
                } else {
                    fileInfo.setAction(ResultSyncAction.valueOf((String) value));
                }
                break;
            case "key" :
                String keyStr = (String) value;
                if (keyStr!=null && !keyStr.startsWith("FO:")) {
                    fileInfo.setKey(keyStr);
                }
//                fileInfo.setKey((String) value);
            break;
            case "paramKey":
                fileInfo.setParamKey((String) value);
                break;
            case "originFile":
                if (value==null) {
                    fileInfo.setOriginFile(null);
                } else {
                    fileInfo.setOriginFile(OriginFile.valueOf((String) value));
                }
                break;
            case "previousLastAccessTime":
                fileInfo.setPreviousLastAccessTime(getDateOrNull(sdf, (String) value));
                break;
            case "previousLastModifiedTime":
                fileInfo.setPreviousLastModifiedTime(getDateOrNull(sdf, (String) value));
                break;
            case "isDirectory":
                fileInfo.setDirectory((Boolean) value);
                break;
            case "isOther":
                fileInfo.setOther((Boolean) value);
                break;
            case "isRegularFile":
                fileInfo.setRegularFile((Boolean) value);
                break;
            case "isSymbolicLink":
                fileInfo.setSymbolicLink((Boolean) value);
                break;
            case "sourceKey":
                break;
            default:
                log.error("Unknown value : " + fieldName + " parse : " + parser.getText());

                break;
        }
    }

    private Date getDateOrNull(SimpleDateFormat sdf, String value) throws ParseException {
        if (value==null) {
            return null;
        }
        return sdf.parse(value);
    }

    public void initService2() throws IOException {
        String cacheEnable = (environment==null)?"true":environment.getProperty("cache.enable");
        if ("true".equals(cacheEnable)) {
//            cacheManager = new DefaultCacheManager("infinispan_leveldb.xml").getCache("syncfiles");
            DefaultCacheManager manager = new DefaultCacheManager("infinispan.xml");

            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.persistence().addStore(LevelDBStoreConfigurationBuilder.class)
                    .location("./cache2/data")
                    .expiredLocation("./cache2/expired")
                    .implementationType(LevelDBStoreConfiguration.ImplementationType.JAVA)
                    .shared(true)
                    .preload(true)
                    .async().disable()
                    .purgeOnStartup(false)
                    .indexing().enabled(true).indexLocalOnly(false)
                    .addProperty("hibernate.search.default.directory_provider", "filesystem")
                    .addProperty("hibernate.search.default.indexBase", "./cache2")
                    .clustering().cacheMode(CacheMode.REPL_SYNC);
            Configuration config = builder.build();
            manager.defineConfiguration("jdbcsyncfiles", config);
            cacheManager = manager.getCache("jdbcsyncfiles");

        }

    }
//    private void createMongoDbCache(DefaultCacheManager manager) {
//        ConfigurationBuilder builder = new ConfigurationBuilder();
//        builder.persistence().addStore(MongoDBStoreConfigurationBuilder.class)
//                .hostname("localhost")
//                .port(27017)
//                .database("syncfiles")
//                .collection("collsyncfiles")
//                .acknowledgment(1)
//                .timeout(1000)
//                .shared(true)
//                .preload(true)
//                .async().disable()
//                .purgeOnStartup(false)
//                .indexing().enabled(true).indexLocalOnly(false)
//                .addProperty("hibernate.search.default.directory_provider", "infinispan")
////                .addProperty("hibernate.search.default.indexBase", "./cache")
////                .clustering().cacheMode(CacheMode.REPL_SYNC)
//        ;
//
//        Configuration config = builder.build();
//
//        manager.defineConfiguration("mongosyncfiles", config);
//        cacheManager = manager.getCache("mongosyncfiles");
//    }
//
    private void createLevelDbCache(DefaultCacheManager manager) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.persistence().addStore(LevelDBStoreConfigurationBuilder.class)
                .location("./cache/data")
                .expiredLocation("./cache/expired")
                .implementationType(LevelDBStoreConfiguration.ImplementationType.JAVA)
                .shared(true)
                .preload(true)
                .purgeOnStartup(false)
                .ignoreModifications(false)
                .async().disable()
                .indexing().index(Index.NONE)
                .eviction().maxEntries(100);
//                .indexLocalOnly(false)
//                .addProperty("hibernate.search.default.directory_provider", "filesystem")
//                .addProperty("hibernate.search.default.indexBase", "./cache")
//                .clustering().cacheMode(CacheMode.REPL_SYNC);
        Configuration config = builder.build();
        manager.defineConfiguration("jdbcsyncfiles", config);
        cacheManager = manager.getCache("jdbcsyncfiles");
    }

    public void createLevelDbTreeCache(DefaultCacheManager manager) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.persistence().addStore(LevelDBStoreConfigurationBuilder.class)
                .location("./cache/data")
                .expiredLocation("./cache/expired")
                .implementationType(LevelDBStoreConfiguration.ImplementationType.JAVA)
                .shared(true)
                .preload(true)
                .purgeOnStartup(true)
                .ignoreModifications(false)
                .async().enable()
                .indexing().index(Index.NONE)
                .eviction().maxEntries(100_000)
//                .clustering().cacheMode(CacheMode.REPL_SYNC)
                .invocationBatching().enable(true);
        Configuration config = builder.build();
        manager.defineConfiguration("treesyncfiles", config);

//            ConfigurationBuilder builder = new ConfigurationBuilder();
//            builder.persistence().addStore(JdbcStringBasedStoreConfigurationBuilder.class)
//                    .fetchPersistentState(true)
//                    .ignoreModifications(false)
//                    .preload(true)
//                    .shared(false)
//                    .async().disable()
//                    .purgeOnStartup(false)
//                    .table()
//                        .dropOnExit(false)
//                        .createOnStart(true)
//                        .tableNamePrefix("CACHE_")
//                        .idColumnName("ID").idColumnType("VARCHAR(255)")
//                        .dataColumnName("DATA").dataColumnType("BLOB")
//                        .timestampColumnName("TIMESTAMP").timestampColumnType("BIGINT")
//                    .connectionPool()
//                        .connectionUrl("jdbc:mysql://localhost:3306/syncfiles")
//                        .username("sync")
//                        .password("sync")
//                        .driverClass("com.mysql.jdbc.Driver")
//                    .indexing().enabled(false).
//                    invocationBatching().enable(true);
//            Configuration config = builder.build();
//            manager.defineConfiguration("treesyncfiles", config);

        treeCacheManager = manager.getCache("treesyncfiles");
    }

    /* (non-Javadoc)
     * @see org.fer.syncfiles.bus.ParamDaoInterface#save(org.fer.syncfiles.model.Param)
     */
	@Override
	public Param save(Param param) {
        cacheManager.put(param.getKey(), param);
        return param;
	}

    @Override
    public void delete(String key) {
        cacheManager.remove(key);
    }
	/* (non-Javadoc)
	 * @see org.fer.syncfiles.bus.ParamDaoInterface#findAll()
	 */
	@Override
	public ParamList findAll() {
		ParamList res = new ParamList();
//        try {
//            SearchManager searchManager = org.infinispan.query.Search.getSearchManager(cacheManager);
//            QueryFactory queryFactory = searchManager.getQueryFactory();
//            Query query = queryFactory.from(Param.class)
//                    .orderBy("name", SortOrder.DESC)
//                    .build();
//            List<Param> paramList = query.list();
//            res.setParams(paramList);
//        } catch (IllegalStateException e) {
//            if (!e.getMessage().equals("Unknown entity name org.fer.syncfiles.model.Param")) {
//                throw e;
//            }
//        }

        Set<Param> paramList = new HashSet<>();

        if (cacheManager!=null && cacheManager.keySet()!=null) {
            for(Object key : cacheManager.keySet()) {
                Object value = cacheManager.get(key);
                if (value instanceof  Param) {
                    paramList.add((Param) value);
                } else {
                    cacheManager.remove(key);
                }
            }
        }

        res.setParams(new ArrayList<>(paramList));

//        LogTreeCacheVisitor logTreeCacheVisitor = new LogTreeCacheVisitor();
//        walkTree(HUBIC_KEY_TREE, logTreeCacheVisitor);
//
        return res;
	}

    @Override
    public Param load(String key) {
        if (key==null) {
            return new Param();
        }

        return (Param) cacheManager.get(key);
    }

    @Override
    public void prepFilesToSynchronize(final Param param) throws IOException, ParseException {
        // Bouton HubicPrep

//        log.info("prepFilesToSynchronize : Début");
//        RemoveActionTreeCacheVisitor removeActionTreeCacheVisitor = new RemoveActionTreeCacheVisitor(treeCache);
//        walkTree(param.getKey(), removeActionTreeCacheVisitor);

        final String message = "prepFilesToSynchronize : Avant Local file";
        log.info(message);
        feedbackEvent.sendEvent(new SynchroInfoDto(message));
        updateLocalFile(param);

        final String message1 = "prepFilesToSynchronize : Début Avant Hubic";
        feedbackEvent.sendEvent(new SynchroInfoDto(message1));
        log.info(message1);
        updateHubicFile(param);

        final String message2 = "prepFilesToSynchronize : Suppression des delete";
        feedbackEvent.sendEvent(new SynchroInfoDto(message2));
        log.info(message2);
        RemoveDeleteActionTreeCacheVisitor removeDeleteActionTreeCacheVisitor = new RemoveDeleteActionTreeCacheVisitor(this);
        walkTree(param.getKey(), removeDeleteActionTreeCacheVisitor);

        final String message3 = "prepFilesToSynchronize : Fin";
        feedbackEvent.sendEvent(new SynchroInfoDto(message3));
        log.info(message3);
    }

    @Override
    public void prepSynchroSourceTargetHubic(Param param) {
        // Bouton HubicSync
        log.info("Prepare synchroHubic : " + param);
        PrepSynchroHubicTreeCacheVisitor prepSynchroHubicTreeCacheVisitor = new PrepSynchroHubicTreeCacheVisitor(param, this, feedbackEvent);
        walkTree(param.getKey(), prepSynchroHubicTreeCacheVisitor);
        prepSynchroHubicTreeCacheVisitor.logStat();

    }

    @Override
    public void synchroHubic(Param param) throws FileNotFoundException {
        // Bouton synchronize
        log.info("initCacheTreeForAllHubicFiles : " + param);
        SynchroHubicTreeCacheVisitor synchroHubicTreeCacheVisitor = new SynchroHubicTreeCacheVisitor(param,this, feedbackEvent);
        walkTree(param.getKey(), synchroHubicTreeCacheVisitor);
        synchroHubicTreeCacheVisitor.logStat();
        writeTreeToJson(param.getKey());
    }

    @Override
    public Param findOne(String key) {
        return (Param) cacheManager.get(key);
    }

    @Deprecated
    public FileInfo saveFileInfo(FileInfo fileInfo) {
        cacheManager.put(fileInfo.getKey(), fileInfo);
        return fileInfo;
    }

    public FileInfo getFileInfo(String key) {
        return (FileInfo) cacheManager.get(key);
    }

    @Override
    public List<MergedFileInfoJson> loadMergedFileFromParam(String paramKey) {
        log.info("loadLocalAndHubicInfoFromCache");
        MergedFileTreeCacheVisitor mergedFileTreeCacheVisitor = new MergedFileTreeCacheVisitor();
        walkTree(paramKey, mergedFileTreeCacheVisitor);

        return mergedFileTreeCacheVisitor.getMergedFileInfoList();
    }

    @Override
    public List<MergedFileInfoJson> loadMergedFileFromParam(String paramKey, String pathNode, String nameFilter, boolean fileToSync) {
        log.info("loadLocalAndHubicInfoFromCache");
        MergedFileSearchTreeCacheVisitor mergedFileSearchTreeCacheVisitor = new MergedFileSearchTreeCacheVisitor(nameFilter, fileToSync);
        String calcNodePath = paramKey;
        if (pathNode!=null) {
            calcNodePath += pathNode;
        }
        walkTree(calcNodePath, mergedFileSearchTreeCacheVisitor);

        return mergedFileSearchTreeCacheVisitor.getMergedFileInfoList();
    }

    @Override
    public List<MergedFileInfoJson> loadFlatMergedFileFromParam(String paramKey, String path) {
        log.info("loadFlatMergedFileFromParam");
        final Fqn prefixeFqn = Fqn.fromString("/"+paramKey + path);
        Node<Object, Object> pathNode = treeCache.getNode(prefixeFqn);

        MergedFileTreeCacheVisitor mergedFileTreeCacheVisitor = new MergedFileTreeCacheVisitor();
        if (pathNode!=null && pathNode.getChildren()!= null) {
            pathNode.getChildren().forEach(mergedFileTreeCacheVisitor::visit);
        }

        return mergedFileTreeCacheVisitor.getMergedFileInfoList();
    }

    @Override
    public MapTreeResult loadMergedFileViewFromParam(String paramKey, String path) {
        log.info("loadMergedFileViewFromParam");
        List<MergedFileInfoJson> mergedFiles = loadMergedFileFromParam(paramKey+path);

        Map<String, List<MergedFileInfoJson>> dirListMap = mergedFiles.stream().limit(10000)
                .collect(Collectors.groupingBy(mergedFileInfoJson -> mergedFileInfoJson.getPath())
                );

        MapTreeResult mapTreeResult = new MapTreeResult(dirListMap, mergedFiles.size());

        return mapTreeResult;
    }

    @Override
    public void initCacheTreeForAllHubicFiles() throws IOException {
        // Bouton ChargeHubic
        hubicSynchro.loadAllHubicFilesIntoCache(HUBIC_KEY_TREE, this);
    }

    public void walkTree(String treeKey, TreeCacheVisitor treeVisitor) {
        final Fqn prefixeFqn = Fqn.fromString("/"+treeKey);
        Node<Object, Object> rootPathNode = treeCache.getNode(prefixeFqn);
        if (rootPathNode!=null) {
            walkInternalTree(rootPathNode, treeVisitor);
        }
    }

    public void walkRootTree(TreeCacheVisitor treeVisitor) {
        log.info("Start print cacheTree");
        Node<Object, Object> rootPathNode = treeCache.getRoot();
        if (rootPathNode!=null) {
            walkInternalTree(rootPathNode, treeVisitor);
        }
        log.info("End print cacheTree");
        log.info("============================================================================================");
//        for (Object obj : treeCacheManager.values()) {
//            if (obj instanceof FileInfo) {
//                FileInfo fileInfo = (FileInfo) obj;
//                log.info("treeCacheManager : " + fileInfo.getTreeKey());
//            } else {
//                log.info("treeCacheManager : " + obj);
//            }
//        }

    }

    private void walkInternalTree(Node<Object, Object> pathNode, TreeCacheVisitor treeVisitor) {
        for(Node<Object, Object> child : pathNode.getChildren()) {
            treeVisitor.visit(child);
            walkInternalTree(child, treeVisitor);
        }
    }

    @Override
    public void putTreeCache(String prefix, FileInfo fileInfo) {
        final Fqn fqn = Fqn.fromRelativeFqn(Fqn.fromString("/" + prefix), Fqn.fromString(fileInfo.getRelativePathString()));
        fileInfo.setTreeKey(fqn.toString());
        treeCache.put(fqn, fileInfo.getKey(), fileInfo);
    }

    @Override
    public void putTreeCache(Fqn fqn, FileInfo fileInfo) {
        treeCache.put(fqn, fileInfo.getKey(), fileInfo);
    }

    @Override
    public void putTreeCache(FileInfo fileInfo) {
        treeCache.put(Fqn.fromString(fileInfo.getTreeKey()), fileInfo.getKey(), fileInfo);
    }

    @Override
    public void removeTreeCache(String prefix, FileInfo fileInfo) {
        treeCache.remove(Fqn.fromRelativeFqn(Fqn.fromString("/" + prefix), Fqn.fromString(fileInfo.getRelativePathString())), fileInfo.getKey());
    }

    @Override
    public void removeTreeCache(Fqn fqn, Object key) {
        treeCache.remove(fqn, key);
    }


    @Override
    public FileInfo getTreeCache(String prefix, String relativePathString, OriginFile originFile) {
        final Fqn fqn = Fqn.fromRelativeFqn(Fqn.fromString("/"+prefix), Fqn.fromString(relativePathString));
        return getTreeCacheByOrigin(fqn, originFile);
    }

    @Override
    public FileInfo getTreeCache(Fqn fqn, Object key) {
        return (FileInfo) treeCache.get(fqn, key);
    }

    @Override
    public FileInfo getTreeCacheByOrigin(Fqn fqn, OriginFile originFile) {
        Map<Object, Object> dataMap = treeCache.getData(fqn);
        if (dataMap !=null) {
            for(Object obj : dataMap.values()) {
                FileInfo fileInfo = (FileInfo) obj;
                if (originFile.equals(fileInfo.getOriginFile())) {
                    return fileInfo;
                }
            }
        }
        return null;
    }

    public List<Object> loadAllObjectFromCache() {
        final Collection<Object> values = cacheManager.values();
        return new ArrayList<>(values);
    }


    private void updateHubicFile(final Param param) throws IOException, ParseException {
        log.info("updateHubicFile");

        UpdateActionTreeCacheVisitor updateActionTreeCacheVisitor = new UpdateActionTreeCacheVisitor(this, FileInfoAction.DELETE, OriginFile.TARGET);
        walkTree(param.getKey(), updateActionTreeCacheVisitor);

        UpdateParamHubicTreeCacheVisitor updateParamHubicTreeCacheVisitor = new UpdateParamHubicTreeCacheVisitor(param, this);
        loadTreeFromJson(HUBIC_KEY_TREE, updateParamHubicTreeCacheVisitor);
    }

    private void updateLocalFile(final Param param) throws IOException {
        log.info("updateLocalFile");
        if (isDebug) {
            return;
        }
        LocalFileJsonWriter localFileJsonWriter = new LocalFileJsonWriter(param.getKey());
        Path masterPath = FileSystems.getDefault().getPath(param.getMasterDir());

        FilesList localFileList = new FilesList(masterPath);
        Files.walkFileTree(masterPath, new SyncFileVisitor(param, localFileList));

        UpdateActionTreeCacheVisitor updateActionTreeCacheVisitor = new UpdateActionTreeCacheVisitor(this, FileInfoAction.DELETE, OriginFile.SOURCE);
        walkTree(param.getKey(), updateActionTreeCacheVisitor);

        for(FileNode fileNode : localFileList.getDirSet()) {
            final FileValue fileValue = fileNode.getValue();
            FileInfo fileInfo = getTreeCache(param.getKey(), fileValue.getRelativePathString(), OriginFile.SOURCE);
            Date lastModifiedTime = new Date(fileValue.getLastModifiedTime());
            if (fileInfo == null || lastModifiedTime.after(fileInfo.getLastModifiedTime())) {
                if (fileInfo==null) {
                    fileInfo = new FileInfo(OriginFile.SOURCE , fileValue);
                    fileInfo.setFileInfoAction(FileInfoAction.CREATE);
                    fileInfo.setDirectory(true);
                    fileInfo.setRegularFile(false);
                    fileInfo.setParamKey(param.getKey());
                } else {
                    initPreviousData(fileInfo);
                    fileInfo.setLastAccessTime(new Date(fileValue.getLastAccessTime()));
                    fileInfo.setLastModifiedTime(new Date(fileValue.getLastModifiedTime()));
                    fileInfo.setSize(fileValue.getSize());
                    fileInfo.setFileInfoAction(FileInfoAction.UPDATE);
                }
            } else {
                fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
            }
            putTreeCache(param.getKey(), fileInfo);
            localFileJsonWriter.writeObject(fileInfo);
        }

        for(FileNode fileNode : localFileList.getFileSet()) {
            final FileValue fileValue = fileNode.getValue();
            FileInfo fileInfo =  getTreeCache(param.getKey(), fileValue.getRelativePathString(), OriginFile.SOURCE);
            Date lastModifiedTime = new Date(fileValue.getLastModifiedTime());
            if (fileInfo == null || lastModifiedTime.after(fileInfo.getLastModifiedTime())) {
                log.info("Calcul du hash md5 pour le fichier : " + fileValue.getRelativePathString());
                String hashMd5 = FileAction.getMd5(new File(param.getMasterDir(), fileValue.getRelativePathString()));
                log.info("Calcul du hash md5 pour le fichier : " + fileValue.getRelativePathString() + "=" + hashMd5);
                if (fileInfo==null) {
                    fileInfo = new FileInfo(OriginFile.SOURCE , fileValue);
                    fileInfo.setFileInfoAction(FileInfoAction.CREATE);
                    fileInfo.setDirectory(false);
                    fileInfo.setRegularFile(true);
                    fileInfo.setParamKey(param.getKey());
                } else {
                    initPreviousData(fileInfo);
                    fileInfo.setLastAccessTime(new Date(fileValue.getLastAccessTime()));
                    fileInfo.setLastModifiedTime(new Date(fileValue.getLastModifiedTime()));
                    fileInfo.setSize(fileValue.getSize());
                    fileInfo.setFileInfoAction(FileInfoAction.UPDATE);
                }
                fileInfo.setHash(hashMd5);
            } else {
                fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
            }
            putTreeCache(param.getKey(), fileInfo);
            localFileJsonWriter.writeObject(fileInfo);
        }
        localFileJsonWriter.close();
    }

    @Override
    public void initPreviousData(FileInfo fileInfo) {
        fileInfo.setPreviousHash(fileInfo.getHash());
        fileInfo.setPreviousLastAccessTime(fileInfo.getLastAccessTime());
        fileInfo.setPreviousLastModifiedTime(fileInfo.getLastModifiedTime());
        fileInfo.setPreviousSize(fileInfo.getSize());
    }

    @Override
    public void deleteHubicObject(String targetFileName) {
        hubicSynchro.deleteHubicObject(targetFileName);
    }

    @Override
    public void updateFileToHubic(Param param, FileInfo fileInfo, String msg) {
        hubicSynchro.updateFileToHubic(param, fileInfo, msg);
    }

    @Override
    public void quitApp() {
        log.info("Start stopping server.");
        try {
            treeCacheManager.endBatch(true);
        } catch (Throwable e) {
            log.error(e);
        }
        treeCacheManager.stop();
        log.info("Cache is stopped.");
        System.exit(0);
    }


    public Cache<Object, Object> getCacheManager() {
        return cacheManager;
    }

    public Cache<Object, Object> getTreeCacheManager() {
        return treeCacheManager;
    }
}
