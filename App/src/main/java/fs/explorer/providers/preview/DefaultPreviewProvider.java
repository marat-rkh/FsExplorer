package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.FsManager;
import fs.explorer.providers.dirtree.archives.ArchivesManager;
import fs.explorer.providers.dirtree.path.ArchiveEntryPath;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.dirtree.path.PathContainer;
import fs.explorer.utils.FileTypeInfo;

import javax.swing.*;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;

/**
 * This class is thread safe if instances of FsManager, PreviewRenderer
 * and ArchivesManager used with it (passed to constructors or methods)
 * are thread safe.
 */
public class DefaultPreviewProvider implements PreviewProvider {
    private volatile FsManager fsManager;
    private final ArchivesManager archivesManager;
    private final List<PreviewRenderer> previewRenderers;

    private static final String FILE_READ_FAILED = "failed to read file";
    private static final String RENDERING_FAILED = "failed to create preview";
    private static final String INTERNAL_ERROR = "internal error";

    public DefaultPreviewProvider(
            FsManager fsManager,
            ArchivesManager archivesManager,
            List<PreviewRenderer> previewRenderers
    ) {
        this.fsManager = fsManager;
        this.archivesManager = archivesManager;
        this.previewRenderers = previewRenderers;
    }

    public void resetFsManager(FsManager fsManager) {
        this.fsManager = fsManager;
    }

    @Override
    public void getPreview(
            TreeNodeData data,
            PreviewContext context,
            PreviewProgressHandler progressHandler
    ) {
        if (data == null) {
            progressHandler.onError(INTERNAL_ERROR);
            return;
        }
        boolean canNotRender = data.pathTargetIsDirectory() ||
                previewRenderers == null ||
                previewRenderers.isEmpty();
        if (canNotRender) {
            progressHandler.onCanNotRenderer();
            return;
        }
        String fileName = data.getPathLastComponent();
        String fileExtension = FileTypeInfo.getExtension(fileName);
        PreviewRenderer renderer = null;
        for (PreviewRenderer r : previewRenderers) {
            if (r.canRenderForExtension(fileExtension)) {
                renderer = r;
                break;
            }
        }
        if (renderer == null) {
            progressHandler.onCanNotRenderer();
            return;
        }
        byte[] fileBytes;
        try {
            fileBytes = readContents(data);
        } catch (InterruptedIOException e) {
            return;
        } catch (IOException e) {
            fileBytes = null;
        }
        if (fileBytes == null) {
            progressHandler.onError(FILE_READ_FAILED);
            return;
        }
        try {
            PreviewRenderingData renderingData = new PreviewRenderingData(
                    fileName, fileBytes, context);
            JComponent preview = renderer.render(renderingData);
            if (preview != null) {
                progressHandler.onComplete(preview);
            } else {
                progressHandler.onError(RENDERING_FAILED);
            }
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    private byte[] readContents(TreeNodeData data) throws IOException {
        PathContainer path = data.getPath();
        if (path.isFsPath()) {
            FsPath dataFsPath = path.asFsPath();
            return fsManager.readFile(dataFsPath);
        } else if (path.isArchiveEntryPath()) {
            ArchiveEntryPath archiveEntryPath = path.asArchiveEntryPath();
            return archivesManager.readEntry(archiveEntryPath, fsManager);
        } else {
            return null;
        }
    }
}
