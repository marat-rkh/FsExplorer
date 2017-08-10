package fs.explorer.providers.preview.renderers;

import org.junit.Test;

import javax.swing.*;
import java.util.Optional;

import static org.junit.Assert.*;

//public class DefaultPreviewRendererTest {
//    private final DefaultPreviewRenderer renderer = new DefaultPreviewRenderer();
//
//    @Test
//    public void rendersText() {
//        JTextArea preview = renderer.renderText(new TextPreviewData("some text"));
//        assertNotNull(preview);
//        assertEquals("some text", preview.getText());
//    }
//
//    @Test
//    public void doesNotRenderTextOnNullText() {
//        JTextArea preview = renderer.renderText(new TextPreviewData(null));
//        assertNull(preview);
//    }
//
//    @Test
//    public void doesNotRenderTextOnNullData() {
//        JTextArea preview = renderer.renderText(null);
//        assertNull(preview);
//    }
//
//    @Test
//    public void rendersImageFromAnyBytesData() {
//        JLabel preview = renderer.renderImage(new ImagePreviewData(new byte[1]));
//        assertNotNull(preview);
//        assertNotNull(preview.getIcon());
//    }
//
//    @Test
//    public void doesNotRenderImageOnNullImageBytes() {
//        JLabel preview = renderer.renderImage(new ImagePreviewData(null));
//        assertNull(preview);
//    }
//
//    @Test
//    public void doesNotRenderImageOnNullData() {
//        JLabel preview = renderer.renderImage(null);
//        assertNull(preview);
//    }
//}