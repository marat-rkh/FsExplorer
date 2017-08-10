package fs.explorer.providers.preview;

import javax.swing.*;

public interface PreviewProgressHandler {
    void onComplete(JComponent preview);
    void onError(String errorMessage);
    void onCanNotRenderer();
}
