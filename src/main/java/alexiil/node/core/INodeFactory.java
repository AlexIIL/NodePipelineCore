package alexiil.node.core;

public interface INodeFactory<T> {
    INode createNode(T value);

    Class<T> getClassType();
}
