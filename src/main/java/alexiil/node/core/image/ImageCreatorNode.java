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
