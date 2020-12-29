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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters;


import java.io.IOException;

import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;



/**
 * Nexus reader for the {@code CHARLABELS} command that produces {@link CharacterDefinitionEvent}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class CharLabelsReader extends AbstractCharLabelsReader implements NexusConstants, ReadWriteConstants {
	private long index = 0;
	
	
	public CharLabelsReader(NexusReaderStreamDataProvider streamDataProvider) {
		super(COMMAND_NAME_CHAR_LABELS, streamDataProvider);
	}

	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		//TODO Buffer comment events
		getStreamDataProvider().consumeWhiteSpaceAndComments();
		PeekReader reader = getStreamDataProvider().getDataReader();
		if (reader.peek() == COMMAND_END) {
			getStreamDataProvider().getDataReader().skip(1);  // Consume ';'.
			setAllDataProcessed(true);
			return false;
		}
		else if ((reader.peek() == KEY_VALUE_SEPARATOR) || (reader.peek() == ELEMENT_SEPARATOR)) {  // This two characters would prevent reading a Nexus word and are illegal in this command.
			throw new JPhyloIOReaderException("The character " + getStreamDataProvider().getDataReader().readChar() + " is not allowed in the Nexus "
					+ COMMAND_NAME_CHAR_LABELS + " command.", reader);
		}
		else {
			String characterName = getStreamDataProvider().readNexusWord();
			if (!characterName.equals("")) {
				addCharacterDefinition(characterName, index);
				index++;  // Labels are always listed consecutively in Nexus.
			}
			getStreamDataProvider().consumeWhiteSpaceAndComments();
			return true;
		}
	}
}
