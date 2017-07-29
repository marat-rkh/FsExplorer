package fs.explorer.providers;

import fs.explorer.utils.OSInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
            return;
        }
        try {
            Path path = Paths.get(nodeFsPath.getPath());
            Map<Boolean, List<TreeNodeData>> data = Files.list(path)
                    .map(LocalFilesProvider::toTreeNodeData)
                    .collect(Collectors.partitioningBy(d -> d.getFsPath().isDirectory()));
            List<TreeNodeData> dirsData = data.get(true);
            List<TreeNodeData> filesData = data.get(false);
            dirsData.sort(Comparator.comparing(TreeNodeData::getLabel));
            filesData.sort(Comparator.comparing(TreeNodeData::getLabel));
            dirsData.addAll(filesData);
            onComplete.accept(dirsData);
        } catch (InvalidPathException | IOException e) {
            onFail.accept(DISK_READ_ERROR);
        }
    }

    private static TreeNodeData toTreeNodeData(Path path) {
        String label = path.getFileName().toString();
        FsPath fsPath = new FsPath(path.toString(), Files.isDirectory(path));
        return new TreeNodeData(label, fsPath);
    }
}
