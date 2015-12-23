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
