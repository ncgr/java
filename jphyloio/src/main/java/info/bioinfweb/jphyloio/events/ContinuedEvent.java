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
 * Base class for events that have a content that may be split across separate subsequent events, if it is large.
 * It implements a property indicating whether the content of the current event is complete or will be continued
 * in a directly following event.
 * <p>
 * This class differs from {@link PartEndEvent}, which terminates part of a larger event sequence and can be directly
 * instantiated.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class ContinuedEvent extends ConcreteJPhyloIOEvent {
	private boolean continuedInNextEvent;
	

	/**
	 * Returns a new instance of this class with the topology type {@link EventTopologyType#SOLE}.
	 * 
	 * @param contentType the content type of the new instance
	 * @param continuedInNextEvent Specify {@code true} here if this event does not contain the final part of 
	 *        its value and more events are ahead or {@code false otherwise}.
	 */
	public ContinuedEvent(EventContentType contentType,	boolean continuedInNextEvent) {
		super(contentType, EventTopologyType.SOLE);
		this.continuedInNextEvent = continuedInNextEvent;
	}


	/**
	 * Returns whether this event only contains a part of a long text (e.g. comment) and the next event(s) will contain
	 * additional characters from the current text. (The final event of a split comment returns {@code true} here.)
	 * 
	 * @return {@code false} if this event includes the final characters of the current text or {@code true} if future 
	 *         events will contain the remaining characters from the current text
	 */
	public boolean isContinuedInNextEvent() {
		return continuedInNextEvent;
	}
}
