/* Node Pipeline.
 *
 * Copyright (C) 2015 Alex Jones (AlexIIL)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; with version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, download
 * the contents of https://raw.githubusercontent.com/AlexIIL/NodePipelineCore/master/LICENSE */
package alexiil.node.core;

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
