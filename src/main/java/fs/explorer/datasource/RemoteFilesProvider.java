package fs.explorer.datasource;

import fs.explorer.ftp.ConnectionInfo;

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
        onComplete.accept(new TreeNodeData(
                connectionInfo.getServer(), TreeNodeData.Type.DIRECTORY));
    }
}