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
package info.bioinfweb.jphyloio.dataadapters.implementations.store;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class StoreTreeNetworkGroupDataAdapter extends StoreAnnotatedDataAdapter<LinkedLabeledIDEvent> implements TreeNetworkGroupDataAdapter {
	private LinkedLabeledIDEvent startEvent;
	private List<TreeNetworkDataAdapter> treesAndNetworks = new ArrayList<TreeNetworkDataAdapter>();
	private StoreObjectListDataAdapter<LinkedLabeledIDEvent> treeAndNetworkSets = new StoreObjectListDataAdapter<LinkedLabeledIDEvent>();
	
	
	public StoreTreeNetworkGroupDataAdapter(LinkedLabeledIDEvent treeOrNetworkGroupStartEvent, List<JPhyloIOEvent> annotations) {
		super(annotations);
		this.startEvent = treeOrNetworkGroupStartEvent;
	}


	@Override
	public LinkedLabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return startEvent;
	}


	@Override
	public Iterator<TreeNetworkDataAdapter> getTreeNetworkIterator(ReadWriteParameterMap parameters) {
		return treesAndNetworks.iterator();
	}


	public List<TreeNetworkDataAdapter> getTreesAndNetworks() {
		return treesAndNetworks;
	}


	@Override
	public StoreObjectListDataAdapter<LinkedLabeledIDEvent> getTreeSets(ReadWriteParameterMap parameters) {
		return treeAndNetworkSets;
	}
}