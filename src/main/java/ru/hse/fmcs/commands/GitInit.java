package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitConstants;
import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;
import ru.hse.fmcs.objects.Branch;
import ru.hse.fmcs.objects.HEAD;
import ru.hse.fmcs.objects.Index;
import ru.hse.fmcs.utils.PathUtil;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.List;

public class GitInit extends GitCommand {
    private final PrintStream stream;
    private final State state;

    public GitInit(State state, PrintStream stream) {
        this.state = state;
        this.stream = stream;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        if (state.isInit()) {
            throw new GitException("Git repository already initialized in " + PathUtil.getWorkingDirectoryPath());
        }
        state.getGitPaths().createDirectories();
        state.setIndex(new Index());
        String author = System.getProperty("user.name");
        state.setHead(new HEAD(author, GitConstants.MASTER, null, state.getIndex().getIndexHash(), GitConstants.INITIAL_COMMIT_MESSAGE, new Timestamp(System.currentTimeMillis())));
        state.setBranch(new Branch(GitConstants.MASTER, state.getHead().getCommitHash()));
        state.serializeIndex();
        state.serializeState();
        stream.println("Git repository initialized");
    }
}
