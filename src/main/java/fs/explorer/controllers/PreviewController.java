package fs.explorer.controllers;

import fs.explorer.providers.TreeNodeData;
import fs.explorer.providers.preview.PreviewProvider;
import fs.explorer.utils.FileTypeInfo;
import fs.explorer.views.PreviewPane;

public class PreviewController {
    private final PreviewPane previewPane;
    private final PreviewProvider previewProvider;

    public PreviewController(PreviewPane previewPane, PreviewProvider previewProvider) {
        this.previewPane = previewPane;
        this.previewProvider = previewProvider;
    }

    public void updatePreview(TreeNodeData nodeData) {
        String path = nodeData.getFsPath().getPath();
        if (FileTypeInfo.isTextFile(path)) {
            previewProvider.getTextPreview(
                    nodeData, previewPane::updatePreview, this::showErrorOnStatusBar);
        } else if (FileTypeInfo.isImageFile(path)) {
            previewProvider.getImagePreview(
                    nodeData, previewPane::updatePreview, this::showErrorOnStatusBar);
        }
    }

    private void showErrorOnStatusBar(String errorMessage) {

    }
}
