package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.path.FsPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FsManager {
    byte[] readFile(FsPath filePath) throws IOException;

    <R> R withFileStream(FsPath fsPath, IOFunction<InputStream, R> streamReader) throws IOException;

    List<FsPath> list(FsPath directoryPath) throws IOException;
}
