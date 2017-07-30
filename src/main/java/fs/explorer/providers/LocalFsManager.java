package fs.explorer.providers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalFsManager implements FsManager {
    @Override
    public byte[] readFile(FsPath fsPath) throws IOException {
        return Files.readAllBytes(Paths.get(fsPath.getPath()));
    }
}
