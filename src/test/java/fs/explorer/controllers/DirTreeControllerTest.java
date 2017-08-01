package fs.explorer.controllers;

import fs.explorer.TestUtils;
import fs.explorer.providers.FsPath;
import fs.explorer.providers.TreeDataProvider;
import fs.explorer.providers.TreeNodeData;
import fs.explorer.models.dirtree.DirTreeModel;
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

    @Before
    public void setUpCommon() {
        dirTreePane = mock(DirTreePane.class);
        dirTreeModel = new DirTreeModel();
        previewController = mock(PreviewController.class);
        statusBarController = mock(StatusBarController.class);

        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                makeTestDataProvider()
        );
    }

    @Test
    public void resetsDataProvider() {
        dirTreeController.resetDataProvider(dirTreeController.getTreeDataProvider());

        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dirTreeModel.getRoot());
        assertEquals(1, chs.size());
        assertEquals("/", getLabel(chs.get(0)));
        assertTrue(getFsPath(chs.get(0)).isDirectory());

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

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, getStatus(dir2));
        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir2);
        assertEquals(3, chs.size());
        assertEquals("newDir1", getLabel(chs.get(0)));
        assertEquals("newDir2", getLabel(chs.get(1)));
        assertEquals("newFile1", getLabel(chs.get(2)));

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dir2.getPath()), captor.getValue());
    }

    @Test
    public void createsEmptyFakeNodeOnNullDirExpansion() {
        setUpEmptyListTestProvider();
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, getStatus(dir2));
        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir2);
        assertEquals(1, chs.size());
        assertEquals("<empty>", getLabel(chs.get(0)));
        assertEquals(Type.FAKE, getType(chs.get(0)));

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dir2.getPath()), captor.getValue());
    }

    @Test
    public void doesNotLoadContentsOnLoadedDirExpansion() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeController.handleTreeExpansion(expansionEvent(dir1));

        verify(dirTreeController.getTreeDataProvider(), never())
                .getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNotLoadContentsOnLoadingDirExpansion() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeModel.getExtNodeData(dir2).setStatus(Status.LOADING);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider(), never())
                .getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNotLoadContentsOnBadExpansionEvent1() {
        setupTestDirTreeModel();
        dirTreeController.handleTreeExpansion(expansionEvent(null));

        verify(dirTreeController.getTreeDataProvider(), never())
                .getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNotLoadContentsOnBadExpansionEvent2() {
        setupTestDirTreeModel();
        TreeExpansionEvent badEvent = mock(TreeExpansionEvent.class);
        when(badEvent.getPath()).thenReturn(null);
        dirTreeController.handleTreeExpansion(badEvent);

        verify(dirTreeController.getTreeDataProvider(), never())
                .getNodesFor(any(), any(), any());
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void createsErrorFakeNodeOnDataProviderFailure() {
        setUpFailingDataProvider();
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, getStatus(dir2));

        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir2);
        assertEquals(1, chs.size());
        assertEquals(Type.FAKE, getType(chs.get(0)));
        assertEquals("<error>", getLabel(chs.get(0)));

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dir2.getPath()), captor.getValue());
    }

    @Test
    public void showsErrorMessageOnDataProviderFailure() {
        setUpFailingDataProvider();
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(statusBarController).setErrorMessage(any(), any());
    }

    private TreeDataProvider makeTestDataProvider() {
        TestDataProvider provider = spy(new TestDataProvider());
        provider.setTestTopNode(nodeData("/", /*isDirectory*/true));
        provider.setTestNodes(Arrays.asList(
                nodeData("newDir1", /*isDirectory*/true),
                nodeData("newDir2", /*isDirectory*/true),
                nodeData("newFile1", /*isDirectory*/false)
        ));
        return provider;
    }

    private void setUpEmptyListTestProvider() {
        TestDataProvider provider = spy(new TestDataProvider());
        provider.setTestNodes(Collections.emptyList());
        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                provider
        );
    }

    private void setUpFailingDataProvider() {
        TreeDataProvider provider = spy(new FailingDataProvider());
        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                provider
        );
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
        assertEquals("dir1", getLabel(nodes.get(1)));
        assertEquals("file1", getLabel(nodes.get(2)));
        assertEquals("dir2", getLabel(nodes.get(3)));
        assertEquals(Type.FAKE, getType(nodes.get(4)));
    }

    private static TreeNodeData nodeData(String label, boolean isDirectory) {
        return new TreeNodeData(label, new FsPath("", isDirectory, ""));
    }

    private TreeExpansionEvent expansionEvent(DefaultMutableTreeNode node) {
        TreePath path = mock(TreePath.class);
        when(path.getLastPathComponent()).thenReturn(node);
        TreeExpansionEvent event = mock(TreeExpansionEvent.class);
        when(event.getPath()).thenReturn(path);
        return event;
    }

    // TODO consider moving this accessors to DirTreeModel
    private String getLabel(DefaultMutableTreeNode node) {
        return dirTreeModel.getExtNodeData(node).getNodeData().getLabel();
    }

    private Type getType(DefaultMutableTreeNode node) {
        return dirTreeModel.getExtNodeData(node).getType();
    }

    private Status getStatus(DefaultMutableTreeNode node) {
        return dirTreeModel.getExtNodeData(node).getStatus();
    }

    private FsPath getFsPath(DefaultMutableTreeNode node) {
        return dirTreeModel.getExtNodeData(node).getNodeData().getFsPath();
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

    private static class FailingDataProvider implements TreeDataProvider {
        @Override
        public void getTopNode(Consumer<TreeNodeData> onComplete) {
            onComplete.accept(null);
        }

        @Override
        public void getNodesFor(
                TreeNodeData node,
                Consumer<List<TreeNodeData>> onComplete,
                Consumer<String> onFail
        ) {
            onFail.accept(null);
        }
    }
}