package fs.explorer.views;

import fs.explorer.controllers.DirTreeController;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DirTreePane {
    private final JScrollPane scrollPane;
    private final JTree tree;
    private final EventsListener eventsListener;

    public DirTreePane(DefaultTreeModel treeModel) {
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        // TODO change to false
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        scrollPane = new JScrollPane(tree);
        eventsListener = new EventsListener(tree);
        tree.addTreeExpansionListener(eventsListener);
        tree.addTreeSelectionListener(eventsListener);
    }

    public void setController(DirTreeController controller) {
        this.eventsListener.setController(controller);
    }

    public JComponent asJComponent() { return scrollPane; }

    public void expandPath(TreePath treePath) {
        tree.expandPath(treePath);
    }

    private static class EventsListener
            implements TreeSelectionListener, TreeExpansionListener {
        private final JTree tree;
        private DirTreeController controller;

        private EventsListener(JTree tree) {
            this.tree = tree;
        }

        public void setController(DirTreeController controller) {
            this.controller = controller;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            if(controller == null) {
                return;
            }
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            controller.handleTreeSelection(e, node);
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            if(controller == null) {
                return;
            }
            controller.handleTreeExpansion(event);
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {}
    }
}
