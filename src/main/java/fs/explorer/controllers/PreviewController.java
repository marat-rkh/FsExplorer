package fs.explorer.controllers;

import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.preview.PreviewProvider;
import fs.explorer.utils.FileTypeInfo;
import fs.explorer.views.PreviewPane;

public class PreviewController {
    private final PreviewPane previewPane;
    private final PreviewProvider previewProvider;
    private final StatusBarController statusBarController;

    private static final String PREVIEW_FAILED = "Preview rendering failed";
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
        if(nodeData.getFsPath().isDirectory()) {
            return;
        }
        String path = nodeData.getFsPath().getPath();
        if (FileTypeInfo.isTextFile(path)) {
            previewProvider.getTextPreview(
                    nodeData, previewPane::updatePreview, this::handlePreviewError);
        } else if (FileTypeInfo.isImageFile(path)) {
            previewProvider.getImagePreview(
                    nodeData, previewPane::updatePreview, this::handlePreviewError);
        }
    }

    private void handlePreviewError(String errorMessage) {
        statusBarController.setErrorMessage(PREVIEW_FAILED, errorMessage);
        previewPane.showDefaultPreview();
    }
}
