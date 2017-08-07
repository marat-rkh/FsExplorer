package fs.explorer.providers.dirtree.remote;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.IOFunction;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.utils.Disposable;
import fs.explorer.utils.FileTypeInfo;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// @ThreadSafe
public class RemoteFsManager implements FsManager, Disposable {
    private final FTPClient ftpClient;

    private static final long KEEP_ALIVE_TIMEOUT_SECONDS = 150;
    private static final int BUFFER_SIZE = 8192;

    public RemoteFsManager() {
        ftpClient = new FTPClient();
    }

    public void connect(FTPConnectionInfo connectionInfo) throws FTPException {
        synchronized(ftpClient) {
            doConnect(connectionInfo);
        }
    }

    public void disconnect() throws FTPException {
        synchronized(ftpClient) {
            doDisconnect();
        }
    }

    public void reconnect(FTPConnectionInfo connectionInfo) throws FTPException {
        synchronized(ftpClient) {
            doDisconnect();
            doConnect(connectionInfo);
        }
    }

    @Override
    public byte[] readFile(FsPath filePath) throws IOException {
        return withFileStream(filePath, is -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
                if(Thread.currentThread().isInterrupted()) {
                    throw new InterruptedIOException();
                }
            }
            return baos.toByteArray();
        });
    }

    @Override
    public <R> R withFileStream(
            FsPath filePath, IOFunction<InputStream, R> streamReader) throws IOException {
        if(filePath == null || filePath.getPath() == null) {
            throw new IOException("bad file path");
        }
        synchronized(ftpClient) {
            R result = null;
            try (
                    InputStream is = ftpClient.retrieveFileStream(filePath.getPath())
            ) {
                if(is == null) {
                    throw new IOException("failed to read remote file");
                }
                result = streamReader.apply(is);
                // read the rest or command completion fails
                skipRest(is);
            }
            if(!ftpClient.completePendingCommand()) {
                throw new IOException("failed to finish remote file read");
            }
            return result;
        }
    }

    @Override
    public List<FsPath> list(FsPath directoryPath) throws IOException {
        if(directoryPath == null) {
            throw new IOException("bad directory path");
        }
        if(!directoryPath.isDirectory()) {
            throw new IOException("not a directory");
        }
        String pathStr = directoryPath.getPath();
        if(pathStr == null) {
            throw new IOException("bad directory path");
        }
        FTPFile[] entries = null;
        synchronized(ftpClient) {
            entries = ftpClient.listFiles(pathStr);
        }
        if(entries == null) {
            throw new IOException("failed to list entries");
        }
        try {
            return Arrays.stream(entries).map(e -> {
                String lastComponent = e.getName();
                String path = Paths.get(pathStr, lastComponent).toString();
                TargetType targetType = null;
                if(e.isDirectory()) {
                    targetType = TargetType.DIRECTORY;
                } else if(FileTypeInfo.isZipArchive(path)) {
                    targetType = TargetType.ZIP_ARCHIVE;
                } else {
                    targetType = TargetType.FILE;
                }
                return new FsPath(path, targetType, lastComponent);
            }).collect(Collectors.toList());
        } catch (InvalidPathException e) {
            throw new IOException("malformed path");
        }
    }

    @Override
    public void dispose() {
        try {
            disconnect();
        } catch (FTPException e) {
            // do nothing
        }
    }

    private void doConnect(FTPConnectionInfo connectionInfo) throws FTPException {
        if(ftpClient.isConnected()) {
            throw new FTPException("already connected");
        }
        try {
            makeConnection(connectionInfo);
            login(connectionInfo);
            configureClient();
        } catch (IOException e) {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
            throw new FTPException(e.getMessage());
        }
    }

    private void doDisconnect() throws FTPException {
        if(ftpClient.isConnected()) {
            try {
                ftpClient.logout();
            } catch (IOException e) {
                throw new FTPException("failed to logout");
            } finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                    // do nothing
                }
            }
        }
    }

    private void makeConnection(FTPConnectionInfo connectionInfo)
            throws IOException, FTPException {
        ftpClient.connect(connectionInfo.getHost());
        int replyCode = ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
            throw new FTPException("connection failed");
        }
    }

    private void login(FTPConnectionInfo connectionInfo) throws IOException, FTPException {
        String user = connectionInfo.getUser();
        String password = new String(connectionInfo.getPassword());
        if(user.isEmpty() && password.isEmpty()) {
            user = "anonymous";
            password = "";
        }
        if(!ftpClient.login(user, password)) {
            ftpClient.disconnect();
            throw new FTPException("login failed");
        }
    }

    private void configureClient() throws IOException, FTPException {
        if(!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
            ftpClient.disconnect();
            throw new FTPException("connection configureClient failed");
        }
        ftpClient.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT_SECONDS);
    }

    private void skipRest(InputStream is) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        while(is.read(buffer) != -1) {
            if(Thread.currentThread().isInterrupted()) {
                throw new InterruptedIOException();
            }
        }
    }
}
