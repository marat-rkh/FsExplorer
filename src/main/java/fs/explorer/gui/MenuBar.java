package fs.explorer.gui;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.datasource.RemoteFilesProvider;

import javax.swing.*;

public final class MenuBar {
    private final String EXPLORE_MENU = "Explore";
    private final String LOCAL_FILES_ITEM = "Local files";
    private final String REMOTE_FILES_ITEM = "Remote files (FTP)";

    private final JMenuBar menuBar;
    private final FTPDialog ftpDialog;
    private final StatusBar statusBar;
    private final DirTree dirTree;

    private final LocalFilesProvider localFilesProvider;
    private final RemoteFilesProvider remoteFilesProvider;

    public MenuBar(
            StatusBar statusBar,
            DirTree dirTree,
            LocalFilesProvider localFilesProvider,
            RemoteFilesProvider remoteFilesProvider
    ) {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu(EXPLORE_MENU);
        menu.add(localFilesItem());
        menu.add(remoteFilesItem());
        menuBar.add(menu);

        ftpDialog = new FTPDialog();
        this.statusBar = statusBar;
        this.dirTree = dirTree;

        this.localFilesProvider = localFilesProvider;
        this.remoteFilesProvider = remoteFilesProvider;
    }

    public JMenuBar asJMenuBar() { return menuBar; }

    private JMenuItem localFilesItem() {
        JMenuItem item = new JMenuItem(LOCAL_FILES_ITEM);
        item.addActionListener(e ->
                dirTree.resetDataProvider(localFilesProvider)
        );
        return item;
    }

    private JMenuItem remoteFilesItem() {
        JMenuItem item = new JMenuItem(REMOTE_FILES_ITEM);
        item.addActionListener(e -> {
            ftpDialog.showAndWaitResult().ifPresent(connectionInfo -> {
                remoteFilesProvider.setConnectionInfo(connectionInfo);
                dirTree.resetDataProvider(remoteFilesProvider);
            });
        });
        return item;
    }
}
