package fs.explorer.providers.preview;

import javax.swing.*;
import java.util.Optional;

public class PreviewRenderer {
    public static Optional<JComponent> renderText(TextPreviewData data) {
        if(data == null) {
            return Optional.empty();
        }
        String text = data.getText();
        if(text == null) {
            return Optional.empty();
        }
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        return Optional.of(textArea);
    }

    public static Optional<JComponent> renderImage(ImagePreviewData data) {
        if(data == null) {
            return Optional.empty();
        }
        byte[] imageBytes = data.getImageBytes();
        if(imageBytes == null) {
            return Optional.empty();
        }
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        JLabel iconLabel = new JLabel(imageIcon, JLabel.CENTER);
        return Optional.of(iconLabel);
    }
}
