package ru.hse.fmcs.utils;

import ru.hse.fmcs.GitException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ru.hse.fmcs.GitConstants.*;

public class PathUtil {
    private static Path workingDirectory;
    private static Path gitDirectory;
    private static Path objectsDirectory;
    private static Path branchesDirectory;
    private static Path index;
    private static Path head;
    private static Path branch;

    public PathUtil(String workingDirectoryPath) {
        workingDirectory = Path.of(workingDirectoryPath);
        gitDirectory = workingDirectory.resolve(GIT_DIR);
        objectsDirectory = workingDirectory.resolve(OBJECTS_DIR);
        branchesDirectory = workingDirectory.resolve(BRANCHES_DIR);
        index = workingDirectory.resolve(INDEX);
        head = workingDirectory.resolve(HEAD);
        branch = workingDirectory.resolve(BRANCH);
    }

    public void createDirectories() throws GitException {
        try {
            Files.createDirectories(gitDirectory);
            Files.createDirectories(objectsDirectory);
            Files.createDirectories(branchesDirectory);
            Files.createFile(index);
            Files.createFile(head);
            Files.createFile(branch);
        } catch (IOException e) {
            throw new GitException("Couldn't create git directories");
        }
    }

    public static Path getFilePath(String fileName) {
        return workingDirectory.resolve(fileName);
    }

    public static Path getWorkingDirectoryPath() {
        return workingDirectory;
    }

    public static Path getGitDirectoryPath() {
        return gitDirectory;
    }

    public static Path getObjectsDirectoryPath() {
        return objectsDirectory;
    }

    public static Path getBranchesDirectory() {
        return branchesDirectory;
    }

    public static Path getIndexPath() {
        return index;
    }

    public static Path getHeadPath() {
        return head;
    }

    public static Path getBranchPath() {
        return branch;
    }
}
