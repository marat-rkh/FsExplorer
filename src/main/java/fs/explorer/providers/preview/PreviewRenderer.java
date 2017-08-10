package fs.explorer.providers.preview;

import javax.swing.*;

public interface PreviewRenderer {
    JComponent renderText(TextPreviewData data) throws InterruptedException;
    JComponent renderImage(ImagePreviewData data) throws InterruptedException;
}
