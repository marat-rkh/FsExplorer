package fs.explorer.models.dirtree;

import fs.explorer.datasource.TreeDataProvider;
import fs.explorer.datasource.TreeNodeData;
import fs.explorer.views.DirTreePane;
import fs.explorer.models.preview.PreviewModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class DirTreeModel {
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;
    private DirTreePane dirTreePane;
    private TreeDataProvider treeDataProvider;
    private final PreviewModel previewModel;

    public DirTreeModel(PreviewModel previewModel) {
        root = new DefaultMutableTreeNode(
                ExtTreeNodeData.fakeNodeData("root"), /*allowsChildren*/true);
        treeModel = new DefaultTreeModel(root);
        this.previewModel = previewModel;
    }

    public DefaultTreeModel getInnerTreeModel() { return treeModel; }

    public void setDirTreePane(DirTreePane pane) { this.dirTreePane = pane; }

    public void resetDataProvider(TreeDataProvider treeDataProvider) {
        this.treeDataProvider = treeDataProvider;
        removeAllChildren(root);
        treeDataProvider.getTopNode(nodeData -> {
            DefaultMutableTreeNode newTop = nullDirNode(nodeData);
            addChild(root, newTop);
            if(dirTreePane != null) {
                dirTreePane.expandPath(new TreePath(root.getPath()));
            }
        });
    }

    public void selectNode(DefaultMutableTreeNode node) {
        ExtTreeNodeData extNodeData = (ExtTreeNodeData) node.getUserObject();
        if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL) {
            previewModel.updatePreview(extNodeData.getNodeData());
        }
    }

    public void expandNode(DefaultMutableTreeNode node) {
        ExtTreeNodeData extNodeData = (ExtTreeNodeData) node.getUserObject();
        if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL &&
                extNodeData.getStatus() == ExtTreeNodeData.Status.NULL) {
            loadContents(node, extNodeData);
        }
    }

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
            if(dirTreePane != null) {
                dirTreePane.expandPath(new TreePath(node.getPath()));
            }
            extNodeData.setStatus(ExtTreeNodeData.Status.LOADED);
        });
    }
}
