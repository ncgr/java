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


import info.bioinfweb.jphyloio.events.JPhyloIOEvent;

import java.util.ArrayList;
import java.util.List;



public class StoreObjectData<E extends JPhyloIOEvent> {
	private E objectStartEvent;
	private List<JPhyloIOEvent> objectContent = new ArrayList<JPhyloIOEvent>();
	
	
	public StoreObjectData(E objectStartEvent) {
		this(objectStartEvent, null);
	}
	
	
	public StoreObjectData(E objectStartEvent, List<JPhyloIOEvent> objectContent) {
		super();
		this.objectStartEvent = objectStartEvent;
		
		if (objectContent == null) {
			this.objectContent = new ArrayList<JPhyloIOEvent>();
		}
		else {
			this.objectContent = objectContent;
		}
	}


	public E getObjectStartEvent() {
		return objectStartEvent;
	}


	public void setObjectStartEvent(E objectStartEvent) {
		this.objectStartEvent = objectStartEvent;
	}


	public List<JPhyloIOEvent> getObjectContent() {
		return objectContent;
	}	
}