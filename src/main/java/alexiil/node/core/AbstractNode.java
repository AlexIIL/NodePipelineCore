/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import alexiil.node.core.NodeGraph.GraphConnection;

/** A simple, mutable node. Will automatically handle inputs and outputs for you, provided you handle cloning properly.
 * 
 * This class is NOT thread safe. */
public abstract class AbstractNode implements INode {
    private final Map<String, GraphConnection<?>> inputs, unmodifiableInputs;
    private final Map<String, GraphConnection<?>> outputs, unmodifiableOutputs;
    private final NodeGraph graph;
    private final NodeRegistry registry;
    private final String name, typeTag;

    /** Creates an {@link AbstractNode} that will be registered with a single registry.
     * 
     * @param registry The registry that this was (or will be) registered with.
     * @param typeTag The type tag that this was (or will be) registered with. */
    public AbstractNode(NodeRegistry registry, String typeTag) {
        this(registry, typeTag, null, "default-type");
    }

    /** Creates an {@link AbstractNode} that will be added to a graph and used for only that graph.
     * 
     * @param registry The registry that this was (or will be) registered with.
     * @param typeTag The type tag that this was (or will be) registered with.
     * @param graph The graph that this node has been added to.
     * @param name The graph-unique name that this will be identified with. */
    public AbstractNode(NodeRegistry registry, String typeTag, NodeGraph graph, String name) {
        this.graph = graph;
        this.registry = registry;
        this.name = name;
        this.typeTag = typeTag;
        inputs = Maps.newHashMap();
        outputs = Maps.newHashMap();
        unmodifiableInputs = Collections.unmodifiableMap(inputs);
        unmodifiableOutputs = Collections.unmodifiableMap(outputs);
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getTypeTag() {
        return typeTag;
    }

    @Override
    public final Map<String, GraphConnection<?>> getInputs() {
        return unmodifiableInputs;
    }

    @Override
    public final Map<String, GraphConnection<?>> getOutputs() {
        return unmodifiableOutputs;
    }

    @Override
    public abstract AbstractNode createCopy(NodeGraph graph, String name);

    @Override
    public final NodeGraph getGraph() {
        return graph;
    }

    @Override
    public final NodeRegistry getRegistry() {
        return registry;
    }

    public <I> GraphConnection<I> addInput(String name, Class<I> inputType) {
        if (graph == null) {
            inputs.put(name, null);
            return null;
        } else {
            GraphConnection<I> connection = graph.provideInputConnection(this, name, inputType);
            inputs.put(name, connection);
            return connection;
        }
    }

    public <O> GraphConnection<O> addOutput(String name, Class<O> outputType) {
        if (graph == null) {
            outputs.put(name, null);
            return null;
        } else {
            GraphConnection<O> connection = graph.provideOutputConnection(this, name, outputType);
            outputs.put(name, connection);
            return connection;
        }
    }

    protected <I> Supplier<I> getInputSupplier(String name) {
        @SuppressWarnings("unchecked")
        final GraphConnection<I> input = (GraphConnection<I>) inputs.get(name);
        return () -> input.pop();
    }

    /** Tells you that everything you need exists for all the inputs.
     * 
     * @return True if you pushed anything to any outputs. */
    protected abstract boolean computeNext();

    @Override
    public void askForElements() {
        // This implements a "simple" node- that requires exactly 1 of each input to make exactly 1 of each output.
        int required = 0;
        for (GraphConnection<?> out : outputs.values()) {
            required = Math.max(required, out.getRequestedElements());
        }
        // We don't actually need any elements- just ignore it then
        if (required == 0)
            return;

        for (GraphConnection<?> in : inputs.values()) {
            in.requestUpTo(required);
        }
    }

    /** @return True if you pushed anything to any outputs. */
    @Override
    public boolean computeIfCan() {
        for (GraphConnection<?> in : inputs.values()) {
            if (in.getRemainingElements() <= 0)
                return false;
        }
        System.out.println("State of " + name + " before computing");
        printState();

        boolean b = computeNext();

        System.out.println("State of " + name + " AFTER computing");
        printState();

        return b;
    }

    @SuppressWarnings("unchecked")
    protected <O> Consumer<O> getOutputConsumer(String name) {
        final GraphConnection<O> connection = (GraphConnection<O>) outputs.get(name);
        return connection == null ? null : connection::push;
    }

    // DEBUG
    public void printState() {
        System.out.println(" Inputs:");
        for (Entry<String, GraphConnection<?>> entry : inputs.entrySet()) {
            System.out.println(" - " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println(" Outputs:");
        for (Entry<String, GraphConnection<?>> entry : outputs.entrySet()) {
            System.out.println(" - " + entry.getKey() + " = " + entry.getValue());
        }
    }
}
