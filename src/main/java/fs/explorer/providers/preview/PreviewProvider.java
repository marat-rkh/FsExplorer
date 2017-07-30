package fs.explorer.providers.preview;

import fs.explorer.providers.TreeNodeData;

import javax.swing.*;
import java.util.function.Consumer;

public interface PreviewProvider {
    void getTextPreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    );

    void getImagePreview(
            TreeNodeData data,
            Consumer<JComponent> onComplete,
            Consumer<String> onFail
    );
}
