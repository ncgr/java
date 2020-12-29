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


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;



/**
 * Base class for all Nexus readers that create set event streams that consist of {@link SetElementEvent}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class IDBasedSetsReader extends AbstractNexusSetReader implements NexusConstants, ReadWriteConstants {
	private String linkedBlockTypeName;
	private EventContentType elementType;
	
	
	public IDBasedSetsReader(EventContentType setType, String commandName, String linkedBlockTypeName, EventContentType elementType, NexusReaderStreamDataProvider streamDataProvider) {
		super(setType, commandName, new String[]{BLOCK_NAME_SETS}, streamDataProvider);
		this.linkedBlockTypeName = linkedBlockTypeName;
		this.elementType = elementType;
	}
	
	
	@Override
	protected String getLinkedID() {
		return getStreamDataProvider().getCurrentLinkedBlockID(linkedBlockTypeName);
	}

	
	@Override
	protected long getElementCount() {
		return getStreamDataProvider().getElementList(elementType, getLinkedID()).size();
	}

	
	@Override
	protected void createEventsForInterval(long start, long end) throws IOException {
		if (end > Integer.MAX_VALUE) {
			throw new JPhyloIOReaderException("This reader implementation does not support taxon sets in Nexus files that contain more than "
					+ Integer.MAX_VALUE + " elements.", getStreamDataProvider().getDataReader());
		}
		else {
			List<String> elements = getStreamDataProvider().getElementList(elementType, getLinkedID());
			Map<String, String> namesToIDMap = getStreamDataProvider().getNexusNameToIDMap(elementType, getLinkedID());
			for (int i = (int)start; i < end; i++) {
				getStreamDataProvider().getCurrentEventCollection().add(new SetElementEvent(namesToIDMap.get(elements.get(i)), elementType));
			}
		}
	}

	
	@Override
	protected long elementIndexByName(String name) {
		return getStreamDataProvider().getElementList(elementType, getLinkedID()).indexOf(name);  //TODO Increase performance by using an ID to index map?
	}
}
