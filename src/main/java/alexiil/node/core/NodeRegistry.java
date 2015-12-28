/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

public final class NodeRegistry {
    private static final Map<String, NodeRegistry> registries = new HashMap<>();

    private final Map<String, INode> nodeRegistry = Maps.newHashMap();
    private final Map<Class<?>, INodeFactory<?>> nodeValueTypes = Maps.newHashMap();

    private boolean immutable = false;

    public final String packageName;

    public NodeRegistry(String name) {
        packageName = name;
        registries.put(name, this);
    }

    public boolean hasNodeType(String tag) {
        return nodeRegistry.containsKey(tag);
    }

    public static NodeRegistry getRegistry(String packageName) {
        return registries.get(packageName);
    }

    public INode getNodeType(String tag, String name) {
        INode node = nodeRegistry.get(tag);
        if (node == null)
            return null;
        return node.createCopy(null, name);
    }

    public void registerNodeType(INode node) {
        if (immutable)
            throw new IllegalStateException("Cannot modify an immutable registry!");
        String tag = node.getTypeTag();
        if (tag == null || tag.contains("/"))
            throw new IllegalArgumentException("Tag was wrong");
        if (nodeRegistry.containsKey(tag))
            throw new IllegalArgumentException("Already contained the tag " + tag);
        if (node == null)
            throw new NullPointerException("node");
        if (node instanceof INodeFactory) {
            registerNodeValue((INodeFactory) node);
        }
        nodeRegistry.put(tag, node);
    }

    private <T> void registerNodeValue(INodeFactory<T> factory) {
        if (immutable)
            throw new IllegalStateException("Cannot modify an immutable registry!");
        nodeValueTypes.put(factory.getClassType(), factory);
    }

    public <V> INode createNodeValue(V val, Class<V> clazz) {
        if (nodeValueTypes.containsKey(clazz)) {
            INodeFactory<V> factory = (INodeFactory<V>) nodeValueTypes.get(clazz);
            return factory.createNode(val);
        }
        throw new IllegalArgumentException("");
    }

    /** This writes out all of the definitions of nodes and value nodes into a text file, as JSON */
    public String writeDefinitions() {
        // FIXME!
        return null;
    }

    /** Makes this registry read only. This cannot be undone. */
    public void setImmutable() {
        immutable = true;
    }
}
