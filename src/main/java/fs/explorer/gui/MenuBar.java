package fs.explorer.gui;

import javax.swing.*;
import java.awt.event.ActionListener;

public final class MenuBar {
    private final String EXPLORE_MENU = "Explore";
    private final String LOCAL_FILES_ITEM = "Local files";
    private final String REMOTE_FILES_ITEM = "Remote files (FTP)";

    private final JMenuBar menuBar;

    public MenuBar(
            ActionListener localFilesItemListener,
            ActionListener remoteFilesItemListener
    ) {
        menuBar = new JMenuBar();
        JMenu menu = new JMenu(EXPLORE_MENU);
        menu.add(menuItem(LOCAL_FILES_ITEM, localFilesItemListener));
        menu.add(menuItem(REMOTE_FILES_ITEM, remoteFilesItemListener));
        menuBar.add(menu);
    }

    public JMenuBar asJMenuBar() { return menuBar; }

    private JMenuItem menuItem(String label, ActionListener itemListener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(itemListener);
        return item;
    }
}
