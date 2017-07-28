package fs.explorer.controllers;

import fs.explorer.models.dirtree.DirTreeModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirTreeControllerTest {
    private DirTreeController controller;
    private DirTreeModel model;

    @Before
    public void setUp() {
        model = mock(DirTreeModel.class);
        controller = new DirTreeController(model);
    }

    @Test
    public void updatesPreviewOnTreeSelection() throws Exception {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        controller.handleTreeSelection(null, node);

        ArgumentCaptor<DefaultMutableTreeNode> captor =
                ArgumentCaptor.forClass(DefaultMutableTreeNode.class);
        verify(model).selectNode(captor.capture());
        assertTrue(captor.getValue() == node);
    }

    @Test
    public void doesNotUpdatePreviewWhenNodeIsNull() throws Exception {
        controller.handleTreeSelection(null, null);
        verify(model, never()).selectNode(any());
    }

    @Test
    public void expandsModelNodeOnTreeExpansion() throws Exception {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        TreePath path = mock(TreePath.class);
        when(path.getLastPathComponent()).thenReturn(node);
        TreeExpansionEvent event = mock(TreeExpansionEvent.class);
        when(event.getPath()).thenReturn(path);

        controller.handleTreeExpansion(event);

        ArgumentCaptor<DefaultMutableTreeNode> captor =
                ArgumentCaptor.forClass(DefaultMutableTreeNode.class);
        verify(model).expandNode(captor.capture());
        assertTrue(captor.getValue() == node);
    }

    @Test
    public void doesNotExpandModelNodeWhenPathIsNull() throws Exception {
        TreeExpansionEvent event = mock(TreeExpansionEvent.class);
        when(event.getPath()).thenReturn(null);

        controller.handleTreeExpansion(event);

        verify(model, never()).expandNode(any());
    }

    @Test
    public void doesNotExpandModelNodeWhenNodeIsNull() throws Exception {
        TreePath path = mock(TreePath.class);
        when(path.getLastPathComponent()).thenReturn(null);
        TreeExpansionEvent event = mock(TreeExpansionEvent.class);
        when(event.getPath()).thenReturn(path);

        controller.handleTreeExpansion(event);

        verify(model, never()).expandNode(any());
    }
}