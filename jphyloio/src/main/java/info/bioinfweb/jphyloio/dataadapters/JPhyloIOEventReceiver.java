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
package info.bioinfweb.jphyloio.dataadapters;


import java.io.IOException;
import java.io.Writer;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.IllegalEventException;



/**
 * Implementations of this interface are used by <i>JPhyloIO</i> writers to receive events
 * from the application (generated from its business model).
 * 
 * @author Ben St&ouml;ver
 */
public interface JPhyloIOEventReceiver {
	/**
	 * Adds a new event to the sequence written to this receiver.
	 * <p>
	 * Note that this acceptor indicates whether writing events shall be aborted by its return value.
	 * This maybe the case, if the writer providing this acceptor was seeking for a specific piece of
	 * information and is (currently) not interested in the remaining events. Data adapter implemented
	 * by applications should consider the return value to allow saving runtime. If event writing goes
	 * on after {@code false} was returned, subsequent events will be ignored but no exception will be
	 * thrown.
	 * <p>
	 * Conversely, a return value of {@code true} does not necessarily mean that more events are expected
	 * in order to complete the expected information.
	 * <p>
	 * Exceptions thrown by this instance should not be caught within any of the {@code write*()} methods
	 * in application implementations of any data adapter (e.g. 
	 * {@link MatrixDataAdapter#writeSequencePartContentData(ReadWriteParameterMap, JPhyloIOEventReceiver, String, long, long)}. 
	 * They will be forwarded by the calling writer and can be handled by the application when 
	 * {@link JPhyloIOEventWriter#writeDocument(DocumentDataAdapter, Writer, ReadWriteParameterMap)}
	 * (or one of the other overloaded versions) are called. 
	 * 
	 * @param event the event to be add
	 * @return {@code true} if more events can be written to this acceptor or {@code false} if writing should
	 *         be aborted
	 * @throws IllegalEventException if the specified event is illegal in this acceptor in general or at
	 *         the current position in the sequence
	 * @throws ClassCastException if an event object was specified that is not an instance of a class associated 
	 *         with its type as document in {@link EventContentType}
	 * @throws IOException if an I/O error occurs when writing to the underlying stream
	 */
	public boolean add(JPhyloIOEvent event) throws IOException;
}
