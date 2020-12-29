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
package info.bioinfweb.jphyloio.dataadapters.implementations;


import java.util.Collections;
import java.util.Iterator;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;



/**
 * An implementation of {@link DocumentDataAdapter} that models an empty document.
 * <p>
 * Adapters that need to implement only some of the methods of {@link DocumentDataAdapter} could be inherited from this class.  
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class EmptyDocumentDataAdapter extends EmptyAnnotatedDataAdapter<ConcreteJPhyloIOEvent> implements DocumentDataAdapter {
	@Override
	public Iterator<OTUListDataAdapter> getOTUListIterator(ReadWriteParameterMap parameters) {
		return Collections.emptyIterator();
	}
	

	@Override
	public long getOTUListCount(ReadWriteParameterMap parameters) {
		return 0;
	}

	
	@Override
	public OTUListDataAdapter getOTUList(ReadWriteParameterMap parameters, String id) throws IllegalArgumentException {
		throw new IllegalArgumentException("No OTU list with the id " + id + " could be found.");
	}

	
	@Override
	public Iterator<MatrixDataAdapter> getMatrixIterator(ReadWriteParameterMap parameters) {
		return Collections.emptyIterator();
	}

	
	@Override
	public Iterator<TreeNetworkGroupDataAdapter> getTreeNetworkGroupIterator(ReadWriteParameterMap parameters) {
		return Collections.emptyIterator();
	}
}
