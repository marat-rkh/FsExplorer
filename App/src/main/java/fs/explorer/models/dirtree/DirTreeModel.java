package fs.explorer.models.dirtree;

import fs.explorer.providers.dirtree.TreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirTreeModel {
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public DirTreeModel() {
        root = new DefaultMutableTreeNode(ExtTreeNodeData.fakeNodeData("root"), true);
        treeModel = new DefaultTreeModel(root);
    }

    public DefaultTreeModel getInnerTreeModel() {
        return treeModel;
    }

    public DefaultMutableTreeNode getRoot() {
        return root;
    }

    public List<DefaultMutableTreeNode> getChildren(DefaultMutableTreeNode node) {
        int childCount = treeModel.getChildCount(node);
        List<DefaultMutableTreeNode> children = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; ++i) {
            children.add((DefaultMutableTreeNode) treeModel.getChild(node, i));
        }
        return children;
    }

    public boolean containsNode(DefaultMutableTreeNode node) {
        TreeNode[] nodes = node.getPath();
        if (nodes == null || nodes.length == 0) {
            return false;
        }
        TreeNode farthestParent = nodes[0];
        // we use reference equality intentionally here
        return farthestParent == root;
    }

    public void removeAllChildren(DefaultMutableTreeNode parent) {
        getChildren(parent).forEach(treeModel::removeNodeFromParent);
    }

    public DefaultMutableTreeNode addNullDirChild(
            DefaultMutableTreeNode parent,
            TreeNodeData nodeData
    ) {
        DefaultMutableTreeNode nullDirNode = new DefaultMutableTreeNode(
                ExtTreeNodeData.nullNodeData(nodeData), true);
        DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("<loading...>"), false);
        nullDirNode.add(loadingNode);
        treeModel.insertNodeInto(nullDirNode, parent, parent.getChildCount());
        return nullDirNode;
    }

    public DefaultMutableTreeNode addFileChild(
            DefaultMutableTreeNode parent,
            TreeNodeData nodeData
    ) {
        DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(
                ExtTreeNodeData.loadedNodeData(nodeData), false);
        treeModel.insertNodeInto(fileNode, parent, parent.getChildCount());
        return fileNode;
    }

    public void addFakeChild(DefaultMutableTreeNode parent, String label) {
        DefaultMutableTreeNode fakeNode = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData(label), false);
        treeModel.insertNodeInto(fakeNode, parent, parent.getChildCount());
    }

    // TODO these static methods should be embedded in DefaultMutableTreeNode
    // So, we need a wrapper for DefaultMutableTreeNode
    public static ExtTreeNodeData getExtNodeData(DefaultMutableTreeNode node) {
        return (ExtTreeNodeData) node.getUserObject();
    }

    /**
     * Enumeration returned by DefaultMutableTreeNode is a raw type.
     * We cast it safely as DirTreeModel uses only instances of DefaultMutableTreeNode.
     */
    @SuppressWarnings("unchecked")
    public static List<DefaultMutableTreeNode> breadthFirstEnumeration(
            DefaultMutableTreeNode node
    ) {
        return Collections.list(node.breadthFirstEnumeration());
    }
}
