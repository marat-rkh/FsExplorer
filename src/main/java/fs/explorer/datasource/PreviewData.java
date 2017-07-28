package fs.explorer.datasource;

public class PreviewData {
    private final byte[] rawData;
    private final Type type;

    public PreviewData(byte[] rawData, Type type) {
        this.rawData = rawData;
        this.type = type;
    }

    public byte[] getBytes() { return rawData; }

    public Type getType() { return type; }

    public enum Type {
        IMAGE,
        TEXT
    }
}
