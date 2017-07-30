package fs.explorer.providers.preview;

import javax.swing.*;

public class PreviewRenderer {
    public static JComponent renderText(TextPreviewData data) {
        JTextArea textArea = new JTextArea(data.getText());
        textArea.setEditable(false);
        return textArea;
    }

    public static JComponent renderImage(ImagePreviewData data) {
        ImageIcon imageIcon = new ImageIcon(data.getImageBytes());
        return new JLabel(imageIcon, JLabel.CENTER);
    }
}
