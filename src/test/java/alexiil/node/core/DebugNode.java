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

import java.util.function.Consumer;

import com.google.common.base.Supplier;

public class DebugNode extends AbstractNode {
    private final Consumer<String> logger;
    private final Supplier<Object> itVal;

    public static final DebugNode usingSystemOut = new DebugNode(System.out::println);

    public DebugNode(Consumer<String> logger) {
        this.logger = logger;
        itVal = null;
    }

    public DebugNode(NodeGraph graph, Consumer<String> logger, String name) {
        super(graph, name);
        this.logger = logger;
        addInput("val", Object.class);
        itVal = getInputSupplier("val");
    }

    @Override
    protected boolean computeNext() {
        try {
            logger.accept(getName() + ": " + itVal.get().toString());
        } catch (Throwable t) {
            throw new IllegalStateException("Could not GET for " + getName(), t);
        }
        return false;
    }

    @Override
    public AbstractNode createCopy(NodeGraph graph, String name) {
        return new DebugNode(graph, logger, name);
    }
}
