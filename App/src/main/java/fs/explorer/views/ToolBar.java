package fs.explorer.views;

import fs.explorer.controllers.ToolBarController;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.net.URL;

public final class ToolBar {
    private final JToolBar toolBar;

    private static final String EXPLORE_LOCAL_FILES_ICON = "/icons/local16.png";
    private static final String EXPLORE_REMOTE_FILES_ICON = "/icons/ftp16.png";
    private static final String RECONNECT_ICON = "/icons/ftp-reconnect16.png";
    private static final String RELOAD_ICON = "/icons/reload16.png";

    private static final String EXPLORE_LOCAL_FILES_TIP = "Explore local files";
    private static final String EXPLORE_REMOTE_FILES_TIP = "Explore remote files (FTP)";
    private static final String RECONNECT_TIP = "Reconnect to remote host";
    private static final String RELOAD_TIP = "Reload selected";

    public ToolBar(ToolBarController toolBarController) {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(button(
                EXPLORE_LOCAL_FILES_ICON,
                EXPLORE_LOCAL_FILES_TIP,
                toolBarController::handleExploreLocalFiles
        ));
        toolBar.addSeparator();
        toolBar.add(button(
                EXPLORE_REMOTE_FILES_ICON,
                EXPLORE_REMOTE_FILES_TIP,
                toolBarController::handleExploreRemoteFiles
        ));
        toolBar.add(button(
                RECONNECT_ICON,
                RECONNECT_TIP,
                toolBarController::handleReconnectToLastRemoteHost
        ));
        toolBar.addSeparator();
        toolBar.add(button(
                RELOAD_ICON,
                RELOAD_TIP,
                toolBarController::handleSelectedReload
        ));
    }

    JComponent asJComponent() {
        return toolBar;
    }

    private JButton button(String iconFilePath, String tip, ActionListener buttonListener) {
        URL iconURL = getClass().getResource(iconFilePath);
        JButton button = new JButton(new ImageIcon(iconURL));
        button.setToolTipText(tip);
        button.addActionListener(buttonListener);
        return button;
    }
}
