package fs.explorer.controllers;

import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.dirtree.DirTreeModel.DirTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

public class TestUtils {
    public static DirTreeNode getChild(DirTreeModel model, DirTreeNode parent, int... indices) {
        DirTreeNode node = parent;
        List<DirTreeNode> chs = null;
        for(int i : indices) {
            chs = model.getChildren(node);
            node = chs.get(i);
        }
        return node;
    }

    public static DirTreeNode getChild(DirTreeModel model, int... indices) {
        return getChild(model, model.getRoot(), indices);
    }
}
