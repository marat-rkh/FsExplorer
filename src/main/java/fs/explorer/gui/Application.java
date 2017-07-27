package fs.explorer.gui;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.gui.preview.PreviewPane;

import javax.swing.*;

public class Application {
    private final MainWindow mainWindow;

    public Application() {
        StatusBar statusBar = new StatusBar("Ready");
        DirTree dirTree = new DirTree();
        LocalFilesProvider localFilesProvider = new LocalFilesProvider();
        RemoteFilesProvider remoteFilesProvider = new RemoteFilesProvider();
        MenuBar menuBar = new MenuBar(statusBar, dirTree, localFilesProvider, remoteFilesProvider);
        PreviewPane previewPane = new PreviewPane();
        this.mainWindow = new MainWindow("FsExplorer", menuBar, statusBar, dirTree, previewPane);
    }

    public void run() {
        SwingUtilities.invokeLater(mainWindow::show);
    }
}
