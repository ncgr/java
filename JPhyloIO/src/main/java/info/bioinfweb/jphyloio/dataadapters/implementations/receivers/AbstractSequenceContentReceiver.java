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
package info.bioinfweb.jphyloio.dataadapters.implementations.receivers;


import info.bioinfweb.jphyloio.AbstractEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleSequenceTokenEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



public abstract class AbstractSequenceContentReceiver<P extends WriterStreamDataProvider<? extends AbstractEventWriter<P>>> extends BasicEventReceiver<P> {
	private boolean longTokens;


	public AbstractSequenceContentReceiver(P streamDataProvider, ReadWriteParameterMap parameterMap, boolean longTokens) {
		super(streamDataProvider, parameterMap);
		this.longTokens = longTokens;
	}


	protected abstract void handleToken(String token, String label) throws IOException, XMLStreamException;
	

	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws XMLStreamException, IOException {
		switch (event.getType().getContentType()) {
			case SINGLE_SEQUENCE_TOKEN:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {					
					SingleSequenceTokenEvent tokenEvent = event.asSingleSequenceTokenEvent();
					handleToken(tokenEvent.getToken(), tokenEvent.getLabel());					
				}  // End events can be ignored here.
				break;
			case SEQUENCE_TOKENS:				
				for (String token : event.asSequenceTokensEvent().getTokens()) {
					handleToken(token, null);
				}				
				break;
			default:
				break;
		}
		
		return true;
	}


	public boolean isLongTokens() {
		return longTokens;
	}
}
