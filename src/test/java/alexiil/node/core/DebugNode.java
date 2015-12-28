/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.util.function.Consumer;

import com.google.common.base.Supplier;

public class DebugNode extends AbstractNode {
    private final Consumer<String> logger;
    private final Supplier<Object> itVal;

    public static final DebugNode usingSystemOut = new DebugNode(null, "", System.out::println);

    public DebugNode(NodeRegistry registry, String typeTag, Consumer<String> logger) {
        super(registry, typeTag);
        this.logger = logger;
        itVal = null;
    }

    public DebugNode(NodeRegistry registry, String typeTag, NodeGraph graph, Consumer<String> logger, String name) {
        super(registry, typeTag, graph, name);
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
        return new DebugNode(getRegistry(), getTypeTag(), graph, logger, name);
    }
}
