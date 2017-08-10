package fs.explorer.providers.preview;

public class PreviewRenderingData {
    private final String fileName;
    // TODO consider using InputStream
    // Renderers may not require entire file contents
    // and this will decrease memory consumption
    private final byte[] fileBytes;

    public PreviewRenderingData(String fileName, byte[] fileBytes) {
        this.fileName = fileName;
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }
}
