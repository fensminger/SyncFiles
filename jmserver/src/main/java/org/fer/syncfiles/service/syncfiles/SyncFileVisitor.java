package org.fer.syncfiles.service.syncfiles;

import org.fer.syncfiles.domain.syncfiles.*;
import org.fer.syncfiles.repository.syncfiles.FileInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Optional;

public class SyncFileVisitor extends SimpleFileVisitor<Path> {
    private static final Logger log = LoggerFactory.getLogger(SyncFileVisitor.class);

    private final OriginFile originFile;
    private FileInfoRepository fileInfoRepository;
    private FileUtils fileUtils;

    private ParamSyncFiles paramSyncFiles;
    private final Path prefix;

    public SyncFileVisitor(FileInfoRepository fileInfoRepository, FileUtils fileUtils, ParamSyncFiles paramSyncFiles, Path prefix, OriginFile originFile) {
        super();
        this.fileInfoRepository = fileInfoRepository;
        this.fileUtils = fileUtils;
        this.paramSyncFiles = paramSyncFiles;
        this.prefix = prefix;
        this.originFile = originFile;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (!(attrs.isSymbolicLink() || attrs.isOther())) {
            fileUtils.createDir(paramSyncFiles, prefix, dir, attrs).ifPresent(fileValue -> addDirOrFile(fileValue));
        }
        return FileVisitResult.CONTINUE;
    }

    private void addDirOrFile(FileValue fileValue) {
        final Optional<FileInfo> fileInfoOpt = fileInfoRepository.findByParamSyncFilesIdAndOriginFileAndRelativePathString(
            paramSyncFiles.getId(), originFile, fileValue.getRelativePathString());

        final FileInfo fileInfoNew = new FileInfo(paramSyncFiles.getId(), originFile, fileValue);
        if (fileInfoOpt.isPresent()) {
            final FileInfo fileInfo = fileInfoOpt.get();
            if (fileInfoNew.getLastModifiedTime().equals(fileInfo.getLastModifiedTime())) {
                fileInfo.setFileInfoAction(FileInfoAction.NOTHING);
                fileInfoRepository.save(fileInfo);
            } else {
                fileInfo.updateInfo(fileInfoNew);
                calcHash(fileInfo);
                fileInfo.setFileInfoAction(FileInfoAction.UPDATE);
                fileInfoRepository.save(fileInfo);
            }
        } else {
            fileInfoNew.setFileInfoAction(FileInfoAction.CREATE);
            calcHash(fileInfoNew);
            fileInfoRepository.save(fileInfoNew);
        }
    }

    private String calcHash(FileInfo fileInfo) {
        if (fileInfo.isRegularFile()) {
            String hashMd5 = getMd5(new File(prefix.toString(), fileInfo.getRelativePathString()));
            log.info("Calcul du hash du fichier : " + fileInfo.getRelativePathString() + " -> " + hashMd5);
            fileInfo.setHash(hashMd5);
        } else {
            fileInfo.setHash(fileInfo.getRelativePathString());
        }
        return fileInfo.getHash();
    }

    private String getMd5(File file) {
        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (!(attrs.isSymbolicLink() || attrs.isOther())) {
            fileUtils.createFile(paramSyncFiles, prefix, file, attrs).ifPresent(fileValue -> addDirOrFile(fileValue));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc)
        throws IOException {
        throw new IOException("Can not walk through file system : " + file);
    }


}
