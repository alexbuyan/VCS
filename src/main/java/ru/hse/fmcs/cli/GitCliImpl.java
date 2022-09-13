package ru.hse.fmcs.cli;

import org.jetbrains.annotations.NotNull;
import ru.hse.fmcs.GitConstants;
import ru.hse.fmcs.GitException;
import ru.hse.fmcs.commands.*;
import ru.hse.fmcs.objects.State;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitCliImpl implements GitCli {
    private static PrintStream outputStream = System.out; // must be initialized before ctor call
    private final State state;
    private final Map<String, GitCommand> commands;

    public GitCliImpl(String workingDirectory) {
        state = new State(workingDirectory);

        // basic part
        commands = new HashMap<>();
        commands.put(GitConstants.INIT, new GitInit(state, outputStream));
        commands.put(GitConstants.ADD, new GitAdd(state));
        commands.put(GitConstants.RM, new GitRm(state));
        commands.put(GitConstants.STATUS, new GitStatus(state, outputStream));
        commands.put(GitConstants.COMMIT, new GitCommit(state, outputStream));
        commands.put(GitConstants.RESET, new GitReset(state, outputStream));
        commands.put(GitConstants.LOG, new GitLog(state, outputStream));
        commands.put(GitConstants.CHECKOUT, new GitCheckout(state));

        // branches part
        commands.put(GitConstants.BRANCH_CREATE, new GitBranchCreate());
        commands.put(GitConstants.BRANCH_REMOVE, new GitBranchRemove());
        commands.put(GitConstants.SHOW_BRANCHES, new GitShowBranches());
    }

    public static PrintStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void runCommand(@NotNull String command, @NotNull List<@NotNull String> arguments) throws GitException {
        if (!command.equals(GitConstants.INIT) && !state.isInit()) {
            throw new GitException("Not a git repository");
        }
        GitCommand gitCommand = commands.get(command);
        if (gitCommand == null) {
            throw new GitException("Operation is not supported");
        }
        gitCommand.run(arguments);
    }

    @Override
    public void setOutputStream(@NotNull PrintStream outputStream) {
        GitCliImpl.outputStream = outputStream;
    }

    @Override
    public @NotNull String getRelativeRevisionFromHead(int n) throws GitException {
        return state.getRelativeRevision(n);
    }
}
