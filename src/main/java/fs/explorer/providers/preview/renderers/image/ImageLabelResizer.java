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
        return makeResizedIcon();
    }

    @Override
    protected void done() {
        try {
            handleResizedIcon(get());
        } catch (InterruptedException e) {
            // doNothing
        } catch (ExecutionException e) {
            handleMakeIconError();
        }
    }

    ImageIcon makeResizedIcon() throws InterruptedException {
        timeUnit.sleep(delay);
        return imageIconMaker.makeIcon(newSize);
    }

    void handleResizedIcon(ImageIcon resizedIcon) {
        if(resizedIcon == null) {
            handleMakeIconError();
        } else {
            label.setIcon(resizedIcon);
        }
    }

    void handleMakeIconError() {
        label.setIcon(null);
        label.setText("Failed to resize image");
    }
}
