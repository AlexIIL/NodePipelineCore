/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core.math;

import java.util.function.Consumer;

import com.google.common.base.Supplier;

import alexiil.node.core.AbstractNode;
import alexiil.node.core.NodeGraph;
import alexiil.node.core.NodeRegistry;

/** Takes 2 inputs and produces 1 output. */
public abstract class SimpleMathNode<N extends Number> extends AbstractNode {
    private final Supplier<N> inA, inB;
    private final Consumer<N> out;

    public SimpleMathNode(NodeRegistry registry, String typeTag) {
        super(registry, typeTag);
        inA = inB = null;
        out = null;
    }

    public SimpleMathNode(NodeRegistry registry, String typeTag, NodeGraph graph, Class<N> clazz, String name) {
        super(registry, typeTag, graph, name);
        addInput("a", clazz);
        addInput("b", clazz);
        addOutput("ans", clazz);
        inA = getInputSupplier("a");
        inB = getInputSupplier("b");
        out = getOutputConsumer("ans");
    }

    @Override
    protected boolean computeNext() {
        N a = inA.get();
        N b = inB.get();
        out.accept(apply(a, b));
        return true;
    }

    protected abstract N apply(N a, N b);

    public static class LongNode extends SimpleMathNode<Long> {
        private final LongApplier applier;

        public LongNode(NodeRegistry registry, String typeTag, LongApplier applier) {
            super(registry, typeTag);
            this.applier = applier;
        }

        public LongNode(NodeRegistry registry, String typeTag, NodeGraph graph, LongApplier applier, String name) {
            super(registry, typeTag, graph, Long.class, name);
            this.applier = applier;
        }

        @Override
        protected Long apply(Long a, Long b) {
            return applier.apply(a, b);
        }

        @Override
        public AbstractNode createCopy(NodeGraph graph, String name) {
            return new LongNode(getRegistry(), getTypeTag(), graph, applier, name);
        }

        public interface LongApplier {
            long apply(long a, long b);
        }
    }

    public static class DoubleNode extends SimpleMathNode<Double> {
        private final DoubleApplier applier;

        public DoubleNode(NodeRegistry registry, String typeTag, DoubleApplier applier) {
            super(registry, typeTag);
            this.applier = applier;
        }

        public DoubleNode(NodeRegistry registry, String typeTag, NodeGraph graph, DoubleApplier applier, String name) {
            super(registry, typeTag, graph, Double.class, name);
            this.applier = applier;
        }

        @Override
        protected Double apply(Double a, Double b) {
            return applier.apply(a, b);
        }

        @Override
        public AbstractNode createCopy(NodeGraph graph, String name) {
            return new DoubleNode(getRegistry(), getTypeTag(), graph, applier, name);
        }

        public interface DoubleApplier {
            double apply(double a, double b);
        }
    }
}
