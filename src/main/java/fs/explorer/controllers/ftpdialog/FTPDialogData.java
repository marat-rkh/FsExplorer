package fs.explorer.controllers.ftpdialog;

// TODO rename to FTPConnectionInfo
public class FTPDialogData {
    private final String server;
    // TODO rename to user
    private final String login;
    private final char[] password;

    public FTPDialogData(String server, String login, char[] password) {
        this.server = server;
        this.login = login;
        this.password = password;
    }

    public String getServer() { return server; }

    public String getLogin() { return login; }

    public char[] getPassword() { return password; }
}
