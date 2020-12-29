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
package info.bioinfweb.jphyloio.dataadapters;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.EmptyAnnotatedDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoSetsTreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.utils.TreeTopologyExtractor;



/**
 * Data adapter interface that provides data for a tree or a network.
 * <p>
 * Applications implementing this adapter may consider to inherit their implementation from 
 * {@link EmptyAnnotatedDataAdapter} or {@link NoSetsTreeNetworkDataAdapter}.
 * <p>
 * Note that {@link TreeTopologyExtractor} can be used by event writer implementations to easily generate a tree 
 * topology from the data provided by this adapter, if {@link #isTree(ReadWriteParameterMap)} returns {@code true}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see TreeTopologyExtractor
 * @see NoSetsTreeNetworkDataAdapter
 */
public interface TreeNetworkDataAdapter extends ElementDataAdapter<LabeledIDEvent> {
	/**
	* Determines whether this instance represents a phylogenetic tree or a phylogenetic network.
	* (Not all formats accept networks.)
	*
	* @param parameters the parameter map of the calling writer that provides context information for the data request
	* @return {@code true} if this instance represents a tree or {@code false} otherwise
	*/
	public boolean isTree(ReadWriteParameterMap parameters);	
	
	/**
	 * Returns the set of nodes contained in this tree or network.
	 * 
	 * @param parameters the parameter map of the calling writer that provides context information for the data request
	 * @return a list of all nodes
	 */
	public ObjectListDataAdapter<NodeEvent> getNodes(ReadWriteParameterMap parameters);
	
	/**
	 * Returns the set of edges contained in this tree or network. All nodes referenced by these edges are contained
	 * on {@link #getEdges(ReadWriteParameterMap)}.
	 * 
	 * @param parameters the parameter map of the calling writer that provides context information for the data request
	 * @return a list of all edges
	 */
	public ObjectListDataAdapter<EdgeEvent> getEdges(ReadWriteParameterMap parameters);
	
	/**
	* Returns a list of node-and-edge-sets defined for the tree modeled by this instance.
	*
	* @param parameters the parameter map of the calling writer that provides context information for the data request
	* @return a (possibly empty) list of node-edge-sets
	*/
	public ObjectListDataAdapter<LinkedLabeledIDEvent> getNodeEdgeSets(ReadWriteParameterMap parameters);
}
