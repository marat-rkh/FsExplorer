package fs.explorer.providers.preview.renderers;

import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderingData;
import fs.explorer.utils.FileTypeInfo;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImagePreviewRenderer implements PreviewRenderer {
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public boolean canRenderForExtension(String fileExtension) {
        return FileTypeInfo.isImageFileExtension(fileExtension);
    }

    @Override
    public JComponent render(PreviewRenderingData data) throws InterruptedException {
        if(data == null) {
            return null;
        }
        byte[] imageBytes = data.getFileBytes();
        if(imageBytes == null) {
            return null;
        }
        Future<ImageIcon> task = executor.submit(() -> {
            // TODO detect when image is corrupted
            return new ImageIcon(imageBytes);
        });
        try {
            ImageIcon imageIcon = task.get();
            return new JScrollPane(new JLabel(imageIcon, JLabel.CENTER));
        } catch (ExecutionException e) {
            return null;
        }
    }
}
