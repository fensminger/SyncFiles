package org.fer.syncfiles.model;

import java.util.*;

/**
 * Created by fer on 04/04/2015.
 */
public class MapTreeResult {
    List<DirFileList> dirFileListList;
    Integer nbOfElements;

    public MapTreeResult(Map<String, List<MergedFileInfoJson>> dirFileMap, Integer nbOfElements) {
        this.nbOfElements = nbOfElements;
        dirFileListList = new ArrayList<>();
        dirFileMap.forEach((dir, files) -> {
            Collections.sort(files, Comparator.comparing(MergedFileInfoJson::getFullName));
            dirFileListList.add(new DirFileList(dir, files));
        });
        Collections.sort(dirFileListList, Comparator.comparing(DirFileList::getDir));
    }

    public List<DirFileList> getDirFileListList() {
        return dirFileListList;
    }

    public Integer getNbOfElements() {
        return nbOfElements;
    }
}
