package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.path.FsPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

// @ThreadSafe
public class LocalFsManager implements FsManager {
    @Override
    public byte[] readFile(FsPath fsPath) throws IOException {
        if(fsPath == null || fsPath.getPath() == null) {
            throw new IOException("bad file path");
        }
        return Files.readAllBytes(Paths.get(fsPath.getPath()));
    }

    @Override
    public List<FsPath> list(FsPath directoryPath) throws IOException {
        if(directoryPath == null) {
            throw new IOException("bad directory path");
        }
        if(!directoryPath.isDirectory()) {
            throw new IOException("not a directory");
        }
        String pathStr = directoryPath.getPath();
        if(pathStr == null) {
            throw new IOException("bad directory path");
        }
        try {
            return Files.list(Paths.get(pathStr))
                    .map(FsPath::fromPath)
                    .collect(Collectors.toList());
        } catch (InvalidPathException e) {
            throw new IOException("malformed directory path");
        }
    }
}
