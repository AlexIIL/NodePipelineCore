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

public class ValueNode<N> extends AbstractNode implements INodeFactory<N> {
    private final Supplier<N> supplier;
    private final Consumer<N> out;
    private final Class<N> clazz;

    public ValueNode(Supplier<N> supplier, Class<N> clazz) {
        this.supplier = supplier;
        this.clazz = clazz;
        addOutput("val", clazz);
        out = getOutputConsumer("val");
        name = "Value " + supplier.get().toString();
    }

    @Override
    public ValueNode<N> createCopy() {
        return new ValueNode<N>(supplier, clazz);
    }

    @Override
    public INode createNode(N value) {
        return new ValueNode<N>(() -> value, clazz);
    }

    @Override
    protected void computeNext() {
        hasComputed = name;

        out.accept(supplier.get());
    }

    @Override
    public Class<N> getClassType() {
        return clazz;
    }
}
