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

import alexiil.node.core.*;
import alexiil.node.core.math.SimpleMathNode.DoubleNode;
import alexiil.node.core.math.SimpleMathNode.LongNode;

public class MathNodes {
    public static final SimpleMathNode.LongNode longAdder = new LongNode((a, b) -> a + b);
    public static final SimpleMathNode.LongNode longSubtracter = new LongNode((a, b) -> a - b);
    public static final SimpleMathNode.LongNode longMultiplier = new LongNode((a, b) -> a * b);
    public static final SimpleMathNode.LongNode longDivider = new LongNode((a, b) -> a / b);

    public static final SimpleMathNode.DoubleNode doubleAdder = new DoubleNode((a, b) -> a + b);
    public static final SimpleMathNode.DoubleNode doubleSubtracter = new DoubleNode((a, b) -> a - b);
    public static final SimpleMathNode.DoubleNode doubleMultiplier = new DoubleNode((a, b) -> a * b);
    public static final SimpleMathNode.DoubleNode doubleDivider = new DoubleNode((a, b) -> a / b);
    public static final SimpleMathNode.DoubleNode doublePower = new DoubleNode((a, b) -> Math.pow(a, b));

    public static final NodeRegistry mathRegistry = new NodeRegistry("math");

    static {
        mathRegistry.registerNodeType(new NodeValueLong("LongCreate", 0L));
        mathRegistry.registerNodeType(new ValueNode<Double>(mathRegistry, "", 0.0, Double.class) {

            @Override
            public INode createNode(Double value) {
                return null;
            }

            @Override
            public INodeAdditionalData modify(String[] additionalData) {
                return null;
            }

            @Override
            public AbstractNode createCopy(NodeGraph graph, String name) {
                return null;
            }
        });

        mathRegistry.registerNodeType(longAdder);
        mathRegistry.registerNodeType(longSubtracter);
        mathRegistry.registerNodeType(longMultiplier);
        mathRegistry.registerNodeType(longDivider);

        mathRegistry.registerNodeType(doubleAdder);
        mathRegistry.registerNodeType(doubleSubtracter);
        mathRegistry.registerNodeType(doubleMultiplier);
        mathRegistry.registerNodeType(doubleDivider);
        mathRegistry.registerNodeType(doublePower);

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
            return new NodeValueLong(getTypeTag(), getGraph(), value, name);
        }
    }

    private static class NodeValueDouble extends ValueNode<Double> {
        private NodeValueDouble(NodeRegistry registry, String typeTag, double value) {
            super(registry, typeTag, value, Double.class);
        }

        @Override
        public NodeValueDouble createNode(Double value) {
            return null;
        }

        @Override
        public INodeAdditionalData modify(String[] additionalData) {
            return null;
        }

        @Override
        public NodeValueDouble createCopy(NodeGraph graph, String name) {
            return null;
        }
    }
}
