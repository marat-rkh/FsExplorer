package fs.explorer.providers.preview.renderers.text;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

class TextChunksReader extends FilterReader {
    private final int chunkSize;

    TextChunksReader(Reader in, int chunkSize) {
        super(in);
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("chunk size must be > 0");
        }
        this.chunkSize = chunkSize;
    }

    String readChunk() throws IOException {
        StringBuilder builder = new StringBuilder();
        int remainingChars = chunkSize;
        int lastReadChar = -1;
        while (remainingChars != 0 && (lastReadChar = in.read()) != -1) {
            builder.append((char) lastReadChar);
            remainingChars -= 1;
        }
        if (lastReadChar != -1) {
            if (Character.isHighSurrogate((char) lastReadChar)) {
                lastReadChar = in.read();
                if (lastReadChar != -1) {
                    builder.append((char) lastReadChar);
                }
            }
        }
        return builder.length() == 0 ? null : builder.toString();
    }
}