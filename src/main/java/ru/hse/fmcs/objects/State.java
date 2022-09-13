package ru.hse.fmcs.objects;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.utils.FileUtil;
import ru.hse.fmcs.utils.PathUtil;
import ru.hse.fmcs.utils.SerializeUtil;

import java.nio.file.Files;
import java.sql.Timestamp;

public class State {
    private final PathUtil gitPaths;
    private Index index;
    private HEAD head;
    private Branch branch;

    public State(String workingDirectory) {
        gitPaths = new PathUtil(workingDirectory);
    }

    public Index getIndex() {
        return index;
    }

    public PathUtil getGitPaths() {
        return gitPaths;
    }

    public HEAD getHead() {
        return head;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public void setHead(HEAD head) {
        this.head = head;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public boolean isInit() {
        return Files.exists(PathUtil.getGitDirectoryPath());
    }

    public void serializeIndex() throws GitException {
        SerializeUtil.serialize(index, PathUtil.getObjectsDirectoryPath().resolve(index.getIndexHash()));
    }

    public void serializeState() throws GitException {
        SerializeUtil.serialize(branch, PathUtil.getBranchPath());
        SerializeUtil.serialize(head, PathUtil.getHeadPath());
        SerializeUtil.serialize(index, PathUtil.getIndexPath());

        SerializeUtil.serialize(head, PathUtil.getObjectsDirectoryPath().resolve(head.getCommitHash()));
        SerializeUtil.serialize(branch, PathUtil.getBranchesDirectory().resolve(branch.getBranchName()));
    }

    public void deserializeState() throws GitException {
        index = SerializeUtil.deserialize(Index.class, PathUtil.getIndexPath());
        index.updateIndex();
        head = SerializeUtil.deserialize(HEAD.class, PathUtil.getHeadPath());
        branch = SerializeUtil.deserialize(Branch.class, PathUtil.getBranchPath());
    }

    public void commit(String message) throws GitException {
        String author = System.getProperty("user.name");
        head = new HEAD(author, branch.getBranchName(), head.getCommitHash(), index.getIndexHash(), message, new Timestamp(System.currentTimeMillis()));
        head.setCommittedFiles(index.getAddedFiles());
        moveHEAD();
        index.removeCommittedFilesFromIndex();
    }

    public void resetToCommit(String commitHash) throws GitException {
        checkoutToCommit(commitHash);
        moveHEAD();
    }

    public void moveHEAD() {
        branch.setLastCommit(head.getCommitHash());
    }

    public void checkoutToCommit(String commitHash) throws GitException {
        FileUtil.clearWorkingDirectoryWithoutUntrackedFiles(this);
        Commit commit = SerializeUtil.deserialize(Commit.class, PathUtil.getObjectsDirectoryPath().resolve(commitHash));
        index = SerializeUtil.deserialize(Index.class, PathUtil.getObjectsDirectoryPath().resolve(commit.getContentHash()));
        head = new HEAD(commit.getCommitAuthor(), branch.getBranchName(), commit.getParentHash(), commit.getContentHash(), commit.getMessage(), commit.getTime());
        head.setCommitHash(commit.getCommitHash());
        FileUtil.createTrackedFilesInWorkingDirectory(this);
    }

    public String getRelativeRevision(int n) throws GitException {
        deserializeState();
        Commit commit = head;
        for (int i = 0; i < n; i++) {
            if (commit.getParentHash() == null) {
                throw new GitException("Couldn't find commit HEAD~" + n);
            }
            commit = SerializeUtil.deserialize(Commit.class, PathUtil.getObjectsDirectoryPath().resolve(commit.getParentHash()));
        }
        return commit.getCommitHash();
    }
}
