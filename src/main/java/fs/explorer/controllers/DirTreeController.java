package fs.explorer.controllers;

import fs.explorer.providers.dirtree.AsyncFsDataProvider;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.dirtree.ExtTreeNodeData;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.providers.dirtree.TreeNodeLoader;
import fs.explorer.views.DirTreePane;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.function.Consumer;

public class DirTreeController {
    private final DirTreePane dirTreePane;
    private final DirTreeModel dirTreeModel;
    private final PreviewController previewController;
    private final StatusBarController statusBarController;

    private AsyncFsDataProvider treeDataProvider;

    private DefaultMutableTreeNode lastSelectedNode;

    private static final String DATA_PROVIDER_ERROR = "Failed to load data";
    private static final String INTERNAL_ERROR = "internal error";

    public DirTreeController(
            DirTreePane dirTreePane,
            DirTreeModel dirTreeModel,
            PreviewController previewController,
            StatusBarController statusBarController,
            AsyncFsDataProvider defaultDataProvider
    ) {
        this.dirTreePane = dirTreePane;
        this.dirTreeModel = dirTreeModel;
        this.previewController = previewController;
        this.statusBarController = statusBarController;
        this.treeDataProvider = defaultDataProvider;
    }

    public DirTreeController(
            DirTreePane dirTreePane,
            DirTreeModel dirTreeModel,
            PreviewController previewController,
            StatusBarController statusBarController
    ) {
        this(dirTreePane, dirTreeModel, previewController, statusBarController, null);
    }

    public AsyncFsDataProvider getTreeDataProvider() {
        return treeDataProvider;
    }

    public void resetDataProvider(AsyncFsDataProvider treeDataProvider) {
        if (treeDataProvider == null) {
            statusBarController.setErrorMessage(DATA_PROVIDER_ERROR, INTERNAL_ERROR);
            return;
        }
        this.treeDataProvider = treeDataProvider;
        DefaultMutableTreeNode root = dirTreeModel.getRoot();
        dirTreeModel.removeAllChildren(root);
        this.treeDataProvider.getTopNode(nodeData -> {
            dirTreeModel.addNullDirChild(root, nodeData);
            dirTreePane.expandPath(new TreePath(root.getPath()));
        });
    }

    public void handleTreeSelection(
            TreeSelectionEvent e, DefaultMutableTreeNode lastSelectedNode) {
        if (lastSelectedNode == null) {
            return;
        }
        this.lastSelectedNode = lastSelectedNode;
        ExtTreeNodeData extNodeData = DirTreeModel.getExtNodeData(lastSelectedNode);
        if (extNodeData.getType() == ExtTreeNodeData.Type.NORMAL) {
            previewController.updatePreview(extNodeData.getNodeData());
        }
    }

    public void handleTreeExpansion(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = getNode(event);
        if (node == null) {
            return;
        }
        ExtTreeNodeData extNodeData = DirTreeModel.getExtNodeData(node);
        if (extNodeData.getType() == ExtTreeNodeData.Type.NORMAL &&
                extNodeData.getStatus() == ExtTreeNodeData.Status.NULL) {
            reloadContents(node, extNodeData);
        }
    }

    public void handleTreeCollapse(TreeExpansionEvent event) {
        DefaultMutableTreeNode node = getNode(event);
        if (node == null) {
            return;
        }
        ExtTreeNodeData extNodeData = DirTreeModel.getExtNodeData(node);
        if (extNodeData.getType() == ExtTreeNodeData.Type.NORMAL &&
                extNodeData.getStatus() == ExtTreeNodeData.Status.LOADING) {
            extNodeData.setStatus(ExtTreeNodeData.Status.NULL);
            if (extNodeData.getLoader() != null) {
                extNodeData.getLoader().cancel(true);
            }
        }
    }

