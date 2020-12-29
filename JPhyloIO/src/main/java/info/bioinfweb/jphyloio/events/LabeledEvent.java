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
package info.bioinfweb.jphyloio.events;


import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;



public class LabeledEvent extends ConcreteJPhyloIOEvent {
	private String label;	


	public LabeledEvent(EventContentType contentType, String label) {
		super(contentType, EventTopologyType.START);
		
		this.label = label;
	}


	/**
	 * A text label associated with the data element represented by this event.
	 * <p>
	 * Note that additional annotations of that object may be specified by nested metaevents, even if 
	 * this event carries no label.
	 * 
	 * @return the labeling text or {@code null}, if no label was specified for the modeled object
	 */
	public String getLabel() {
		return label;
	}
	
	
	/**
	 * Indicates whether the modeled data element carries a label.
	 * 
	 * @return {@code true} if the data element carries a label, {@code false} otherwise.
	 */
	public boolean hasLabel() {
		return getLabel() != null;
	}
}
