package alexiil.node.core.image;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import alexiil.node.core.AbstractNode;
import alexiil.node.core.Consumer;

public class ImageCreatorNode extends AbstractNode {
    private final Iterator<Long> sizeX;
    private final Iterator<Long> sizeY;
    private final Consumer<BufferedImage> img;

    @Override
    protected void computeNext() {

    }

    @Override
    protected Set<String> getNeededInputs() {
        return ImmutableSet.of("");
    }
}
