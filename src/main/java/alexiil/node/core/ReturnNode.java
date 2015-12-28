/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import com.google.common.base.Supplier;

import alexiil.node.core.NodeGraph.GraphConnection;

/** @author AlexIIL
 *
 * @param <V> The type to return */
public class ReturnNode<V> extends AbstractNode implements Supplier<V> {
    private final Class<V> clazz;
    private final Supplier<V> in;
    private final GraphConnection<V> connection;

    public ReturnNode(NodeRegistry registry, String typeTag, Class<V> clazz) {
        super(registry, typeTag);
        this.clazz = clazz;
        in = null;
        connection = null;
    }

    public ReturnNode(NodeRegistry registry, String typeTag, NodeGraph graph, Class<V> clazz, String name) {
        super(registry, typeTag, graph, name);
        this.clazz = clazz;
        connection = addInput("val", clazz);
        in = getInputSupplier("val");
    }

    @Override
    public AbstractNode createCopy(NodeGraph graph, String name) {
        return new ReturnNode<V>(getRegistry(), getTypeTag(), graph, clazz, name);
    }

    @Override
    protected boolean computeNext() {
        return false;
    }

    @Override
    public V get() {
        try {
            connection.requestUpTo(1);
            getGraph().iterate();
            return in.get();
        } catch (Throwable t) {
            throw new IllegalStateException("Could not GET for " + getName(), t);
        }
    }
}
