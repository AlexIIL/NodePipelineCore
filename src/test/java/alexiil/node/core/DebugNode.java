package alexiil.node.core;

import java.util.function.Consumer;

import com.google.common.base.Supplier;

public class DebugNode extends AbstractNode {
    private final Consumer<String> logger;
    private final Supplier<Object> itVal;

    public DebugNode(Consumer<String> logger) {
        this.logger = logger;
        addInput("val", Object.class);
        itVal = getInputSupplier("val");
    }

    @Override
    protected void computeNext() {
        hasComputed = name;
        try {
            logger.accept(name + ": " + itVal.get().toString());
        } catch (Throwable t) {
            throw new IllegalStateException("Could not GET for " + name, t);
        }
    }

    @Override
    public DebugNode createCopy() {
        return new DebugNode(logger);
    }
}
