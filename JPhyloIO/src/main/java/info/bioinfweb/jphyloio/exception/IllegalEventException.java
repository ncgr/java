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
package info.bioinfweb.jphyloio.exception;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;



/**
 * This exception in thrown by implementations of {@link JPhyloIOEventReceiver} and indicates that a data adapter passed
 * an event that is illegal at the current position according to the grammar defined in the documentation of 
 * {@link JPhyloIOEventReader}.
 * <p>
 * The cause for this exception would usually be an error in the implementation of a data adapter.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class IllegalEventException extends InconsistentAdapterDataException {
	private static final long serialVersionUID = 1L;
	
	
	private JPhyloIOEventReceiver receiver;
	private JPhyloIOEvent parentEvent;
	private JPhyloIOEvent invalidEvent;
	
	
	/**
	 * Tool method that creates a new instance of this class. It uses the specified parent event. If that
	 * parent event should be {@code null}, a different exception message will be used.
	 * 
	 * @param receiver the receiver throwing the returned exception
	 * @param parentEvent the current parent event or {@code null}
	 * @param invalidEvent the invalid element that was encountered
	 * @return the new instance
	 */
	public static IllegalEventException newInstance(JPhyloIOEventReceiver receiver, JPhyloIOEvent parentEvent, JPhyloIOEvent invalidEvent) {
		// This method is provided instead of an additional constructor, since it would not be possible to execute code to generate 
		// the message before calling the super constructor.
		
		String message;
		if (parentEvent == null) {
			message = "An event of the type " + invalidEvent.getType().getContentType() + " was encountered under the root event of this receiver " + 
					"which is invalid here.";
		}
		else {
			message = "An event of the type " + invalidEvent.getType().getContentType() + " was encountered under an event of the type " + 
					parentEvent.getType() + " which is invalid in this receiver.";
		}
		return new IllegalEventException(message, receiver, parentEvent, invalidEvent);
	}
	
	
	public IllegalEventException(String message, JPhyloIOEventReceiver receiver, JPhyloIOEvent parentEvent, JPhyloIOEvent invalidEvent) {
		super(message);
		
		if (receiver == null) {
			throw new NullPointerException("receiver must not be null");
		}
		else if (invalidEvent == null) {
			throw new NullPointerException("invalidEvent must not be null");
		}  // parentEvent may be null.
		else {
			this.receiver = receiver;
			this.parentEvent = parentEvent;
			this.invalidEvent = invalidEvent;
		}
	}


	public JPhyloIOEventReceiver getReceiver() {
		return receiver;
	}


	public JPhyloIOEvent getParentEvent() {
		return parentEvent;
	}
	
	
	public boolean wasAtReceiverRootLevel() {
		return parentEvent == null;
	}


	public JPhyloIOEvent getInvalidEvent() {
		return invalidEvent;
	}
}
