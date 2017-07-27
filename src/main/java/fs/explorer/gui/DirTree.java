package fs.explorer.gui;

import fs.explorer.datasource.TreeDataProvider;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirTree {
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;
    private TreeDataProvider treeDataProvider;

    public DirTree() {
        root = new DefaultMutableTreeNode("root", /*allowsChildren*/true);
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
    }

    public void resetDataProvider(TreeDataProvider treeDataProvider) {
        this.treeDataProvider = treeDataProvider;
        removeAllChildren(root);
        treeDataProvider.getTopNode(nodeData -> {
            DefaultMutableTreeNode newTop = lazyDirNode(nodeData.getLabel());
            treeModel.insertNodeInto(newTop, root, root.getChildCount());
            tree.expandPath(new TreePath(root.getPath()));
        });
    }

    public JComponent asJComponent() { return tree; }

    private void removeAllChildren(DefaultMutableTreeNode parent) {
        for(int i = 0; i < treeModel.getChildCount(parent); ++i) {
            treeModel.removeNodeFromParent(
                    (DefaultMutableTreeNode)treeModel.getChild(parent, i));
        }
    }

    private DefaultMutableTreeNode lazyDirNode(String name) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name, /*allowsChildren*/true);
        DefaultMutableTreeNode tmpNode =
                new DefaultMutableTreeNode("loading...", /*allowsChildren*/false);
        node.add(tmpNode);
        return node;
    }
}
