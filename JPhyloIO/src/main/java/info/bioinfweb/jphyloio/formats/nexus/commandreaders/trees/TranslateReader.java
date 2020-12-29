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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees;


import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractNexusCommandEventReader;

import java.io.EOFException;
import java.io.IOException;



public class TranslateReader extends AbstractNexusCommandEventReader implements NexusConstants, ReadWriteConstants {
	public TranslateReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_TRANSLATE, new String[]{BLOCK_NAME_TREES}, nexusDocument);
	}

	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		try {
			while (reader.peekChar() != COMMAND_END) {
				getStreamDataProvider().consumeWhiteSpaceAndComments();
				String key = getStreamDataProvider().readNexusWord();
				getStreamDataProvider().consumeWhiteSpaceAndComments();
				String value = getStreamDataProvider().readNexusWord();
				getStreamDataProvider().consumeWhiteSpaceAndComments();
				getStreamDataProvider().getTreesTranslationTable().add(key, value);
				
				if (reader.peekChar() == ELEMENT_SEPARATOR) {
					reader.read();  // Skip ELEMENT_SEPARATOR.
				}
			}
			
			reader.read();  // Skip COMMAND_END.
			setAllDataProcessed(true);
			return false;  // This reader does not produce any events.
		}
		catch (EOFException e) {
			throw new JPhyloIOReaderException("Unexpected end of file in " + getCommandName() + " command.", reader);
		}
	}
}
