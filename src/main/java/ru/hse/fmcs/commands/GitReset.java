package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;

import java.io.PrintStream;
import java.util.List;

public class GitReset extends GitCommand {
    private final PrintStream stream;
    private final State state;

    public GitReset(State state, PrintStream stream) {
        this.state = state;
        this.stream = stream;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        if (arguments.size() > 1) {
            throw new GitException("fatal: ambiguous arguments");
        }
        state.deserializeState();

        String commitHash = arguments.get(0);
        state.resetToCommit(commitHash);

        state.serializeState();
        stream.println("Successful reset to commit " + commitHash);
    }
}
