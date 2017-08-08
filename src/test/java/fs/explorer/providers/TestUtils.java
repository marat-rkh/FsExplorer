package fs.explorer.providers;

import java.util.function.Consumer;

public class TestUtils {
    public static class DummyConsumer<T> implements Consumer<T> {
        @Override
        public void accept(T t) {}
    }
}
