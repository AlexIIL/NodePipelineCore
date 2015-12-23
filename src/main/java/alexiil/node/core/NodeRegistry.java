package alexiil.node.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import alexiil.node.core.INode.IInput;
import alexiil.node.core.INode.IOutput;

public final class NodeRegistry {
    private final Map<String, Map<Class<?>, INode>> nodeRegistry = Maps.newHashMap();
    private final Map<Class<?>, INodeFactory<?>> nodeValueTypes = Maps.newHashMap();

    public boolean hasNodeType(String tag) {
        return nodeRegistry.containsKey(tag);
    }

    public INode getNodeType(String tag, Class<?> clazz) {
        Map<Class<?>, INode> map = nodeRegistry.get(tag);
        if (map == null)
            return null;
        INode node = map.get(clazz);
        if (node == null)
            return null;
        return node.createCopy();
    }

    public void registerNodeType(String tag, INode node) {
        if (nodeRegistry.containsKey(tag) && nodeRegistry.get(tag).containsKey(node.getClass()))
            throw new IllegalArgumentException("Already contained the tag " + tag);
        if (node == null)
            throw new NullPointerException("node");
        if (!nodeRegistry.containsKey(tag))
            nodeRegistry.put(tag, new HashMap<Class<?>, INode>());
        nodeRegistry.get(tag).put(node.getClass(), node);
    }

    public <T> void registerNodeValue(INodeFactory<T> factory, Class<T> clazz) {
        if (factory.getClassType() != clazz)
            throw new IllegalArgumentException("The class types did not match!");
        nodeValueTypes.put(clazz, factory);
    }

    public <V> INode createNodeValue(V val, Class<V> clazz) {
        if (nodeValueTypes.containsKey(clazz)) {
            INodeFactory<V> factory = (INodeFactory<V>) nodeValueTypes.get(clazz);
            return factory.createNode(val);
        }
        throw new IllegalArgumentException("");
    }

    public String writeDefinitions() {
        StringBuilder builder = new StringBuilder();
        List<Class<?>> classes = Lists.newArrayList();
        for (Class<?> clazz : nodeValueTypes.keySet()) {
            builder.append("t " + clazz.getSimpleName() + "\n");
            classes.add(clazz);
        }
        int tagIndex = 0;
        int nodeIndex = 0;
        for (Entry<String, Map<Class<?>, INode>> entry : nodeRegistry.entrySet()) {
            String tag = entry.getKey();
            builder.append("n " + tag + "\n");
            tagIndex++;
            for (Entry<Class<?>, INode> entry2 : entry.getValue().entrySet()) {
                builder.append("d " + tagIndex + " " + classes.indexOf(entry2.getKey()) + "\n");
                INode node = entry2.getValue();
                for (Entry<String, ? extends IInput<?>> in : node.getInputs().entrySet()) {
                    builder.append("i " + nodeIndex + " " + in.getKey() + " " + classes.indexOf(in.getValue().getInputClass()));
                }
                for (Entry<String, ? extends IOutput<?>> out : node.getOutputs().entrySet()) {
                    builder.append("o " + nodeIndex + " " + out.getKey() + " " + classes.indexOf(out.getValue().getOutputClass()));
                }
                nodeIndex++;
            }
        }
        return builder.toString();
    }
}
