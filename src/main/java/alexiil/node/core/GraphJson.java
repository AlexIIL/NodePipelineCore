/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import alexiil.node.core.NodeGraph.GraphConnection;

public class GraphJson {
    public static NodeGraph read(InputStream stream) throws IOException, ClassNotFoundException {
        NodeGraph graph = new NodeGraph();
        JsonGraph jsonGraph = new Gson().fromJson(new InputStreamReader(stream), JsonGraph.class);

        Map<String, INode> nodes = new HashMap<>();

        for (JsonNode jNode : jsonGraph.nodes) {
            if (nodes.containsKey(jNode.name))
                throw new IOException("Found a second node with the name " + jNode.name);
            NodeRegistry registry = NodeRegistry.getRegistry(jNode.registryPackage);
            INode node = registry.getNodeType(jNode.tag, jNode.name);

            if (node instanceof INodeAdditionalData) {
                INodeAdditionalData addNode = (INodeAdditionalData) node;
                String[] data = new String[addNode.dataKeys().length];
                for (int i = 0; i < jNode.extraData.length; i++) {
                    data[i] = jNode.extraData[i].value;
                }
                node = addNode.modify(data);
            }

            nodes.put(jNode.name, graph.addCopyOf(node, jNode.name));
        }

        // Lastly go back through and connect up all of the nodes
        for (JsonNode nodeJson : jsonGraph.nodes) {
            if (nodeJson.inputs != null) {
                INode node = graph.getNode(nodeJson.name);
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
        for (int nodeIndex = 0; nodeIndex < nodes.size(); nodeIndex++) {
            INode node = nodes.get(nodeIndex);
            JsonNode jNode = new JsonNode();
            jsonGraph.nodes[nodeIndex] = jNode;

            jNode.name = node.getName();
            jNode.registryPackage = node.getRegistry().packageName;
            jNode.tag = node.getTypeTag();

            if (node instanceof INodeAdditionalData) {
                INodeAdditionalData addNode = (INodeAdditionalData) node;
                String[] addData = addNode.addtionalData();
                String[] keys = addNode.dataKeys();
                jNode.extraData = new JsonEntry[addData.length];
                for (int i = 0; i < jNode.extraData.length; i++) {
                    JsonEntry entry = new JsonEntry();
                    jNode.extraData[i] = entry;
                    entry.key = keys[i];
                    entry.value = addData[i];
                }
            }

            Set<Entry<String, GraphConnection<?>>> inputs = node.getInputs().entrySet();
            Iterator<Entry<String, GraphConnection<?>>> inputIterator = inputs.iterator();
            jNode.inputs = new JsonInput[inputs.size()];

            for (int inputIndex = 0; inputIndex < jNode.inputs.length; inputIndex++) {
                JsonInput jInput = new JsonInput();
                jNode.inputs[inputIndex] = jInput;

                Entry<String, GraphConnection<?>> entry = inputIterator.next();
                jInput.inName = entry.getKey();
                jInput.outName = entry.getValue().connectedOutput.getName();
                jInput.otherNodeName = entry.getValue().connectedOutput.getNode().getName();
            }
        }
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(jsonGraph);
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.write(json);
        writer.close();
    }

    private static class JsonGraph {
        JsonNode[] nodes;
    }

    private static class JsonNode {
        String name, registryPackage, tag;
        JsonInput[] inputs;
        JsonEntry[] extraData;
    }

    private static class JsonEntry {
        String key, value;
    }

    private static class JsonInput {
        String inName;
        String outName;
        String otherNodeName;
    }
}
