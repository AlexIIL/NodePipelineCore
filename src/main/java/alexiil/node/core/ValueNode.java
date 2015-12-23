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

import com.google.common.base.Supplier;

import alexiil.node.core.NodeGraph.GraphConnection;

public class ValueNode<N> extends AbstractNode implements INodeFactory<N> {
    private final Supplier<N> supplier;
    private final Consumer<N> out;
    private final Class<N> clazz;
    private final GraphConnection<N> connection;

    public ValueNode(Supplier<N> supplier, Class<N> clazz) {
        this(null, supplier, clazz);
    }

    public ValueNode(NodeGraph graph, Supplier<N> supplier, Class<N> clazz) {
        super(graph);
        this.supplier = supplier;
        this.clazz = clazz;
        connection = addOutput("val", clazz);
        out = getOutputConsumer("val");
        name = "Value " + supplier.get().toString();
    }

    @Override
    public ValueNode<N> createCopy(NodeGraph graph) {
        return new ValueNode<N>(graph, supplier, clazz);
    }

    @Override
    public INode createNode(NodeGraph graph, N value) {
        return new ValueNode<N>(graph, () -> value, clazz);
    }

    @Override
    protected boolean computeNext() {
        System.out.println("Computing " + connection.getRequestedElements() + " values");
        boolean ret = connection.getRequestedElements() > 0;
        while (connection.getRequestedElements() > 0) {
            hasComputed = name;
            out.accept(supplier.get());
        }
        return ret;
    }

    @Override
    public Class<N> getClassType() {
        return clazz;
    }
}
