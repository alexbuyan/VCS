package ru.hse.fmcs.utils;

import org.apache.commons.codec.digest.DigestUtils;
import ru.hse.fmcs.GitException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class HashUtil {
    public static String getFileHash(Path filePath) throws GitException {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return DigestUtils.sha1Hex(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new GitException("Couldn't hash file " + filePath.getFileName(), e);
        }
    }

    public static String getStringHash(String string) {
        return DigestUtils.sha1Hex(string);
    }
}
