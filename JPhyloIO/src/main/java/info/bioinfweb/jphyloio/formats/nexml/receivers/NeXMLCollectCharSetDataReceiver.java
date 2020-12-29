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
package info.bioinfweb.jphyloio.formats.nexml.receivers;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * Receiver that collects the IDs of character set elements.
 * 
 * @author Sarah Wiechers 
 */
public class NeXMLCollectCharSetDataReceiver extends NeXMLCollectNamespaceReceiver {
	private String charSetID;
	

	public NeXMLCollectCharSetDataReceiver(NeXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, String charSetID) {
		super(streamDataProvider, parameterMap);
		this.charSetID = charSetID;
	}


	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IOException, XMLStreamException {
		switch (event.getType().getContentType()) {
			case CHARACTER_SET_INTERVAL:
				CharacterSetIntervalEvent intervalEvent = event.asCharacterSetIntervalEvent();
				for (long i = intervalEvent.getStart(); i < intervalEvent.getEnd(); i++) {
					getStreamDataProvider().getCurrentAlignmentInfo().getCharSets().get(charSetID).add(i);
				}
				break;
			default:
				break;
		}
		return true;
	}	
}
