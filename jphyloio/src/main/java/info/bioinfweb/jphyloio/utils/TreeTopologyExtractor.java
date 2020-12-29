/*
 * JPhyloIO - Event based parsing and stream writing of multiple sequence alignment and tree formats. 
 * Copyright (C) 2015-2019  Ben Stöver, Sarah Wiechers
 * <http://bioinfweb.info/JPhyloIO>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.jphyloio.utils;


import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.events.EdgeEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



/**
 * Tool class that allows to determine a tree topology from a {@link TreeNetworkDataAdapter}. It is useful for implementations
 * of {@link JPhyloIOEventWriter} for formats with a hierarchical tree representation (e.g. <i>Newick</i> or <i>PhyloXML</i>).
 *  
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public class TreeTopologyExtractor {
	private Map<String, TopoplogicalNodeInfo> idToNodeInfoMap = new HashMap<String, TopoplogicalNodeInfo>();
	private Set<String> networkEdges = new HashSet<String>();
	private String paintStartID;
	

	public TreeTopologyExtractor(TreeNetworkDataAdapter adapter, ReadWriteParameterMap parameters) {
		super();
		fillTopologicalMap(adapter, parameters);
	}
	
	
	private void fillTopologicalMap(TreeNetworkDataAdapter adapter, ReadWriteParameterMap parameters) {
		Set<String> possiblePaintStarts = new HashSet<String>();
		
		// Process node events
		Iterator<String> nodeIDIterator = adapter.getNodes(parameters).getIDIterator(parameters);
		while (nodeIDIterator.hasNext()) {
			String nodeID = nodeIDIterator.next();
			possiblePaintStarts.add(nodeID);
			idToNodeInfoMap.put(nodeID, new TopoplogicalNodeInfo());
		}
		
		// Process edge events
		Iterator<String> edgeIDIterator = adapter.getEdges(parameters).getIDIterator(parameters);
		while (edgeIDIterator.hasNext()) {
			String edgeID = edgeIDIterator.next();
			EdgeEvent edge = adapter.getEdges(parameters).getObjectStartEvent(parameters, edgeID);			
			TopoplogicalNodeInfo sourceNode = idToNodeInfoMap.get(edge.getSourceID());
			TopoplogicalNodeInfo targetNode = idToNodeInfoMap.get(edge.getTargetID());
			
	
			if (targetNode.getParentNodeID() == null) {
				targetNode.setParentNodeID(edge.getSourceID());
				targetNode.setAfferentBranchID(edge.getID());
				
				if (sourceNode != null) {
					sourceNode.getChildNodeIDs().add(edge.getTargetID());
					possiblePaintStarts.remove(edge.getTargetID());  // Nodes that were not referenced as target are possible paint starts
				}
			}
			else {  // Edge is network edge
				networkEdges.add(edge.getID());
			}			
		}		
		
		// Select paint start node
		paintStartID = possiblePaintStarts.iterator().next();			
//		if (possiblePaintStarts.size() == 1) {
//			paintStartID = possiblePaintStarts.iterator().next();			
//		}
//		else {
//			//TODO what to do if more than one or no paint start is available?
//		}
	}


	public Map<String, TopoplogicalNodeInfo> getIDToNodeInfoMap() {
		return idToNodeInfoMap;
	}


	public String getPaintStartID() {
		return paintStartID;
	}


	public Set<String> getNetworkEdgeIDs() {
		return networkEdges;
	}
}
