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
package info.bioinfweb.jphyloio.demo.tree;


import info.bioinfweb.commons.collections.NumberedStringsIterator;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreeNode;



/**
 * Abstract implementation of the object list data adapter to be used to provide node and branch events from the
 * business model of this example application to <i>JPhyloIO</i> writers.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <E> the event type of start events representing elements of this list
 */
public abstract class NodeEdgeListDataAdapter<E extends LabeledIDEvent> implements ObjectListDataAdapter<E> {
	private List<TreeNode> nodes;
	private String idPrefix;
	
	
	public NodeEdgeListDataAdapter(List<TreeNode> nodes, String idPrefix) {
		super();
		this.nodes = nodes;
		this.idPrefix = idPrefix;
	}
	
	
	protected List<TreeNode> getNodes() {
		return nodes;
	}


	protected String getIdPrefix() {
		return idPrefix;
	}


	protected abstract E createEvent(String id, int index, TreeNode node);
	
	
	@Override
	public E getObjectStartEvent(ReadWriteParameterMap parameters, String id) throws IllegalArgumentException {
		int index = NumberedStringsIterator.extractIntIndexFromString(id, idPrefix);
		return createEvent(id, index, nodes.get(index));
	}

	
	@Override
	public long getCount(ReadWriteParameterMap parameters) {
		return nodes.size();
	}

	
	@Override
	public Iterator<String> getIDIterator(ReadWriteParameterMap parameters) {
		return new NumberedStringsIterator(idPrefix, nodes.size());
	}

	
	/**
	 * This method could be empty respective to this demo application. It is just implemented as a basis for the metadata demo and
	 * it's content is not relevant for this demo.
	 */
	@Override
	public void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id) 
			throws IOException, IllegalArgumentException {
		
		int index = NumberedStringsIterator.extractIntIndexFromString(id, idPrefix);
		writeContentData(parameters, receiver, id, index, nodes.get(index));
	}
	
	
	/**
	 * This method is empty but declared here to implement it in the metadata demo.
	 */
	protected void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id, int index, 
			TreeNode node) throws IOException, IllegalArgumentException {}  // No node or branch metadata present in the model of this application.
}
