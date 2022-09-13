package ru.hse.fmcs.objects;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.utils.FileUtil;
import ru.hse.fmcs.utils.HashUtil;
import ru.hse.fmcs.utils.PathUtil;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Index implements Serializable {
    // with hash
    private final Map<String, String> trackedFiles = new HashMap<>();
    private final Map<String, String> addedFiles = new HashMap<>();
    private final Map<String, String> modifiedFiles = new HashMap<>();

    // without hash
    private Set<String> untrackedFiles = new HashSet<>();
    private final Set<String> newFiles = new HashSet<>();
    private final Set<String> deletedFiles = new HashSet<>();

    public Map<String, String> getTrackedFiles() {
        return trackedFiles;
    }

    public Map<String, String> getAddedFiles() {
        return addedFiles;
    }

    public Map<String, String> getModifiedFiles() {
        return modifiedFiles;
    }

    public Set<String> getUntrackedFiles() {
        return untrackedFiles;
    }

    public Set<String> getNewFiles() {
        return newFiles;
    }

    public Set<String> getDeletedFiles() {
        return deletedFiles;
    }

    public boolean add(String fileName) throws GitException {
        if (!FileUtil.fileExists(fileName)) {
            return false;
        }
        Path filePath = PathUtil.getFilePath(fileName);
        String fileHash = HashUtil.getFileHash(filePath);
        Path copiedPath = PathUtil.getObjectsDirectoryPath().resolve(fileHash);
        try {
            Files.copy(filePath, copiedPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new GitException("Couldn't add file", e);
        }
        if (!trackedFiles.containsKey(fileName)) {
            trackedFiles.put(fileName, fileHash);
            addedFiles.put(fileName, fileHash);
            newFiles.add(fileName);
        } else if (!fileHash.equals(trackedFiles.get(fileName))) {
            addedFiles.put(fileName, fileHash);
        }
        modifiedFiles.remove(fileName);
        untrackedFiles.remove(fileName);
        return true;
    }

    public boolean restore(String fileName) {
        if (!FileUtil.fileExists(fileName)) {
            return false;
        }
        if (!trackedFiles.containsKey(fileName)) {
            return false;
        }
        trackedFiles.remove(fileName);
        addedFiles.remove(fileName);
        modifiedFiles.remove(fileName);
        newFiles.remove(fileName);
        untrackedFiles.add(fileName);
        return true;
    }

    public String getIndexHash() {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, String> trackedFile : trackedFiles.entrySet()) {
            content.append(trackedFile).append(trackedFile);
        }
        return HashUtil.getStringHash(content.toString());
    }

    public void updateIndex() throws GitException {
        untrackedFiles = new HashSet<>();
        List<String> fileNames = FileUtil.listFilesInWorkingDirectory();
        for (String fileName : fileNames) {
            if (!trackedFiles.containsKey(fileName)) {
                untrackedFiles.add(fileName);
            } else {
                Path filePath = PathUtil.getFilePath(fileName);
                String fileHash = HashUtil.getFileHash(filePath);
                if (fileHash.equals(trackedFiles.get(fileName))) {
                    continue;
                }
                if (!addedFiles.containsKey(fileName) || !fileHash.equals(addedFiles.get(fileName))) {
                    modifiedFiles.put(fileName, fileHash);
                    addedFiles.remove(fileName);
                    newFiles.remove(fileName);
                }
            }
        }
        Set<String> trackedFileNames = trackedFiles.keySet();
        for (String trackedFileName : trackedFileNames) {
            if (FileUtil.fileExists(trackedFileName)) {
                continue;
            }
            addedFiles.remove(trackedFileName);
            modifiedFiles.remove(trackedFileName);
            newFiles.remove(trackedFileName);
            deletedFiles.add(trackedFileName);
        }
    }

    public void removeCommittedFilesFromIndex() {
        addedFiles.clear();
    }
}
