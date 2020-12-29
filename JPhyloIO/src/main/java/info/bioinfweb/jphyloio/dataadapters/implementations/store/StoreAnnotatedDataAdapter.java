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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.AnnotatedDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;



public abstract class StoreAnnotatedDataAdapter<E extends JPhyloIOEvent> implements AnnotatedDataAdapter {
	private List<JPhyloIOEvent> annotations;
	
	
	public StoreAnnotatedDataAdapter() {
		this(null);
	}


	public StoreAnnotatedDataAdapter(List<JPhyloIOEvent> annotations) {
		super();
		
		if (annotations == null) {
			this.annotations = new ArrayList<JPhyloIOEvent>();
		}
		else {
			this.annotations = annotations;
		}
	}


	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {
		for (JPhyloIOEvent annotation : annotations) {
			receiver.add(annotation);
		}		
	}


	public List<JPhyloIOEvent> getAnnotations() {
		return annotations;
	}
}
