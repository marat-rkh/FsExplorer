package fs.explorer.providers.preview;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class PreviewRenderersService {
    public static List<PreviewRenderer> getRenderers() {
        ServiceLoader<PreviewRenderer> loader = ServiceLoader.load(PreviewRenderer.class);
        List<PreviewRenderer> renderers = new ArrayList<>();
        loader.iterator().forEachRemaining(renderers::add);
        return renderers;
    }
}
