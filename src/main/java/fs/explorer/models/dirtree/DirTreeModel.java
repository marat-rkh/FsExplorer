package fs.explorer.models.dirtree;

import fs.explorer.providers.dirtree.TreeNodeData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DirTreeModel {
    private final DefaultTreeModel treeModel;
    private final DefaultMutableTreeNode root;

    public DirTreeModel() {
        root = makeInnerNode(ExtTreeNodeData.fakeNodeData("root"), true);
        treeModel = new DefaultTreeModel(root);
    }

    public DefaultTreeModel getInnerTreeModel() {
        return treeModel;
    }

    // TODO remove this method by making appropriate listeners
    // Listeners must convert DefaultMutableTreeNode to DirTreeNode
    public DirTreeNode fromInnerNode(DefaultMutableTreeNode innerNode) {
        return new DirTreeNode(innerNode);
    }

    public DirTreeNode getRoot() {
        return new DirTreeNode(root);
    }

    public List<DirTreeNode> getChildren(DirTreeNode node) {
        int childCount = treeModel.getChildCount(node.getInnerNode());
        List<DirTreeNode> children = new ArrayList<>(childCount);
        for (int i = 0; i < childCount; ++i) {
            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) treeModel.getChild(node.getInnerNode(), i);
            children.add(new DirTreeNode(child));
        }
        return children;
    }

    public boolean containsNode(DirTreeNode node) {
        TreeNode[] nodes = node.getInnerNode().getPath();
        if (nodes == null || nodes.length == 0) {
            return false;
        }
        TreeNode farthestParent = nodes[0];
        // we use reference equality intentionally here
        return farthestParent == root;
    }

    public void removeAllChildren(DirTreeNode parent) {
        getChildren(parent).forEach(ch ->
                treeModel.removeNodeFromParent(ch.getInnerNode())
        );
    }

    public DirTreeNode addNullDirChild(DirTreeNode parent, TreeNodeData nodeData) {
        DefaultMutableTreeNode nullDirNode = makeInnerNode(
                ExtTreeNodeData.nullNodeData(nodeData), true);
        DefaultMutableTreeNode loadingNode = makeInnerNode(
                ExtTreeNodeData.fakeNodeData("<loading...>"), false);
        nullDirNode.add(loadingNode);
        return addChild(parent, nullDirNode);
    }

    public DirTreeNode addFileChild(DirTreeNode parent, TreeNodeData nodeData) {
        DefaultMutableTreeNode fileNode = makeInnerNode(
                ExtTreeNodeData.loadedNodeData(nodeData), false);
        return addChild(parent, fileNode);
    }

    public DirTreeNode addFakeChild(DirTreeNode parent, String label) {
        DefaultMutableTreeNode fakeNode = makeInnerNode(
                ExtTreeNodeData.fakeNodeData(label), false);
        return addChild(parent, fakeNode);
    }

    private DirTreeNode addChild(DirTreeNode parent, DefaultMutableTreeNode innerChild) {
        DefaultMutableTreeNode innerParent = parent.getInnerNode();
        treeModel.insertNodeInto(innerChild, innerParent, innerParent.getChildCount());
        return new DirTreeNode(innerChild);
    }

    private DefaultMutableTreeNode makeInnerNode(ExtTreeNodeData data, boolean allowsChildren) {
        return new DefaultMutableTreeNode(data, allowsChildren);
    }

    public static class DirTreeNode {
        private final DefaultMutableTreeNode innerNode;

        private DirTreeNode(DefaultMutableTreeNode innerNode) {
            this.innerNode = innerNode;
        }

        // TODO this should be private
        public DefaultMutableTreeNode getInnerNode() {
            return innerNode;
        }

        public ExtTreeNodeData getExtTreeNodeData() {
            return (ExtTreeNodeData) innerNode.getUserObject();
        }

        // TODO test
        public TreePath getTreePath() {
            return new TreePath(innerNode.getPath());
        }

        public boolean getAllowsChildren() {
            return innerNode.getAllowsChildren();
        }

        /**
         * Node: enumeration returned by DefaultMutableTreeNode is a raw type.
         * We can safely cast it as DirTreeModel controls the actual type.
         */
        @SuppressWarnings("unchecked")
        public List<DirTreeNode> breadthFirstEnumeration() {
            List<DefaultMutableTreeNode> innerNodes = Collections.list(
                    innerNode.breadthFirstEnumeration());
            return innerNodes.stream().map(DirTreeNode::new).collect(Collectors.toList());
        }
    }
}
