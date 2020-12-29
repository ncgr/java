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


import java.io.IOException;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;



/**
 * Interface to be implemented by classes that listen to {@link JPhyloIOEvent}s. Such listeners can be
 * registered at an instance of {@link EventForwarder}.
 * 
 * @author Ben St&ouml;ver
 */
public interface JPhyloIOEventListener {
	/**
	 * Called after an event was received from a reader instance. Implementing classes should never request
	 * additional events directly from {@code source}, because other registered event listeners would not 
	 * be notified of these events. 
	 * 
	 * @param source the reader from which the event was fired 
	 * @param event the event that was fired from the parsed document
	 * @throws Exception any exception thrown by the implementation
	 */
	public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) throws IOException;
}
