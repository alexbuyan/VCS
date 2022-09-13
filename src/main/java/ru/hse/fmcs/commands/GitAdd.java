package ru.hse.fmcs.commands;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;

import java.io.PrintStream;
import java.util.List;

public class GitAdd extends GitCommand {
    private final State state;

    public GitAdd(State state) {
        this.state = state;
    }

    @Override
    public void run(List<String> arguments) throws GitException {
        if (arguments.size() == 0) {
            throw new GitException("Nothing specified, nothing added.\n" +
                    "hint: Maybe you wanted to say 'git add .'?");
        }
        state.deserializeState();

        boolean success;
        for (String fileName : arguments) {
            success = state.getIndex().add(fileName);
            if (!success) {
                throw new GitException("fatal: '" + fileName + "' did not match any files");
            }
        }
        state.serializeState();
    }
}
