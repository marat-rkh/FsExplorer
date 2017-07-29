package fs.explorer.datasource;

import fs.explorer.utils.OSInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LocalFilesProvider implements TreeDataProvider {
    private final Path topDir;

    private static final String DISK_READ_ERROR = "disk read error";

    public LocalFilesProvider() {
        if(OSInfo.isWindows()) {
            topDir = Paths.get("C:\\");
        } else if(OSInfo.isMac() || OSInfo.isUnix()) {
            topDir = Paths.get("/");
        } else {
            throw new IllegalStateException("Unsupported OS");
        }
    }

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        FsPath fsPath = new FsPath(topDir.toString(), /*isDirectory*/true);
        onComplete.accept(new TreeNodeData(topDir.toString(), fsPath));
    }

    @Override
    public void getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail) {
        FsPath nodeFsPath = node.getFsPath();
        if(!nodeFsPath.isDirectory()) {
            onFail.accept("");
        }
        try {
            Path path = Paths.get(nodeFsPath.getPath());
            List<TreeNodeData> childNodes = Files.list(path).map(p -> {
                String label = p.getFileName().toString();
                FsPath fsPath = new FsPath(p.toString(), Files.isDirectory(p));
                return new TreeNodeData(label, fsPath);
            }).collect(Collectors.toList());
            onComplete.accept(childNodes);
        } catch (InvalidPathException | IOException e) {
            onFail.accept(DISK_READ_ERROR);
        }
    }
}
