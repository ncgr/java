package org.ncgr.pangenomics;

import org.jgrapht.*;
import org.jgrapht.io.*;

import java.util.*;

/**
 * Provides a node by implementing VertexProvider, in case a method requires a VertexProvider.
 * The id is the string version of the long node id, the sole attributes entry is the sequence keyed by "sequence".
 *
 * @author Sam Hokin
 */
public class NodeProvider implements VertexProvider<Node> {
    public Node buildVertex(String id, Map<String,Attribute> attributes) {
        String sequence = attributes.get("sequence").getValue();
        long nodeId = Long.parseLong(id);
        return new Node(nodeId, sequence);
    }
}
    
