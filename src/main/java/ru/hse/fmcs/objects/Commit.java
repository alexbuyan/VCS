package ru.hse.fmcs.objects;

import ru.hse.fmcs.utils.HashUtil;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Commit implements Serializable {
    private final String commitAuthor;
    private final String parentHash;
    private final String contentHash;
    private final String message;
    private final Timestamp time;
    private Map<String, String> committedFiles;

    public Commit(String commitAuthor, String parentHash, String contentHash, String message, Timestamp time) {
        this.commitAuthor = commitAuthor;
        this.parentHash = parentHash;
        this.contentHash = contentHash;
        this.message = message;
        this.time = time;
        committedFiles = new HashMap<>();
    }

    public String getCommitAuthor() {
        return commitAuthor;
    }

    public String getParentHash() {
        return parentHash;
    }

    public String getContentHash() {
        return contentHash;
    }

    public String getCommitHash() {
        return HashUtil.getStringHash(parentHash + contentHash + message);
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTime() {
        return time;
    }

    public Map<String, String> getCommittedFiles() {
        return committedFiles;
    }

    public void setCommittedFiles(Map<String, String> committedFiles) {
        this.committedFiles.putAll(committedFiles);
    }

    public String log() {
        String log;
        if (message != null) {
            log = "commit " + getCommitHash() + "\n" +
                    "Date:\t" + time + "\n" +
                    "Author:\t" + commitAuthor + "\n" +
                    "\n\t" + message + "\n";
        } else {
            log = "commit " + getCommitHash() + "\n" +
                    "Date:\t" + time + "\n" +
                    "Author:\t" + commitAuthor + "\n";
        }
        return log;
    }
}
