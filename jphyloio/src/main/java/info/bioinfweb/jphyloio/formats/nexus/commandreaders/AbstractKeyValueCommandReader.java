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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders;


import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.text.KeyValueInformation;

import java.io.EOFException;
import java.io.IOException;



public abstract class AbstractKeyValueCommandReader extends AbstractNexusCommandEventReader implements NexusConstants {
	public AbstractKeyValueCommandReader(String commandName, String[] validBlocks, NexusReaderStreamDataProvider nexusDocument) {
		super(commandName, validBlocks, nexusDocument);
	}


	/**
	 * Inherited classes should implement this method by adding one or more events to the queue, which have
	 * been generated from the specified key value pair.
	 * 
	 * @param info the key and value
	 * @return {@code true} if at least one event has been added to the queue by this or {@code false} otherwise
	 * @throws IOException if an exception occurs when trying to read from the underlying stream
	 */
	protected abstract boolean processSubcommand(KeyValueInformation info) throws IOException;
	
	
	/**
	 * Implementations that need to store events, until all generated events are known should add such events
	 * to the queue, when this method is called. It is guaranteed that this method will not be called by 
	 * {@link AbstractKeyValueCommandReader}, before the whole command was processed.
	 * 
	 * @return {@code true} if events were added by this method or {@code false} otherwise
	 */
	protected abstract boolean addStoredEvents();
	
	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		try {
			while (reader.peekChar() != COMMAND_END) {
				getStreamDataProvider().consumeWhiteSpaceAndComments();
				KeyValueInformation info = getStreamDataProvider().readKeyValueMetaInformation();
				if (processSubcommand(info)) {
					return true;
				}
			}
			boolean result = addStoredEvents();  // Add possibly stored events to the queue.
			
			reader.skip(1); // Consume ';'.
			setAllDataProcessed(true);
			return result;
		}
		catch (EOFException e) {
			throw new JPhyloIOReaderException("Unexpected end of file in Nexus " + getCommandName() + " command.", reader, e);
		}
	}
}
