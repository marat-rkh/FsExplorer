package fs.explorer.datasource;

import fs.explorer.ftp.ConnectionInfo;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RemoteFilesProvider implements TreeDataProvider{
    private ConnectionInfo connectionInfo;

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    @Override
    public void getTopNode(Consumer<TreeNodeData> onComplete) {
        if(connectionInfo == null) {
            throw new IllegalStateException("connection info is not set");
        }
        FsPath fsPath = new FsPath(
                connectionInfo.getServer(),
                connectionInfo.getServer(),
                /*isDirectory*/true
        );
        onComplete.accept(new TreeNodeData(fsPath));
    }

    @Override
    public void getNodesFor(TreeNodeData node, Consumer<List<TreeNodeData>> onComplete) {
        onComplete.accept(Collections.emptyList());
    }
}
