package fs.explorer.controllers;

import fs.explorer.datasource.RemoteFilesProvider;
import fs.explorer.gui.FTPDialog;
import fs.explorer.gui.dirtree.DirTreePane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoteFilesExplorer implements ActionListener {
    private final FTPDialog ftpDialog;
    private final DirTreePane dirTreePane;
    private final RemoteFilesProvider remoteFilesProvider;

    public RemoteFilesExplorer(
            FTPDialog ftpDialog,
            DirTreePane dirTreePane,
            RemoteFilesProvider remoteFilesProvider
    ) {
        this.ftpDialog = ftpDialog;
        this.dirTreePane = dirTreePane;
        this.remoteFilesProvider = remoteFilesProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ftpDialog.showAndWaitResult().ifPresent(connectionInfo -> {
            remoteFilesProvider.setConnectionInfo(connectionInfo);
            dirTreePane.resetDataProvider(remoteFilesProvider);
        });
    }
}
