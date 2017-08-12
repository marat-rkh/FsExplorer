package fs.explorer.providers.preview.renderers.image;

import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderingData;
import fs.explorer.providers.preview.renderers.image.ImageIconMaker;
import fs.explorer.providers.preview.renderers.image.ResizableImageLabel;
import fs.explorer.utils.FileTypeInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.ExecutionException;

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
        ResizableImageLabel resizableImage = renderResizableImage(imageBytes);
        return resizableImage == null ? null : resizableImage.asJComponent();
    }

    ResizableImageLabel renderResizableImage(byte[] imageBytes) throws InterruptedException {
        Image originalImage;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
            originalImage = ImageIO.read(bais);
        } catch (InterruptedIOException e) {
            throw new InterruptedException();
        } catch (IOException e) {
            return null;
        }
        if (originalImage == null) {
            return null;
        }
        ImageIconMaker imageIconMaker = new ImageIconMaker(originalImage);
        // TODO make initial scaling
        JLabel label = new JLabel(imageIconMaker.makeIcon(), JLabel.CENTER);
        return new ResizableImageLabel(label, imageIconMaker);
    }
}
