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

import org.semanticweb.owlapi.io.XMLUtils;



/**
 * Event that indicate data objects that carry an unique ID and a label.
 * 
 * @author Ben St&ouml;ver
 */
public class LabeledIDEvent extends LabeledEvent {
	private String id;
	
	
	/**
	 * Creates a new instance of this class. Instances are always start events.
	 * 
	 * @param contentType the content type of the event
	 * @param id the unique ID associated with the represented data element (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label a label associated with the represented data element (Maybe {@code null}.)
	 * @throws NullPointerException if {@code contentType}, {@code topologyType} or {@code id} are {@code null}
	 * @throws IllegalArgumentException if the specified ID is not a valid 
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>
	 */
	public LabeledIDEvent(EventContentType contentType, String id, String label) {
		super(contentType, label);
		
		checkID(id, "ID");
		this.id = id;
	}


	protected void checkID(String id, String idName) {
		if (id == null) {
			throw new NullPointerException("The " + idName + " of this event must not be null.");
		}
		else if ("".equals(id)) {
			throw new IllegalArgumentException("The " + idName + " of this event must not be an empty string.");
		}
		else if (!XMLUtils.isNCName(id)) {
			throw new IllegalArgumentException("The " + idName + " (\"" + id + "\") of this event is not a valid NCName.");
		}
	}


	/**
	 * Returns the document-wide unique ID of the data element represented by this event. 
	 * 
	 * @return a string ID without whitespace and never {@code null}
	 */
	public String getID() {
		return id;
	}

	
	public LabeledIDEvent cloneWithNewID(String newID) {
		LabeledIDEvent result = (LabeledIDEvent)clone();
		result.id = newID;
		return result;
	}
}
