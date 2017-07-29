package fs.explorer.controllers;

import fs.explorer.controllers.preview.PreviewController;
import fs.explorer.datasource.TreeDataProvider;
import fs.explorer.datasource.TreeNodeData;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.dirtree.ExtTreeNodeData;
import fs.explorer.views.DirTreePane;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DirTreeController {
    private final DirTreePane dirTreePane;
    private final DirTreeModel dirTreeModel;
    private final PreviewController previewController;
    private TreeDataProvider treeDataProvider;

    public DirTreeController(
            DirTreePane dirTreePane,
            DirTreeModel dirTreeModel,
            PreviewController previewController,
            TreeDataProvider defaultDataProvider
    ) {
        this.dirTreePane = dirTreePane;
        this.dirTreeModel = dirTreeModel;
        this.previewController = previewController;
        this.treeDataProvider = defaultDataProvider;
    }

    public TreeDataProvider getTreeDataProvider() { return treeDataProvider; }

    public void resetDataProvider(TreeDataProvider treeDataProvider) {
        this.treeDataProvider = treeDataProvider;
        DefaultMutableTreeNode root = dirTreeModel.getRoot();
        dirTreeModel.removeAllChildren(root);
        treeDataProvider.getTopNode(nodeData -> {
            dirTreeModel.addNullDirChild(root, nodeData);
            dirTreePane.expandPath(new TreePath(root.getPath()));
        });
    }

    public void handleTreeSelection(
            TreeSelectionEvent e, DefaultMutableTreeNode lastSelectedNode) {
        if (lastSelectedNode == null) {
            return;
        }
        ExtTreeNodeData extNodeData = dirTreeModel.getExtNodeData(lastSelectedNode);
        if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL) {
            previewController.updatePreview(extNodeData.getNodeData());
        }
    }

    public void handleTreeExpansion(TreeExpansionEvent event) {
        TreePath treePath = event.getPath();
        if(treePath == null) {
            return;
        }
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) treePath.getLastPathComponent();
        if(node == null) {
            return;
        }
        ExtTreeNodeData extNodeData = dirTreeModel.getExtNodeData(node);
        if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL &&
                extNodeData.getStatus() == ExtTreeNodeData.Status.NULL) {
            loadContents(node, extNodeData);
        }
    }

    private void loadContents(DefaultMutableTreeNode node, ExtTreeNodeData extNodeData) {
        extNodeData.setStatus(ExtTreeNodeData.Status.LOADING);
        treeDataProvider.getNodesFor(extNodeData.getNodeData(), contents -> {
            dirTreeModel.removeAllChildren(node);
            if(contents.isEmpty()) {
                dirTreeModel.addFakeChild(node, "<empty>");
            } else {
                for(TreeNodeData nodeData : contents) {
                    if(nodeData.getFsPath().isDirectory()) {
                        dirTreeModel.addNullDirChild(node, nodeData);
                    } else {
                        dirTreeModel.addFileChild(node, nodeData);
                    }
                }
            }
            dirTreePane.expandPath(new TreePath(node.getPath()));
            extNodeData.setStatus(ExtTreeNodeData.Status.LOADED);
        });
    }
}
