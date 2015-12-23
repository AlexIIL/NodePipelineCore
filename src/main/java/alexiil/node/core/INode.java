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

import java.util.Map;

import alexiil.node.core.NodeGraph.GraphConnection;

public interface INode {
    /** Creates a copy of this node with all of its outputs disconnected from any inputs. */
    INode createCopy(NodeGraph graph);

    /** @return A map (most likely immutable or unmodifiable) that contains all of the inputs this has. */
    Map<String, GraphConnection<?>> getInputs();

    /** @return A map (most likely immutable or unmodifiable) that contains all of the outputs this has. */
    Map<String, GraphConnection<?>> getOutputs();

    NodeGraph getGraph();

    /** Computes the outputs of this node if it has all of the inputs
     * 
     * @return True if it did actually compute something. */
    boolean computeIfCan();

    /** Calls {@link IConnection#requestUpTo(int)} for every input the number of times it will need that input to output
     * what has been requested by the outputs. */
    void askForElements();
}
