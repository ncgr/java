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



/**
 * Event object that models an ID-based link to an object that is contained in set.
 * <p>
 * Besides {@link #getLinkedID()} this event additionally offers the property {@link #getLinkedObjectType()}. Although IDs are 
 * document wide unique and therefore the linked ID would already sufficiently determine the type of linked object, the 
 * additional property is offered for convenience.
 * <p>
 * Note that in contrast to other implementations of {@link LinkedIDEvent}, the linked ID may never be {@code null} in instances 
 * of this class.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SetElementEvent extends ConcreteJPhyloIOEvent implements LinkedIDEvent {
	private String linkedID;
	private EventContentType linkedObjectType;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param linkedID the ID of the event to be linked to this set
	 * @param linkedObjectType the type of object (event) that is linked by this event
	 * @throws NullPointerException if {@code linkedID} or {@code linkedObjectType} is {@code null}
	 */
	public SetElementEvent(String linkedID, EventContentType linkedObjectType) {
		super(EventContentType.SET_ELEMENT, EventTopologyType.SOLE);
		if (linkedID == null) {
			throw new NullPointerException("The linked ID must not be null. (If a set shall be empty, omit any nested instance of this class.)");
		}
		else if (linkedObjectType == null) {
			throw new NullPointerException("linkedObjectType must not be null.");
		}
		else {
			this.linkedID = linkedID;
			this.linkedObjectType = linkedObjectType;
		}
	}

	
	/**
	 * Returns the ID of the event modeling the member of the set this event is nested in.
	 * 
	 * @return the linked event (Note that links can never be null is instance of this class.)
	 */
	@Override
	public String getLinkedID() {
		return linkedID;
	}

	
	/**
	 * This method is present to fully implement {@link LinkedIDEvent}. In instances of this class, it will always return {@code true}
	 * since set elements not linking any element are not allowed.
	 * 
	 * @return always {@code true} in instances of this class
	 */
	@Override
	public boolean hasLink() {
		return linkedID != null;
	}


	/**
	 * Returns the type of event that is linked to this set. Some sets may contain different types of objects, e.g. a 
	 * {@link EventContentType#TREE_NETWORK_SET} may contain links to trees and networks. This property determines which
	 * object is linked here.
	 * 
	 * @return the content type of the linked object
	 */
	public EventContentType getLinkedObjectType() {
		return linkedObjectType;
	}


	@Override
	public SetElementEvent cloneWithNewIDs(String newEventID, String newLinkedID) {
		SetElementEvent result = (SetElementEvent)clone();
		result.linkedID = newLinkedID;
		return result;
	}
}
