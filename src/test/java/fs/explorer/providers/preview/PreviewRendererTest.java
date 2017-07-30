package fs.explorer.providers.preview;

import org.junit.Test;

import javax.swing.*;
import java.util.Optional;

import static org.junit.Assert.*;

public class PreviewRendererTest {
    @Test
    public void rendersText() {
        Optional<JComponent> optPreview =
                PreviewRenderer.renderText(new TextPreviewData("some text"));
        assertTrue(optPreview.isPresent());
    }

    @Test
    public void doesNotRenderTextOnNullText() {
        Optional<JComponent> optPreview =
                PreviewRenderer.renderText(new TextPreviewData(null));
        assertFalse(optPreview.isPresent());
    }

    @Test
    public void doesNotRenderTextOnNullData() {
        Optional<JComponent> optPreview = PreviewRenderer.renderText(null);
        assertFalse(optPreview.isPresent());
    }

    @Test
    public void rendersImageFromAnyBytesData() {
        Optional<JComponent> optPreview =
                PreviewRenderer.renderImage(new ImagePreviewData(new byte[1]));
        assertTrue(optPreview.isPresent());
    }

    @Test
    public void doesNotRenderImageOnNullImageBytes() {
        Optional<JComponent> optPreview =
                PreviewRenderer.renderImage(new ImagePreviewData(null));
        assertFalse(optPreview.isPresent());
    }

    @Test
    public void doesNotRenderImageOnNullData() {
        Optional<JComponent> optPreview = PreviewRenderer.renderImage(null);
        assertFalse(optPreview.isPresent());
    }
}