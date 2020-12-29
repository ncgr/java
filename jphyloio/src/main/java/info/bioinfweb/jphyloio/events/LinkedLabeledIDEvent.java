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



/**
 * Instances of this class model data elements with an ID and an optional label, that link another data
 * element in a phylogenetic document. (Examples would be a sequence or a tree node that links an OTU,
 * an alignment that links an OTU list or a character set that links an alignment.) 
 * 
 * @author Ben St&ouml;ver
 */
public class LinkedLabeledIDEvent extends LabeledIDEvent implements LinkedIDEvent {
	private String linkedID;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param contentType the content type of the modeled data element (e.g. 
	 *        {@link EventContentType#OTU} or {@link EventContentType#SEQUENCE})
	 * @param id the unique ID associated with the represented data element (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label the label of the modeled data element (Maybe {@code null}, if no label is present.)
	 * @param linkedID the ID if a linked data element (Maybe {@code null}, if none is linked.)
	 * @throws NullPointerException if {@code contentType} or {@code id} are {@code null}
	 * @throws IllegalArgumentException if {@code id} or {@code otuID} are not valid 
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCNames</a>
	 */
	public LinkedLabeledIDEvent(EventContentType contentType, String id, String label, String linkedID) {
		super(contentType, id, label);
		
		if (linkedID != null) {
			checkID(linkedID, "linked OTU ID");
		}
		this.linkedID = linkedID;
	}


	/**
	 * Returns the ID of a data element, linked to the element modeled by this event.
	 * 
	 * @return the linked ID or {@code null} if this object does not have an associated data element
	 */
	@Override
	public String getLinkedID() {
		return linkedID;
	}
	
	
	/**
	 * Indicates whether this event links another data element.
	 * 
	 * @return {@code true} if an ID is present, {@code false} otherwise
	 */
	@Override
	public boolean hasLink() {
		return getLinkedID() != null;
	}


	@Override
	public LinkedLabeledIDEvent cloneWithNewIDs(String newEventID, String newLinkedID) {
		LinkedLabeledIDEvent result = (LinkedLabeledIDEvent)cloneWithNewID(newEventID);
		result.linkedID = newLinkedID;
		return result;
	}
}
