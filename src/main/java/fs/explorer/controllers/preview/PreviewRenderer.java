package fs.explorer.controllers.preview;

import fs.explorer.datasource.PreviewData;

import javax.swing.*;

public class PreviewRenderer {
    public static JComponent render(PreviewData data) {
        if(data == null) {
            return new JLabel("<no preview>");
        }
        switch(data.getType()) {
            case TEXT: return new JLabel(new String(data.getBytes()));
            case IMAGE: {
                JLabel image = new JLabel();
                image.setIcon(new ImageIcon(data.getBytes()));
                return image;
            }
            default: return new JLabel("<no preview>");
        }
    }
}
