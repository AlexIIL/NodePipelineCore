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

public class ReturnNode<V> extends AbstractNode implements Supplier<V> {
    private final Class<V> clazz;
    private final Supplier<V> in;
    private final GraphConnection<V> connection;

    public ReturnNode(Class<V> clazz) {
        this.clazz = clazz;
        in = null;
        connection = null;
    }

    public ReturnNode(NodeGraph graph, Class<V> clazz, String name) {
        super(graph, name);
        this.clazz = clazz;
        connection = addInput("val", clazz);
        in = getInputSupplier("val");
    }

    @Override
    public AbstractNode createCopy(NodeGraph graph, String name) {
        return new ReturnNode<V>(graph, clazz, name);
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
