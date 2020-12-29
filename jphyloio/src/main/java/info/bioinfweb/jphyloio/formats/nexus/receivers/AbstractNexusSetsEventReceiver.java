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
package info.bioinfweb.jphyloio.formats.nexus.receivers;


import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.exception.IllegalEventException;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.formats.nexus.NexusEventWriter;
import info.bioinfweb.jphyloio.formats.nexus.NexusWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * General implementation for event receivers writing Nexus set commands.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AbstractNexusSetsEventReceiver extends AbstractNexusEventReceiver {
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param streamDataProvider the stream data provider of the calling {@link NexusEventWriter}.
	 */
	public AbstractNexusSetsEventReceiver(NexusWriterStreamDataProvider streamDataProvider) {
		super(streamDataProvider);
	}

	
	/**
	 * This method is called every time a {@link CharacterSetIntervalEvent} is encountered on the top level by
	 * this receiver.
	 * <p>
	 * This default implementation always returns {@code false} that leads to an {@link IllegalEventException}.
	 * Inherited classes that support {@link CharacterSetIntervalEvent}s should overwrite this method.
	 * 
	 * @param event the character set interval event that was encountered on the top level by this receiver
	 * @return {@code true} if the encountered event is legal at this position or {@code false} otherwise 
	 * @throws IOException if an I/O error occurs while trying to write the contents of the event (This cannot
	 *         happen in this default implementation, but may happen in overwritten implementations.)
	 */
	protected boolean handleCharacterSetInterval(CharacterSetIntervalEvent event) throws IOException {
		return false;  // Will throw exception.
	}
	
	
	/**
	 * This method is called every time a {@link SetElementEvent} is encountered on the top level by
	 * this receiver and must be implemented by inherited classes.
	 * 
	 * @param event the set element event that was encountered on the top level by this receiver
	 * @return {@code true} if the encountered event is legal at this position or {@code false} otherwise 
	 *         (If {@code false} is returned by inherited classes, an {@link IllegalEventException} will
	 *         be thrown as a result afterwards.)
	 * @throws IOException if an I/O error occurs while trying to write the contents of the event
	 */
	protected abstract boolean handleSetElement(SetElementEvent event) throws IOException;
	
	
	protected void writeElementReference(SetElementEvent event) throws IOException {
		getStreamDataProvider().getWriter().write(' ');
		String referencedSet = getParameterMap().getLabelEditingReporter().getEditedLabel(event.getLinkedObjectType(), event.getLinkedID());
		if (referencedSet == null) {
			throw new InconsistentAdapterDataException("A set references the other element with the ID " + event.getLinkedID() 
					+ " that was not previously written (or is known at all). Referencing (currently) undeclared elements is not possible in Nexus.");  //TODO If a subsequent set is referenced here, it could alternatively be fetched from the provider and be written directly here instead of referencing it.
		}
		else {
			getStreamDataProvider().getWriter().write(NexusEventWriter.formatToken(referencedSet));
		}
	}
	
	
	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IOException, XMLStreamException {
		boolean result = false;
		if (getParentEvent() == null) {  // Such events are only allowed on the top level.
			switch (event.getType().getContentType()) {  // No check for topology type necessary, since only SOLE is possible.
				case CHARACTER_SET_INTERVAL:
					result = handleCharacterSetInterval(event.asCharacterSetIntervalEvent());
					break;
				case SET_ELEMENT:
					result = handleSetElement(event.asSetElementEvent());
					break;
				default:  // Throw exception below for illegal events. (Comment and metadata events have already been handled by superclasses.)
					break;
			}
		}
		
		if (result) {
			return true;
		}
		else {
			throw IllegalEventException.newInstance(this, getParentEvent(), event);
		}
	}
}
