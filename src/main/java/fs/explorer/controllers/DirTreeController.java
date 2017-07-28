package fs.explorer.controllers;

import fs.explorer.model.dirtree.DirTreeModel;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DirTreeController {
    private final DirTreeModel dirTreeModel;

    public DirTreeController(DirTreeModel dirTreeModel) {
        this.dirTreeModel = dirTreeModel;
    }

    public void handleTreeSelection(
            TreeSelectionEvent e, DefaultMutableTreeNode lastSelectedNode) {
        if (lastSelectedNode == null) {
            return;
        }
        dirTreeModel.selectNode(lastSelectedNode);
    }

    public void handleTreeExpanded(TreeExpansionEvent event) {
        TreePath treePath = event.getPath();
        if(treePath == null) {
            return;
        }
        DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) treePath.getLastPathComponent();
        if(node == null) {
            return;
        }
        dirTreeModel.expandNode(node);
    }
}
