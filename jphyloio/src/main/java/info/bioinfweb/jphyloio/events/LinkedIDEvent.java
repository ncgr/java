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



/**
 * Interface to be implemented by event objects that can link previous events by their ID.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface LinkedIDEvent extends JPhyloIOEvent {
	/**
	 * Returns the ID of a data element, linked to the element modeled by this event.
	 * 
	 * @return the linked ID or {@code null} if this object does not have an associated data element
	 */
	public String getLinkedID();
	
	/**
	 * Indicates whether this event links another data element.
	 * 
	 * @return {@code true} if an ID is present, {@code false} otherwise
	 */
	public boolean hasLink();
	
	/**
	 * Creates a copy of this instance with a new ID and new linked ID.
	 * 
	 * @param newEventID the new ID for the event to be returned
	 * @param newLinkedID the new ID linked by the event to be returned
	 * @return the new event with the specified IDs
	 * @since 2.0.0
	 */
	public LinkedIDEvent cloneWithNewIDs(String newEventID, String newLinkedID);
}
