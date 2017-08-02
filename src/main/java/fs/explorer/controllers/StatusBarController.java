package fs.explorer.controllers;

import fs.explorer.utils.CustomColors;
import fs.explorer.views.StatusBar;

import java.awt.*;

public class StatusBarController {
    private final StatusBar statusBar;

    public StatusBarController(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public void setInfoMessage(String msg) {
        statusBar.setText(msg);
        statusBar.setTextColor(CustomColors.DARK_GREEN);
    }

    public void setInfoMessage(String msg, String optionalDetail) {
        setInfoMessage(fullMessage(msg, optionalDetail));
    }

    public void setProgressMessage(String msg) {
        statusBar.setText(msg);
        statusBar.setTextColor(Color.BLUE);
    }

    public void setProgressMessage(String msg, String optionalDetail) {
        setProgressMessage(fullMessage(msg, optionalDetail));
    }

    public void setErrorMessage(String msg) {
        statusBar.setText(msg);
        statusBar.setTextColor(Color.RED);
    }

    public void setErrorMessage(String msg, String optionalDetail) {
        setErrorMessage(fullMessage(msg, optionalDetail));
    }

    private String fullMessage(String msg, String optionalDetail) {
        String fullMessage = msg;
        if(optionalDetail != null && !optionalDetail.isEmpty()) {
            fullMessage += ": " + optionalDetail;
        }
        return fullMessage;
    }
}
