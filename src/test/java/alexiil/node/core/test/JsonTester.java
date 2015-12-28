package alexiil.node.core.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import alexiil.node.core.GraphReader;
import alexiil.node.core.GraphReader.SaveType;
import alexiil.node.core.INode;
import alexiil.node.core.NodeGraph;
import alexiil.node.core.ReturnNode;
import alexiil.node.core.math.MathNodes;

public class JsonTester {
    @Test
    public void testGraphOutput() throws IllegalArgumentException, IOException {
        NodeGraph graph = TestUtils.makeTestMathLongGraph();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GraphReader.writeNodeGraph(baos, graph, SaveType.READABLE_JSON);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

        graph = GraphReader.readNodeGraph(bais);

        graph.printState();

        INode fifthAdder = graph.getNode("fifthAdder");
        ReturnNode<Long> returnNode = graph.addCopyOf(MathNodes.longReturner, "return");
        graph.connectIO(fifthAdder, "ans", returnNode, "val");

        long output = returnNode.get();
        Assert.assertEquals(28, output);
    }
}
