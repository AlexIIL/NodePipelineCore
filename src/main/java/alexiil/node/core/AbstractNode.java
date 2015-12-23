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

import java.util.*;
import java.util.Map.Entry;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/** A simple, mutable node. Will automatically handle inputs and outputs for you, provided you handle cloning
 * properly. */
public abstract class AbstractNode implements INode {
    private final Map<String, SimpleInput<?>> inputs, unmodifiableInputs;
    private final Map<String, SimpleOutput<?>> outputs, unmodifiableOutputs;

    private final Map<String, Consumer<?>> internalOutputs;

    // DEBUG!
    public String hasComputed = null;
    // DEBUG
    public String name;

    public AbstractNode() {
        inputs = Maps.newHashMap();
        outputs = Maps.newHashMap();
        unmodifiableInputs = Collections.unmodifiableMap(inputs);
        unmodifiableOutputs = Collections.unmodifiableMap(outputs);
        internalOutputs = Maps.newHashMap();
    }

    @Override
    public Map<String, ? extends IInput<?>> getInputs() {
        return unmodifiableInputs;
    }

    @Override
    public Map<String, ? extends IOutput<?>> getOutputs() {
        return unmodifiableOutputs;
    }

    @Override
    public AbstractNode createCopy() {
        throw new IllegalStateException("The subclass " + getClass() + " should override the createCopy method!");
    }

    public <I> void addInput(String name, Class<I> inputType) {
        SimpleInput<I> input = new SimpleInput<I>(inputType);
        inputs.put(name, input);
    }

    public <O> void addOutput(String name, Class<O> outputType) {
        SimpleOutput<O> output = new SimpleOutput<>(outputType);
        outputs.put(name, output);
        Consumer<O> consumer = val -> output.sendValue(val);
        internalOutputs.put(name, consumer);
    }

    protected <I> Supplier<I> getInputSupplier(String name) {
        @SuppressWarnings("unchecked")
        final SimpleInput<I> input = (SimpleInput<I>) inputs.get(name);
        return () -> {
            if (input.internalDeque.isEmpty())
                input.getOutput().requestImmediatly();
            return input.internalDeque.pop();
        };
    }

    /** Tells you that everything you need exists for all the inputs */
    protected abstract void computeNext();

    boolean out = true;

    @Override
    public void requestOutput() {
        out = false;
        // All of the nodes we have already requested from- we don't want to request from them twice
        Set<INode> requested = new HashSet<>();
        for (SimpleInput<?> in : inputs.values()) {
            if (in.internalDeque.isEmpty()) {
                IOutput<?> out = in.getOutput();
                if (requested.contains(out.getHolder()))
                    continue;
                requested.add(out.getHolder());
                System.out.println("State of " + name + " before requesting from " + ((AbstractNode) out.getHolder()).name);
                AbstractNode.this.printState();
                out.requestImmediatly();
            }
        }
        // Lots of computational madness...

        // It feels like the state pushes values and computes them, before returning and recomputing them because the
        // input queue is empty....
        if (!out) {
            System.out.println("State of " + name + " before computing");
            AbstractNode.this.printState();
            // It is possible that the inputs did not compute so just check all of them to see
            computeIfCan();
            System.out.println("State of " + name + " AFTER computing");
            AbstractNode.this.printState();
            out = true;
        }
    }

    /** @return True if the outputs were successfully computed */
    private void computeIfCan() {
        if (hasComputed != null)
            throw new IllegalStateException("ALREADY COMPUTED!");
        for (SimpleInput<?> in : inputs.values()) {
            if (in.internalDeque.isEmpty())
                return;
        }
        computeNext();
    }

    @SuppressWarnings("unchecked")
    protected <O> Consumer<O> getOutputConsumer(String name) {
        return (Consumer<O>) internalOutputs.get(name);
    }

    // DEBUG
    public void printState() {
        System.out.println("  Inputs:");
        for (Entry<String, SimpleInput<?>> entry : inputs.entrySet()) {
            System.out.println("   - " + entry.getKey() + " = " + entry.getValue().internalDeque);
        }
        System.out.println("  Outputs:");
        for (Entry<String, SimpleOutput<?>> entry : outputs.entrySet()) {
            System.out.println("   - " + entry.getKey());
        }
    }

    private class SimpleInput<I> implements IInput<I> {
        private final Class<I> inputClass;
        private final Deque<I> internalDeque = Queues.newArrayDeque();
        private IOutput<? extends I> output;

        public SimpleInput(Class<I> inputClass) {
            this.inputClass = inputClass;
        }

        @Override
        public void accept(I t) {
            System.out.println("State of " + name + " before pushing");
            AbstractNode.this.printState();
            internalDeque.push(t);
            System.out.println("State of " + name + " AFTER pushing");
            AbstractNode.this.printState();
            computeIfCan();
        }

        @Override
        public void processInput() {
            // computeIfCan();
        }

        @Override
        public Class<I> getInputClass() {
            return inputClass;
        }

        @Override
        public void setOutput(IOutput<? extends I> out) {
            this.output = out;
        }

        @Override
        public IOutput<? extends I> getOutput() {
            return output;
        }

        @Override
        public String toString() {
            final int maxLen = 10;
            return "SimpleInput [inputClass=" + inputClass + ", internalDeque=" + (internalDeque != null ? toString(internalDeque, maxLen) : null)
                + ", AbstractNode$this=" + AbstractNode.this + "]";
        }

        private String toString(Collection<?> collection, int maxLen) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            int i = 0;
            for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
                if (i > 0)
                    builder.append(", ");
                builder.append(iterator.next());
            }
            builder.append("]");
            return builder.toString();
        }

        @Override
        public INode getHolder() {
            return AbstractNode.this;
        }
    }

    private class SimpleOutput<O> implements IOutput<O> {
        private final Class<O> outputClass;
        private final Set<IInput<? super O>> inputSet = Sets.newHashSet();

        public SimpleOutput(Class<O> outputClass) {
            this.outputClass = outputClass;
        }

        @Override
        public void connect(IInput<? super O> consumer) {
            inputSet.add(consumer);
            consumer.setOutput(this);
        }

        @Override
        public void disconnect() {
            inputSet.clear();
        }

        @Override
        public void requestImmediatly() {
            try {
                requestOutput();
            } catch (Throwable t) {
                throw new IllegalStateException("Thrown for " + AbstractNode.this.name, t);
            }
        }

        @Override
        public Class<O> getOutputClass() {
            return outputClass;
        }

        void sendValue(O val) {
            if (val == null)
                throw new NullPointerException("value");
            out = true;
            inputSet.forEach(input -> input.accept(val));
            inputSet.forEach(input -> input.processInput());
        }

        @Override
        public INode getHolder() {
            return AbstractNode.this;
        }
    }
}
