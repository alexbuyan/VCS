package ru.hse.fmcs.objects;

import java.io.Serializable;

public class Branch implements Serializable {
    private final String branchName;
    private String lastCommitHash;

    public Branch(String branchName, String commitHash) {
        this.branchName = branchName;
        this.lastCommitHash = commitHash;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getLastCommit() {
        return lastCommitHash;
    }

    public void setLastCommit(String lastCommitHash) {
        this.lastCommitHash = lastCommitHash;
    }
}