    public void reloadLastSelectedNode() {
        if (lastSelectedNode == null) {
            return;
        }
        ExtTreeNodeData extNodeData = DirTreeModel.getExtNodeData(lastSelectedNode);
        ExtTreeNodeData.Status status = extNodeData.getStatus();
        boolean reloadNeeded = extNodeData.getType() == ExtTreeNodeData.Type.NORMAL &&
                (status == ExtTreeNodeData.Status.NULL || status == ExtTreeNodeData.Status.LOADED);
        if (reloadNeeded) {
            TargetType targetType = extNodeData.getNodeData().getPathTargetType();
            // TODO support reload for zip archives
            if (targetType == TargetType.DIRECTORY) {
                reloadContents(lastSelectedNode, extNodeData);
            } else if (targetType == TargetType.FILE) {
                previewController.updatePreview(extNodeData.getNodeData());
            }
        }
    }

    private DefaultMutableTreeNode getNode(TreeExpansionEvent event) {
        TreePath treePath = event.getPath();
        if (treePath == null) {
            return null;
        }
        return (DefaultMutableTreeNode) treePath.getLastPathComponent();
    }

    private void reloadContents(DefaultMutableTreeNode node, ExtTreeNodeData extNodeData) {
        if (treeDataProvider == null) {
            statusBarController.setErrorMessage(DATA_PROVIDER_ERROR, INTERNAL_ERROR);
            return;
        }
        extNodeData.setStatus(ExtTreeNodeData.Status.LOADING);
        List<DefaultMutableTreeNode> children = dirTreeModel.getChildren(node);
        dirTreeModel.removeAllChildren(node);
        dirTreeModel.addFakeChild(node, "<loading...>");
        dirTreePane.expandPath(new TreePath(node.getPath()));
        stopLoadings(children);
        if (extNodeData.getLoader() != null) {
            extNodeData.getLoader().cancel(true);
        }
        TreeNodeLoader loader = treeDataProvider.getNodesFor(
                extNodeData.getNodeData(),
                contentsInserter(node, extNodeData),
                loadContentsErrorHandler(node, extNodeData)
        );
        extNodeData.setLoader(loader);
    }

    // TODO it is better to run this in a background thread
    private void stopLoadings(List<DefaultMutableTreeNode> removedNodes) {
        removedNodes.forEach(root ->
                DirTreeModel.breadthFirstEnumeration(root).forEach(child -> {
                    ExtTreeNodeData extNodeData = DirTreeModel.getExtNodeData(child);
                    TreeNodeLoader loader = extNodeData.getLoader();
                    if (loader != null) {
                        loader.cancel(true);
                    }
                    extNodeData.setLoader(null);
                })
        );
    }

    private Consumer<List<TreeNodeData>> contentsInserter(
            DefaultMutableTreeNode node, ExtTreeNodeData extNodeData) {
        return contents -> {
            if (!dirTreeModel.containsNode(node)) {
                return;
            }
            dirTreeModel.removeAllChildren(node);
            if (contents.isEmpty()) {
                dirTreeModel.addFakeChild(node, "<empty>");
            } else {
                for (TreeNodeData nodeData : contents) {
                    TargetType targetType = nodeData.getPathTargetType();
                    if (targetType == TargetType.DIRECTORY ||
                            targetType == TargetType.ZIP_ARCHIVE) {
                        dirTreeModel.addNullDirChild(node, nodeData);
                    } else {
                        dirTreeModel.addFileChild(node, nodeData);
                    }
                }
            }
            loadingFinished(node, extNodeData);
        };
    }

    private Consumer<String> loadContentsErrorHandler(
            DefaultMutableTreeNode node, ExtTreeNodeData extNodeData) {
        return errorMessage -> {
            if (!dirTreeModel.containsNode(node)) {
                return;
            }
            dirTreeModel.removeAllChildren(node);
            dirTreeModel.addFakeChild(node, "<error>");
            statusBarController.setErrorMessage(DATA_PROVIDER_ERROR, errorMessage);
            loadingFinished(node, extNodeData);
        };
    }

    private void loadingFinished(DefaultMutableTreeNode node, ExtTreeNodeData extNodeData) {
        extNodeData.setStatus(ExtTreeNodeData.Status.LOADED);
        extNodeData.setLoader(null);
        dirTreePane.expandPath(new TreePath(node.getPath()));
    }
}
