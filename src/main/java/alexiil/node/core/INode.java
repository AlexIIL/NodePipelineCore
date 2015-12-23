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

public interface INode {
    /** Creates a copy of this node with all of its outputs disconnected from any inputs. */
    INode createCopy();

    /** @return A map (most likely immutable or unmodifiable) that contains all of the inputs this has. */
    Map<String, ? extends IInput<?>> getInputs();

    /** @return A map (most likely immutable or unmodifiable) that contains all of the outputs this has. */
    Map<String, ? extends IOutput<?>> getOutputs();

    /** Requests that this node immediately compute its return value by computing all of the input nodes. */
    void requestOutput();

    // Duplicate this for pre-jdk8 deps (Like MC)
    public interface IInput<O> extends Consumer<O> {
        Class<O> getInputClass();

        void setOutput(IOutput<? extends O> out);

        IOutput<? extends O> getOutput();

        /** Actually process any input. This will always be called after {@link #accept(Object)}, however due to the way
         * that multiple nodes interact (And if this node has multiple inputs) that you don't need to do anything on
         * subsequent calls. Basically you need to check to see if you have any values left on the stack to use. */
        void processInput();

        /** Just add the value to an internal stack- leave actual usage of the input to {@link #processInput()}, which
         * will always be called after accept. */
        @Override
        void accept(O val);

        INode getHolder();
    }

    public interface IOutput<O> {
        /** Adds the given consumer as an input */
        void connect(IInput<? super O> consumer);

        /** Disconnects this output from anything */
        void disconnect();

        /** Immediately makes this output compute the next value and put it into all the consumers. */
        void requestImmediatly();

        Class<O> getOutputClass();

        INode getHolder();
    }
}
