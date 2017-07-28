package fs.explorer.controllers;

import fs.explorer.model.ftpdialog.FTPDialogModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoteFilesExplorer implements ActionListener {
    private final FTPDialogModel ftpDialogModel;

    public RemoteFilesExplorer(FTPDialogModel ftpDialogModel) {
        this.ftpDialogModel = ftpDialogModel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ftpDialogModel.show();
    }
}
