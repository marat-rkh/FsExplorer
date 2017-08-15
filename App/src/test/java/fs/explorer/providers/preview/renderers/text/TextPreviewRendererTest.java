package fs.explorer.providers.preview.renderers.text;

import fs.explorer.providers.preview.PreviewRenderingData;
import org.junit.Test;

import java.util.Arrays;
import java.util.Queue;

import static org.junit.Assert.*;

public class TextPreviewRendererTest {
    @Test(expected = IllegalArgumentException.class)
    public void failsOnNonPositiveTextChunkSize() throws InterruptedException {
        new TextPreviewRenderer(0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failsOnNegativeMaxPreLoadedChunks() throws InterruptedException {
        new TextPreviewRenderer(10, -1);
    }

    @Test
    public void doesNotRenderOnNullData() throws InterruptedException {
        TextPreviewRenderer renderer = new TextPreviewRenderer();
        assertNull(renderer.render(null));
    }

    @Test
    public void doesNotRenderOnNullDataBytes() throws InterruptedException {
        TextPreviewRenderer renderer = new TextPreviewRenderer();
        PreviewRenderingData data = new PreviewRenderingData("", null, null);
        assertNull(renderer.render(data));
    }

    @Test
    public void rendersLazyTextArea() throws InterruptedException {
        TextPreviewRenderer renderer = new TextPreviewRenderer(2, 2);
        String text = "some text";
        LazyScrollableTextArea lazyTextArea = renderer.renderLazyTextArea(text.getBytes());
        assertNotNull(lazyTextArea);
        String preLoadedText = lazyTextArea.getTextArea().getText();
        assertEquals("some", preLoadedText);
        Queue<String> textToLoad = lazyTextArea.getTextChunks();
        assertEquals(Arrays.asList(" t", "ex", "t"), textToLoad);
    }

    @Test
    public void rendersLazyTextAreaWithNoTextToLoad1() throws InterruptedException {
        TextPreviewRenderer renderer = new TextPreviewRenderer(10, 2);
        String text = "some text";
        LazyScrollableTextArea lazyTextArea = renderer.renderLazyTextArea(text.getBytes());
        assertNotNull(lazyTextArea);
        String preLoadedText = lazyTextArea.getTextArea().getText();
        assertEquals("some text", preLoadedText);
        Queue<String> textToLoad = lazyTextArea.getTextChunks();
        assertTrue(textToLoad.isEmpty());
    }

    @Test
    public void rendersLazyTextAreaWithNoTextToLoad2() throws InterruptedException {
        TextPreviewRenderer renderer = new TextPreviewRenderer(2, 6);
        String text = "some text";
        LazyScrollableTextArea lazyTextArea = renderer.renderLazyTextArea(text.getBytes());
        assertNotNull(lazyTextArea);
        String preLoadedText = lazyTextArea.getTextArea().getText();
        assertEquals("some text", preLoadedText);
        Queue<String> textToLoad = lazyTextArea.getTextChunks();
        assertTrue(textToLoad.isEmpty());
    }

    @Test
    public void rendersEmptyLazyTextAreaOnEmptyTextBytes() throws InterruptedException {
        TextPreviewRenderer renderer = new TextPreviewRenderer(2, 6);
        String text = "";
        LazyScrollableTextArea lazyTextArea = renderer.renderLazyTextArea(text.getBytes());
        assertNotNull(lazyTextArea);
        String preLoadedText = lazyTextArea.getTextArea().getText();
        assertEquals("", preLoadedText);
        Queue<String> textToLoad = lazyTextArea.getTextChunks();
        assertTrue(textToLoad.isEmpty());
    }
}