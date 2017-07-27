package fs.explorer.gui.dirtree;

import fs.explorer.datasource.TreeDataProvider;
import fs.explorer.datasource.TreeNodeData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirTreePane {
    private final JScrollPane scrollPane;
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;
    private TreeDataProvider treeDataProvider;

    public DirTreePane() {
        root = new DefaultMutableTreeNode("root", /*allowsChildren*/true);
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        scrollPane = new JScrollPane(tree);
    }

    public void resetDataProvider(TreeDataProvider treeDataProvider) {
        this.treeDataProvider = treeDataProvider;
        removeAllChildren(root);
        treeDataProvider.getTopNode(nodeData -> {
            DefaultMutableTreeNode newTop = lazyDirNode(nodeData);
            treeModel.insertNodeInto(newTop, root, root.getChildCount());
            tree.expandPath(new TreePath(root.getPath()));
        });
    }

    public void addTreeSelectionListener(DirTreeSelectionListener listener) {
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (node == null) {
                return;
            }
            TreeNodeData nodeData = (TreeNodeData) node.getUserObject();
            listener.valueChanged(nodeData);
        });
    }

    public JComponent asJComponent() { return scrollPane; }

    private void removeAllChildren(DefaultMutableTreeNode parent) {
        for(int i = 0; i < treeModel.getChildCount(parent); ++i) {
            treeModel.removeNodeFromParent(
                    (DefaultMutableTreeNode)treeModel.getChild(parent, i));
        }
    }

    private DefaultMutableTreeNode lazyDirNode(TreeNodeData nodeData) {
        DefaultMutableTreeNode node =
                new DefaultMutableTreeNode(nodeData, /*allowsChildren*/true);
        DefaultMutableTreeNode tmpNode =
                new DefaultMutableTreeNode("loading...", /*allowsChildren*/false);
        node.add(tmpNode);
        return node;
    }
}
