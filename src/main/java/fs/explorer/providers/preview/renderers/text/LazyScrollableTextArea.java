package fs.explorer.providers.preview.renderers.text;

import javax.swing.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class LazyScrollableTextArea {
    private final JTextArea textArea;
    private final Queue<String> textChunks;
    private final JScrollPane scrollPane;

    LazyScrollableTextArea(JTextArea textArea, List<String> textChunksList) {
        this.textArea = textArea;
        this.textChunks = new LinkedList<>(textChunksList);
        this.scrollPane = new JScrollPane(textArea);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        TextChunksAppender appender = new TextChunksAppender(
                textArea, new LinkedList<>(textChunksList), verticalScrollBar);
        verticalScrollBar.addAdjustmentListener(appender);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public Queue<String> getTextChunks() {
        return textChunks;
    }

    JComponent asJComponent() {
        return scrollPane;
    }

    private static class TextChunksAppender implements AdjustmentListener {
        private final JTextArea textArea;
        private final Queue<String> textChunks;
        private final JScrollBar targetScrollBar;

        private static final int BOTTOM_POSITION_THRESHOLD = 40;

        private TextChunksAppender(
                JTextArea textArea,
                Queue<String> textChunks,
                JScrollBar targetScrollBar
        ) {
            this.textArea = textArea;
            this.textChunks = textChunks;
            this.targetScrollBar = targetScrollBar;
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
            if (current > maximum - BOTTOM_POSITION_THRESHOLD) {
                textArea.append(textChunks.remove());
            }
        }
    }
}
