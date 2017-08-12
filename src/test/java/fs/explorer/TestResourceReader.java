package fs.explorer;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public interface TestResourceReader {
    default String testFilePath(String relativePath) throws URISyntaxException {
        return Paths.get(getClass().getResource(relativePath).toURI()).toString();
    }
}
