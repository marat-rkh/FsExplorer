package fs.explorer.providers.preview.renderers.image;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class ImageLabelResizerTest {
    private JLabel label;
    private ImageIconMaker imageIconMaker;

    @Before
    public void setUp() {
        label = mock(JLabel.class);
        imageIconMaker = mock(ImageIconMaker.class);
    }

    @Test
    public void makesIcon() throws Exception {
        ImageLabelResizer labelResizer = new ImageLabelResizer(
                label, imageIconMaker, null, 0, TimeUnit.SECONDS);
        labelResizer.makeResizedIcon();
        verify(imageIconMaker).makeIcon(any());
    }

    @Test
    public void handlesNullResizedIcon() throws Exception {
        ImageLabelResizer labelResizer = new ImageLabelResizer(
                label, imageIconMaker, null, 0, TimeUnit.SECONDS);
        labelResizer.handleResizedIcon(null);
        verify(label).setIcon(null);
        verify(label).setText(any());
    }

    @Test
    public void handlesResizedIcon() throws Exception {
        ImageLabelResizer labelResizer = new ImageLabelResizer(
                label, imageIconMaker, null, 0, TimeUnit.SECONDS);
        ImageIcon resizedIcon = new ImageIcon();
        labelResizer.handleResizedIcon(resizedIcon);
        verify(label).setIcon(same(resizedIcon));
        verify(label, never()).setText(any());
    }

    @Test
    public void handlesMakeIconError() throws Exception {
        ImageLabelResizer labelResizer = new ImageLabelResizer(
                label, imageIconMaker, null, 0, TimeUnit.SECONDS);
        labelResizer.handleMakeIconError();
        verify(label).setIcon(null);
        verify(label).setText(any());
    }
}