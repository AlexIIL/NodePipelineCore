package alexiil.node.core.test;

import alexiil.node.core.*;
import alexiil.node.core.math.MathNodes;
import alexiil.node.core.math.SimpleMathNode.LongNode;

public class MainTester {
    public static void main(String[] args) {
        NodeRegistry reg = new NodeRegistry();
        MathNodes.populateRegistry(reg);

        // 1 4 2
        // \--|-----/ \-----|--/
        // [+] [-]
        // \-----|-----/
        // |
        // /-|-\
        // [ + ]
        // |
        // /-----|-----\
        // [+] [+]
        // \----\ /----/
        // [+]
        // |
        // OUT

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
