package fs.explorer.providers.preview.renderers.text;

import fs.explorer.TestResourceReader;
import fs.explorer.providers.preview.renderers.text.TextChunksReader;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TextChunksReaderTest implements TestResourceReader {
    @Test(expected = IllegalArgumentException.class)
    public void throwsOnZeroChunkSize() throws URISyntaxException, IOException {
        try (
                Reader reader = new FileReader(testFilePath("/texts/empty.txt"));
                TextChunksReader chunksReader = new TextChunksReader(reader, 0)
        ) {
            chunksReader.readChunk();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnNegativeChunkSize() throws URISyntaxException, IOException {
        try (
                Reader reader = new FileReader(testFilePath("/texts/empty.txt"));
                TextChunksReader chunksReader = new TextChunksReader(reader, -1)
        ) {
            chunksReader.readChunk();
        }
    }

    @Test
    public void readsNullChunkOnEmptyFile() throws URISyntaxException, IOException {
        try (
                Reader reader = new FileReader(testFilePath("/texts/empty.txt"));
                TextChunksReader chunksReader = new TextChunksReader(reader, 1)
        ) {
            assertNull(chunksReader.readChunk());
        }
    }

    @Test
    public void readsTwoChunks() throws URISyntaxException, IOException {
        try (
                Reader reader = new FileReader(testFilePath("/texts/english.txt"));
                TextChunksReader chunksReader = new TextChunksReader(reader, 71)
        ) {
            String fstChunk = chunksReader.readChunk();
            String fstExpected = "Take this kiss upon the brow!\n" +
                    "And, in parting from you now,\n" +
                    "Thus much l";
            assertEquals(fstExpected, fstChunk);
            String sndChunk = chunksReader.readChunk();
            String sndExpected = "et me avow--\n" +
                    "You are not wrong, who deem\n" +
                    "That my days have been a dream";
            assertEquals(sndExpected, sndChunk);
            assertNull(chunksReader.readChunk());
        }
    }

    @Test
    public void readsTwoUnequalChunks() throws URISyntaxException, IOException {
        try (
                Reader reader = new FileReader(testFilePath("/texts/english.txt"));
                TextChunksReader chunksReader = new TextChunksReader(reader, 111)
        ) {
            String fstChunk = chunksReader.readChunk();
            String fstExpected = "Take this kiss upon the brow!\n" +
                    "And, in parting from you now,\n" +
                    "Thus much let me avow--\n" +
                    "You are not wrong, who deem";
            assertEquals(fstExpected, fstChunk);
            String sndChunk = chunksReader.readChunk();
            String sndExpected = "\n" +
                    "That my days have been a dream";
            assertEquals(sndExpected, sndChunk);
            assertNull(chunksReader.readChunk());
        }
    }

    @Test
    public void readsWholeTextInOneChunk() throws URISyntaxException, IOException {
        try (
                Reader reader = new FileReader(testFilePath("/texts/english.txt"));
                TextChunksReader chunksReader = new TextChunksReader(reader, 500)
        ) {
            String fstChunk = chunksReader.readChunk();
            String fstExpected = "Take this kiss upon the brow!\n" +
                    "And, in parting from you now,\n" +
                    "Thus much let me avow--\n" +
                    "You are not wrong, who deem\n" +
                    "That my days have been a dream";
            assertEquals(fstExpected, fstChunk);
            assertNull(chunksReader.readChunk());
        }
    }

    @Test
    public void readsTillTheEndOfSurrogatePair() throws IOException {
        String testInput =
                "the letter '\uD801\uDC12' is from Deseret alphabet";
        byte[] utf32Bytes = testInput.getBytes(Charset.forName("UTF-32"));
        try (
                InputStream is = new ByteArrayInputStream(utf32Bytes);
                Reader reader = new InputStreamReader(is, Charset.forName("UTF-32"));
                TextChunksReader chunksReader = new TextChunksReader(reader, 13)
        ) {
            // the first chunk is 14 characters long
            assertEquals("the letter '\uD801\uDC12", chunksReader.readChunk());
            assertEquals("' is from Des", chunksReader.readChunk());
            assertEquals("eret alphabet", chunksReader.readChunk());
            assertNull(chunksReader.readChunk());
        }
    }
}