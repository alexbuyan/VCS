package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;

import java.io.PrintStream;
import java.util.List;

public class GitCommit extends GitCommand {
    private final PrintStream stream;
    private final State state;

    public GitCommit(State state, PrintStream stream) {
        this.state = state;
        this.stream = stream;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        state.deserializeState();

        if (state.getIndex().getAddedFiles().isEmpty()) {
            throw new GitException("No changes added to commit");
        }

        String message = "";
        if (!arguments.isEmpty()) {
            message = arguments.get(0);
        }
        state.commit(message);
        state.serializeIndex();

        state.serializeState();
        stream.println("Files committed successfully");
    }
}