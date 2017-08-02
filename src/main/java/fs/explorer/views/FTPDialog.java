package fs.explorer.views;

import fs.explorer.providers.dirtree.remote.FTPConnectionInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class FTPDialog {
    private static final String TITLE = "Connect to FTP host";
    private final JTextField host = new JTextField();
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JLabel errorMessage = new JLabel();
    private final JComponent[] components;

    public FTPDialog() {
        errorMessage.setForeground(Color.RED);
        this.components = new JComponent[] {
                new JLabel("Host"),
                host,
                new JLabel("Username"),
                username,
                new JLabel("Password"),
                password,
                errorMessage
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

    public void setErrorMessage(String msg) {
        errorMessage.setText(msg);
    }
}
