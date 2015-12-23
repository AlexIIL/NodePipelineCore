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

import alexiil.node.core.NodeRegistry;
import alexiil.node.core.ValueNode;
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

    public static void populateRegistry(NodeRegistry registry) {
        registry.registerNodeValue(new ValueNode<Long>(() -> 0L, Long.class), Long.class);
        registry.registerNodeValue(new ValueNode<Double>(() -> 0.0, Double.class), Double.class);

        registry.registerNodeType("MathLongAdd", longAdder);
        registry.registerNodeType("MathLongSub", longSubtracter);
        registry.registerNodeType("MathLongMul", longMultiplier);
        registry.registerNodeType("MathLongDiv", longDivider);

        registry.registerNodeType("MathDoubleAdd", doubleAdder);
        registry.registerNodeType("MathDoubleSub", doubleSubtracter);
        registry.registerNodeType("MathDoubleMul", doubleMultiplier);
        registry.registerNodeType("MathDoubleDiv", doubleDivider);
        registry.registerNodeType("MathDoublePow", doublePower);
    }
}
