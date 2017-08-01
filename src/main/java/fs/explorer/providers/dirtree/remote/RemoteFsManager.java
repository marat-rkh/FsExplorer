package fs.explorer.providers.dirtree.remote;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.FsPath;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteFsManager implements FsManager {
    private final FTPClient ftpClient;

    private static final long KEEP_ALIVE_TIMEOUT_SECONDS = 150;

    public RemoteFsManager() {
        ftpClient = new FTPClient();
    }

    public void connect(FTPConnectionInfo connectionInfo) throws FTPException {
        try {
            ftpClient.connect(connectionInfo.getHost());
            int replyCode = ftpClient.getReplyCode();
            if(!FTPReply.isPositiveCompletion(replyCode)) {
                ftpClient.disconnect();
                throw new FTPException("connection failed");
            }
            String user;
            String password;
            if(connectionInfo.getUser().isEmpty()) {
                user = "anonymous";
                password = "";
            } else {
                user = connectionInfo.getUser();
                password = new String(connectionInfo.getPassword());
            }
            if(!ftpClient.login(user, password)) {
                ftpClient.disconnect();
                throw new FTPException("login failed");
            }
            if(!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
                ftpClient.disconnect();
                throw new FTPException("connection config failed");
            }
            ftpClient.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT_SECONDS);
        } catch(IOException e) {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch(IOException ioe) {
                    // do nothing
                }
            }
            throw new FTPException(e.getMessage());
        }
    }

    // TODO add to resource cleanup
    public void disconnect() throws FTPException {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            throw new FTPException("failed to logout");
        } finally {
            if(ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch(IOException ioe) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public byte[] readFile(FsPath filePath) throws IOException {
        if(filePath == null || filePath.getPath() == null) {
            throw new IOException("bad file path");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ftpClient.retrieveFile(filePath.getPath(), baos);
        return baos.toByteArray();
    }

    @Override
    public List<FsPath> list(FsPath directoryPath) throws IOException {
        if(!directoryPath.isDirectory()) {
            throw new IOException("not a directory");
        }
        String pathStr = directoryPath.getPath();
        if(pathStr == null) {
            throw new IOException("bad directory path");
        }
        FTPFile[] entries = ftpClient.listFiles(pathStr);
        if(entries == null) {
            throw new IOException("failed to list entries");
        }
        try {
            return Arrays.stream(entries).map(e -> {
                String lastComponent = e.getName();
                String path = Paths.get(pathStr, lastComponent).toString();
                return new FsPath(path, /*isDirectory*/e.isDirectory(), lastComponent);
            }).collect(Collectors.toList());
        } catch (InvalidPathException e) {
            throw new IOException("malformed path");
        }
    }
}
