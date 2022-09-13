package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;

import java.io.PrintStream;
import java.util.List;
import java.util.Set;

public class GitStatus extends GitCommand {
    private final PrintStream stream;
    private final State state;

    public GitStatus(State state, PrintStream stream) {
        this.state = state;
        this.stream = stream;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        state.deserializeState();
        String lastCommitHash = state.getBranch().getLastCommit();
        String headHash = state.getHead().getCommitHash();
        if (!lastCommitHash.equals(headHash)) {
            stream.println("Error while performing status: Head is detached");
            return;
        }
        stream.println("Current branch is '" + state.getBranch().getBranchName() + "'");

        // & used to make all prints
        boolean EverythingUpToDate = printChangesToBeCommitted() & printChangesNotStaged() & printUntrackedFiles();
        if (EverythingUpToDate) {
            stream.println("Everything is up to date");
        }

        state.serializeState();
    }

    private boolean printChangesToBeCommitted() {
        Set<String> addedFiles = state.getIndex().getAddedFiles().keySet();
        Set<String> newFiles = state.getIndex().getNewFiles();
        if (addedFiles.isEmpty()) {
            return true;
        }
        stream.println("Changes to be committed:");
        for (String fileName : addedFiles) {
            if (newFiles.contains(fileName)) {
                stream.println("\tnew file:\t" + fileName);
            } else {
                stream.println("\tmodified:\t" + fileName);
            }
        }
        return false;
    }

    private boolean printChangesNotStaged() {
        Set<String> modifiedFiles = state.getIndex().getModifiedFiles().keySet();
        Set<String> deletedFiles = state.getIndex().getDeletedFiles();
        if (modifiedFiles.isEmpty() && deletedFiles.isEmpty()) {
            return true;
        }
        stream.println("Changes not staged for commit:");
        for (String fileName : modifiedFiles) {
            stream.println("\tmodified:\t" + fileName);
        }
        for (String fileName : deletedFiles) {
            stream.println("\tdeleted:\t" + fileName);
        }
        return false;
    }

    private boolean printUntrackedFiles() {
        Set<String> untrackedFiles = state.getIndex().getUntrackedFiles();
        if (untrackedFiles.isEmpty()) {
            return true;
        }
        stream.println("Untracked files:");
        for (String fileName : untrackedFiles) {
            stream.println("\t" + fileName);
        }
        return false;
    }
}
