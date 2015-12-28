package alexiil.node.core;

public interface INodeAdditionalData extends INode {
    /** @param additionalData Equal data to what has been returned by {@link #addtionalData()}
     * @return A modified version of this node that should have the same state as what what produced
     *         {@link #addtionalData()} */
    INodeAdditionalData modify(String[] additionalData);

    /** @return All additional data that should be saved and fed into {@link #modify(String[])}. The returned array
     *         *must* be the same length (or shorter) than {@link #dataKeys()}. */
    String[] addtionalData();

    /** @return All of the data keys that will be saved in JSON. */
    String[] dataKeys();
}
