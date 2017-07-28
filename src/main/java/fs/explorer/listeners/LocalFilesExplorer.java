package fs.explorer.listeners;

import fs.explorer.datasource.LocalFilesProvider;
import fs.explorer.gui.dirtree.DirTreePane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LocalFilesExplorer implements ActionListener {
    private final DirTreePane dirTreePane;
    private final LocalFilesProvider provider;

    public LocalFilesExplorer(DirTreePane dirTreePane, LocalFilesProvider provider) {
        this.dirTreePane = dirTreePane;
        this.provider = provider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dirTreePane.resetDataProvider(provider);
    }
}
