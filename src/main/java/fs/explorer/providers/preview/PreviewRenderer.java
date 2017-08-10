package fs.explorer.providers.preview;

import javax.swing.*;

public interface PreviewRenderer {
    boolean canRenderForExtension(String fileExtension);
    JComponent render(PreviewRenderingData data) throws InterruptedException;
}
