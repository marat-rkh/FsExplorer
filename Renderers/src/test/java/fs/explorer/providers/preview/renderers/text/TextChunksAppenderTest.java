package fs.explorer.providers.preview.renderers.text;

import fs.explorer.providers.preview.renderers.text.TextChunksAppender;
import org.junit.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TextChunksAppenderTest {
    @Test(expected = IllegalArgumentException.class)
    public void failsOnNegativeAppendOffset() {
        new TextChunksAppender(null, null, null, -1);
    }

    @Test
    public void removesScrollListenerOnEmptyTextChunks() {
        JTextArea textArea = new JTextArea();
        JTextArea spiedTextArea = spy(textArea);
        JScrollBar scrollBar = mock(JScrollBar.class);
        TextChunksAppender appender = new TextChunksAppender(
                spiedTextArea, new LinkedList<>(), scrollBar, 1);
        appender.adjustmentValueChanged(null);

        verify(scrollBar).removeAdjustmentListener(same(appender));
        verify(spiedTextArea, never()).append(any());
    }

    @Test
    public void doesNotAppendWhenAboveBottomOffset() {
        JTextArea textArea = new JTextArea();
        JTextArea spiedTextArea = spy(textArea);
        List<String> textChunks = Arrays.asList("chunk1", "chunk2");
        JScrollBar scrollBar = mock(JScrollBar.class);
        setTestValues(scrollBar, 5);
        setTestExtents(scrollBar, 5);
        setTestMaxValues(scrollBar, 16);
        TextChunksAppender appender = new TextChunksAppender(
                spiedTextArea, new LinkedList<>(textChunks), scrollBar, 5);
        appender.adjustmentValueChanged(null);

        verify(scrollBar, never()).removeAdjustmentListener(any());
        verify(spiedTextArea, never()).append(any());
    }

    @Test
    public void appendsWhenBelowBottomOffset() {
        JTextArea textArea = new JTextArea();
        JTextArea spiedTextArea = spy(textArea);
        List<String> textChunks = Arrays.asList("chunk1", "chunk2");
        JScrollBar scrollBar = mock(JScrollBar.class);
        setTestValues(scrollBar, 7);
        setTestExtents(scrollBar, 5);
        setTestMaxValues(scrollBar, 16);
        TextChunksAppender appender = new TextChunksAppender(
                spiedTextArea, new LinkedList<>(textChunks), scrollBar, 5);
        appender.adjustmentValueChanged(null);

        verify(scrollBar, never()).removeAdjustmentListener(any());
        verify(spiedTextArea).append("chunk1");
    }

    @Test
    public void appendsTwice() {
        JTextArea textArea = new JTextArea();
        JTextArea spiedTextArea = spy(textArea);
        List<String> textChunks = Arrays.asList("chunk1", "chunk2");
        JScrollBar scrollBar = mock(JScrollBar.class);
        setTestValues(scrollBar, 7, 17);
        setTestExtents(scrollBar, 5);
        setTestMaxValues(scrollBar, 16, 17);
        TextChunksAppender appender = new TextChunksAppender(
                spiedTextArea, new LinkedList<>(textChunks), scrollBar, 5);
        appender.adjustmentValueChanged(null);
        appender.adjustmentValueChanged(null);

        verify(scrollBar, never()).removeAdjustmentListener(any());
        verify(spiedTextArea, times(2)).append(any());
        assertEquals("chunk1chunk2", textArea.getText());
    }

    @Test
    public void removesScrollListenerWhenNoMoreTextChunks() {
        JTextArea textArea = new JTextArea();
        JTextArea spiedTextArea = spy(textArea);
        List<String> textChunks = Arrays.asList("chunk1", "chunk2");
        JScrollBar scrollBar = mock(JScrollBar.class);
        setTestValues(scrollBar, 7, 17);
        setTestExtents(scrollBar, 5);
        setTestMaxValues(scrollBar, 16, 17);
        TextChunksAppender appender = new TextChunksAppender(
                spiedTextArea, new LinkedList<>(textChunks), scrollBar, 5);
        appender.adjustmentValueChanged(null);
        appender.adjustmentValueChanged(null);
        appender.adjustmentValueChanged(null);

        verify(scrollBar).removeAdjustmentListener(same(appender));
        verify(spiedTextArea, times(2)).append(any());
        assertEquals("chunk1chunk2", textArea.getText());
    }

    @Test
    public void doesNotAppendOnValueDecreased() {
        JTextArea textArea = new JTextArea();
        JTextArea spiedTextArea = spy(textArea);
        List<String> textChunks = Arrays.asList("chunk1", "chunk2", "chunk3");
        JScrollBar scrollBar = mock(JScrollBar.class);
        setTestValues(scrollBar, 7, 0, 20);
        setTestExtents(scrollBar, 5);
        setTestMaxValues(scrollBar, 16, 17);
        TextChunksAppender appender = new TextChunksAppender(
                spiedTextArea, new LinkedList<>(textChunks), scrollBar, 5);
        appender.adjustmentValueChanged(null);
        appender.adjustmentValueChanged(null);
        appender.adjustmentValueChanged(null);

        verify(scrollBar, never()).removeAdjustmentListener(any());
        verify(spiedTextArea, times(2)).append(any());
        assertEquals("chunk1chunk2", textArea.getText());
    }

    private static void setTestExtents(JScrollBar mockScrollBar, Integer e, Integer... es) {
        BoundedRangeModel model = mock(BoundedRangeModel.class);
        when(model.getExtent()).thenReturn(e, es);
        when(mockScrollBar.getModel()).thenReturn(model);
    }

    private static void setTestValues(JScrollBar mockScrollBar, Integer v, Integer... vs) {
        when(mockScrollBar.getValue()).thenReturn(v, vs);
    }

    private static void setTestMaxValues(JScrollBar mockScrollBar, Integer m, Integer... ms) {
        when(mockScrollBar.getMaximum()).thenReturn(m, ms);
    }
}