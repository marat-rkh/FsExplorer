package fs.explorer.providers.preview.renderers.text;

import fs.explorer.providers.preview.PreviewRenderer;
import fs.explorer.providers.preview.PreviewRenderingData;
import fs.explorer.utils.FileTypeInfo;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class TextPreviewRenderer implements PreviewRenderer {
    private final int textChunkSize;
    private final int maxPreLoadedChunks;

    private static final int DEFAULT_TEXT_CHUNK_SIZE = 5000;
    private static final int DEFAULT_MAX_PRE_LOADED_CHUNKS = 4;

    public TextPreviewRenderer() {
        textChunkSize = DEFAULT_TEXT_CHUNK_SIZE;
        maxPreLoadedChunks = DEFAULT_MAX_PRE_LOADED_CHUNKS;
    }

    public TextPreviewRenderer(int textChunkSize, int maxPreLoadedChunks) {
        this.textChunkSize = textChunkSize;
        this.maxPreLoadedChunks = maxPreLoadedChunks;
    }

    @Override
    public boolean canRenderForExtension(String fileExtension) {
        return FileTypeInfo.isTextFileExtension(fileExtension);
    }

    @Override
    public JComponent render(PreviewRenderingData data) throws InterruptedException {
        if (data == null) {
            return null;
        }
        byte[] bytes = data.getFileBytes();
        if (bytes == null) {
            return null;
        }
        return renderLazyTextArea(bytes);
    }

    private JScrollPane renderLazyTextArea(byte[] bytes)
            throws InterruptedException {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        List<String> textChunks = new ArrayList<>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             Reader reader = new InputStreamReader(bais);
             TextChunksReader chunksReader = new TextChunksReader(reader, textChunkSize)
        ) {
            int preLoadedChunks = 0;
            String chunk;
            while ((chunk = chunksReader.readChunk()) != null) {
                if (preLoadedChunks < maxPreLoadedChunks) {
                    textArea.append(chunk);
                    preLoadedChunks += 1;
                } else {
                    textChunks.add(chunk);
                }
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
            }
        } catch (IOException e) {
            return null;
        }
        return makeLazyTextArea(textArea, textChunks);
    }

    private JScrollPane makeLazyTextArea(JTextArea textArea, List<String> textChunksList) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        TextChunksAppender appender = new TextChunksAppender(
                textArea,
                new LinkedList<>(textChunksList),
                verticalScrollBar
        );
        verticalScrollBar.addAdjustmentListener(appender);
        return scrollPane;
    }
}
