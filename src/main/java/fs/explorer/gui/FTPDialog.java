package fs.explorer.gui;

import fs.explorer.ftp.ConnectionInfo;

import javax.swing.*;
import java.util.Optional;

public class FTPDialog {
    private final String TITLE = "Connect to FTP server";
    private final JTextField server = new JTextField();
    private final JTextField login = new JTextField();
    private final JPasswordField password = new JPasswordField();
    private final JComponent[] components;

    public FTPDialog() {
        this.components = new JComponent[] {
                new JLabel("Server"),
                server,
                new JLabel("Login"),
                login,
                new JLabel("Password"),
                password
        };
    }

    public Optional<ConnectionInfo> showAndWaitResult() {
        int result = JOptionPane.showConfirmDialog(
                null, components, TITLE, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return Optional.of(new ConnectionInfo(
                    server.getText(), login.getText(), password.getPassword()));
        }
        return Optional.empty();
    }
}