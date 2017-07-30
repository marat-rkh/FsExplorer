package fs.explorer.providers;

import java.io.IOException;

public interface FsManager {
    byte[] readFile(FsPath filePath) throws IOException;
}
