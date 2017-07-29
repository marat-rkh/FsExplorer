package fs.explorer.controllers;

import fs.explorer.TestUtils;
import fs.explorer.controllers.preview.PreviewController;
import fs.explorer.providers.FsPath;
import fs.explorer.providers.TreeDataProvider;
import fs.explorer.providers.TreeNodeData;
import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.models.dirtree.ExtTreeNodeData;
import fs.explorer.views.DirTreePane;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import static fs.explorer.models.dirtree.ExtTreeNodeData.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DirTreeControllerTest {
    private DirTreeController dirTreeController;
    private DirTreePane dirTreePane;
    private DirTreeModel dirTreeModel;
    private PreviewController previewController;
    private StatusBarController statusBarController;
    private TestDataProvider treeDataProvider;

    @Before
    public void setUp() {
        dirTreePane = mock(DirTreePane.class);
        dirTreeModel = new DirTreeModel();
        previewController = mock(PreviewController.class);
        statusBarController = mock(StatusBarController.class);

        treeDataProvider = spy(new TestDataProvider());
        setupTestDataProvider();
        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                treeDataProvider
        );
    }

    @Test
    public void resetsDataProvider() {
        dirTreeController.resetDataProvider(treeDataProvider);

        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dirTreeModel.getRoot());
        assertEquals(1, chs.size());
        TreeNodeData nodeData = dirTreeModel.getExtNodeData(chs.get(0)).getNodeData();
        assertEquals("/", nodeData.getLabel());
        assertTrue(nodeData.getFsPath().isDirectory());

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dirTreeModel.getRoot()), captor.getValue());
    }

    @Test
    public void callsPreviewUpdateOnDirSelection() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeController.handleTreeSelection(null, dir1);

        ArgumentCaptor<TreeNodeData> captor = ArgumentCaptor.forClass(TreeNodeData.class);
        verify(previewController).updatePreview(captor.capture());
        assertEquals(dirTreeModel.getExtNodeData(dir1).getNodeData(), captor.getValue());
    }

    @Test
    public void callsPreviewUpdateOnFileSelection() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode file1 = TestUtils.getChild(dirTreeModel, 0, 0);
        dirTreeController.handleTreeSelection(null, file1);

        ArgumentCaptor<TreeNodeData> captor = ArgumentCaptor.forClass(TreeNodeData.class);
        verify(previewController).updatePreview(captor.capture());
        assertEquals(dirTreeModel.getExtNodeData(file1).getNodeData(), captor.getValue());
    }

    @Test
    public void doesNotCallPreviewUpdateOnFakeNodeSelection() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode fakeNode = TestUtils.getChild(dirTreeModel, 0, 1, 0);
        dirTreeController.handleTreeSelection(null, fakeNode);

        verify(previewController, never()).updatePreview(any());
    }

    @Test
    public void doesNotCallPreviewUpdateOnNullNodeSelection() {
        dirTreeController.handleTreeSelection(null, null);
        verify(previewController, never()).updatePreview(any());
    }

    @Test
    public void loadsContentsOnNullDirExpansion() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(treeDataProvider).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, dirTreeModel.getExtNodeData(dir2).getStatus());
        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir2);
        assertEquals(3, chs.size());
        assertEquals("newDir1", getNodeDataLabel(chs.get(0)));
        assertEquals("newDir2", getNodeDataLabel(chs.get(1)));
        assertEquals("newFile1", getNodeDataLabel(chs.get(2)));

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dir2.getPath()), captor.getValue());
    }

    @Test
    public void createsEmptyFakeNodeOnNullDirExpansion() {
        treeDataProvider.setTestNodes(Collections.emptyList());
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(treeDataProvider).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, dirTreeModel.getExtNodeData(dir2).getStatus());
        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir2);
        assertEquals(1, chs.size());
        ExtTreeNodeData childData = dirTreeModel.getExtNodeData(chs.get(0));
        assertEquals("<empty>", childData.getNodeData().getLabel());
        assertEquals(Type.FAKE, childData.getType());

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dir2.getPath()), captor.getValue());
    }

    @Test
    public void doesNotLoadContentsOnLoadedDirExpansion() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeController.handleTreeExpansion(expansionEvent(dir1));

        verify(treeDataProvider, never()).getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNotLoadContentsOnLoadingDirExpansion() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeModel.getExtNodeData(dir2).setStatus(Status.LOADING);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(treeDataProvider, never()).getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNotLoadContentsOnBadExpansionEvent1() {
        setupTestDirTreeModel();
        dirTreeController.handleTreeExpansion(expansionEvent(null));

        verify(treeDataProvider, never()).getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNotLoadContentsOnBadExpansionEvent2() {
        setupTestDirTreeModel();
        TreeExpansionEvent badEvent = mock(TreeExpansionEvent.class);
        when(badEvent.getPath()).thenReturn(null);
        dirTreeController.handleTreeExpansion(badEvent);

        verify(treeDataProvider, never()).getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    private void setupTestDataProvider() {
        treeDataProvider.setTestTopNode(nodeData("/", /*isDirectory*/true));
        treeDataProvider.setTestNodes(Arrays.asList(
                nodeData("newDir1", /*isDirectory*/true),
                nodeData("newDir2", /*isDirectory*/true),
                nodeData("newFile1", /*isDirectory*/false)
        ));
    }

    private void setupTestDirTreeModel() {
        dirTreeModel.removeAllChildren(dirTreeModel.getRoot());
        DefaultMutableTreeNode dir1 = dirTreeModel.addNullDirChild(
                dirTreeModel.getRoot(), nodeData("dir1", /*isDirectory*/true));
        dirTreeModel.removeAllChildren(dir1);
        dirTreeModel.getExtNodeData(dir1).setStatus(Status.LOADED);
        dirTreeModel.addFileChild(dir1, nodeData("file1", /*isDirectory*/false));
        dirTreeModel.addNullDirChild(dir1, nodeData("dir2", /*isDirectory*/true));
    }

    private void checkTestDirTreeModelNotChanged() {
        List<DefaultMutableTreeNode> nodes = TestUtils.getNodesInBFSOrder(dirTreeModel);
        assertEquals(5, nodes.size());
        assertTrue(dirTreeModel.getRoot() == nodes.get(0));
        assertEquals("dir1", getNodeDataLabel(nodes.get(1)));
        assertEquals("file1", getNodeDataLabel(nodes.get(2)));
        assertEquals("dir2", getNodeDataLabel(nodes.get(3)));
        assertEquals(Type.FAKE, dirTreeModel.getExtNodeData(nodes.get(4)).getType());
    }

    private static TreeNodeData nodeData(String label, boolean isDirectory) {
        return new TreeNodeData(label, new FsPath("", isDirectory));
    }

    private TreeExpansionEvent expansionEvent(DefaultMutableTreeNode node) {
        TreePath path = mock(TreePath.class);
        when(path.getLastPathComponent()).thenReturn(node);
        TreeExpansionEvent event = mock(TreeExpansionEvent.class);
        when(event.getPath()).thenReturn(path);
        return event;
    }

    private String getNodeDataLabel(DefaultMutableTreeNode node) {
        return dirTreeModel.getExtNodeData(node).getNodeData().getLabel();
    }

    private static class TestDataProvider implements TreeDataProvider {
        private TreeNodeData testTopNode;
        private List<TreeNodeData> testNodes;

        @Override
        public void getTopNode(Consumer<TreeNodeData> onComplete) {
            onComplete.accept(testTopNode);
        }

        @Override
        public void getNodesFor(
                TreeNodeData node,
                Consumer<List<TreeNodeData>> onComplete,
                Consumer<String> onFail
        ) {
            onComplete.accept(testNodes);
        }

        public void setTestTopNode(TreeNodeData testTopNode) {
            this.testTopNode = testTopNode;
        }

        public void setTestNodes(List<TreeNodeData> testNodes) {
            this.testNodes = testNodes;
        }
    }
}