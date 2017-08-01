package fs.explorer.providers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class LocalFsManager implements FsManager {
    @Override
    public byte[] readFile(FsPath fsPath) throws IOException {
        return Files.readAllBytes(Paths.get(fsPath.getPath()));
    }

    @Override
    public List<FsPath> list(FsPath directoryPath) throws IOException {
        if(!directoryPath.isDirectory()) {
            throw new IOException("not a directory");
        }
        try {
            return Files.list(Paths.get(directoryPath.getPath()))
                    .map(LocalFsManager::toFsPath)
                    .collect(Collectors.toList());
        } catch (InvalidPathException e) {
            throw new IOException("malformed directory path");
        }
    }

    private static FsPath toFsPath(Path path) {
        Path fileName = path.getFileName();
        String lastComponent = fileName == null ? "" : fileName.toString();
        return new FsPath(path.toString(), Files.isDirectory(path), lastComponent);
    }
}
