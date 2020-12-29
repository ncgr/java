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


import java.util.Map;

import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractNexusCommandEventReader;



/**
 * Implements shared functionality for Nexus command readers that process character (alignment column) definitions. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AbstractCharLabelsReader extends AbstractNexusCommandEventReader implements NexusConstants, ReadWriteConstants {
	protected AbstractCharLabelsReader(String commandName, NexusReaderStreamDataProvider streamDataProvider) {
		super(commandName, new String[]{BLOCK_NAME_CHARACTERS, BLOCK_NAME_UNALIGNED, BLOCK_NAME_DATA}, streamDataProvider);
	}

	
	@SuppressWarnings("unchecked")
	protected void addCharacterDefinition(String characterName, long characterIndex) {
		String id = DEFAULT_CHARACTER_DEFINITION_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID();
		getStreamDataProvider().getCurrentEventCollection().add(new CharacterDefinitionEvent(id, characterName, characterIndex));
		getStreamDataProvider().getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.CHARACTER_DEFINITION));
		
		((Map<String, Long>)getStreamDataProvider().getMap(NexusReaderStreamDataProvider.INFO_CHARACTER_NAME_TO_INDEX_MAP)).put(characterName, characterIndex);
	}
}
