package fs.explorer.providers.preview;

public class PreviewRenderingData {
    private final String fileName;
    // TODO consider using InputStream
    // Renderers may not require entire file contents
    // and this will decrease memory consumption
    private final byte[] fileBytes;
    private final PreviewContext previewContext;

    public PreviewRenderingData(String fileName, byte[] fileBytes, PreviewContext previewContext) {
        this.fileName = fileName;
        this.fileBytes = fileBytes;
        this.previewContext = previewContext;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public PreviewContext getPreviewContext() {
        return previewContext;
    }
}
