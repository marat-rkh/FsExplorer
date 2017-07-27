package fs.explorer.datasource;

public class TreeNodeData {
    private final String label;
    private final Type type;

    public TreeNodeData(String label, Type type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() { return label; }

    public Type getType() { return type; }

    public enum Type {
        DIRECTORY,
        FILE
    }
}
