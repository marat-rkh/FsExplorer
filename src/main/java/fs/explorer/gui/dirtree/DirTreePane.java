package fs.explorer.gui.dirtree;

import fs.explorer.datasource.TreeDataProvider;
import fs.explorer.datasource.TreeNodeData;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirTreePane implements TreeExpansionListener {
    private final JScrollPane scrollPane;
    private final JTree tree;
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;
    private TreeDataProvider treeDataProvider;

    public DirTreePane() {
        root = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("root"), /*allowsChildren*/true);
        treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.addTreeExpansionListener(this);
        scrollPane = new JScrollPane(tree);
    }

    public void resetDataProvider(TreeDataProvider treeDataProvider) {
        this.treeDataProvider = treeDataProvider;
        removeAllChildren(root);
        treeDataProvider.getTopNode(nodeData -> {
            DefaultMutableTreeNode newTop = nullDirNode(nodeData);
            addChild(root, newTop);
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
            ExtTreeNodeData extNodeData = (ExtTreeNodeData) node.getUserObject();
            if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL) {
                listener.valueChanged(extNodeData.getNodeData());
            }
        });
    }

    public JComponent asJComponent() { return scrollPane; }

    @Override
    public void treeExpanded(TreeExpansionEvent event) {
        TreePath treePath = event.getPath();
        if(treePath == null) {
            return;
        }
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) treePath.getLastPathComponent();
        if(node == null) {
            return;
        }
        ExtTreeNodeData extNodeData = (ExtTreeNodeData) node.getUserObject();
        if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL &&
                extNodeData.getStatus() == ExtTreeNodeData.Status.NULL) {
            loadContents(node, extNodeData);
        }
    }

    @Override
    public void treeCollapsed(TreeExpansionEvent event) {}

    private void removeAllChildren(DefaultMutableTreeNode parent) {
        for(int i = 0; i < treeModel.getChildCount(parent); ++i) {
            treeModel.removeNodeFromParent(
                    (DefaultMutableTreeNode) treeModel.getChild(parent, i));
        }
    }

    private void addChild(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
        treeModel.insertNodeInto(child, parent, parent.getChildCount());
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

    private void loadContents(DefaultMutableTreeNode node, ExtTreeNodeData extNodeData) {
        extNodeData.setStatus(ExtTreeNodeData.Status.LOADING);
        treeDataProvider.getNodesFor(extNodeData.getNodeData(), contents -> {
            removeAllChildren(node);
            if(contents.isEmpty()) {
                DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode(
                        ExtTreeNodeData.fakeNodeData("<empty>"), /*allowsChildren*/false);
                addChild(node, emptyNode);
            } else {
                for(TreeNodeData nodeData : contents) {
                    if(nodeData.getFsPath().isDirectory()) {
                        addChild(node, nullDirNode(nodeData));
                    } else {
                        addChild(node, fileNode(nodeData));
                    }
                }
            }
            tree.expandPath(new TreePath(node.getPath()));
            extNodeData.setStatus(ExtTreeNodeData.Status.LOADED);
        });
    }
}
