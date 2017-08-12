package fs.explorer.providers.preview.renderers.image;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ImageLabelResizer extends SwingWorker<ImageIcon, Void> {
    private final JLabel label;
    private final ImageIconMaker imageIconMaker;
    private final Dimension newSize;
    private final long delay;
    private final TimeUnit timeUnit;

    ImageLabelResizer(
            JLabel label,
            ImageIconMaker imageIconMaker,
            Dimension newSize,
            long delay,
            TimeUnit timeUnit
    ) {
        this.label = label;
        this.imageIconMaker = imageIconMaker;
        this.newSize = newSize;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    @Override
    protected ImageIcon doInBackground() throws Exception {
        timeUnit.sleep(delay);
        return imageIconMaker.makeIcon(newSize);
    }

    @Override
    protected void done() {
        try {
            ImageIcon resizedIcon = get();
            label.setIcon(resizedIcon);
        } catch (InterruptedException e) {
            // doNothing
        } catch (ExecutionException e) {
            label.setIcon(null);
            label.setText("Failed to resize image");
        }
    }
}
