package fs.explorer.providers.preview.renderers.text;

import fs.explorer.providers.preview.renderers.text.LazyScrollableTextArea;
import fs.explorer.providers.preview.renderers.text.TextChunksAppender;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.AdjustmentListener;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

public class LazyScrollableTextAreaTest {
    @Test
    public void createdWithCorrectVerticalScrollBarListener() {
        LazyScrollableTextArea lazyTextArea = new LazyScrollableTextArea(
                new JTextArea(), Collections.emptyList());
        AdjustmentListener[] listeners = lazyTextArea.getScrollPane()
                .getVerticalScrollBar().getAdjustmentListeners();
        assertTrue(Arrays.stream(listeners).anyMatch(l -> l instanceof TextChunksAppender));
    }
}