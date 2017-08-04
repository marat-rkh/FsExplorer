package fs.explorer.controllers;

import fs.explorer.providers.dirtree.path.PathContainerUtils;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.preview.PreviewProvider;
import fs.explorer.utils.FileTypeInfo;
import fs.explorer.views.PreviewPane;

import javax.swing.*;

public class PreviewController {
    private final PreviewPane previewPane;
    private final PreviewProvider previewProvider;
    private final StatusBarController statusBarController;

    private static final String LOADING_PREVIEW = "Loading preview...";
    private static final String PREVIEW_LOADED = "Preview loaded";
    private static final String PREVIEW_FAILED = "Preview not loaded";
    private static final String INTERNAL_ERROR = "internal error";

    public PreviewController(
            PreviewPane previewPane,
            PreviewProvider previewProvider,
            StatusBarController statusBarController
    ) {
        this.previewPane = previewPane;
        this.previewProvider = previewProvider;
        this.statusBarController = statusBarController;
    }

    public void updatePreview(TreeNodeData nodeData) {
        if(nodeData == null) {
            statusBarController.setErrorMessage(PREVIEW_FAILED, INTERNAL_ERROR);
            previewPane.showDefaultPreview();
            return;
        }
        if(PathContainerUtils.isDirectoryPath(nodeData.getPath())) {
            return;
        }
        String lastComponent = PathContainerUtils.getPathLastComponent(nodeData.getPath());
        if (FileTypeInfo.isTextFile(lastComponent)) {
            statusBarController.setProgressMessage(LOADING_PREVIEW);
            previewProvider.getTextPreview(
                    nodeData,
                    preview -> handlePreview(lastComponent, preview),
                    this::handlePreviewError
            );
        } else if (FileTypeInfo.isImageFile(lastComponent)) {
            statusBarController.setProgressMessage(LOADING_PREVIEW);
            previewProvider.getImagePreview(
                    nodeData,
                    preview -> handlePreview(lastComponent, preview),
                    this::handlePreviewError
            );
        }
    }

    private void handlePreview(String path, JComponent preview) {
        previewPane.updatePreview(preview);
        statusBarController.setInfoMessage(PREVIEW_LOADED, path);
    }

    private void handlePreviewError(String errorMessage) {
        statusBarController.setErrorMessage(PREVIEW_FAILED, errorMessage);
        previewPane.showDefaultPreview();
    }
}
