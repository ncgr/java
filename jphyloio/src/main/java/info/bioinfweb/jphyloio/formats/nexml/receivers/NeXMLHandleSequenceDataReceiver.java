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
import info.bioinfweb.jphyloio.dataadapters.implementations.receivers.AbstractSequenceContentReceiver;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleSequenceTokenEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLConstants;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;


/**
 * General implementation of a receiver that processes sequence tokens.
 * 
 * @author Sarah Wiechers
 */
public abstract class NeXMLHandleSequenceDataReceiver extends AbstractSequenceContentReceiver<NeXMLWriterStreamDataProvider> implements NeXMLConstants {
	private boolean nestedUnderSingleToken = false;
	private long tokenIndex = 0;


	public NeXMLHandleSequenceDataReceiver(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameterMap, boolean longTokens) {
		super(streamDataProvider, parameterMap, longTokens);
	}


	/**
	 * Determines whether the current event is nested under a {@link SingleTokenDefinitionEvent} or not.
	 * 
	 * @return {@code true} if the current event is nested under a {@link SingleTokenDefinitionEvent}
	 */
	public boolean isNestedUnderSingleToken() {
		return nestedUnderSingleToken;
	}


	public void setNestedUnderSingleToken(boolean nestedUnderSingleToken) {
		this.nestedUnderSingleToken = nestedUnderSingleToken;
	}
	
	
	/**
	 * Returns the index of the alignment character the currently handled token belongs to.
	 * 
	 * @return the character index of the currently handled token
	 */
	public long getTokenIndex() {
		return tokenIndex;
	}


	public void setTokenIndex(long tokenIndex) {
		this.tokenIndex = tokenIndex;
	}
	
	
	protected void handleTokenEnd() throws XMLStreamException {}
	
	
	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IOException, XMLStreamException {
		switch (event.getType().getContentType()) {
			case SINGLE_SEQUENCE_TOKEN:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					SingleSequenceTokenEvent tokenEvent = event.asSingleSequenceTokenEvent();
					handleToken(tokenEvent.getToken(), tokenEvent.getLabel());		
					
					setNestedUnderSingleToken(true);
					
					if (tokenEvent.getLabel() != null) {
						getStreamDataProvider().getCurrentAlignmentInfo().setWriteCellsTags(true);
					}
				}
				else {
					handleTokenEnd();
					setNestedUnderSingleToken(false);
				}
				break;
			case SEQUENCE_TOKENS:
				for (String token : event.asSequenceTokensEvent().getTokens()) {
					handleToken(token, null);
					handleTokenEnd();
				}
				break;
			default:
				break;
		}
		return true;
	}
}
