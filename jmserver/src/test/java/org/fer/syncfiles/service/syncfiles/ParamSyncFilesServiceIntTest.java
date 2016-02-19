package org.fer.syncfiles.service.syncfiles;

import org.fer.syncfiles.Application;
import org.fer.syncfiles.domain.User;
import org.fer.syncfiles.domain.syncfiles.*;
import org.fer.syncfiles.repository.UserRepository;
import org.fer.syncfiles.service.UserService;
import org.fer.syncfiles.service.util.RandomUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ParamSyncFilesServiceIntTest {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private ParamSyncFilesService paramSyncFilesService;

    @Test
    public void paramTest() {
        final ParamSyncFiles paramSyncFiles = new ParamSyncFiles();
        paramSyncFiles.setName("Test");
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        paramSyncFiles.setCreationDate(zonedDateTime);
        paramSyncFiles.setMasterDir("/master");
        paramSyncFiles.setSlaveDir("/slave");
        paramSyncFiles.setSyncState(SyncState.FINISHED);

        final ParamSyncFiles paramSyncFilesSaved = paramSyncFilesService.save(paramSyncFiles);

        assertThat(paramSyncFilesSaved.getName()).isEqualTo(paramSyncFiles.getName());
        assertThat(paramSyncFilesSaved.getMasterDir()).isEqualTo(paramSyncFiles.getMasterDir());
        assertThat(paramSyncFilesSaved.getSlaveDir()).isEqualTo(paramSyncFiles.getSlaveDir());
        assertThat(paramSyncFilesSaved.getSyncState()).isEqualTo(paramSyncFiles.getSyncState());
        assertThat(paramSyncFilesSaved.getCreationDate()).isEqualTo(paramSyncFiles.getCreationDate());

        final ParamSyncFiles paramSyncFilesLoaded = paramSyncFilesService.findByName(paramSyncFiles.getName());

        assertThat(paramSyncFilesLoaded.getName()).isEqualTo(paramSyncFiles.getName());
        assertThat(paramSyncFilesLoaded.getMasterDir()).isEqualTo(paramSyncFiles.getMasterDir());
        assertThat(paramSyncFilesLoaded.getSlaveDir()).isEqualTo(paramSyncFiles.getSlaveDir());
        assertThat(paramSyncFilesLoaded.getSyncState()).isEqualTo(paramSyncFiles.getSyncState());
        assertThat(paramSyncFilesLoaded.getCreationDate()).isEqualTo(paramSyncFiles.getCreationDate());

        paramSyncFilesService.delete(paramSyncFilesSaved.getId());
        final ParamSyncFiles paramSyncFilesNotFound = paramSyncFilesService.findByName(paramSyncFiles.getName());

        assertThat(paramSyncFilesNotFound).isNull();
    }

    @Test
    public void loadFileTreeTest() throws IOException {
        // C:\gitrepo\SyncFiles\jmserver\src\test\resources\tree\source1

        String dir = new File(getClass().getResource("/tree/source1").getFile()).getPath().toString();
        String dir2 = new File(getClass().getResource("/tree/source2").getFile()).getPath().toString();

        File arrowSource = new File(dir, "rep1\\arrow.svg");
        File arrowDest = new File(dir2, "rep1\\arrow.svg");
        long lastModified = new Date().getTime();
        arrowSource.setLastModified(lastModified);
        arrowDest.setLastModified(lastModified);

        File arrow2Source = new File(dir, "rep1\\arrow2.svg");
        File arrow2Dest = new File(dir2, "rep1\\arrow2.svg");
        arrow2Source.setLastModified(lastModified);
        arrow2Dest.setLastModified(lastModified);

        final ParamSyncFiles paramSyncFiles = new ParamSyncFiles();
        paramSyncFiles.setName("Test");
        paramSyncFiles.setId("Test1");
        paramSyncFiles.setMasterDir(dir);

        log.info("\n");
        log.info(dir);

        paramSyncFilesService.updateFilesTree(paramSyncFiles);

        HashMap<String, FileInfo> source1Map = new HashMap<>();
        for(FileInfo fileInfo : paramSyncFilesService.findFileInfoByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.SOURCE)) {
            log.info(fileInfo.toString());
            assertThat(fileInfo.getFileInfoAction()).isEqualTo(FileInfoAction.CREATE);
            source1Map.put(fileInfo.getRelativePathString(), fileInfo);
        }

        log.info("-----------------------------------------------------------------------------------------------------------------");
        paramSyncFiles.setMasterDir(dir2);
        paramSyncFilesService.updateFilesTree(paramSyncFiles);

        HashMap<String, FileInfo> source2Map = new HashMap<>();
        for(FileInfo fileInfo : paramSyncFilesService.findFileInfoByParamSyncFilesIdAndOriginFile(paramSyncFiles.getId(), OriginFile.SOURCE)) {
            log.info(fileInfo.toString());
            source2Map.put(fileInfo.getRelativePathString(), fileInfo);
        }

        final FileInfo arrow = source2Map.get("rep1\\arrow.svg");
        assertThat(arrow.getFileInfoAction()).isEqualTo(FileInfoAction.NOTHING);
        assertThat(arrow.getPreviousHash()).isNull();

        final FileInfo arrow2 = source2Map.get("rep1\\arrow2.svg");
        assertThat(arrow2.getFileInfoAction()).isEqualTo(FileInfoAction.NOTHING);
        assertThat(arrow2.getPreviousHash()).isNull();

        final FileInfo fileInfoCreated = source2Map.get("settings.gradle.new2");
        final FileInfo fileInfoDeleted = source2Map.get("settings.gradle.new");
        assertThat(fileInfoCreated.getFileInfoAction()).isEqualTo(FileInfoAction.CREATE);
        assertThat(fileInfoDeleted.getFileInfoAction()).isEqualTo(FileInfoAction.DELETE);
        assertThat(fileInfoCreated.getHash()).isEqualTo(fileInfoDeleted.getHash());

        final FileInfo fileInfoUpdated = source2Map.get("rep2\\rep21\\download.svg");
        assertThat(fileInfoUpdated.getFileInfoAction()).isEqualTo(FileInfoAction.UPDATE);
        assertThat(fileInfoUpdated.getHash()).isNotEqualTo(fileInfoUpdated.getPreviousHash());

        assertThat(source1Map.size()+1).isEqualTo(source2Map.size());
    }

}
