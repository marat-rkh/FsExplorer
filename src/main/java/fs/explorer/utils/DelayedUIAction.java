package fs.explorer.utils;

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class DelayedUIAction {
    private final int delayMilliseconds;
    private SwingWorker<Void, Void> worker;

    public DelayedUIAction(int delayMilliseconds) {
        this.delayMilliseconds = delayMilliseconds;
    }

    public void execute(Runnable action) {
        if (worker != null) {
            worker.cancel(true);
        }
        if (delayMilliseconds <= 0) {
            action.run();
        } else {
            worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(delayMilliseconds);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        // if background was interrupted by cancel
                        // this get will throw InterruptedException
                        get();
                        action.run();
                    } catch (InterruptedException | CancellationException e) {
                        // doNothing
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }
}
