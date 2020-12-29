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
package info.bioinfweb.jphyloio.formats;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;

import java.util.Collection;



/**
 * This class is used by {@link JPhyloIOEventReader}s processing phylogenetic trees or networks to model nodes and edges.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 *
 */
public class NodeEdgeInfo {
	private String id;
	private String label;
	private boolean isRoot = false;
	
	private String source;
	private String target;
	private double length;
	
	private Collection<JPhyloIOEvent> nestedNodeEvents;
	private Collection<JPhyloIOEvent> nestedEdgeEvents;

	
	public NodeEdgeInfo(String id, double length, Collection<JPhyloIOEvent> nestedNodeEvents, Collection<JPhyloIOEvent> nestedEdgeEvents) {
		super();
		this.id = id;
		this.length = length;
		this.nestedNodeEvents = nestedNodeEvents;
		this.nestedEdgeEvents = nestedEdgeEvents;
	}
	

	public String getID() {
		return id;
	}
	

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public boolean isRoot() {
		return isRoot;
	}


	public void setIsRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}


	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public String getTarget() {
		return target;
	}


	public void setTarget(String target) {
		this.target = target;
	}


	public double getLength() {
		return length;
	}
	

	public void setLength(double length) {
		this.length = length;
	}


	public Collection<JPhyloIOEvent> getNestedNodeEvents() {
		return nestedNodeEvents;
	}


	public Collection<JPhyloIOEvent> getNestedEdgeEvents() {
		return nestedEdgeEvents;
	}
}
