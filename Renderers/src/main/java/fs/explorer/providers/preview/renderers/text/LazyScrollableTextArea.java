package fs.explorer.providers.preview.renderers.text;

import javax.swing.*;
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
                textArea, textChunks, verticalScrollBar);
        verticalScrollBar.addAdjustmentListener(appender);
    }

    JTextArea getTextArea() {
        return textArea;
    }

    Queue<String> getTextChunks() {
        return textChunks;
    }

    JScrollPane getScrollPane() {
        return scrollPane;
    }

    JComponent asJComponent() {
        return getScrollPane();
    }
}
