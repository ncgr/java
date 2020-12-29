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
package info.bioinfweb.jphyloio;


import info.bioinfweb.commons.LongIDManager;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.formats.newick.NewickStringReader;
import info.bioinfweb.jphyloio.utils.SequenceTokensEventManager;

import java.util.Collection;



/**
 * Stream data providers are objects used by helper classes of JPhyloIO event readers (e.g. command readers for Nexus,
 * {@link NewickStringReader} or element readers for XML formats). They have two major functions:
 * <ol>
 *   <li>Delegate protected properties of their associated event reader to allow accessing them by helper classes
 *       in other packages.</li>
 *   <li>Act as a repository for data that shall be shared among different helper classes of a reader.</li>
 * </ol>
 * 
 * @author Ben St&ouml;ver
 * @see AbstractEventReader#getStreamDataProvider()
 * @see AbstractEventReader#createStreamDataProvider()
 */
public class ReaderStreamDataProvider<R extends AbstractEventReader<? extends ReaderStreamDataProvider<R>>> {
	private R eventReader;
	
	
	public ReaderStreamDataProvider(R eventReader) {
		super();
		this.eventReader = eventReader;
	}


	public R getEventReader() {
		return eventReader;
	}
	
	
	/**
	 * Removes the event collection at the top of the eventCollections stack and returns it.
	 * 
	 * @return the removed event collection
	 */
	public Collection<JPhyloIOEvent> resetCurrentEventCollection() {
		return getEventReader().resetCurrentEventCollection();
	}
	
	
	/**
	 * Adds a new current event collection to the stack.
	 * 
	 * @param newCollection the new collection to take up new events from now on 
	 * @return the event collection that was previously at the top of the stack
	 * @throws NullPointerException if {@code newCollection} is {@code null}
	 */
	public Collection<JPhyloIOEvent> setCurrentEventCollection(Collection<JPhyloIOEvent> newCollection) {
		return getEventReader().setCurrentEventCollection(newCollection);
	}
	
	
	/**
	 * Returns the event collection that is currently used to take up new events.
	 * 
	 * @return the current event collection
	 */
	public Collection<JPhyloIOEvent> getCurrentEventCollection() {
		return getEventReader().getCurrentEventCollection();
	}
	
	
	/**
	 * Determines whether the current event collection is different from the queue of upcoming events.
	 * 
	 * @return {@code false} if {@link #getCurrentEventCollection()} returns the same instance as {@link #getUpcomingEvents()}
	 *         or {@code true} otherwise
	 */
	public boolean hasSpecialEventCollection() {
		return getEventReader().hasSpecialEventCollection(); 
	}
	

	/**
	 * Checks whether events are waiting the event queue of the associated event reader.
	 * <p>
	 * Note that this event queue maybe different from the {@link #getCurrentEventCollection()}. Therefore
	 * this method may return {@code false}, even if an event has just been added.
	 * 
	 * @return {@code true} if at least one event is wainting, {@code false} otherwise
	 */
	public boolean eventsUpcoming() {
		return !getEventReader().getUpcomingEvents().isEmpty();
	}
	
	
	/**
	 * Returns the sequence tokens event manager of the associated reader.
	 * 
	 * @return the sequence tokens event manager
	 */
	public SequenceTokensEventManager getSequenceTokensEventManager() {
		return getEventReader().getSequenceTokensEventManager();
	}

	
	/**
	 * Returns the parameter map that was specified when the constructor of the associated reader was called.
	 * 
	 * @return the parameter map of the associated reader
	 */
	public ReadWriteParameterMap getParameters() {
		return getEventReader().getParameters();
	}
	

	/**
	 * Returns the ID manager of the associated reader.
	 * 
	 * @return the ID manager to be used to create unique IDs for generated events
	 */
	public LongIDManager getIDManager() {
		return getEventReader().getIDManager();
	}
}
