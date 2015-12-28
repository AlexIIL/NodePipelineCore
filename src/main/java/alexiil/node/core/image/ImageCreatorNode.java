/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core.image;

import alexiil.node.core.AbstractNode;
import alexiil.node.core.NodeGraph;
import alexiil.node.core.NodeRegistry;

/** Created on 28 Dec 2015
 *
 * @author AlexIIL */
public class ImageCreatorNode extends AbstractNode {

    // private final Iterator<Long> sizeX;
    // private final Iterator<Long> sizeY;
    // private final Consumer<BufferedImage> img;

    public ImageCreatorNode(NodeRegistry registry, String typeTag, NodeGraph graph, String name) {
        super(registry, typeTag, graph, name);
        // TODO Auto-generated constructor stub
    }

    public ImageCreatorNode(NodeRegistry registry, String typeTag) {
        super(registry, typeTag);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean computeNext() {
        return false;
    }

    @Override
    public ImageCreatorNode createCopy(NodeGraph graph, String name) {
        return null;
    }
}
