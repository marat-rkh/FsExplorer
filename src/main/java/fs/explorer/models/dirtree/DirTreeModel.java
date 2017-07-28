package fs.explorer.models.dirtree;

import fs.explorer.datasource.TreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;

public class DirTreeModel {
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public DirTreeModel() {
        root = rootNode();
        treeModel = new DefaultTreeModel(root);
    }

    public DefaultTreeModel getInnerTreeModel() { return treeModel; }

    public DefaultMutableTreeNode getRoot() { return root; }

    public List<DefaultMutableTreeNode> getChildren(DefaultMutableTreeNode node) {
        int childCount = treeModel.getChildCount(node);
        List<DefaultMutableTreeNode> children = new ArrayList<>(childCount);
        for(int i = 0; i < childCount; ++i) {
            children.add((DefaultMutableTreeNode) treeModel.getChild(node, i));
        }
        return children;
    }

    public ExtTreeNodeData getExtNodeData(DefaultMutableTreeNode node) {
        return (ExtTreeNodeData) node.getUserObject();
    }

    public void removeAllChildren(DefaultMutableTreeNode parent) {
        getChildren(parent).forEach(treeModel::removeNodeFromParent);
    }

    public DefaultMutableTreeNode addNullDirChild(
            DefaultMutableTreeNode parent, TreeNodeData nodeData) {
        return addChild(parent, nullDirNode(nodeData));
    }

    public DefaultMutableTreeNode addFileChild(
            DefaultMutableTreeNode parent, TreeNodeData nodeData) {
        return addChild(parent, fileNode(nodeData));
    }

    public DefaultMutableTreeNode addFakeChild(
            DefaultMutableTreeNode parent, String label) {
        return addChild(parent, fakeNode(label));
    }

    private DefaultMutableTreeNode addChild(
            DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        treeModel.insertNodeInto(child, parent, parent.getChildCount());
        return child;
    }

    private DefaultMutableTreeNode nullDirNode(TreeNodeData nodeData) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(
                ExtTreeNodeData.nullNodeData(nodeData), /*allowsChildren*/true);
        DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("<loading...>"), /*allowsChildren*/false);
        node.add(loadingNode);
        return node;
    }

    private DefaultMutableTreeNode fileNode(TreeNodeData nodeData) {
        return new DefaultMutableTreeNode(
                ExtTreeNodeData.loadedNodeData(nodeData), /*allowsChildren*/false);
    }

    private DefaultMutableTreeNode fakeNode(String label) {
        return new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData(label), /*allowsChildren*/false);
    }

    private DefaultMutableTreeNode rootNode() {
        return new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("root"), /*allowsChildren*/true);
    }
}
