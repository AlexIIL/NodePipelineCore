/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;

public class NodeGraph {
    private final List<INode> nodes = Lists.newArrayList();

    private final Multimap<GraphConnection<?>, GraphConnection<?>> connections = HashMultimap.create();

    private boolean isIterating = false;

    List<INode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public <N extends INode> N addCopyOf(N node, String name) {
        INode copy = node.createCopy(this, name);
        addNode(copy);
        return (N) copy;
    }

    public void addNode(INode node) {
        if (node == null)
            throw new NullPointerException("node");
        if (nodes.contains(node))
            throw new IllegalArgumentException("Already contained the node!");
        if (node.getGraph() != this)
            throw new IllegalArgumentException("The node was contained within a different graph!");
        nodes.add(node);
    }

    public INode getNode(String name) {
        for (INode node : nodes) {
            if (node.getName().equals(name))
                return node;
        }
        return null;
    }

    /** @param node The node to check
     * @throws IllegalArgumentException if the node was invalid */
    private void checkNode(INode node, String name) throws IllegalArgumentException {
        if (node == null)
            throw new NullPointerException(name + " was null!");
        if (!nodes.contains(node))
            throw new IllegalArgumentException("Did not contain " + node.getName() + " for " + name);
    }

    public void connectIO(INode fromNode, String fromName, INode toNode, String toName) {
        checkNode(fromNode, "fromNode");
        checkNode(toNode, "toNode");

        if (nodes.indexOf(fromNode) >= nodes.indexOf(toNode))
            throw new IllegalArgumentException("Bad order!");
        if (!fromNode.getOutputs().containsKey(fromName))
            throw new IllegalArgumentException("Did not contain the output key " + fromName);
        if (!toNode.getInputs().containsKey(toName))
            throw new IllegalArgumentException("Did not contain the input key " + toName);

        // Ignore the generic type warning, its fine :)
        GraphConnection out = fromNode.getOutputs().get(fromName);
        GraphConnection in = toNode.getInputs().get(toName);
        // Basically if (out instanceof in)
        if (in.getTypeClass().isAssignableFrom(out.getTypeClass()))
            connect(out, in, in.getTypeClass());
        else
            throw new IllegalArgumentException("The nodes did not share a common class!");
    }

    private <T> void connect(GraphConnection<? extends T> out, GraphConnection<T> in, Class<T> typeClass) {
        connections.put(out, in);
        in.connectedOutput = out;
        out.connectedInputs.add(in);
    }

    public void printState() {
        for (INode node : nodes) {
            System.out.println("Node #" + nodes.indexOf(node) + " called " + node.getName());
            if (node instanceof AbstractNode) {
                AbstractNode abs = (AbstractNode) node;
                abs.printState();
            }
        }
    }

    public <E> GraphConnection<E> provideOutputConnection(INode node, String outputName, Class<E> clazz) {
        GraphConnection<E> conn = new GraphConnection<E>(clazz, node, outputName);
        // Hmmm. I feel like I should do something here
        return conn;
    }

    public <E> GraphConnection<E> provideInputConnection(INode node, String inputName, Class<E> clazz) {
        GraphConnection<E> conn = new GraphConnection<E>(clazz, node, inputName);
        // Hmmm. I feel like I should do something here
        return conn;
    }

    public void iterate() {
        if (isIterating)
            return;
        isIterating = true;
        boolean computed;
        do {
            propagateOutputRequests();
            int n = 0;
            computed = false;
            for (INode node : nodes) {
                System.out.println("    Loop START " + n);
                computed |= node.computeIfCan();
                System.out.println("    Loop END " + n++);
            }
        } while (computed);
        isIterating = false;
    }

    /** Pushes the requests up the list of nodes from bottom up. Returns something? Does it even need to? */
    private void propagateOutputRequests() {
        int i = nodes.size();
        for (INode node : Lists.reverse(nodes)) {
            System.out.println("Request START " + --i);
            node.askForElements();
            for (GraphConnection<?> conn : node.getInputs().values()) {
                if (conn.getRequestedElements() > 0) {
                    GraphConnection<?> out = conn.connectedOutput;
                    int max = out.getRequestedElements();
                    for (GraphConnection<?> conn2 : out.connectedInputs) {
                        max = Math.max(max, conn2.getRequestedElements());
                    }
                    out.requestUpTo(max);
                }
            }
        }
    }

    public class GraphConnection<E> {
        final List<GraphConnection<? super E>> connectedInputs = Lists.newArrayList();
        GraphConnection<? extends E> connectedOutput;
        private final Class<E> clazz;
        private final INode node;
        private final String name;

        private final Deque<E> internalDeque = Queues.newArrayDeque();
        private int requested = 0;

        public GraphConnection(Class<E> clazz, INode node, String name) {
            this.clazz = clazz;
            this.node = node;
            this.name = name;
        }

        public Class<E> getTypeClass() {
            return clazz;
        }

        public INode getNode() {
            return node;
        }

        public void push(E val) {
            String call = connectedOutput == null ? "output" : "input";
            System.out.println("State of " + call + " " + name + " for node " + node.getName() + " before pushing");
            ((AbstractNode) node).printState();
            if (requested > 0) {
                requested--;
            }
            if (connectedOutput == null) {
                for (GraphConnection<? super E> conn : connectedInputs) {
                    conn.push(val);
                }
            } else {
                internalDeque.push(val);
            }
            System.out.println("State of " + call + " " + name + " for node " + node.getName() + " AFTER pushing");
            ((AbstractNode) node).printState();
        }

        public E pop() {
            if (getRemainingElements() == 0)
                throw new IllegalStateException("Not enough elements!");
            return internalDeque.pop();
        }

        public int getRemainingElements() {
            return internalDeque.size();
        }

        public int getRequestedElements() {
            return requested;
        }

        public String getName() {
            return name;
        }

        /** Requests that the number of elements be available to use */
        public void requestUpTo(int count) {
            String call = connectedOutput == null ? "output" : "input";
            System.out.println("State of " + call + " " + name + " for node " + node.getName() + " before requesting " + count);
            ((AbstractNode) node).printState();

            requested = Math.max(requested, count - getRemainingElements());

            System.out.println("State of " + call + " " + name + " for node " + node.getName() + " AFTER requesting " + count);
            ((AbstractNode) node).printState();
        }

        @Override
        public String toString() {
            String out = (connectedOutput == null ? "null" : connectedOutput.node.getName() + "." + connectedOutput.name);

            String[] ins = new String[connectedInputs.size()];
            for (int i = 0; i < ins.length; i++) {
                ins[i] = connectedInputs.get(i).node.getName() + "." + connectedInputs.get(i).name;
            }
            return "requested = " + requested + ", input = " + out + ", outputs = " + Arrays.toString(ins) + ", elements = " + internalDeque;
        }
    }
}
