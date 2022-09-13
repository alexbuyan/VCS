package ru.hse.fmcs.utils;

import ru.hse.fmcs.GitException;
import ru.hse.fmcs.cli.GitCliImpl;
import ru.hse.fmcs.objects.State;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtil {
    public static boolean fileExists(String fileName) {
        return Files.exists(PathUtil.getFilePath(fileName));
    }

    public static List<String> listFilesInWorkingDirectory() throws GitException {
        List<String> fileNames;
        try (Stream<Path> pathStream = Files.walk(PathUtil.getWorkingDirectoryPath())) {
            fileNames = pathStream.filter(file -> !file.startsWith(PathUtil.getGitDirectoryPath()))
                    .map(file -> PathUtil.getWorkingDirectoryPath().relativize(file).toString())
                    .filter(file -> file.length() != 0)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new GitException("Couldn't list files in working directory", e);
        }
        return fileNames;
    }

    public static void clearWorkingDirectory(State state, boolean deleteUntrackedFiles) throws GitException {
        try {
            for (Path path : Files.newDirectoryStream(PathUtil.getWorkingDirectoryPath())) {
                if (Files.isRegularFile(path)) {
                    if (deleteUntrackedFiles) {
                        if (state.getIndex().getUntrackedFiles().contains(path.toFile().getName())) {
                            Files.delete(path);
                        }
                    } else {
                        Files.delete(path);
                    }
                }
                // delete only directories that are not associated with git
                if (Files.isDirectory(path) && !path.startsWith(PathUtil.getGitDirectoryPath())) {
                    clearDirectory(path);
                }
            }
        } catch (IOException e) {
            throw new GitException("Couldn't clear the working directory", e);
        }
    }

    // TODO maybe unnecessary
    public static void clearWorkingDirectoryWithUntrackedFiles(State state) throws GitException {
        clearWorkingDirectory(state, true);
    }

    public static void clearWorkingDirectoryWithoutUntrackedFiles(State state) throws GitException {
        clearWorkingDirectory(state, false);
    }

    public static void clearDirectory(Path directory) throws GitException {
        try {
            for (Path path : Files.newDirectoryStream(directory)) {
                if (Files.isDirectory(path)) {
                    clearDirectory(path);
                }
                if (Files.isRegularFile(path)) {
                    Files.delete(path);
                }
            }
            // delete empty directory
            Files.delete(directory);
        } catch (IOException e) {
            throw new GitException("Couldn't clear the directory", e);
        }
    }

    public static void createTrackedFilesInWorkingDirectory(State state) throws GitException {
        try {
            for (Map.Entry<String, String> file : state.getIndex().getTrackedFiles().entrySet()) {
                Path filePath = PathUtil.getWorkingDirectoryPath().resolve(file.getKey());
                Path parentDirectory = filePath.getParent();
                if (parentDirectory != null && !Files.exists(parentDirectory)) {
                    Files.createDirectories(parentDirectory);
                }
                Path sourceCode = PathUtil.getObjectsDirectoryPath().resolve(file.getValue());
                Files.copy(sourceCode, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new GitException("Couldn't update tracked files", e);
        }
    }
}
