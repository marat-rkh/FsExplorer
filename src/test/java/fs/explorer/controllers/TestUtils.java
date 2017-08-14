package fs.explorer.controllers;

import fs.explorer.models.dirtree.DirTreeModel;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

public class TestUtils {
    public static DefaultMutableTreeNode getChild(
            DirTreeModel model, DefaultMutableTreeNode parent, int... indices) {
        DefaultMutableTreeNode node = parent;
        List<DefaultMutableTreeNode> chs = null;
        for(int i : indices) {
            chs = model.getChildren(node);
            node = chs.get(i);
        }
        return node;
    }

    public static DefaultMutableTreeNode getChild(
            DirTreeModel model, int... indices) {
        return getChild(model, model.getRoot(), indices);
    }
}
