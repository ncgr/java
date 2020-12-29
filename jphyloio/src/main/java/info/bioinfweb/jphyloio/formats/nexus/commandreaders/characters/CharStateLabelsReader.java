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
import java.util.ArrayList;
import java.util.Collection;

import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;



/**
 * Nexus reader for the {@code CHARSTATELABELS} command that produces {@link CharacterDefinitionEvent}s.
 * <p>
 * The character state (token) definitions that are also provided by the command are ignored in the current version. Future versions
 * may use this information to include it in token sets. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class CharStateLabelsReader extends AbstractCharLabelsReader implements NexusConstants, ReadWriteConstants {
	public CharStateLabelsReader(NexusReaderStreamDataProvider streamDataProvider) {
		super(COMMAND_NAME_CHAR_STATE_LABELS, streamDataProvider);
	}
	
	
	private boolean consumeStateNames(Collection<JPhyloIOEvent> savedCommentEvents) throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		boolean result = reader.peekChar() == CHARACTER_NAME_STATES_SEPARATOR;
		if (result) {  // No character label, only state names.
			reader.skip(1);  // Consume '/'.
			consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
			
			char c = reader.peekChar();
			while ((c != ELEMENT_SEPARATOR) && (c != COMMAND_END)) {
				String stateName = getStreamDataProvider().readNexusWord();  //TODO Use names for token sets in later versions.
				if (stateName.equals("")) {
					throw new JPhyloIOReaderException("The character '" + c + "' is invalid at this position inside a Nexus " + 
							COMMAND_NAME_CHAR_STATE_LABELS + " command.", reader);
				}
				consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
				c = reader.peekChar();
			}
		}
		return result;
	}

	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		Collection<JPhyloIOEvent> savedCommentEvents = new ArrayList<JPhyloIOEvent>();
		boolean result = true;
		boolean eventCreated = false;
		while (result && !eventCreated) {
			PeekReader reader = getStreamDataProvider().getDataReader();
			consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
			if (reader.peek() == COMMAND_END) {
				getStreamDataProvider().getDataReader().skip(1);  // Consume ';'.
				setAllDataProcessed(true);
				result = false;
			}
			else if ((reader.peek() == KEY_VALUE_SEPARATOR)) {  // This two characters would prevent reading a Nexus word and are illegal in this command.
				throw new JPhyloIOReaderException("The character " + getStreamDataProvider().getDataReader().readChar() + " is not allowed in the Nexus "
						+ COMMAND_NAME_CHAR_STATE_LABELS + " command.", reader);
			}
			else {
				long index = getStreamDataProvider().readPositiveInteger(-1);
				if (index < 0) {
					throw new JPhyloIOReaderException("Invalid character index (starting with '" + reader.peekChar() + "') found in Nexus " + 
							COMMAND_NAME_CHAR_STATE_LABELS + " command.", reader);
				}
				else {
					consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
					eventCreated = !consumeStateNames(savedCommentEvents); 
					if (eventCreated) {  // Character label not omitted.
						String characterName = getStreamDataProvider().readNexusWord();
						if (!characterName.equals("")) { 
							addCharacterDefinition(characterName, index - 1);  // Nexus indices start with 1.
							consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
							consumeStateNames(savedCommentEvents);
						}
						else {
							throw new JPhyloIOReaderException("The character '" + reader.peekChar() + "' is invalid at this position inside a Nexus " + 
									COMMAND_NAME_CHAR_STATE_LABELS + " command.", reader);
						}
					}
					
					if (reader.peek() == ELEMENT_SEPARATOR) {
						reader.skip(1);  // Consume ','.
					}
				}
			}
			getStreamDataProvider().getCurrentEventCollection().addAll(savedCommentEvents);
			savedCommentEvents.clear();
		}
		return result;
	}
}
