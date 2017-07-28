package fs.explorer.models.ftpdialog;

public class FTPDialogData {
    private final String server;
    private final String login;
    private final char[] password;

    public FTPDialogData(String server, String login, char[] password) {
        this.server = server;
        this.login = login;
        this.password = password;
    }

    public String getServer() { return server; }
}
