/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

public interface INodeFactory<T> {
    INode createNode(T value);

    Class<T> getClassType();
}
