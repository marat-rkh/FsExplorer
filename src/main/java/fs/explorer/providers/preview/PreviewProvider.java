package fs.explorer.providers.preview;

import fs.explorer.providers.dirtree.TreeNodeData;

public interface PreviewProvider {
    void getPreview(TreeNodeData data, PreviewProgressHandler progressHandler);
}
