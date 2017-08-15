package fs.explorer.providers.dirtree;

import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.path.*;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is thread safe if instances of FsManager and ArchivesManager
 * used with it (passed to constructors or methods) are thread safe.
 */
public class DefaultFsDataProvider implements FsDataProvider {
    private final FsPath topDir;
    private final FsManager fsManager;
    private final ArchivesManager archivesManager;

    private static final String INTERNAL_ERROR = "internal error";
    private static final String DATA_READ_ERROR = "data read error";

    public DefaultFsDataProvider(
            FsPath topDir,
            FsManager fsManager,
            ArchivesManager archivesManager
    ) {
        this.topDir = topDir;
        this.fsManager = fsManager;
        this.archivesManager = archivesManager;
    }

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        onComplete.accept(new TreeNodeData(topDir.getLastComponent(), topDir));
    }

    @Override
    public void getNodesFor(
            TreeNodeData node,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    ) {
        if (node == null || node.getPath() == null) {
            onFail.accept(INTERNAL_ERROR);
            return;
        }
        try {
            PathContainer path = node.getPath();
            if (path.isFsPath()) {
                handleFsPath(path.asFsPath(), onComplete, onFail);
            } else if (path.isArchiveEntryPath()) {
                ArchiveEntryPath archiveEntryPath = path.asArchiveEntryPath();
                handleArchiveEntryPath(archiveEntryPath, onComplete, onFail);
            } else {
                onFail.accept(INTERNAL_ERROR);
            }
        } catch (InterruptedIOException e) {
            // do nothing
        } catch (IOException e) {
            onFail.accept(DATA_READ_ERROR + " - " + node.pathToString());
        }
    }

    private void handleFsPath(
            FsPath path,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    ) throws IOException {
        TargetType targetType = path.getTargetType();
        if (targetType == TargetType.DIRECTORY) {
            List<FsPath> entries = fsManager.list(path);
            if (entries == null) {
                onFail.accept(INTERNAL_ERROR);
                return;
            }
            List<TreeNodeData> data = entries.stream()
                    .map(DefaultFsDataProvider::toTreeNodeData)
                    .collect(Collectors.toList());
            onComplete.accept(groupAndSort(data));
        } else if (targetType == TargetType.ZIP_ARCHIVE) {
            archivesManager.addArchiveIfAbsent(path, fsManager);
            List<ArchiveEntryPath> entries = archivesManager.listArchive(path, fsManager);
            if (entries == null) {
                onFail.accept(INTERNAL_ERROR);
                return;
            }
            List<TreeNodeData> data = entries.stream()
                    .map(DefaultFsDataProvider::toTreeNodeData)
                    .collect(Collectors.toList());
            onComplete.accept(groupAndSort(data));
        } else {
            onFail.accept(INTERNAL_ERROR);
        }
    }

    private void handleArchiveEntryPath(
            ArchiveEntryPath path,
            Consumer<List<TreeNodeData>> onComplete,
            Consumer<String> onFail
    ) throws IOException {
        TargetType targetType = path.getTargetType();
        if (targetType == TargetType.DIRECTORY || targetType == TargetType.ZIP_ARCHIVE) {
            List<ArchiveEntryPath> entries = archivesManager.listSubEntry(path, fsManager);
            if (entries == null) {
                onFail.accept(INTERNAL_ERROR);
                return;
            }
            List<TreeNodeData> data = entries.stream()
                    .map(DefaultFsDataProvider::toTreeNodeData)
                    .collect(Collectors.toList());
            onComplete.accept(groupAndSort(data));
        } else {
            onFail.accept(INTERNAL_ERROR);
        }
    }

    private static TreeNodeData toTreeNodeData(FsPath path) {
        String label = path.getLastComponent();
        if (label.isEmpty()) {
            label = "?";
        }
        return new TreeNodeData(label, path);
    }

    private static TreeNodeData toTreeNodeData(ArchiveEntryPath path) {
        String label = path.getLastComponent();
        if (label.isEmpty()) {
            label = "?";
        }
        return new TreeNodeData(label, path);
    }

    private List<TreeNodeData> groupAndSort(List<TreeNodeData> data) {
        List<TreeNodeData> dirsData = new ArrayList<>();
        List<TreeNodeData> filesData = new ArrayList<>();
        for (TreeNodeData d : data) {
            if (d.pathTargetIsDirectory()) {
                dirsData.add(d);
            } else {
                filesData.add(d);
            }
        }
        dirsData.sort(Comparator.comparing(TreeNodeData::getLabel));
        filesData.sort(Comparator.comparing(TreeNodeData::getLabel));
        dirsData.addAll(filesData);
        return dirsData;
    }
}
