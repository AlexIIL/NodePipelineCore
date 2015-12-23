package alexiil.node.core;

import java.util.List;

import com.google.common.collect.Lists;

import alexiil.node.core.INode.IInput;
import alexiil.node.core.INode.IOutput;

public class NodeGraph {
    private final List<INode> nodes = Lists.newArrayList();

    public <N extends INode, O extends N> N addCopyOf(N node) {
        INode copy = node.createCopy();
        addNode(copy);
        return (N) copy;
    }

    public void addNode(INode node) {
        if (node == null)
            throw new NullPointerException("node");
        if (nodes.contains(node))
            throw new IllegalArgumentException("Already contained the node!");
        nodes.add(node);
    }

    public void connectIO(INode fromNode, String fromName, INode toNode, String toName) {
        if (nodes.contains(fromNode) && nodes.contains(toNode)) {
            if (nodes.indexOf(fromNode) >= nodes.indexOf(toNode))
                throw new IllegalArgumentException("Bad order!");
            if (!fromNode.getOutputs().containsKey(fromName))
                throw new IllegalArgumentException("Did not contain the output key " + fromName);
            if (!toNode.getInputs().containsKey(toName))
                throw new IllegalArgumentException("Did not contain the input key " + toName);

            // Ignore the generic type warning, its fine :)
            IOutput out = fromNode.getOutputs().get(fromName);
            IInput in = toNode.getInputs().get(toName);
            if (in.getInputClass().isAssignableFrom(out.getOutputClass()))
                connect(out, in, in.getInputClass());
            else
                throw new IllegalArgumentException("The nodes did not share a common class!");
        }
    }

    private <T> void connect(IOutput<? extends T> out, IInput<T> in, Class<T> inClazz) {
        out.connect(in);
    }

    public void printState() {
        for (INode node : nodes) {
            AbstractNode abs = (AbstractNode) node;
            System.out.println("Node #" + nodes.indexOf(node) + " called " + abs.name);
            abs.printState();
        }
    }
}