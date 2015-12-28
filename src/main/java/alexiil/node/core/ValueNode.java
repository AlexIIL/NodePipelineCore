/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.util.function.Consumer;

import alexiil.node.core.NodeGraph.GraphConnection;

/** Represents a simple value based creation node. This will repeatedly output a single value.
 * 
 * @author AlexIIL
 *
 * @param <N> The type of value to supply */
public abstract class ValueNode<N> extends AbstractNode implements INodeFactory<N>, INodeAdditionalData {
    protected final N value;
    private final Consumer<N> out;
    private final Class<N> clazz;
    private final GraphConnection<N> connection;

    public ValueNode(NodeRegistry registry, String typeTag, N value, Class<N> clazz) {
        super(registry, typeTag);
        this.value = value;
        out = null;
        this.clazz = clazz;
        connection = null;
    }

    public ValueNode(NodeRegistry registry, String typeTag, NodeGraph graph, N value, Class<N> clazz, String name) {
        super(registry, typeTag, graph, name);
        this.value = value;
        this.clazz = clazz;
        connection = addOutput("val", clazz);
        out = getOutputConsumer("val");
        name = "Value " + value.toString();
    }

    @Override
    public String[] addtionalData() {
        return new String[] { value.toString() };
    }

    @Override
    public String[] dataKeys() {
        return new String[] { "value" };
    }

    @Override
    protected boolean computeNext() {
        System.out.println("Computing " + connection.getRequestedElements() + " values");
        boolean ret = connection.getRequestedElements() > 0;
        while (connection.getRequestedElements() > 0) {
            out.accept(value);
        }
        return ret;
    }

    @Override
    public Class<N> getClassType() {
        return clazz;
    }
}
