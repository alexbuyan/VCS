package ru.hse.fmcs.cli;

import ru.hse.fmcs.GitException;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GitExec {
    public static void main(String[] args) {
        PrintStream outputStream = GitCliImpl.getOutputStream();
        if (args.length < 2) {
            outputStream.println("Not enough arguments");
            return;
        }
        String gitDirectoryName = args[0];
        String command = args[1];
        List<String> arguments = Arrays.stream(args).skip(2).collect(Collectors.toList());
        GitCliImpl gitCli = new GitCliImpl(gitDirectoryName);
        gitCli.setOutputStream(System.out);
        try {
            gitCli.runCommand(command, arguments);
        } catch (GitException e) {
            GitCliImpl.getOutputStream().println(e.getMessage());
        }
    }
}
