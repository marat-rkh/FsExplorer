package fs.explorer.controllers;

import fs.explorer.models.dirtree.DirTreeModel;
import fs.explorer.providers.dirtree.AsyncFsDataProvider;
import fs.explorer.providers.dirtree.TreeNodeData;
import fs.explorer.providers.dirtree.path.FsPath;
import fs.explorer.providers.dirtree.path.TargetType;
import fs.explorer.providers.utils.loading.TreeNodeLoader;
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

import static fs.explorer.models.dirtree.ExtTreeNodeData.Status;
import static fs.explorer.models.dirtree.ExtTreeNodeData.Type;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
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
        assertTrue(isDirectoryNode(chs.get(0)));

        ArgumentCaptor<TreePath> captor = ArgumentCaptor.forClass(TreePath.class);
        verify(dirTreePane).expandPath(captor.capture());
        assertEquals(new TreePath(dirTreeModel.getRoot()), captor.getValue());
    }

    @Test
    public void doesNotResetDataProviderOnNull() {
        assertEquals(0, dirTreeModel.getChildren(dirTreeModel.getRoot()).size());
        dirTreeController.resetDataProvider(null);
        assertEquals(0, dirTreeModel.getChildren(dirTreeModel.getRoot()).size());
        verify(statusBarController).setErrorMessage(any(), any());
    }

    @Test
    public void callsPreviewUpdateOnDirSelection() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeController.handleTreeSelection(null, dir1);

        ArgumentCaptor<TreeNodeData> captor = ArgumentCaptor.forClass(TreeNodeData.class);
        verify(previewController).updatePreview(captor.capture());
        assertTrue(DirTreeModel.getExtNodeData(dir1).getNodeData() == captor.getValue());
    }

    @Test
    public void callsPreviewUpdateOnFileSelection() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode file1 = TestUtils.getChild(dirTreeModel, 0, 0);
        dirTreeController.handleTreeSelection(null, file1);

        ArgumentCaptor<TreeNodeData> captor = ArgumentCaptor.forClass(TreeNodeData.class);
        verify(previewController).updatePreview(captor.capture());
        assertTrue(DirTreeModel.getExtNodeData(file1).getNodeData() == captor.getValue());
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
        DirTreeModel.getExtNodeData(dir2).setStatus(Status.LOADING);
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

    @Test
    public void doesNothingOnExpansionIfNodeIsNotInModel1() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeModel.removeAllChildren(dirTreeModel.getRoot());
        DirTreeModel spiedModel = changeDirTreeModelToSpied();
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());
        verify(spiedModel).containsNode(dir2);
        verify(spiedModel, never()).removeAllChildren(any());
        verify(spiedModel, never()).addNullDirChild(any(), any());
        verify(spiedModel, never()).addFileChild(any(), any());
        verify(spiedModel, never()).addFakeChild(any(), any());
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNothingOnExpansionIfNodeIsNotInModel2() {
        setUpFailingDataProvider();
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeModel.removeAllChildren(dirTreeModel.getRoot());
        DirTreeModel spiedModel = changeDirTreeModelToSpied();
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());
        verify(spiedModel).containsNode(dir2);
        verify(spiedModel, never()).removeAllChildren(any());
        verify(spiedModel, never()).addNullDirChild(any(), any());
        verify(spiedModel, never()).addFileChild(any(), any());
        verify(spiedModel, never()).addFakeChild(any(), any());
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void doesNothingOnExpansionIfNodeIsNotInModel3() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeModel.removeAllChildren(dir1);
        DirTreeModel spiedModel = changeDirTreeModelToSpied();
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());
        verify(spiedModel).containsNode(dir2);
        verify(spiedModel, never()).removeAllChildren(any());
        verify(spiedModel, never()).addNullDirChild(any(), any());
        verify(spiedModel, never()).addFileChild(any(), any());
        verify(spiedModel, never()).addFakeChild(any(), any());
        verify(dirTreePane, never()).expandPath(any());
    }

    @Test
    public void createsNullDirNodesForLoadedZipArchives() {
        setupTestDirTreeModel();
        setUpTestProviderWithZipArchives();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());

        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir2);
        assertEquals(3, chs.size());
        assertEquals("newDir", getLabel(chs.get(0)));
        assertEquals("newArch1.zip", getLabel(chs.get(1)));
        assertTrue(chs.get(1).getAllowsChildren());
        assertEquals("newArch2.zip", getLabel(chs.get(2)));
        assertTrue(chs.get(2).getAllowsChildren());
    }

    @Test
    public void doesNotHandleTreeExpansionOnNullProvider() {
        setupTestDirTreeModel();
        setUpNullProvider();
        DefaultMutableTreeNode dir2 = TestUtils.getChild(dirTreeModel, 0, 1);
        dirTreeController.handleTreeExpansion(expansionEvent(dir2));

        assertEquals(Status.NULL, getStatus(dir2));
        checkTestDirTreeModelNotChanged();
        verify(dirTreePane, never()).expandPath(any());
        verify(statusBarController).setErrorMessage(any(), any());
    }

    @Test
    public void reloadsLastSelectedNullDirNode() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeController.handleTreeSelection(null, dir1);
        dirTreeController.reloadLastSelectedNode();

        verify(dirTreeController.getTreeDataProvider()).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, getStatus(dir1));
        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir1);
        assertEquals(3, chs.size());
        assertEquals("newDir1", getLabel(chs.get(0)));
        assertEquals("newDir2", getLabel(chs.get(1)));
        assertEquals("newFile1", getLabel(chs.get(2)));
    }

    @Test
    public void reloadsLastSelectedAlreadyLoadedDirNode() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode dir1 = TestUtils.getChild(dirTreeModel, 0);
        dirTreeController.handleTreeSelection(null, dir1);
        dirTreeController.reloadLastSelectedNode();
        dirTreeController.reloadLastSelectedNode();

        verify(dirTreeController.getTreeDataProvider(), times(2)).getNodesFor(any(), any(), any());

        assertEquals(Status.LOADED, getStatus(dir1));
        List<DefaultMutableTreeNode> chs = dirTreeModel.getChildren(dir1);
        assertEquals(3, chs.size());
        assertEquals("newDir1", getLabel(chs.get(0)));
        assertEquals("newDir2", getLabel(chs.get(1)));
        assertEquals("newFile1", getLabel(chs.get(2)));
    }

    @Test
    public void reloadsPreviewForLastSelectedFileNode() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode file1 = TestUtils.getChild(dirTreeModel, 0, 0);
        dirTreeController.handleTreeSelection(null, file1);
        dirTreeController.reloadLastSelectedNode();

        ArgumentCaptor<TreeNodeData> captor = ArgumentCaptor.forClass(TreeNodeData.class);
        verify(previewController, times(2)).updatePreview(captor.capture());
        assertTrue(DirTreeModel.getExtNodeData(file1).getNodeData() == captor.getValue());
    }

    @Test
    public void doesNotReloadLastSelectedNodeOnFakeNode() {
        setupTestDirTreeModel();
        DefaultMutableTreeNode fakeNode = TestUtils.getChild(dirTreeModel, 0, 1, 0);
        dirTreeController.handleTreeSelection(null, fakeNode);
        dirTreeController.reloadLastSelectedNode();

        verify(previewController, never()).updatePreview(any());
        verify(dirTreeController.getTreeDataProvider(), never()).getNodesFor(any(), any(), any());
        assertNotEquals(Status.LOADING, getStatus(fakeNode));
    }

    // TODO test loaders handling (removing on operations completion)

    private AsyncFsDataProvider makeTestDataProvider() {
        TestDataProvider provider = spy(new TestDataProvider());
        provider.setTestTopNode(nodeData("/", TargetType.DIRECTORY));
        provider.setTestNodes(Arrays.asList(
                nodeData("newDir1", TargetType.DIRECTORY),
                nodeData("newDir2", TargetType.DIRECTORY),
                nodeData("newFile1", TargetType.FILE)
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
        FailingDataProvider provider = spy(new FailingDataProvider());
        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                provider
        );
    }

    private void setUpTestProviderWithZipArchives() {
        TestDataProvider provider = spy(new TestDataProvider());
        provider.setTestNodes(Arrays.asList(
                nodeData("newDir", TargetType.DIRECTORY),
                nodeData("newArch1.zip", TargetType.ZIP_ARCHIVE),
                nodeData("newArch2.zip", TargetType.ZIP_ARCHIVE)
        ));
        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                provider
        );
    }

    private void setUpNullProvider() {
        dirTreeController = new DirTreeController(
                dirTreePane,
                dirTreeModel,
                previewController,
                statusBarController,
                null
        );
    }

    private void setupTestDirTreeModel() {
        dirTreeModel.removeAllChildren(dirTreeModel.getRoot());
        DefaultMutableTreeNode dir1 = dirTreeModel.addNullDirChild(
                dirTreeModel.getRoot(), nodeData("dir1", TargetType.DIRECTORY));
        dirTreeModel.removeAllChildren(dir1);
        DirTreeModel.getExtNodeData(dir1).setStatus(Status.LOADED);
        dirTreeModel.addFileChild(dir1, nodeData("file1", TargetType.FILE));
        dirTreeModel.addNullDirChild(dir1, nodeData("dir2", TargetType.DIRECTORY));
    }

    private DirTreeModel changeDirTreeModelToSpied() {
        DirTreeModel spiedModel = spy(dirTreeModel);
        dirTreeController = new DirTreeController(
                dirTreePane,
                spiedModel,
                previewController,
                statusBarController,
                dirTreeController.getTreeDataProvider()
        );
        return spiedModel;
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

    private static TreeNodeData nodeData(String label, TargetType targetType) {
        return new TreeNodeData(label, new FsPath("", targetType, ""));
    }

    private TreeExpansionEvent expansionEvent(DefaultMutableTreeNode node) {
        TreePath path = mock(TreePath.class);
        when(path.getLastPathComponent()).thenReturn(node);
        TreeExpansionEvent event = mock(TreeExpansionEvent.class);
        when(event.getPath()).thenReturn(path);
        return event;
    }

    private String getLabel(DefaultMutableTreeNode node) {
        return DirTreeModel.getExtNodeData(node).getNodeData().getLabel();
    }

    private Type getType(DefaultMutableTreeNode node) {
        return DirTreeModel.getExtNodeData(node).getType();
    }

    private Status getStatus(DefaultMutableTreeNode node) {
        return DirTreeModel.getExtNodeData(node).getStatus();
    }

    private boolean isDirectoryNode(DefaultMutableTreeNode node) {
        return DirTreeModel.getExtNodeData(node).getNodeData().pathTargetIsDirectory();
    }

    private static class TestDataProvider implements AsyncFsDataProvider {
        private TreeNodeData testTopNode;
        private List<TreeNodeData> testNodes;

        @Override
        public void getTopNode(Consumer<TreeNodeData> onComplete) {
            onComplete.accept(testTopNode);
        }

        @Override
        public TreeNodeLoader getNodesFor(
                TreeNodeData node,
                Consumer<List<TreeNodeData>> onComplete,
                Consumer<String> onFail
        ) {
            onComplete.accept(testNodes);
            return null;
        }

        void setTestTopNode(TreeNodeData testTopNode) {
            this.testTopNode = testTopNode;
        }

        void setTestNodes(List<TreeNodeData> testNodes) {
            this.testNodes = testNodes;
        }
    }

    private static class FailingDataProvider implements AsyncFsDataProvider {
        @Override
        public void getTopNode(Consumer<TreeNodeData> onComplete) {
            onComplete.accept(null);
        }

        @Override
        public TreeNodeLoader getNodesFor(
                TreeNodeData node,
                Consumer<List<TreeNodeData>> onComplete,
                Consumer<String> onFail
        ) {
            onFail.accept(null);
            return null;
        }
    }
}