package fs.explorer.controllers.ftpdialog;

public class FTPConnectionInfo {
    private final String host;
    private final String user;
    private final char[] password;

    public FTPConnectionInfo(String host, String user, char[] password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public String getHost() { return host; }

    public String getUser() { return user; }

    public char[] getPassword() { return password; }
}
