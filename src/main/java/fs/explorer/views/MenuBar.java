package fs.explorer.views;

import fs.explorer.controllers.MenuBarController;

import javax.swing.*;
import java.awt.event.ActionListener;

public final class MenuBar {
    private final JMenuBar menuBar;

    private static final String EXPLORE_MENU = "Explore";
    private static final String LOCAL_FILES_ITEM = "Local files";
    private static final String REMOTE_FILES_ITEM = "Remote files (FTP)";

    private static final String SELECTED_MENU = "Selected";
    private static final String RELOAD_ITEM = "Reload";

    public MenuBar(MenuBarController menuBarController) {
        menuBar = new JMenuBar();

        JMenu exploreMenu = new JMenu(EXPLORE_MENU);
        exploreMenu.add(menuItem(LOCAL_FILES_ITEM, menuBarController::handleExploreLocalFiles));
        exploreMenu.add(menuItem(REMOTE_FILES_ITEM, menuBarController::handleExploreRemoteFiles));
        menuBar.add(exploreMenu);

        JMenu selectedMenu = new JMenu(SELECTED_MENU);
        selectedMenu.add(menuItem(RELOAD_ITEM, menuBarController::handleSelectedReload));
        menuBar.add(selectedMenu);
    }

    public JMenuBar asJMenuBar() { return menuBar; }

    private JMenuItem menuItem(String label, ActionListener itemListener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(itemListener);
        return item;
    }
}
