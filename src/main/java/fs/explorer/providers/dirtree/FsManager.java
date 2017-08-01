package fs.explorer.providers.dirtree;

import java.io.IOException;
import java.util.List;

public interface FsManager {
    byte[] readFile(FsPath filePath) throws IOException;
    List<FsPath> list(FsPath directoryPath) throws IOException;
}
