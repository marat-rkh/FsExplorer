package fs.explorer.providers.preview;

import javax.swing.*;
import java.util.Optional;

// @ThreadSafe
public class DefaultPreviewRenderer implements PreviewRenderer {
    public JTextArea renderText(TextPreviewData data) {
        if(data == null) {
            return null;
        }
        String text = data.getText();
        if(text == null) {
            return null;
        }
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        return textArea;
    }

    public JLabel renderImage(ImagePreviewData data) {
        if(data == null) {
            return null;
        }
        byte[] imageBytes = data.getImageBytes();
        if(imageBytes == null) {
            return null;
        }
        // TODO detect when image is corrupted
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        return new JLabel(imageIcon, JLabel.CENTER);
    }
}
