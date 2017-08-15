package fs.explorer.providers.preview.renderers.image;

import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderingData;
import fs.explorer.utils.FileTypeInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;

public class ImagePreviewRenderer implements PreviewRenderer {

    @Override
    public boolean canRenderForExtension(String fileExtension) {
        return FileTypeInfo.isImageFileExtension(fileExtension);
    }

    @Override
    public JComponent render(PreviewRenderingData data) throws InterruptedException {
        if (data == null) {
            return null;
        }
        byte[] imageBytes = data.getFileBytes();
        if (imageBytes == null) {
            return null;
        }
        Dimension preferredSize = data.getPreviewContext().getPreferredSize();
        ResizableImageLabel resizableImage = renderResizableImage(imageBytes, preferredSize);
        return resizableImage == null ? null : resizableImage.asJComponent();
    }

    ResizableImageLabel renderResizableImage(byte[] imageBytes, Dimension preferredSize)
            throws InterruptedException {
        BufferedImage originalImage;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            originalImage = ImageIO.read(bais);
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        } catch (IOException e) {
            return null;
        }
        if (originalImage == null) {
            return null;
        }
        ImageIconMaker imageIconMaker = new ImageIconMaker(originalImage);
        JLabel label = new JLabel(imageIconMaker.makeIcon(preferredSize), JLabel.CENTER);
        return new ResizableImageLabel(label, imageIconMaker);
    }
}
