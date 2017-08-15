package fs.explorer.providers.dirtree.remote;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;

public class FTPConnection implements AutoCloseable {
    private final FTPClient ftpClient;
    private final FTPConnectionInfo connectionInfo;

    private boolean isClosed = false;

    private static final int CONNECTION_TIMEOUT_MILLISECONDS = 10000;
    private static final long KEEP_ALIVE_TIMEOUT_SECONDS = 150;

    FTPConnection(FTPConnectionInfo connectionInfo) {
        ftpClient = new FTPClient();
        this.connectionInfo = connectionInfo;
    }

    @Override
    public void close() throws FTPException {
        if (!isClosed && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                throw new FTPException("failed to disconnect");
            } finally {
                isClosed = true;
            }
        }
    }

    void open() throws FTPException {
        if (isClosed) {
            throw new FTPException("attempt to reopen closed connection");
        }
        if (ftpClient.isConnected()) {
            throw new FTPException("connection is already open");
        }
        try {
            makeConnection();
            login();
            configureClient();
        } catch (IOException e) {
            throw new FTPException(e.getMessage());
        }
    }

    InputStream retrieveFileStream(String path) throws IOException {
        return ftpClient.retrieveFileStream(path);
    }

    boolean completePendingCommand() throws IOException {
        return ftpClient.completePendingCommand();
    }

    FTPFile[] listFiles(String path) throws IOException {
        return ftpClient.listFiles(path);
    }

    private void makeConnection() throws IOException, FTPException {
        ftpClient.setConnectTimeout(CONNECTION_TIMEOUT_MILLISECONDS);
        ftpClient.connect(connectionInfo.getHost());
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            throw new FTPException("connection failed");
        }
    }

    private void login() throws IOException, FTPException {
        String user = connectionInfo.getUser();
        String password = new String(connectionInfo.getPassword());
        if (user.isEmpty() && password.isEmpty()) {
            user = "anonymous";
            password = "";
        }
        if (!ftpClient.login(user, password)) {
            throw new FTPException("login failed");
        }
    }

    private void configureClient() throws IOException, FTPException {
        if (!ftpClient.setFileType(FTP.BINARY_FILE_TYPE)) {
            throw new FTPException("connection configureClient failed");
        }
        ftpClient.setControlKeepAliveTimeout(KEEP_ALIVE_TIMEOUT_SECONDS);
    }
}
