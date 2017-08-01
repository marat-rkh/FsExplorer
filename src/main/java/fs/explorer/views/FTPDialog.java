package fs.explorer.views;

import fs.explorer.providers.FTPConnectionInfo;

import javax.swing.*;
import java.util.Optional;

public class FTPDialog {
    private final String TITLE = "Connect to FTP host";
    private final JTextField host = new JTextField();
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JComponent[] components;

    public FTPDialog() {
        this.components = new JComponent[] {
                new JLabel("Host"),
                host,
                new JLabel("Username"),
                username,
                new JLabel("Password"),
                password
        };
    }

    public Optional<FTPConnectionInfo> showAndWaitResult() {
        int result = JOptionPane.showConfirmDialog(
                null, components, TITLE, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return Optional.of(new FTPConnectionInfo(
                    host.getText(), username.getText(), password.getPassword()));
        }
        return Optional.empty();
    }
}
