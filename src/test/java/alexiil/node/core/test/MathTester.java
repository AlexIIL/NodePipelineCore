/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core.test;

import org.junit.Assert;
import org.junit.Test;

import alexiil.node.core.DebugNode;
import alexiil.node.core.INode;
import alexiil.node.core.NodeGraph;
import alexiil.node.core.ReturnNode;
import alexiil.node.core.math.MathNodes;

public class MathTester {
    @Test
    public void testComplexMathGraph() {
        NodeGraph graph = TestUtils.makeTestMathLongGraph();

        INode fifthAdder = graph.getNode("fifthAdder");

        DebugNode debugNode = graph.addCopyOf(DebugNode.usingSystemOut, "debug");
        ReturnNode<Long> returnNode = graph.addCopyOf(MathNodes.longReturner, "return");

        graph.connectIO(fifthAdder, "ans", debugNode, "val");
        graph.connectIO(fifthAdder, "ans", returnNode, "val");

        long val = returnNode.get();
        Assert.assertEquals(28, val);
    }
}
