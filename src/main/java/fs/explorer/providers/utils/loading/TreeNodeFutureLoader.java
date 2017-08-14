package fs.explorer.providers.utils.loading;

import java.util.concurrent.Future;

public class TreeNodeFutureLoader implements TreeNodeLoader {
    private final Future<?> future;

    public TreeNodeFutureLoader(Future<?> future) {
        this.future = future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }
}
