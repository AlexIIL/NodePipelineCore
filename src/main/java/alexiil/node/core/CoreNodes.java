/* Copyright (c) 2015 AlexIIL
 *
 * See the file "LICENSE" for copying permission. */
package alexiil.node.core;

/** Stores all miscellaneous core nodes:
 * 
 * Created on 28 Dec 2015
 *
 * @author AlexIIL */
public class CoreNodes {
    public static final NodeRegistry coreRegistry = new NodeRegistry("core");

    static {

        coreRegistry.setImmutable();
    }
}
