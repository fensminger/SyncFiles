package org.fer.syncfiles.dto;

import org.fer.syncfiles.domain.FileInfo;

import java.util.List;

/**
 * Created by fensm on 17/11/2016.
 */
public class FileInfoPage {
    private int page;
    private final int pageSize;
    private int totalPages;
    private long totalElements;
    private List<FileInfo> fileInfoList;

    public FileInfoPage(int page, int pageSize, int totalPages, long totalElements, List<FileInfo> fileInfoList) {
        super();
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.fileInfoList = fileInfoList;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

    public void setFileInfoList(List<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    public int getPageSize() {
        return pageSize;
    }
}
