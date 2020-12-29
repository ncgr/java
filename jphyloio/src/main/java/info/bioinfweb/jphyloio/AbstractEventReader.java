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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import info.bioinfweb.commons.LongIDManager;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.events.type.EventType;
import info.bioinfweb.jphyloio.push.JPhyloIOEventListener;
import info.bioinfweb.jphyloio.utils.SequenceTokensEventManager;



/**
 * Basic implementation for event readers in PhyloIO.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractEventReader<P extends ReaderStreamDataProvider<? extends AbstractEventReader<P>>> 
		implements JPhyloIOEventReader, ReadWriteConstants {
	
	private JPhyloIOEvent next = null;
	private JPhyloIOEvent previous = null;
	private JPhyloIOEvent lastNonComment = null;
	private ParentEventInformation parentEventInformation = new ParentEventInformation();
	private P streamDataProvider;  // Must not be set to anything here.
	private Queue<JPhyloIOEvent> upcomingEvents = new LinkedList<JPhyloIOEvent>();
	private Stack<Collection<JPhyloIOEvent>> eventCollections = new Stack<Collection<JPhyloIOEvent>>();
	private boolean beforeFirstAccess = true;
	private boolean dataSourceClosed = false;
	private ReadWriteParameterMap parameters;
	private LongIDManager idManager = new LongIDManager();
	private SequenceTokensEventManager sequenceTokensEventManager;
	private List<JPhyloIOEventListener> eventListeners = new ArrayList<JPhyloIOEventListener>();

	
	public AbstractEventReader(ReadWriteParameterMap parameters, String matchToken) {
		super();
		this.parameters = parameters;
		
		sequenceTokensEventManager = new SequenceTokensEventManager(this, matchToken);
		streamDataProvider = createStreamDataProvider();
		eventCollections.add(upcomingEvents);
	}
	
	
	/**
	 * The returned object provides information on the start events fired by this reader until now. It allows
	 * applications to determine information on the nesting of the current event.
	 * 
	 * @return the parent information object
	 */
	@Override
	public ParentEventInformation getParentInformation() {
		return parentEventInformation;
	}


	/**
	 * This method is called in the constructor of {@link AbstractEventReader} to initialize the stream
	 * data provider that will be returned by {@link #getStreamDataProvider()}. Inherit classes that use
	 * their own stream data provider implementation should overwrite this method.
	 * <p>
	 * This default implementation creates a new instance of {@link ReaderStreamDataProvider}.
	 * 
	 * @return the stream data provider to be used with this instance
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected P createStreamDataProvider() {
		return (P)new ReaderStreamDataProvider(this);  // Cannot be created generic, since this implementation is used by different inherited classes.
	}

	
	protected P getStreamDataProvider() {
		return streamDataProvider;
	}
	
	
	protected ReadWriteParameterMap getParameters() {
		return parameters;
	}


	/**
	 * Removes the event collection at the top of the eventCollections stack and returns it.
	 * 
	 * @return the removed event collection
	 */
	protected Collection<JPhyloIOEvent> resetCurrentEventCollection() {
		return eventCollections.pop();
	}
	
	
	/**
	 * Adds a new current event collection to the stack.
	 * 
	 * @param newCollection the new collection to take up new events from now on 
	 * @return the event collection that was previously at the top of the stack
	 * @throws NullPointerException if {@code newCollection} is {@code null}
	 */
	protected Collection<JPhyloIOEvent> setCurrentEventCollection(Collection<JPhyloIOEvent> newCollection) {
		if (newCollection == null) {
			throw new NullPointerException("The current event collection must not be null.");
		}
		else {
			Collection<JPhyloIOEvent> previous = eventCollections.peek();
			eventCollections.add(newCollection);
			return previous;
		}
	}
	
	
	/**
	 * Returns the event collection that is currently used to take up new events.
	 * 
	 * @return the current event collection
	 */
	protected Collection<JPhyloIOEvent> getCurrentEventCollection() {
		return eventCollections.peek();
	}
	
	
	/**
	 * Determines whether the current event collection is different from the queue of upcoming events.
	 * 
	 * @return {@code false} if {@link #getCurrentEventCollection()} returns the same instance as {@link #getUpcomingEvents()}
	 *         or {@code true} otherwise
	 */
	protected boolean hasSpecialEventCollection() {
		return upcomingEvents != eventCollections.peek();  // equals() does not make sense here, because //TODO why?
	}
	
	
	/**
	 * Returns the queue to store events in, that shall be returned by upcoming calls of {@link #next()}.
	 * <p>
	 * Note that new events should generally be added to {@link #getCurrentEventCollection()} instead.
	 * 
	 * @return the queue of upcoming events
	 */
	protected Queue<JPhyloIOEvent> getUpcomingEvents() {
		return upcomingEvents;
	}


	protected LongIDManager getIDManager() {
		return idManager;
	}


	/**
	 * Returns the manager object used by this instance to create {@link SequenceTokensEvent}s. Such events should
	 * always be created using the returned object by inherited classes and never directly. 
	 * 
	 * @return the character event manager used by this instance (never {@code null})
	 */
	protected SequenceTokensEventManager getSequenceTokensEventManager() {
		return sequenceTokensEventManager;
	}
	
	
	protected void fireEvent(JPhyloIOEvent event) throws IOException {
		for (JPhyloIOEventListener eventListener : eventListeners.toArray(new JPhyloIOEventListener[eventListeners.size()])) {  // List must be copied to avoid ConcurrentModificationException in case listeners that are part of the list remove themselves from it
			eventListener.processEvent(this, event);
		}
	}


	/**
	 * Returns the event that has been returned by the previous call of {@link #readNextEvent()}.
	 * 
	 * @return the previous event or {@code null} if there was no previous call of {@link #readNextEvent()}
	 */
	public JPhyloIOEvent getPreviousEvent() {
		return previous;
	}
	
	
	/**
	 * Returns the last event that has been returned by previous calls of {@link #readNextEvent()}
	 * that was not a comment event.
	 * 
	 * @return the last non-comment event or {@code null} if no non-comment event was returned until now
	 */
	public JPhyloIOEvent getLastNonCommentEvent() {
		return lastNonComment;
	}


	/**
	 * Indicates whether there have been any previous calls of {@link #readNextEvent()} since the last call
	 * of {@link #reset()}.
	 * <p>
	 * This method in meant for internal use in {@link #readNextEvent()}.
	 * 
	 * @return {@code true} if there were no previous calls, {@code false} otherwise
	 */
	protected boolean isBeforeFirstAccess() {
		return beforeFirstAccess;
	}

	
	private JPhyloIOEvent getNextEventFromQueue() throws IOException {
		if (getUpcomingEvents().isEmpty()) {
			readNextEvent();
		}
		return getUpcomingEvents().poll();  // May still return null, if no further event could be added by readNextEvent().
	}
	
	
	@Override
	public boolean hasNextEvent() throws IOException {
		ensureFirstEvent();
		return !dataSourceClosed && (next != null);
	}

	
	@Override
	public JPhyloIOEvent next() throws IOException {
		// ensureFirstEvent() is called in hasNextEvent()
		if (!hasNextEvent()) {
			throw new NoSuchElementException("The end of the document was already reached.");
		}
		else {
			if ((previous != null) && previous.getType().getTopologyType().equals(EventTopologyType.START)) {
				parentEventInformation.add(previous);
			}
			if ((next != null) && next.getType().getTopologyType().equals(EventTopologyType.END)) {
				parentEventInformation.pop();
			}
			
			previous = next;  // previous needs to be set before readNextEvent() is called, because it could be accessed in there.
			if (!(previous instanceof CommentEvent)) {  // Also works for possible future subelements of CommentEvent
				lastNonComment = previous;
			}
			next = getNextEventFromQueue();
			fireEvent(previous);
			return previous;
		}
	}


	@Override
	public JPhyloIOEvent nextOfType(Set<EventType> types) throws IOException {
		try {
			JPhyloIOEvent result = next();
			while (!types.contains(result.getType())) {
				result = next();
			}
			return result;
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}


	@Override
	public JPhyloIOEvent peek() throws IOException {
		// ensureFirstEvent() is called in hasNextEvent()
		if (!hasNextEvent()) {  //
			throw new NoSuchElementException("The end of the document was already reached.");
		}
		else {
			return next;
		}
	}


	private void ensureFirstEvent() throws IOException {
		if (beforeFirstAccess) {
			next = getNextEventFromQueue();
			beforeFirstAccess = false;
		}
	}
	
	
	@Override
	public void addEventListener(JPhyloIOEventListener listener) {
		eventListeners.add(listener);		
	}


	@Override
	public void removeEventListener(JPhyloIOEventListener listener) {
		eventListeners.remove(listener);		
	}


	/**
	 * Method to be implemented be inherited classes that adds at least one additional event (determined from 
	 * the underlying data source) to the event queue.
	 */
	protected abstract void readNextEvent() throws IOException;


	@Override
	public void close() throws IOException {
		dataSourceClosed = true;
	}
}
