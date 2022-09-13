package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;

import java.io.PrintStream;
import java.util.List;

public class GitRm extends GitCommand {
    private final State state;

    public GitRm(State state) {
        this.state = state;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        state.deserializeState();
        boolean success;
        for (String fileName : arguments) {
            success = state.getIndex().restore(fileName);
            if (!success) {
                throw new GitException("error: '" + fileName + "' did not match any file(s) known to git");
            }
        }
        state.serializeState();
    }
}
