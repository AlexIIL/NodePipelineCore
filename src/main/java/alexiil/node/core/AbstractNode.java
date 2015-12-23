/* Node Pipeline.
 *
 * Copyright (C) 2015 Alex Jones (AlexIIL)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; with version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, download
 * the contents of https://raw.githubusercontent.com/AlexIIL/NodePipelineCore/master/LICENSE */
package alexiil.node.core;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import alexiil.node.core.NodeGraph.GraphConnection;

/** A simple, mutable node. Will automatically handle inputs and outputs for you, provided you handle cloning
 * properly. */
public abstract class AbstractNode implements INode {
    private final Map<String, GraphConnection<?>> inputs, unmodifiableInputs;
    private final Map<String, GraphConnection<?>> outputs, unmodifiableOutputs;
    private final NodeGraph graph;

    // DEBUG!
    public String hasComputed = null;
    // DEBUG
    public String name;

    public AbstractNode() {
        this(null);
    }

    public AbstractNode(NodeGraph graph) {
        this.graph = graph;
        inputs = Maps.newHashMap();
        outputs = Maps.newHashMap();
        unmodifiableInputs = Collections.unmodifiableMap(inputs);
        unmodifiableOutputs = Collections.unmodifiableMap(outputs);
    }

    @Override
    public Map<String, GraphConnection<?>> getInputs() {
        return unmodifiableInputs;
    }

    @Override
    public Map<String, GraphConnection<?>> getOutputs() {
        return unmodifiableOutputs;
    }

    @Override
    public AbstractNode createCopy(NodeGraph graph) {
        throw new IllegalStateException("The subclass " + getClass() + " should override the createCopy method!");
    }

    @Override
    public NodeGraph getGraph() {
        return graph;
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
