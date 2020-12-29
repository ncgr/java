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
package info.bioinfweb.jphyloio.push;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;



/**
 * Tool class that consumes all events provided by an implementation of {@link JPhyloIOEventReader} and forwards them
 * to one or more {@link JPhyloIOEventListener}s.
 * 
 * @author Ben St&ouml;ver
 */
public class EventForwarder {
	private List<JPhyloIOEventListener> listeners = new ArrayList<JPhyloIOEventListener>();
	
	
	/**
	 * Returns the list of listeners to which this instance forwards its consumed events.
	 * 
	 * @return the modifiable listener list
	 */
	public List<JPhyloIOEventListener> getListeners() {
		return listeners;
	}


	/**
	 * Consumes all available events from the specified listener and forwards them to the registered listeners.
	 * 
	 * @param reader the reader to read the events from
	 * @throws IOException if {@code reader} throws an I/O exception while parsing
	 */
	public void readAll(JPhyloIOEventReader reader) throws IOException {
		doReadUntil(reader, null);
	}
	
	
	/**
	 * Consumes all available events from the specified listener and forwards them to the registered listeners
	 * until an event of the specified type is reached. (The event of the specified type is not consumed.)
	 * 
	 * @param reader the reader to read the events from
	 * @param type the type of the event that shall trigger the end of reading
	 * @throws IOException if {@code reader} throws an I/O exception while parsing
	 */
	public void readUntil(JPhyloIOEventReader reader, EventContentType type) throws IOException {
		doReadUntil(reader, EnumSet.of(type));
	}
	
	
	/**
	 * Consumes all available events from the specified listener and forwards them to the registered listeners
	 * until any event of one of the specified types is reached. (The event of the specified type is not consumed.)
	 * 
	 * @param reader the reader to read the events from
	 * @param types a set of types of the events that shall trigger the end of reading
	 * @throws IOException if {@code reader} throws an exception while parsing
	 */
	public void readUntil(JPhyloIOEventReader reader, Set<EventContentType> types) throws IOException {
		doReadUntil(reader, types);
	}
	
	
	/**
	 * Consumes all events between a start and its respective end event. The reader must be positioned at a start event
	 * when calling this method and will consume all events including the respective end event on the same level.
	 * 
	 * @param reader the reader to read the events from
	 * @throws IOException if {@code reader} throws an I/O exception while parsing
	 */
	public void readCurrentNode(JPhyloIOEventReader reader) throws IOException {
		if (reader.hasNextEvent() && reader.peek().getType().getTopologyType().equals(EventTopologyType.START)) {
			fireNextEvent(reader);  // Consume start event to increase parent count.
			int parentCount = reader.getParentInformation().size();
			do {
				fireNextEvent(reader);  // Would throw an exception, if an end event for a start event is missing.
			} while (parentCount < reader.getParentInformation().size());
		}
		else {
			throw new IllegalArgumentException("The specified reader is not positioned at the start event.");
		}
	}	
	
	
	private void doReadUntil(JPhyloIOEventReader reader, Set<EventContentType> types) throws IOException {
		while (reader.hasNextEvent() && ((types == null) || !types.contains(reader.peek().getType()))) {
			fireNextEvent(reader);
		}
	}
	
	
	private void fireNextEvent(JPhyloIOEventReader reader) throws IOException {
		JPhyloIOEvent event = reader.next();
		for (JPhyloIOEventListener listener : listeners) {
			listener.processEvent(reader, event);
		}
	}
}
