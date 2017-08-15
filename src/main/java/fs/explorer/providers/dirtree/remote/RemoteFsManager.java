package fs.explorer.providers.dirtree.remote;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.IOFunction;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.utils.FTPPathUtils;
import fs.explorer.utils.FileTypeInfo;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteFsManager implements FsManager {
    private final FTPConnectionInfo connectionInfo;

    private static final int BUFFER_SIZE = 8192;

    public RemoteFsManager(FTPConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public void checkConnection() throws FTPException {
        try (FTPConnection connection = new FTPConnection(connectionInfo)) {
            connection.open();
        }
    }

    @Override
    public byte[] readFile(FsPath filePath) throws IOException {
        return withFileStream(filePath, is -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedIOException();
                }
            }
            return baos.toByteArray();
        });
    }

    @Override
    public <R> R withFileStream(FsPath filePath, IOFunction<InputStream, R> streamReader)
            throws IOException {
        if (filePath == null || filePath.getPath() == null) {
            throw new IOException("bad file path");
        }
        try (FTPConnection connection = new FTPConnection(connectionInfo)) {
            connection.open();
            R result;
            try (InputStream is = connection.retrieveFileStream(filePath.getPath())) {
                if (is == null) {
                    throw new IOException("failed to read remote file");
                }
                result = streamReader.apply(is);
                // read the rest or command completion fails
                skipRest(is);
            } catch (InterruptedIOException e) {
                connection.completePendingCommand();
                throw new InterruptedIOException(e.getMessage());
            }
            if (!connection.completePendingCommand()) {
                throw new IOException("failed to finish remote file read");
            }
            return result;
        } catch (FTPException e) {
            throw new IOException(e.getMessage());
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
        FTPFile[] entries;
        try (FTPConnection connection = new FTPConnection(connectionInfo)) {
            connection.open();
            entries = connection.listFiles(pathStr);
        } catch (FTPException e) {
            throw new IOException(e.getMessage());
        }
        // we do not have control over listFiles so we can
        // only check interruption after operation is completed
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException();
        }
        if (entries == null) {
            throw new IOException("failed to list entries");
        }
        return Arrays.stream(entries).map(e -> {
            String lastComponent = e.getName();
            String path = FTPPathUtils.append(pathStr, lastComponent);
            TargetType targetType;
            if (e.isDirectory()) {
                targetType = TargetType.DIRECTORY;
            } else if (FileTypeInfo.isZipArchive(path)) {
                targetType = TargetType.ZIP_ARCHIVE;
            } else {
                targetType = TargetType.FILE;
            }
            return new FsPath(path, targetType, lastComponent);
        }).collect(Collectors.toList());
    }

    private void skipRest(InputStream is) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (is.read(buffer) != -1) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedIOException();
            }
        }
    }
}
