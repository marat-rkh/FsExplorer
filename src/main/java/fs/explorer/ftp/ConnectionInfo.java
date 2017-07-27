package fs.explorer.ftp;

public class ConnectionInfo {
    private final String server;
    private final String login;
    private final char[] password;

    public ConnectionInfo(String server, String login, char[] password) {
        this.server = server;
        this.login = login;
        this.password = password;
    }

    public String getServer() { return server; }
}
