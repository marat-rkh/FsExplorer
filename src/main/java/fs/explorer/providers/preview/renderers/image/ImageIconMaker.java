package fs.explorer.providers.preview.renderers.image;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class ImageIconMaker {
    private final BufferedImage originalImage;

    ImageIconMaker(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    ImageIcon makeIcon(Dimension size) throws InterruptedException {
        Image resized;
        if (size == null) {
            resized = originalImage;
        } else {
            if(originalImage.getHeight() > originalImage.getWidth()) {
                resized = originalImage.getScaledInstance(-1, size.height, Image.SCALE_DEFAULT);
            } else {
                resized = originalImage.getScaledInstance(size.width, -1, Image.SCALE_DEFAULT);
            }
        }
        ImageIcon icon = new ImageIcon(resized);
        // ImageIcon constructor can be interrupted but it does not throw
        // InterruptedException and does not restore interrupt status.
        // See: https://bugs.openjdk.java.net/browse/JDK-6421373
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
        return icon;
    }
}
