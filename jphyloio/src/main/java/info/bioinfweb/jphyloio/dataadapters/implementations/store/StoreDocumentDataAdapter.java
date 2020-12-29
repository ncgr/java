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
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.OrderedMap;
import org.apache.commons.collections4.map.ListOrderedMap;



public class StoreDocumentDataAdapter extends StoreAnnotatedDataAdapter<ConcreteJPhyloIOEvent> implements DocumentDataAdapter {
	private OrderedMap<String, OTUListDataAdapter> otuListsMap;
	private List<MatrixDataAdapter> matrices;
	private List<TreeNetworkGroupDataAdapter> treeNetworkGroups;
	
	
	public StoreDocumentDataAdapter(List<JPhyloIOEvent> annotations, OrderedMap<String, OTUListDataAdapter> otusMap, 
			List<MatrixDataAdapter> matrices, List<TreeNetworkGroupDataAdapter> treeNetworkGroups) {
		
		super(annotations);
		
		if (otusMap == null) {
			this.otuListsMap = new ListOrderedMap<String, OTUListDataAdapter>();
		}
		else {
			this.otuListsMap = otusMap;
		}
		
		if (matrices == null) {
			this.matrices = new ArrayList<MatrixDataAdapter>();
		}
		else {
			this.matrices = matrices;
		}
		
		if (treeNetworkGroups == null) {
			this.treeNetworkGroups = new ArrayList<TreeNetworkGroupDataAdapter>();
		}
		else {
			this.treeNetworkGroups = treeNetworkGroups;
		}
	}
	
	
	/**
	 * Creates a new instance of this class with empty array lists for all properties.
	 * <p>
	 * Using this constructor is equivalent to calling {@link #StoreDocumentDataAdapter(List, List, List)}
	 * with only {@code null} arguments.
	 */
	public StoreDocumentDataAdapter() {
		this(null, null, null, null);
	}
	
	
	public OrderedMap<String, OTUListDataAdapter> getOTUListsMap() {
		return otuListsMap;
	}


	public List<MatrixDataAdapter> getMatrices() {
		return matrices;
	}


	public List<TreeNetworkGroupDataAdapter> getTreesNetworks() {
		return treeNetworkGroups;
	}


	@Override
	public Iterator<OTUListDataAdapter> getOTUListIterator(ReadWriteParameterMap parameters) {
		return otuListsMap.values().iterator();
	}

	
	@Override
	public long getOTUListCount(ReadWriteParameterMap parameters) {
		return otuListsMap.size();
	}


	@Override
	public OTUListDataAdapter getOTUList(ReadWriteParameterMap parameters, String id)	throws IllegalArgumentException {
		return otuListsMap.get(id);
	}


	@Override
	public Iterator<MatrixDataAdapter> getMatrixIterator(ReadWriteParameterMap parameters) {
		return matrices.iterator();
	}

	
	@Override
	public Iterator<TreeNetworkGroupDataAdapter> getTreeNetworkGroupIterator(ReadWriteParameterMap parameters) {
		return treeNetworkGroups.iterator();
	}
}
