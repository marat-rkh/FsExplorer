package fs.explorer.gui;

import javax.swing.*;

public final class MenuBar {
    private final String EXPLORE_MENU = "Explore";
    private final String LOCAL_FILES_ITEM = "Local files";
    private final String REMOTE_FILES_ITEM = "Remote files (FTP)";

    private final JMenuBar menuBar;
    private final FTPDialog ftpDialog;
    private final StatusBar statusBar;
    private final DirTree dirTree;

    public MenuBar(StatusBar statusBar, DirTree dirTree) {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu(EXPLORE_MENU);
        menu.add(localFilesItem());
        menu.add(remoteFilesItem());
        menuBar.add(menu);

        ftpDialog = new FTPDialog();
        this.statusBar = statusBar;
        this.dirTree = dirTree;
    }

    public JMenuBar asJMenuBar() { return menuBar; }

    private JMenuItem localFilesItem() {
        JMenuItem item = new JMenuItem(LOCAL_FILES_ITEM);
        item.addActionListener(e -> {
            dirTree.resetTopNode("/");
        });
        return item;
    }

    private JMenuItem remoteFilesItem() {
        JMenuItem item = new JMenuItem(REMOTE_FILES_ITEM);
        item.addActionListener(e -> {
            ftpDialog.showAndWaitResult().ifPresent(connectionInfo ->
                    dirTree.resetTopNode(connectionInfo.getServer())
            );
        });
        return item;
    }
}
