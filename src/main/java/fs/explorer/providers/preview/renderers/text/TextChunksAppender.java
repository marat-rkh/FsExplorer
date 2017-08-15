package fs.explorer.providers.preview.renderers.text;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.Queue;

class TextChunksAppender implements AdjustmentListener {
    private final JTextArea textArea;
    private final Queue<String> textChunks;
    private final JScrollBar targetScrollBar;
    private final int appendOffset;

    private static final int DEFAULT_APPEND_OFFSET = 40;

    TextChunksAppender(JTextArea textArea, Queue<String> textChunks, JScrollBar targetScrollBar) {
        this(textArea, textChunks, targetScrollBar, DEFAULT_APPEND_OFFSET);
    }

    TextChunksAppender(
            JTextArea textArea,
            Queue<String> textChunks,
            JScrollBar targetScrollBar,
            int appendOffset
    ) {
        if (appendOffset < 0) {
            throw new IllegalArgumentException("append offset must be >= 0");
        }
        this.textArea = textArea;
        this.textChunks = textChunks;
        this.targetScrollBar = targetScrollBar;
        this.appendOffset = appendOffset;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent event) {
        if (textChunks.isEmpty()) {
            targetScrollBar.removeAdjustmentListener(this);
            return;
        }
        int extent = targetScrollBar.getModel().getExtent();
        int current = targetScrollBar.getValue() + extent;
        int maximum = targetScrollBar.getMaximum();
        if (current >= maximum - appendOffset) {
            textArea.append(textChunks.remove());
        }
    }
}