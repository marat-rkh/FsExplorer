package fs.explorer.providers.dirtree.local;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.IOFunction;
import fs.explorer.providers.dirtree.path.FsPath;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFsManager implements FsManager {
    private static final int BUFFER_SIZE = 8196;

    @Override
    public byte[] readFile(FsPath fsPath) throws IOException {
        return withFileStream(fsPath, fis -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedIOException();
                }
            }
            return baos.toByteArray();
        });
    }

    @Override
    public <R> R withFileStream(FsPath fsPath, IOFunction<InputStream, R> streamReader)
            throws IOException {
        if (fsPath == null || fsPath.getPath() == null) {
            throw new IOException("bad file path");
        }
        try (FileInputStream fis = new FileInputStream(fsPath.getPath())) {
            return streamReader.apply(fis);
        }
    }

    @Override
    public List<FsPath> list(FsPath directoryPath) throws IOException {
        if (directoryPath == null) {
            throw new IOException("bad directory path");
        }
        if (!directoryPath.isDirectory()) {
            throw new IOException("not a directory");
        }
        String pathStr = directoryPath.getPath();
        if (pathStr == null) {
            throw new IOException("bad directory path");
        }
        try {
            List<FsPath> entries = Files.list(Paths.get(pathStr))
                    .map(FsPath::fromPath)
                    .collect(Collectors.toList());
            // TODO consider checking interruption every N read entries
            // Local reads should be fast enough, so for now we check
            // interruption only after everything is read
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedIOException();
            }
            return entries;
        } catch (InvalidPathException e) {
            throw new IOException("malformed directory path");
        }
    }
}
