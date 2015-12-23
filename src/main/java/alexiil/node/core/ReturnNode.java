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

public class ReturnNode<V> extends AbstractNode implements Supplier<V> {
    private final Class<V> clazz;
    private final Supplier<V> in;

    public ReturnNode(Class<V> clazz) {
        this.clazz = clazz;
        addInput("val", clazz);
        in = getInputSupplier("val");
    }

    @Override
    public AbstractNode createCopy() {
        return new ReturnNode<V>(clazz);
    }

    @Override
    protected void computeNext() {
        hasComputed = name;
    }

    @Override
    public V get() {
        try {
            return in.get();
        } catch (Throwable t) {
            throw new IllegalStateException("Could not GET for " + name, t);
        }
    }
}
