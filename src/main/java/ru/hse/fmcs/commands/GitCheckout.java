package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.objects.Branch;
import ru.hse.fmcs.objects.Commit;
import ru.hse.fmcs.objects.State;
import ru.hse.fmcs.utils.PathUtil;
import ru.hse.fmcs.utils.SerializeUtil;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GitCheckout extends GitCommand {
    private final State state;

    public GitCheckout(State state) {
        this.state = state;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        if (arguments.isEmpty()) {
            throw new GitException("Please provide revision or files for checkout");
        }
        state.deserializeState();
        String option = arguments.get(0);
        if (option.equals("--")) {
            checkoutFiles(arguments.subList(1, arguments.size()));
        } else if (option.startsWith("HEAD~")) {
            checkoutHEAD(option.substring("HEAD~".length() + 1));
        } else if (Files.exists(PathUtil.getBranchesDirectory().resolve(option))) {
            checkoutMaster(option);
        } else {
            state.checkoutToCommit(option);
        }
        state.serializeState();
    }

    private void checkoutMaster(String branchName) throws GitException {
        Branch branch = SerializeUtil.deserialize(Branch.class, PathUtil.getBranchesDirectory().resolve(branchName));
        state.setBranch(branch);
        state.checkoutToCommit(branch.getLastCommit());
    }

    private void checkoutHEAD(String numberOfCommits) throws GitException {
        if (numberOfCommits.isEmpty()) {
            throw new GitException("Specify the number of commits from HEAD");
        }
        int n = Integer.parseInt(numberOfCommits);
        Commit commit = state.getHead();
        for (int i = 0; i < n; i++) {
            if (commit.getParentHash() == null) {
                throw new GitException("Couldn't find commit HEAD~" + n);
            }
            commit = SerializeUtil.deserialize(Commit.class, PathUtil.getObjectsDirectoryPath().resolve(commit.getParentHash()));
        }
        state.checkoutToCommit(commit.getCommitHash());
    }

    private void checkoutFiles(List<String> fileNames) throws GitException {
        Map<String, String> addedFiles = state.getIndex().getAddedFiles();
        Map<String, String> modifiedFiles = state.getIndex().getModifiedFiles();
        Set<String> untrackedFiles = state.getIndex().getUntrackedFiles();
        Set<String> deletedFiles = state.getIndex().getDeletedFiles();
        Commit commit = SerializeUtil.deserialize(Commit.class, PathUtil.getHeadPath());
        Map<String, String> committedFiles = commit.getCommittedFiles();
        for (String fileName : fileNames) {
            try {
                if (!untrackedFiles.contains(fileName)) {
                    addedFiles.remove(fileName);
                    deletedFiles.remove(fileName);
                    modifiedFiles.remove(fileName);
                    String fileHash = committedFiles.get(fileName);
                    Path filePath = PathUtil.getObjectsDirectoryPath().resolve(fileHash);
                    Path copiedPath = PathUtil.getFilePath(fileName);
                    Files.copy(filePath, copiedPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new GitException("Couldn't restore file '" + fileName + "'", e);
            }
        }
    }
}
