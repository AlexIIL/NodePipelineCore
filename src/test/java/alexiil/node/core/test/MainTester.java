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
package alexiil.node.core.test;

import alexiil.node.core.*;
import alexiil.node.core.math.MathNodes;
import alexiil.node.core.math.SimpleMathNode.LongNode;

public class MainTester {
    public static void main(String[] args) {
        NodeRegistry reg = new NodeRegistry();
        MathNodes.populateRegistry(reg);

        NodeGraph graph = new NodeGraph();
        INode oneNode = graph.addCopyOf(new ValueNode<>(() -> 1L, Long.class));
        INode fourNode = graph.addCopyOf(new ValueNode<>(() -> 4L, Long.class));
        INode twoNode = graph.addCopyOf(new ValueNode<>(() -> 2L, Long.class));
        LongNode firstAdder = graph.addCopyOf(MathNodes.longAdder);
        firstAdder.name = "firstAdder";

        graph.connectIO(oneNode, "val", firstAdder, "a");
        graph.connectIO(fourNode, "val", firstAdder, "b");

        LongNode subtractor = graph.addCopyOf(MathNodes.longSubtracter);
        subtractor.name = "subtractor";

        graph.connectIO(fourNode, "val", subtractor, "a");
        graph.connectIO(twoNode, "val", subtractor, "b");

        LongNode secondAdder = graph.addCopyOf(MathNodes.longAdder);
        secondAdder.name = "secondAdder";

        graph.connectIO(firstAdder, "ans", secondAdder, "a");
        graph.connectIO(subtractor, "ans", secondAdder, "b");

        LongNode thirdAdder = graph.addCopyOf(MathNodes.longAdder);
        thirdAdder.name = "thirdAdder";

        graph.connectIO(secondAdder, "ans", thirdAdder, "a");
        graph.connectIO(secondAdder, "ans", thirdAdder, "b");

        LongNode forthAdder = graph.addCopyOf(MathNodes.longAdder);
        forthAdder.name = "forthAdder";

        graph.connectIO(secondAdder, "ans", forthAdder, "a");
        graph.connectIO(secondAdder, "ans", forthAdder, "b");

        LongNode fifthAdder = graph.addCopyOf(MathNodes.longAdder);
        fifthAdder.name = "fifthAdder";

        graph.connectIO(thirdAdder, "ans", fifthAdder, "a");
        graph.connectIO(forthAdder, "ans", fifthAdder, "b");

        // LongNode adder = graph.addCopyOf(MathNodes.longAdder);
        // adder.name = "adder";
        // graph.connectIO(oneNode, "val", adder, "a");
        // graph.connectIO(oneNode, "val", adder, "b");

        DebugNode debugNode = graph.addCopyOf(new DebugNode(System.out::println));
        debugNode.name = "debug";
        ReturnNode<Long> returnNode = graph.addCopyOf(new ReturnNode<>(Long.class));
        returnNode.name = "return";

        graph.connectIO(fifthAdder, "ans", debugNode, "val");
        graph.connectIO(fifthAdder, "ans", returnNode, "val");

        // graph.connectIO(adder, "ans", debugNode, "val");
        // graph.connectIO(adder, "ans", returnNode, "val");

        try {
            System.out.println("Returned " + returnNode.get());
        } catch (Throwable t) {
            graph.printState();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(t);
            }
            throw t;
        }
    }
}
