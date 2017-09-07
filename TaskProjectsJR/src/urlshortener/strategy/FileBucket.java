package urlshortener.strategy;

import urlshortener.ExceptionHandler;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBucket {
    private Path path;

    public FileBucket() {
        try {
            path = Files.createTempFile("_temp", null);
            Files.deleteIfExists(path);
            Files.createFile(path);
            path.toFile().deleteOnExit();

        } catch (IOException e) {
            ExceptionHandler.log(e);
        }
    }

    public long getFileSize() {
        try {
            return Files.size(path);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }

        return 0L;
    }

    public void putEntry(Entry entry) {

        try {
            OutputStream fileOutputStream = Files.newOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(entry);
            fileOutputStream.close();
            objectOutputStream.close();
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }

    }

    public Entry getEntry() {

        Entry entry = null;

        try {
            if (getFileSize() > 0) {

                try {
                    InputStream fileInputStream = Files.newInputStream(path);
                    ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    Object object = objectInputStream.readObject();
                    objectInputStream.close();
                    fileInputStream.close();
                    entry = (Entry) object;
                } catch (IOException e) {
                    ExceptionHandler.log(e);
                } catch (ClassNotFoundException e) {
                    ExceptionHandler.log(e);
                }

            }
        } catch (Exception e) {
            ExceptionHandler.log(e);
        }

        return entry;

    }

    public void remove() {
        try {
            Files.delete(path);
        } catch (IOException e) {
            ExceptionHandler.log(e);
        }
    }
}
