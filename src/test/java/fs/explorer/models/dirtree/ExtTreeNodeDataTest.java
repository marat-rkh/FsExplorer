package fs.explorer.models.dirtree;

import fs.explorer.datasource.TreeNodeData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExtTreeNodeDataTest {
    @Test
    public void createsFakeNodeData() {
        ExtTreeNodeData data = ExtTreeNodeData.fakeNodeData("label");
        assertEquals(data.getType(), ExtTreeNodeData.Type.FAKE);
        assertEquals(data.getStatus(), ExtTreeNodeData.Status.LOADED);
        assertEquals(data.toString(), "label");
    }

    @Test
    public void createsNullNodeData() {
        TreeNodeData simpleData = mock(TreeNodeData.class);
        when(simpleData.toString()).thenReturn("simpleData");
        ExtTreeNodeData data = ExtTreeNodeData.nullNodeData(simpleData);
        assertEquals(data.getType(), ExtTreeNodeData.Type.NORMAL);
        assertEquals(data.getStatus(), ExtTreeNodeData.Status.NULL);
        assertEquals(data.toString(), "simpleData");
    }

    @Test
    public void createsLoadedNodeData() {
        TreeNodeData simpleData = mock(TreeNodeData.class);
        when(simpleData.toString()).thenReturn("simpleData");
        ExtTreeNodeData data = ExtTreeNodeData.loadedNodeData(simpleData);
        assertEquals(data.getType(), ExtTreeNodeData.Type.NORMAL);
        assertEquals(data.getStatus(), ExtTreeNodeData.Status.LOADED);
        assertEquals(data.toString(), "simpleData");
    }

    // TODO test toString as it is used by views
}