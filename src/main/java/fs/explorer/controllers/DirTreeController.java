package fs.explorer.controllers;

import fs.explorer.datasource.TreeDataProvider;
import fs.explorer.datasource.TreeNodeData;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.dirtree.ExtTreeNodeData;
import fs.explorer.models.preview.PreviewModel;
import fs.explorer.views.DirTreePane;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DirTreeController {
    private final DirTreePane dirTreePane;
    private final DirTreeModel dirTreeModel;
    private final PreviewModel previewModel;
    private TreeDataProvider treeDataProvider;

    public DirTreeController(
            DirTreePane dirTreePane,
            DirTreeModel dirTreeModel,
            PreviewModel previewModel
    ) {
        this.dirTreePane = dirTreePane;
        this.dirTreeModel = dirTreeModel;
        this.previewModel = previewModel;
    }

    public void resetDataProvider(TreeDataProvider treeDataProvider) {
        this.treeDataProvider = treeDataProvider;
        dirTreeModel.removeAllChildren(dirTreeModel.getRoot());
        treeDataProvider.getTopNode(nodeData -> {
            DefaultMutableTreeNode newTop = DirTreeModel.nullDirNode(nodeData);
            dirTreeModel.addChild(dirTreeModel.getRoot(), newTop);
            dirTreePane.expandPath(new TreePath(dirTreeModel.getRoot().getPath()));
        });
    }

    public void handleTreeSelection(
            TreeSelectionEvent e, DefaultMutableTreeNode lastSelectedNode) {
        if (lastSelectedNode == null) {
            return;
        }
        ExtTreeNodeData extNodeData = dirTreeModel.getExtTreeNodeData(lastSelectedNode);
        if(extNodeData.getType() == ExtTreeNodeData.Type.NORMAL) {
            previewModel.updatePreview(extNodeData.getNodeData());
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
        ExtTreeNodeData extNodeData = dirTreeModel.getExtTreeNodeData(node);
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
                DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode(
                        ExtTreeNodeData.fakeNodeData("<empty>"), /*allowsChildren*/false);
                dirTreeModel.addChild(node, emptyNode);
            } else {
                for(TreeNodeData nodeData : contents) {
                    if(nodeData.getFsPath().isDirectory()) {
                        dirTreeModel.addChild(node, DirTreeModel.nullDirNode(nodeData));
                    } else {
                        dirTreeModel.addChild(node, DirTreeModel.fileNode(nodeData));
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
