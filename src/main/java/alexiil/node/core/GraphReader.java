/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GraphReader {
    private static final Map<Integer, SaveType> typeMap = new HashMap<>();

    public enum SaveType {
        /** Outputs a readable version of the graph. This is a compact variant that refers to graph elements by their
         * index previously specified. */
        READABLE_COMPACT(1, GraphReader::readBufferedStream, GraphReader::writeBufferedStream),
        /** Outputs the graph as very readable JSON text. This is a non-compact huge way of saving the graph, so it is
         * not recommended for anything other than debugging or small graphs. */
        READABLE_JSON(2, GraphReader::readJsonStream, GraphReader::writeJsonStream),
        /** Outputs the graph using as little space as possible- useful with passing over a network connection. This can
         * be optionally compressed for more space saving. */
        BYTES_COMPACT(0, GraphReader::readByteStream, GraphReader::writeByteStream, Flag.COMPRESSED);

        final int version;
        final SimpleReader reader;
        final SimpleWriter writer;
        final Set<Flag> compatibleFlags = new HashSet<>();

        private SaveType(int version, SimpleReader reader, SimpleWriter writer, Flag... compatibleFlags) {
            this.version = version;
            this.reader = reader;
            this.writer = writer;
            Arrays.stream(compatibleFlags).forEach(flag -> this.compatibleFlags.add(flag));
            typeMap.put(version, this);
        }
    }

    public interface SimpleReader {
        NodeGraph read(InputStream stream, int flags) throws IOException;
    }

    public interface SimpleWriter {
        void write(NodeGraph graph, OutputStream stream) throws IOException;
    }

    // Stored outside the Flag Enum because of java
    private static int maxFlag;

    public enum Flag {
        /** Compresses the resulting bytes to make them take up as little space as possible. This uses GZIP for
         * compression. This is only available with {@link SaveType#COMPACT}. */
        COMPRESSED(0);

        final int flag;

        private Flag(int flag) {
            this.flag = 1 >> flag;
            if (maxFlag <= flag)
                maxFlag = flag + 1;
        }

        boolean isActive(int flags) {
            return (flags & flag) == 1;
        }

        static boolean isValid(int flags) {
            return flags - (flags & maxFlag) == 0;
        }
    }

    /** Reads a node graph from a file given by an {@link Path}
     * 
     * @param path The path for the file to read from
     * @return The read graph
     * @throws IOException if anything goes wrong while reading from the file */
    public static NodeGraph readNodeGraph(Path path) throws IOException {
        SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
        InputStream stream = Channels.newInputStream(channel);
        try {
            return readNodeGraph(stream);
        } finally {
            stream.close();
        }
    }

    /** Reads a node graph from a given {@link InputStream}
     * 
     * @param input The stream to read the graph from
     * @return The read graph
     * @throws IOException if anything goes wrong while reading from the stream */
    public static NodeGraph readNodeGraph(InputStream input) throws IOException {
        // Header reading
        int flags = input.read();
        int version = input.read();

        // Header interpreting
        if (Flag.COMPRESSED.isActive(flags)) {
            // Replace the stream straight away with a decompressed one
            input = new GZIPInputStream(input);
            // Remove it because the resulting stream is NOT compressed
            flags -= Flag.COMPRESSED.flag;
        }

        if (!Flag.isValid(flags))
            throw new IOException("Found extra flags! (" + Integer.toBinaryString(flags) + ")");

        // Get the correct reader for the given version
        SaveType type = typeMap.get(version);
        if (type != null) {
            return type.reader.read(input, flags);
        }
        throw new IOException("Unknown version " + version);
    }

    /** Writes a node graph directly to a file with the specified {@link SaveType} and flags.
     * 
     * @param path The file path to write to
     * @param graph The graph to write out
     * @param type How the graph should be saved- look at {@link SaveType#BYTES_COMPACT} and
     *            {@link SaveType#READABLE_COMPACT} for more details
     * @param flags The flags to save with
     * @throws IOException if any IO exception occurred while writing to the stream
     * @throws IllegalArgumentException if any of the flags given are incompatable with the {@link SaveType}
     * @throws NullPointerException if any of the arguments are null
     * @implNote This will call {@link #writeNodeGraph(OutputStream, NodeGraph, SaveType, Flag...)} after creating a
     *           file (or erasing the old one). */
    public static void writeNodeGraph(Path path, NodeGraph graph, SaveType type, Flag... flags) throws IOException, IllegalArgumentException {
        OutputStream stream = Files.newOutputStream(path);
        try {
            writeNodeGraph(stream, graph, type, flags);
        } finally {
            stream.close();
        }
    }

    /** Writes a node graph directly to a stream with the specified {@link SaveType} and flags.
     * 
     * @param stream The stream to write to.
     * @param graph The graph to write out
     * @param type How the graph should be saved- look at {@link SaveType#BYTES_COMPACT} and
     *            {@link SaveType#READABLE_COMPACT} for more details
     * @param flags The flags to save with
     * @throws IOException if any IO exception occurred while writing to the stream
     * @throws IllegalArgumentException if any of the flags given are incompatable with the {@link SaveType}
     * @throws NullPointerException if any of the arguments are null */
    public static void writeNodeGraph(OutputStream stream, NodeGraph graph, SaveType type, Flag... flags) throws IOException,
            IllegalArgumentException {
        // Flag building
        int flagsInt = 0;
        for (Flag flag : flags) {
            if (!type.compatibleFlags.contains(flag)) {
                throw new IllegalArgumentException("Tried to use the flag " + flag + " with the save type " + type + "!");
            }
            flagsInt |= flag.flag;
        }
        // Header writing
        stream.write(flagsInt);
        stream.write(type.version);

        // Compression flag
        if (Flag.COMPRESSED.isActive(flagsInt)) {
            stream = new GZIPOutputStream(stream);
        }

        // Actual writing
        type.writer.write(graph, stream);
    }

    /*
     * Private methods that implement the actual reading and writing functionality
     */

    // VERSION 0- DIRECT BYTES

    /** Reads the input in as small a manor as possible- using bytes to represent classes and requires a node registry
     * to already exist to load the classes from. */
    private static NodeGraph readByteStream(InputStream stream, int flags) throws IOException {
        throw new IOException("I don't know how to read!");
    }

    private static void writeByteStream(NodeGraph graph, OutputStream stream) throws IOException {
        throw new IOException("I don't know how to write!");
    }

    // VERSION 1- COMPACT TEXT

    /** Reads the input in a manor that can be opened by a normal text file editor and understood by a human. (Quite a
     * lot of redundancy, good for debugging a graph itself.) */
    private static NodeGraph readBufferedStream(InputStream stream, int flags) throws IOException {
        throw new IOException("I don't know how to read!");
    }

    private static void writeBufferedStream(NodeGraph graph, OutputStream stream) throws IOException {
        throw new IOException("I don't know how to write!");
    }

    // VERSION 2- JSON TEXT
    private static NodeGraph readJsonStream(InputStream stream, int flags) throws IOException {
        try {
            return GraphJson.read(stream);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unknown class ", e);
        }
    }

    private static void writeJsonStream(NodeGraph graph, OutputStream stream) throws IOException {
        GraphJson.write(graph, stream);
    }
}
