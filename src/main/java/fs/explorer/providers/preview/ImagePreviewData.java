package fs.explorer.providers.preview;

public class ImagePreviewData {
    private final byte[] imageBytes;

    public ImagePreviewData(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public byte[] getImageBytes() { return imageBytes; }
}
