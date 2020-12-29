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
package info.bioinfweb.jphyloio.formats.nexus.blockhandlers;


import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandEventReader;



/**
 * A <i>Nexus</i> block handler to read sequence data from the {@code CHARACTERS}, {@code DATA} or {@code UNALIGNED}
 * blocks.
 * 
 * @author Ben St&ouml;ver
 */
public class CharactersDataUnalignedBlockHandler extends AbstractNexusBlockHandler 
		implements NexusBlockHandler, ReadWriteConstants, NexusConstants {
	
	public CharactersDataUnalignedBlockHandler() {
		super(new String[]{BLOCK_NAME_CHARACTERS, BLOCK_NAME_DATA, BLOCK_NAME_UNALIGNED});
	}


	@Override
	public void handleBegin(NexusReaderStreamDataProvider streamDataProvider) {
		streamDataProvider.getSharedInformationMap().put(NexusReaderStreamDataProvider.INFO_KEY_CURRENT_BLOCK_ID, 
				DEFAULT_MATRIX_ID_PREFIX + streamDataProvider.getIDManager().createNewID());
	}

	
	@Override
	public void handleEnd(NexusReaderStreamDataProvider streamDataProvider) {
		streamDataProvider.getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.ALIGNMENT, EventTopologyType.END));
	}


	@Override
	public void beforeCommand(NexusReaderStreamDataProvider streamDataProvider,	String commandName, NexusCommandEventReader commandReader) {
		ParameterMap map = streamDataProvider.getSharedInformationMap();
		if (!map.getBoolean(NexusReaderStreamDataProvider.INFO_KEY_BLOCK_START_EVENT_FIRED, false) 
				&& (commandName.equals(COMMAND_NAME_DIMENSIONS) || commandName.equals(COMMAND_NAME_FORMAT) 
						|| commandName.equals(COMMAND_NAME_MATRIX))) {  // Fire start event, as soon as one of these commands is encountered.
			
			String blockID = map.getString(NexusReaderStreamDataProvider.INFO_KEY_CURRENT_BLOCK_ID);
			streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.ALIGNMENT, blockID, 
					map.getString(NexusReaderStreamDataProvider.INFO_KEY_BLOCK_TITLE), streamDataProvider.getCurrentLinkedBlockID(BLOCK_NAME_TAXA)));
			String blockTypeName = streamDataProvider.getEventReader().getCurrentBlockName();
			//TODO Output metadata whether sequences are aligned (with the same predicate as for the NeXML aligned attribute), depending on the block type name. (Is this already done somewhere else?)
			if (!streamDataProvider.getBlockTitleToIDMap().hasDefaultBlockID(blockTypeName)) {				
				streamDataProvider.getBlockTitleToIDMap().putDefaultBlockID(blockTypeName, blockID);  // Set first block as the default.
			}
			map.put(NexusReaderStreamDataProvider.INFO_KEY_BLOCK_START_EVENT_FIRED, true);
		}
	}
}
