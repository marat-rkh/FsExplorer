package fs.explorer.providers.preview;

import javax.swing.*;

public interface PreviewRenderer {
    JComponent renderText(TextPreviewData data);
    JComponent renderImage(ImagePreviewData data);
}
