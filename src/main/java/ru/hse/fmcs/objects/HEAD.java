package ru.hse.fmcs.objects;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

public class HEAD extends Commit implements Serializable {
    private String commitHash;
    private String branch;

    public HEAD(String author, String branch, String parentHash, String contentHash, String message, Timestamp time) {
        super(author, parentHash, contentHash, message, time);
        this.branch = branch;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    // to move head to another commit
    // maybe will be used in checkout
    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }
}
