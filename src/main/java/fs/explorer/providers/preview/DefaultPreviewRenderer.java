package fs.explorer.providers.preview;

import javax.swing.*;
import java.util.Comparator;
import java.util.concurrent.*;

// @ThreadSafe
public class DefaultPreviewRenderer implements PreviewRenderer {
    private ExecutorService executor = Executors.newCachedThreadPool();

    public JTextArea renderText(TextPreviewData data) throws InterruptedException {
        if(data == null) {
            return null;
        }
        String text = data.getText();
        if(text == null) {
            return null;
        }
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

    public JLabel renderImage(ImagePreviewData data) throws InterruptedException {
        if(data == null) {
            return null;
        }
        byte[] imageBytes = data.getImageBytes();
        if(imageBytes == null) {
            return null;
        }
        Future<ImageIcon> task = executor.submit(() -> {
            // TODO detect when image is corrupted
            return new ImageIcon(imageBytes);
        });
        try {
            ImageIcon imageIcon = task.get();
            return new JLabel(imageIcon, JLabel.CENTER);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
