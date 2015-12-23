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
