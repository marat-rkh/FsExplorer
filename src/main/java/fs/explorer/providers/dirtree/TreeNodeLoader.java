package fs.explorer.providers.dirtree;

import java.util.concurrent.Future;

public class TreeNodeLoader {
    private final Future<?> future;

    public TreeNodeLoader(Future<?> future) {
        this.future = future;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }
}
