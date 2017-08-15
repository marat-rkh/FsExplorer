package fs.explorer.providers.preview.renderers.image;

import fs.explorer.TestResourceReader;
import fs.explorer.providers.preview.PreviewRenderingData;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ImagePreviewRendererTest implements TestResourceReader {
    @Test
    public void doesNotRenderOnNullData() throws InterruptedException {
        ImagePreviewRenderer renderer = new ImagePreviewRenderer();
        assertNull(renderer.render(null));
    }

    @Test
    public void doesNotRenderOnNullDataBytes() throws InterruptedException {
        ImagePreviewRenderer renderer = new ImagePreviewRenderer();
        PreviewRenderingData data = new PreviewRenderingData("", null, null);
        assertNull(renderer.render(data));
    }

    @Test
    public void rendersResizableImageLabel()
            throws InterruptedException, URISyntaxException, IOException {
        ImagePreviewRenderer renderer = new ImagePreviewRenderer();
        byte[] imageBytes = Files.readAllBytes(Paths.get(testFilePath("/imgs/wide.jpg")));
        ResizableImageLabel resizableImage = renderer.renderResizableImage(
                imageBytes, new Dimension(100, 100));
        assertNotNull(resizableImage.getLabel());
        assertNotNull(resizableImage.getLabel().getIcon());
    }
}