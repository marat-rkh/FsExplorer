package fs.explorer.controllers;

public interface FsTypeSwitchProgressHandler {
    void onComplete();

    void onFail(String errorMessage);
}
