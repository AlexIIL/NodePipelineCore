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
package alexiil.node.core.math;

import com.google.common.base.Supplier;

import alexiil.node.core.AbstractNode;
import alexiil.node.core.Consumer;

/** Takes 2 inputs and produces 1 output. */
public abstract class SimpleMathNode<N extends Number> extends AbstractNode {
    private final Supplier<N> itA;
    private final Supplier<N> itB;
    private final Consumer<N> out;

    public SimpleMathNode(Class<N> clazz) {
        addInput("a", clazz);
        addInput("b", clazz);
        addOutput("ans", clazz);
        itA = getInputSupplier("a");
        itB = getInputSupplier("b");
        out = getOutputConsumer("ans");
    }

    @Override
    protected void computeNext() {
        N a = itA.get();
        N b = itB.get();
        hasComputed = name;
        out.accept(apply(a, b));
    }

    protected abstract N apply(N a, N b);

    public static class LongNode extends SimpleMathNode<Long> {
        private final LongApplier applier;

        public LongNode(LongApplier applier) {
            super(Long.class);
            this.applier = applier;
        }

        @Override
        protected Long apply(Long a, Long b) {
            return applier.apply(a, b);
        }

        @Override
        public AbstractNode createCopy() {
            return new LongNode(applier);
        }

        public interface LongApplier {
            long apply(long a, long b);
        }
    }

    public static class DoubleNode extends SimpleMathNode<Double> {
        private final DoubleApplier applier;

        public DoubleNode(DoubleApplier applier) {
            super(Double.class);
            this.applier = applier;
        }

        @Override
        protected Double apply(Double a, Double b) {
            return applier.apply(a, b);
        }

        @Override
        public AbstractNode createCopy() {
            return new DoubleNode(applier);
        }

        public interface DoubleApplier {
            double apply(double a, double b);
        }
    }
}
