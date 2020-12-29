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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.sets;


import java.util.Map;

import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;



/**
 * Parser for the {@code CHARSET} command in the {@code SETS} block.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class CharSetReader extends AbstractNexusSetReader implements NexusConstants, ReadWriteConstants {
	// No default SETS block is ever specified using BlockTitleToIDMap.putDefaultBlockID() since there is no JPhyloIO event that models
	// a sets block.

	public CharSetReader(NexusReaderStreamDataProvider streamDataProvider) {
		super(EventContentType.CHARACTER_SET, COMMAND_NAME_CHAR_SET, new String[]{BLOCK_NAME_SETS}, streamDataProvider);
	}
	

	@Override
	protected long getElementCount() {
		Long result = getStreamDataProvider().getMatrixWidthsMap().get(getStreamDataProvider().getMatrixLink());  //TODO Catch any null values and throw according exceptions?
		if (result == null) {
			return -1;
		}
		else {
			return result;
		}
	}


	@Override
	protected String getLinkedID() {
		return getStreamDataProvider().getMatrixLink();  // Will link null if no CHARACTERS, DATA or UNALIGNED block was defined before.  //TODO Should an exception be thrown instead?  //TODO Sets for UNALIGNED blocks are not allowed according to the Nexus paper. (Is it a problem to support them anyway?)
	}
	

	@Override
	protected void createEventsForInterval(long start, long end) {
		getStreamDataProvider().getCurrentEventCollection().add(new CharacterSetIntervalEvent(start, end));
	}

	
	@Override
	protected long elementIndexByName(String id) {
		@SuppressWarnings("unchecked")
		Long index = ((Map<String, Long>)getStreamDataProvider().getMap(NexusReaderStreamDataProvider.INFO_CHARACTER_NAME_TO_INDEX_MAP)).get(id);
		if (index != null) {
			return index;
		}
		else {
			return -1;
		}
	}
}
