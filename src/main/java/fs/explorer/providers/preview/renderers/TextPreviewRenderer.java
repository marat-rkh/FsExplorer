package fs.explorer.providers.preview.renderers;

import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderingData;
import fs.explorer.utils.FileTypeInfo;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextPreviewRenderer implements PreviewRenderer {
    private ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public boolean canRenderForExtension(String fileExtension) {
        return FileTypeInfo.isTextFileExtension(fileExtension);
    }

    @Override
    public JComponent render(PreviewRenderingData data) throws InterruptedException {
        if(data == null) {
            return null;
        }
        byte[] bytes = data.getFileBytes();
        if(bytes == null) {
            return null;
        }
        String text = new String(bytes);
        Future<JTextArea> task = executor.submit(() -> {
            JTextArea textArea = new JTextArea(text);
            textArea.setEditable(false);
            return textArea;
        });
        try {
            return task.get();
        } catch (ExecutionException e) {
            return null;
        }
    }
}
