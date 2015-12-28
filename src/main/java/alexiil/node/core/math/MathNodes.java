/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core.math;

import alexiil.node.core.NodeGraph;
import alexiil.node.core.NodeRegistry;
import alexiil.node.core.ReturnNode;
import alexiil.node.core.ValueNode;
import alexiil.node.core.math.SimpleMathNode.DoubleNode;
import alexiil.node.core.math.SimpleMathNode.LongNode;

/** Stores all mathematical nodes: input, creation, modification and returning. Handles {@link Long} and {@link Double}
 * 
 * Created on 28 Dec 2015
 *
 * @author AlexIIL */
public class MathNodes {
    public static final NodeRegistry mathRegistry = new NodeRegistry("math");

    public static final ValueNode<Long> longCreator;
    // public static final InputNode<Long> longInput;
    public static final ReturnNode<Long> longReturner;
    public static final SimpleMathNode.LongNode longAdder;
    public static final SimpleMathNode.LongNode longSubtractor;
    public static final SimpleMathNode.LongNode longMultiplier;
    public static final SimpleMathNode.LongNode longDivider;

    public static final ValueNode<Double> doubleCreator;
    // public static final InputNode<Double> doubleInput;
    public static final ReturnNode<Double> doubleReturner;
    public static final SimpleMathNode.DoubleNode doubleAdder;
    public static final SimpleMathNode.DoubleNode doubleSubtractor;
    public static final SimpleMathNode.DoubleNode doubleMultiplier;
    public static final SimpleMathNode.DoubleNode doubleDivider;
    public static final SimpleMathNode.DoubleNode doublePower;

    static {
        mathRegistry.registerNodeType(longCreator = new NodeValueLong("LongCreate", 0L));
        // mathRegistry.registerNodeType(longInput = new InputNode<>(mathRegistry, "LongInput", Long.class));
        mathRegistry.registerNodeType(longReturner = new ReturnNode<>(mathRegistry, "LongReturn", Long.class));
        mathRegistry.registerNodeType(longAdder = new LongNode(mathRegistry, "LongAdder", (a, b) -> a + b));
        mathRegistry.registerNodeType(longSubtractor = new LongNode(mathRegistry, "LongSubtractor", (a, b) -> a - b));
        mathRegistry.registerNodeType(longMultiplier = new LongNode(mathRegistry, "LongMultiplier", (a, b) -> a * b));
        mathRegistry.registerNodeType(longDivider = new LongNode(mathRegistry, "LongDivider", (a, b) -> a / b));

        mathRegistry.registerNodeType(doubleCreator = new NodeValueDouble("DoubleCreate", 0.0));
        // mathRegistry.registerNodeType(doubleInput = new InputNode<>(mathRegistry, "DoubleInput", Double.class));
        mathRegistry.registerNodeType(doubleReturner = new ReturnNode<>(mathRegistry, "DoubleReturn", Double.class));
        mathRegistry.registerNodeType(doubleAdder = new DoubleNode(mathRegistry, "DoubleAdder", (a, b) -> a + b));
        mathRegistry.registerNodeType(doubleSubtractor = new DoubleNode(mathRegistry, "DoubleSubtractor", (a, b) -> a - b));
        mathRegistry.registerNodeType(doubleMultiplier = new DoubleNode(mathRegistry, "DoubleMultiplier", (a, b) -> a * b));
        mathRegistry.registerNodeType(doubleDivider = new DoubleNode(mathRegistry, "DoubleDivider", (a, b) -> a / b));
        mathRegistry.registerNodeType(doublePower = new DoubleNode(mathRegistry, "DoublePower", (a, b) -> Math.pow(a, b)));

        mathRegistry.setImmutable();
    }

    private static class NodeValueLong extends ValueNode<Long> {
        private NodeValueLong(String typeTag, long value) {
            super(mathRegistry, typeTag, value, Long.class);
        }

        private NodeValueLong(String typeTag, NodeGraph graph, Long value, String name) {
            super(mathRegistry, typeTag, graph, value, Long.class, name);
        }

        @Override
        public NodeValueLong createNode(Long value) {
            return new NodeValueLong(getTypeTag(), value);
        }

        @Override
        public NodeValueLong modify(String[] additionalData) {
            String val = additionalData[0];
            Long lVal = Long.decode(val);
            return new NodeValueLong(getTypeTag(), getGraph(), lVal, getName());
        }

        @Override
        public NodeValueLong createCopy(NodeGraph graph, String name) {
            return new NodeValueLong(getTypeTag(), graph, value, name);
        }
    }

    private static class NodeValueDouble extends ValueNode<Double> {
        private NodeValueDouble(String typeTag, double value) {
            super(mathRegistry, typeTag, value, Double.class);
        }

        private NodeValueDouble(String typeTag, NodeGraph graph, Double value, String name) {
            super(mathRegistry, typeTag, graph, value, Double.class, name);
        }

        @Override
        public NodeValueDouble createNode(Double value) {
            return new NodeValueDouble(getTypeTag(), value);
        }

        @Override
        public NodeValueDouble modify(String[] additionalData) {
            String val = additionalData[0];
            Double lVal = Double.valueOf(val);
            return new NodeValueDouble(getTypeTag(), getGraph(), lVal, getName());
        }

        @Override
        public NodeValueDouble createCopy(NodeGraph graph, String name) {
            return new NodeValueDouble(getTypeTag(), getGraph(), value, name);
        }
    }
}
