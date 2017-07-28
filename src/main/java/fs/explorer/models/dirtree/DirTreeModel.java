package fs.explorer.models.dirtree;

import fs.explorer.datasource.TreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class DirTreeModel {
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public DirTreeModel() {
        root = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("root"), /*allowsChildren*/true);
        treeModel = new DefaultTreeModel(root);
    }

    public DefaultTreeModel getInnerTreeModel() { return treeModel; }

    public DefaultMutableTreeNode getRoot() { return root; }

    public ExtTreeNodeData getExtTreeNodeData(DefaultMutableTreeNode node) {
        return (ExtTreeNodeData) node.getUserObject();
    }

    public void removeAllChildren(DefaultMutableTreeNode parent) {
        for(int i = 0; i < treeModel.getChildCount(parent); ++i) {
            treeModel.removeNodeFromParent(
                    (DefaultMutableTreeNode) treeModel.getChild(parent, i));
        }
    }

    public void addChild(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        treeModel.insertNodeInto(child, parent, parent.getChildCount());
    }

    public static DefaultMutableTreeNode nullDirNode(TreeNodeData nodeData) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                ExtTreeNodeData.nullNodeData(nodeData), /*allowsChildren*/true);
        DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("<loading...>"), /*allowsChildren*/false);
        node.add(loadingNode);
        return node;
    }

    public static DefaultMutableTreeNode fileNode(TreeNodeData nodeData) {
        return new DefaultMutableTreeNode(
                ExtTreeNodeData.loadedNodeData(nodeData), /*allowsChildren*/false);
    }
}
