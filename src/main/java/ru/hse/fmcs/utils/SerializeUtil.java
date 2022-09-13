package ru.hse.fmcs.utils;

import ru.hse.fmcs.GitException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerializeUtil {
    public static <T> void serialize(T object, Path filePath) throws GitException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            throw new GitException("Couldn't serialize object to file", e);
        }
    }

    public static <T> T deserialize(Class<T> clazz, Path filePath) throws GitException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(Files.newInputStream(filePath))) {
            Object object = objectInputStream.readObject();
            return clazz.cast(object);
        } catch (ClassNotFoundException | ClassCastException | IOException e) {
            throw new GitException("Couldn't deserialize object from file", e);
        }
    }
}
