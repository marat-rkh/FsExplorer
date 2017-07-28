package fs.explorer.datasource;

import fs.explorer.model.ftpdialog.FTPDialogData;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RemoteFilesProvider implements TreeDataProvider{
    private FTPDialogData FTPDialogData;

    public void setConnectionInfo(FTPDialogData FTPDialogData) {
        this.FTPDialogData = FTPDialogData;
    }

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        if(FTPDialogData == null) {
            throw new IllegalStateException("connection info is not set");
        }
        FsPath fsPath = new FsPath(
                FTPDialogData.getServer(),
                FTPDialogData.getServer(),
                /*isDirectory*/true
        );
        onComplete.accept(new TreeNodeData(fsPath));
    }

    @Override
    public void getNodesFor(TreeNodeData node, Consumer<List<TreeNodeData>> onComplete) {
        onComplete.accept(Collections.emptyList());
    }
}
