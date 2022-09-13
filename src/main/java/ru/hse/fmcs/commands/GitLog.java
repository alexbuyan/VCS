package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;
import ru.hse.fmcs.objects.Commit;
import ru.hse.fmcs.utils.PathUtil;
import ru.hse.fmcs.utils.SerializeUtil;

import java.io.PrintStream;
import java.util.List;

public class GitLog extends GitCommand {
    private final PrintStream stream;
    private final State state;

    public GitLog(State state, PrintStream stream) {
        this.state = state;
        this.stream = stream;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        state.deserializeState();

        if (!arguments.isEmpty()) {
            logFromRevision(arguments.get(0));
        } else {
            logFromRevision(state.getHead().getCommitHash());
        }

        state.serializeState();
    }

    private void logFromRevision(String commitHash) throws GitException {
        Commit commit = SerializeUtil.deserialize(Commit.class, PathUtil.getObjectsDirectoryPath().resolve(commitHash));
        while (commit.getParentHash() != null) {
            stream.println(commit.log());
            commitHash = commit.getParentHash();
            commit = SerializeUtil.deserialize(Commit.class, PathUtil.getObjectsDirectoryPath().resolve(commitHash));
        }
        stream.println(commit.log());
    }
}
