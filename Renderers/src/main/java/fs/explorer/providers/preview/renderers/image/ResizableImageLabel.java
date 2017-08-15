package fs.explorer.providers.preview.renderers.image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.TimeUnit;

class ResizableImageLabel {
    private final JLabel label;

    private static final long RESIZE_START_DELAY_MILLISECONDS = 50;

    ResizableImageLabel(JLabel label, ImageIconMaker imageIconMaker) {
        this.label = label;
        this.label.addComponentListener(new LabelListener(label, imageIconMaker));
    }

    JLabel getLabel() {
        return label;
    }

    JComponent asJComponent() {
        return getLabel();
    }

    private static class LabelListener implements ComponentListener {
        private final JLabel targetLabel;
        private final ImageIconMaker imageIconMaker;
        private ImageLabelResizer lastResizer;

        LabelListener(JLabel targetLabel, ImageIconMaker imageIconMaker) {
            this.targetLabel = targetLabel;
            this.imageIconMaker = imageIconMaker;
        }

        @Override
        public void componentResized(ComponentEvent e) {
            Component component = e.getComponent();
            if (component == null) {
                return;
            }
            Dimension newSize = component.getSize();
            if (newSize == null) {
                return;
            }
            if (lastResizer != null) {
                lastResizer.cancel(true);
            }
            lastResizer = new ImageLabelResizer(
                    targetLabel,
                    imageIconMaker,
                    newSize,
                    RESIZE_START_DELAY_MILLISECONDS,
                    TimeUnit.MILLISECONDS
            );
            lastResizer.execute();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }
}
