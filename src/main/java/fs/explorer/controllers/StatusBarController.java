package fs.explorer.controllers;

import fs.explorer.views.StatusBar;

import java.awt.*;

public class StatusBarController {
    private final StatusBar statusBar;

    public StatusBarController(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public void setInfoMessage(String msg) {
        statusBar.setText(msg);
        statusBar.setTextColor(Color.GREEN);
    }

    public void setProgressMessage(String msg) {
        statusBar.setText(msg);
        statusBar.setTextColor(Color.BLUE);
    }

    public void setErrorMessage(String msg) {
        statusBar.setText(msg);
        statusBar.setTextColor(Color.RED);
    }
}
