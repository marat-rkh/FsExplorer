package fs.explorer.providers.preview;

import java.awt.*;

public class PreviewContext {
    private final Dimension preferredSize;

    public PreviewContext(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }
}
