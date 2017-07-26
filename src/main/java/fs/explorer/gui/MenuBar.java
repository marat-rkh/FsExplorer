package fs.explorer.gui;

import javax.swing.*;

public final class MenuBar {
    private final static String EXPLORE_MENU = "Explore";
    private final static String LOCAL_FILES_ITEM = "Local files";
    private final static String REMOTE_FILES_ITEM = "Remote files (FTP)";

    public static JMenuBar create() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu(EXPLORE_MENU);
        menu.add(localFilesItem());
        menu.add(remoteFilesItem());

        menuBar.add(menu);
        return menuBar;
    }

    private static JMenuItem localFilesItem() {
        JMenuItem item = new JMenuItem(LOCAL_FILES_ITEM);
        // TODO addActionListener
        return item;
    }

    private static JMenuItem remoteFilesItem() {
        JMenuItem item = new JMenuItem(REMOTE_FILES_ITEM);
        // TODO addActionListener
        return item;
    }
}
