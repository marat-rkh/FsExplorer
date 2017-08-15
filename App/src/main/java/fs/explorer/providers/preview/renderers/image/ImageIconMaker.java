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
        Dimension originalSize = new Dimension(originalImage.getWidth(), originalImage.getHeight());
        boolean resizeNotNeeded = size == null ||
                (originalSize.getHeight() <= size.height && originalSize.getWidth() <= size.width);
        if (resizeNotNeeded) {
            resized = originalImage;
        } else if (isNewAspectRationGreater(originalSize, size)) {
            resized = originalImage.getScaledInstance(-1, size.height, Image.SCALE_DEFAULT);
        } else {
            resized = originalImage.getScaledInstance(size.width, -1, Image.SCALE_DEFAULT);
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

    private boolean isNewAspectRationGreater(Dimension oldSize, Dimension newSize) {
        long newWidth = newSize.width;
        long newHeight = newSize.height;
        long oldWidth = oldSize.width;
        long oldHeight = oldSize.height;
        return newWidth * oldHeight > oldWidth * newHeight;
    }
}
