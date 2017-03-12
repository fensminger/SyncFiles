package org.fer.syncfiles.services;

import org.fer.syncfiles.domain.FileInfo;
import org.fer.syncfiles.domain.FileInfoAction;
import org.fer.syncfiles.domain.OriginFile;
import org.fer.syncfiles.domain.SyncState;
import org.fer.syncfiles.dto.FileInfoPage;
import org.fer.syncfiles.dto.SummaryFileInfo;
import org.fer.syncfiles.repository.FileInfoRepository;
import org.fer.syncfiles.repository.ParamSyncFilesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fensm on 04/02/2016.
 */
@Service
public class InfosFilesService {
    private static final Logger log = LoggerFactory.getLogger(InfosFilesService.class);

    @Autowired
    private ParamSyncFilesRepository paramSyncFilesRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public FileInfoPage loadFileInfo(String paramSyncFilesId, OriginFile originFile, int pageNumber, int pageSize
            , String filterName, Date startDate, Date endDate, List<FileInfoAction> fileInfoActionList, List<SyncState> syncStateList) {
        Sort sort = new Sort(Sort.Direction.ASC, "relativePathString");
        Query query = query(where("paramSyncFilesId").is(paramSyncFilesId).and("originFile").is(originFile));

        if (filterName!=null) {
            query.addCriteria(where("relativePathString").regex(filterName));
        }

        if (startDate!=null && endDate!=null) {
            query.addCriteria(where("lastModifiedTime").gte(startDate).lte(endDate));
        } else {

            if (startDate != null) {
                query.addCriteria(where("lastModifiedTime").gte(startDate));
            }

            if (endDate != null) {
                query.addCriteria(where("lastModifiedTime").lte(endDate));
            }
        }

        if (fileInfoActionList!=null) {
            query.addCriteria(where("fileInfoAction").in(fileInfoActionList));
        }

        if (syncStateList!=null) {
            query.addCriteria(where("syncState").in(syncStateList));
        }

        long totalElements = mongoTemplate.count(query, FileInfo.class);
        long numberOfPages = Math.round((double) totalElements / (double) pageSize + 0.5d);

        if (pageNumber>numberOfPages) {
            pageNumber = 1;
        }
        Pageable pageable = new PageRequest(pageNumber, pageSize, sort);
        query.with(sort).with(pageable);
        List<FileInfo> contentList = mongoTemplate.find(query, FileInfo.class);
        return new FileInfoPage(pageNumber, pageSize, (int) numberOfPages, totalElements, contentList);

//        Page<FileInfo> fileInfoPage = fileInfoRepository.findByParamSyncFilesIdAndOriginFile(paramSyncFilesId, originFile, pageable);
//        return new FileInfoPage(pageNumber, pageSize, fileInfoPage.getTotalPages(), fileInfoPage.getTotalElements(), fileInfoPage.getContent());
    }

    public List<FileInfo> loadTreeInfo(String paramSyncFilesId, OriginFile originFile, String parentPath) {
        Sort sort = new Sort(Sort.Direction.DESC, "isDirectory");
        Sort sort2 = new Sort(Sort.Direction.ASC, "relativePathString");
        Query query = query(where("paramSyncFilesId").is(paramSyncFilesId)
                .and("originFile").is(originFile)
                .and("parentPath").is(parentPath));

        query.with(sort).with(sort2);
        List<FileInfo> contentList = mongoTemplate.find(query, FileInfo.class);
        return contentList;

    }

    public List<SummaryFileInfo> getDirectories(Path path) throws IOException {
        if (path==null) {
            return Arrays.stream(File.listRoots())
                    .map(f -> getSummaryFileInfo(f))
                    .collect(Collectors.toList());
        } else {
            return Files.list(path)
                    .map(p -> p.toFile())
                    .filter(f -> f.isDirectory())
                    .map(f -> getSummaryFileInfo(f))
                    .collect(Collectors.toList());
        }
    }

    private SummaryFileInfo getSummaryFileInfo(File f) {
        SummaryFileInfo fileInfo = new SummaryFileInfo();
        fileInfo.setName(f.getName());
        return fileInfo;
    }

}
