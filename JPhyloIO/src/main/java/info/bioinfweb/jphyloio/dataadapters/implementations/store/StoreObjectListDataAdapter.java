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
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.map.ListOrderedMap;



/**
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 *
 * @param <E>
 */
public class StoreObjectListDataAdapter<E extends LabeledIDEvent> implements ObjectListDataAdapter<E> {	
	private ListOrderedMap<String, StoreObjectData<E>> objectMap = new ListOrderedMap<String, StoreObjectData<E>>();
	

	public ListOrderedMap<String, StoreObjectData<E>> getObjectMap() {
		return objectMap;
	}


	@Override
	public E getObjectStartEvent(ReadWriteParameterMap parameters, String id)	throws IllegalArgumentException {
		StoreObjectData<E> data = objectMap.get(id);
		if (data != null) {
			return data.getObjectStartEvent();
		}
		else {
			throw new IllegalArgumentException("No event with the ID \"" + id + "\" was found.");
		}
	}
	
	
	public void setObjectStartEvent(E event) throws IllegalArgumentException {
		StoreObjectData<E> data = objectMap.get(event.getID());
		if (data != null) {
			data.setObjectStartEvent(event);
		}
		else {
			data = new StoreObjectData<E>(event);
			objectMap.put(event.getID(), data);
		}
	}
	
	
	public List<JPhyloIOEvent> getObjectContent(String id) {
		return objectMap.get(id).getObjectContent();
	}

	
	@Override
	public long getCount(ReadWriteParameterMap parameters) {
		return objectMap.size();
	}

	
	@Override
	public Iterator<String> getIDIterator(ReadWriteParameterMap parameters) {
		return objectMap.keyList().iterator();
	}

	
	@Override
	public void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id) throws IOException, IllegalArgumentException {
		StoreObjectData<E> data = objectMap.get(id);
		if (data != null) {
			for (JPhyloIOEvent event : objectMap.get(id).getObjectContent()) {
				receiver.add(event);
			}
		}
		else {
			throw new IllegalArgumentException("No object with the ID \"" + id + "\" was found.");
		}
	}	
}
