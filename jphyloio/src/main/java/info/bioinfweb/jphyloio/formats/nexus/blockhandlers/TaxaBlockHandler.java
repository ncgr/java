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


import java.util.ArrayList;

import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandEventReader;



public class TaxaBlockHandler extends AbstractNexusBlockHandler implements NexusBlockHandler, NexusConstants {
	public TaxaBlockHandler() {
		super(new String[]{BLOCK_NAME_TAXA});
	}

	
	@Override
	public void handleBegin(NexusReaderStreamDataProvider streamDataProvider) {
		streamDataProvider.setCurrentEventCollection(new ArrayList<JPhyloIOEvent>());  // Collect leading comment events until ORU list start event is fired in TaxLabelsReader.
	}

	
	@Override
	public void handleEnd(NexusReaderStreamDataProvider streamDataProvider) {
		streamDataProvider.getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.OTU_LIST, EventTopologyType.END));
	}

	
	@Override
	public void beforeCommand(NexusReaderStreamDataProvider streamDataProvider,	String commandName, NexusCommandEventReader commandReader) {}
}
