/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.util.Map;

import alexiil.node.core.NodeGraph.GraphConnection;

/** Represents an operation on inputs, providing a set number of outputs.
 * 
 * Subclasses are generally NOT thread safe.
 * 
 * @author AlexIIL */
public interface INode {
    /** Creates a copy of this node with all of its outputs disconnected from any inputs.
     * 
     * @param name The graph-unique name for this node. Can be a human-friendly name rather than a short programmatic
     *            name as space-saving writers (the ones expressed in {@link GraphReader}) do not save this more than
     *            once. */
    INode createCopy(NodeGraph graph, String name);

    /** @return A map (most likely immutable or unmodifiable) that contains all of the inputs this has. */
    Map<String, GraphConnection<?>> getInputs();

    /** @return A map (most likely immutable or unmodifiable) that contains all of the outputs this has. */
    Map<String, GraphConnection<?>> getOutputs();

    /** @return The graph that this node is used for. This will be null if this is not registered with a graph (for
     *         example is part of a registry.) If you call {@link #createCopy(NodeGraph, String)} with a non-null graph
     *         this MUST return that graph, and it must never change. */
    NodeGraph getGraph();

    /** @return The registry that more instances of this node can be created from. */
    NodeRegistry getRegistry();

    /** @return The registry-unique tag that identifies this for saving and loading. */
    String getTypeTag();

    /** Gets the name for this node- it is used for comments and linking nodes with JSON so it must be unique within the
     * graph */
    String getName();

    /** Computes the outputs of this node if it has all of the inputs
     * 
     * @return True if it pushed any values to any of its outputs. */
    boolean computeIfCan();

    /** Calls {@link IConnection#requestUpTo(int)} for every input the number of times it will need that input to output
     * what has been requested by the outputs. */
    void askForElements();
}
