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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.taxa;


import java.io.EOFException;
import java.io.IOException;
import java.util.Collection;

import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractNexusCommandEventReader;



public class TaxLabelsReader extends AbstractNexusCommandEventReader implements NexusConstants, ReadWriteConstants {
	private boolean beforeStart = true;
	private String currentListID;
	
	
	public TaxLabelsReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_TAX_LABELS, new String[]{BLOCK_NAME_TAXA}, nexusDocument);
	}

	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		try {
			if (beforeStart) {
				beforeStart = false;
				
				currentListID = DEFAULT_OTU_LIST_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID();
				String label = getStreamDataProvider().getSharedInformationMap().getString(NexusReaderStreamDataProvider.INFO_KEY_BLOCK_TITLE);
				if (!getStreamDataProvider().getBlockTitleToIDMap().hasDefaultBlockID(BLOCK_NAME_TAXA)) {
					getStreamDataProvider().getBlockTitleToIDMap().putDefaultBlockID(BLOCK_NAME_TAXA, currentListID);  // Set first OTU list as the default.
				}
				if (label != null) {
					getStreamDataProvider().getBlockTitleToIDMap().putID(BLOCK_NAME_TAXA, label, currentListID);
				}
				
				Collection<JPhyloIOEvent> leadingComments = getStreamDataProvider().resetCurrentEventCollection();
				getStreamDataProvider().getCurrentEventCollection().add(new LabeledIDEvent(EventContentType.OTU_LIST, 
						currentListID, label));
				getStreamDataProvider().getCurrentEventCollection().addAll(leadingComments);
				return true;
			}
			else {
				getStreamDataProvider().consumeWhiteSpaceAndComments();
				char c = reader.peekChar();
				if (c == COMMAND_END) {
					reader.skip(1);  // Consume ';'.
					setAllDataProcessed(true);
					return false;
				}
				else {
					String taxon = getStreamDataProvider().readNexusWord();
					String id = DEFAULT_OTU_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID();
					
					getStreamDataProvider().getElementList(EventContentType.OTU, currentListID).add(taxon);
					getStreamDataProvider().getNexusNameToIDMap(EventContentType.OTU, currentListID).put(taxon, id);
					
					getStreamDataProvider().getCurrentEventCollection().add(new LabeledIDEvent(EventContentType.OTU, id, taxon));
					getStreamDataProvider().getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.OTU, EventTopologyType.END));
					return true;
				}
			}
		}
		catch (EOFException e) {
			throw new JPhyloIOReaderException("Unexpected end of file in " + getCommandName() + " command.", reader);
		}
	}
}
