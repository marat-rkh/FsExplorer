package fs.explorer.providers.preview.renderers.image;

import fs.explorer.TestResourceReader;
import fs.explorer.providers.preview.renderers.image.ImageIconMaker;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImageIconMakerTest implements TestResourceReader {
    @Test
    public void makesShrunkIconForWideImage()
            throws URISyntaxException, IOException, InterruptedException {
        BufferedImage image = ImageIO.read(new File(testFilePath("/imgs/wide.jpg")));
        ImageIconMaker iconMaker = new ImageIconMaker(image);
        ImageIcon icon = iconMaker.makeIcon(new Dimension(200, 200));
        assertEquals(200, icon.getIconWidth());
        assertTrue(icon.getIconHeight() < 200);
    }

    @Test
    public void keepsOriginalSizeWhenScreenIsBiggerForWideImage()
            throws URISyntaxException, IOException, InterruptedException {
        BufferedImage image = ImageIO.read(new File(testFilePath("/imgs/wide.jpg")));
        ImageIconMaker iconMaker = new ImageIconMaker(image);
        ImageIcon icon = iconMaker.makeIcon(new Dimension(800, 800));
        assertEquals(450, icon.getIconWidth());
        assertEquals(112, icon.getIconHeight());
    }

    @Test
    public void makesShrunkIconForTallImage()
            throws URISyntaxException, IOException, InterruptedException {
        BufferedImage image = ImageIO.read(new File(testFilePath("/imgs/tall.jpg")));
        ImageIconMaker iconMaker = new ImageIconMaker(image);
        ImageIcon icon = iconMaker.makeIcon(new Dimension(100, 100));
        assertEquals(100, icon.getIconHeight());
        assertTrue(icon.getIconWidth() < 100);
    }

    @Test
    public void keepsOriginalSizeWhenScreenIsBiggerForTallImage()
            throws URISyntaxException, IOException, InterruptedException {
        BufferedImage image = ImageIO.read(new File(testFilePath("/imgs/tall.jpg")));
        ImageIconMaker iconMaker = new ImageIconMaker(image);
        ImageIcon icon = iconMaker.makeIcon(new Dimension(400, 400));
        assertEquals(218, icon.getIconHeight());
        assertEquals(89, icon.getIconWidth());
    }
}