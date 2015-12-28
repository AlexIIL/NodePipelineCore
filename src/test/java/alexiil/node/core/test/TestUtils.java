package alexiil.node.core.test;

import alexiil.node.core.INode;
import alexiil.node.core.NodeGraph;
import alexiil.node.core.math.MathNodes;
import alexiil.node.core.math.SimpleMathNode.LongNode;

public class TestUtils {
    public static NodeGraph makeTestMathLongGraph() {
        NodeGraph graph = new NodeGraph();
        INode oneNode = graph.addCopyOf(MathNodes.longCreator.createNode(1L), "1");
        INode fourNode = graph.addCopyOf(MathNodes.longCreator.createNode(4L), "4");
        INode twoNode = graph.addCopyOf(MathNodes.longCreator.createNode(2L), "2");
        LongNode firstAdder = graph.addCopyOf(MathNodes.longAdder, "firstAdder");

        graph.connectIO(oneNode, "val", firstAdder, "a");
        graph.connectIO(fourNode, "val", firstAdder, "b");

        LongNode subtractor = graph.addCopyOf(MathNodes.longSubtractor, "subtractor");

        graph.connectIO(fourNode, "val", subtractor, "a");
        graph.connectIO(twoNode, "val", subtractor, "b");

        LongNode secondAdder = graph.addCopyOf(MathNodes.longAdder, "secondAdder");

        graph.connectIO(firstAdder, "ans", secondAdder, "a");
        graph.connectIO(subtractor, "ans", secondAdder, "b");

        LongNode thirdAdder = graph.addCopyOf(MathNodes.longAdder, "thirdAdder");

        graph.connectIO(secondAdder, "ans", thirdAdder, "a");
        graph.connectIO(secondAdder, "ans", thirdAdder, "b");

        LongNode forthAdder = graph.addCopyOf(MathNodes.longAdder, "forthAdder");

        graph.connectIO(secondAdder, "ans", forthAdder, "a");
        graph.connectIO(secondAdder, "ans", forthAdder, "b");

        LongNode fifthAdder = graph.addCopyOf(MathNodes.longAdder, "fifthAdder");

        graph.connectIO(thirdAdder, "ans", fifthAdder, "a");
        graph.connectIO(forthAdder, "ans", fifthAdder, "b");
        return graph;
    }
}
