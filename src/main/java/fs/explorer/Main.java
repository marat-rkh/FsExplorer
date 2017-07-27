package fs.explorer;

import fs.explorer.gui.MainWindow;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow("FsExplorer");
        SwingUtilities.invokeLater(mainWindow::show);
    }
}
