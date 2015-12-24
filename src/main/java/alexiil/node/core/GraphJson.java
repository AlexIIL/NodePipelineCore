package alexiil.node.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class GraphJson {
    public static NodeGraph read(InputStream stream) throws IOException, ClassNotFoundException {
        NodeGraph graph = new NodeGraph();
        JsonGraph jsonGraph = new Gson().fromJson(new InputStreamReader(stream), JsonGraph.class);
        NodeRegistry registry = RegistryProvider.provide(jsonGraph.registryName);

        Map<String, INode> nodes = new HashMap<>();

        for (JsonNode nodeJson : jsonGraph.nodes) {
            if (nodes.containsKey(nodeJson.name))
                throw new IOException("Found a second node with the name " + nodeJson.name);
            INode node = registry.getNodeType(nodeJson.tag, Class.forName(nodeJson.classType), nodeJson.name);
            nodes.put(nodeJson.name, graph.addCopyOf(node, nodeJson.name));

            if (nodeJson.inputs != null) {
                for (JsonInput input : nodeJson.inputs) {
                    INode fromNode = nodes.get(input.otherNodeName);
                    graph.connectIO(fromNode, input.outName, node, input.inName);
                }
            }
        }
        return graph;
    }

    public static void write(NodeGraph graph, OutputStream stream) throws IOException {
        List<INode> nodes = graph.getNodes();
        JsonGraph jsonGraph = new JsonGraph();
        jsonGraph.nodes = new JsonNode[nodes.size()];
        for (INode node : nodes) {

        }
    }

    private static class JsonGraph {
        String registryName;
        JsonNode[] nodes;
    }

    private static class JsonNode {
        String name, tag, classType;
        JsonInput[] inputs;
    }

    private static class JsonInput {
        String outName, inName;
        String otherNodeName;
    }
}
