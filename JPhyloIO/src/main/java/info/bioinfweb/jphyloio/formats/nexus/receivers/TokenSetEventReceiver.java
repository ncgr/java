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
package info.bioinfweb.jphyloio.formats.nexus.receivers;


import java.io.IOException;

import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.exception.IllegalEventException;
import info.bioinfweb.jphyloio.formats.nexus.NexusEventWriter;
import info.bioinfweb.jphyloio.formats.nexus.NexusWriterStreamDataProvider;



public class TokenSetEventReceiver extends AbstractNexusEventReceiver {
	private StringBuilder singleTokens = new StringBuilder();
	
	
	public TokenSetEventReceiver(NexusWriterStreamDataProvider streamDataProvider) {
		super(streamDataProvider);
	}

	
	public String getSingleTokens() {
		if (singleTokens.length() > 0) {
			return singleTokens.toString();
		}
		else {
			return null;
		}
	}


	public void clear() {
		singleTokens.delete(0, singleTokens.length());
	}
	
	
	private void writeSingleTokenDefinition(String key, SingleTokenDefinitionEvent singleTokenEvent) throws IOException {
		getStreamDataProvider().getWriter().write(' ');
		NexusEventWriter.writeKeyValueExpression(getStreamDataProvider().getWriter(), key, 
				NexusEventWriter.formatToken(singleTokenEvent.getTokenName()));  //TODO Token names that need to be delimited would anyway not be valid in Nexus. => An according exception should be thrown or the token should be replaced somehow.
	}


	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IllegalArgumentException,	IOException {
		if (event.getType().getContentType().equals(EventContentType.SINGLE_TOKEN_DEFINITION) && (getParentEvent() == null)) {  // Such events are only allowed on the top level.
			if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
				SingleTokenDefinitionEvent singleTokenEvent = event.asSingleTokenDefinitionEvent();
				switch (singleTokenEvent.getMeaning()) {
					case CHARACTER_STATE:
						if (singleTokens.length() > 0) {
							singleTokens.append(" ");
						}
						singleTokens.append(singleTokenEvent.getTokenName());  // TODO Check token name for invalid characters
						break;
					case GAP:
						writeSingleTokenDefinition(FORMAT_SUBCOMMAND_GAP_CHAR, singleTokenEvent);
						break;
					case MISSING:
						writeSingleTokenDefinition(FORMAT_SUBCOMMAND_MISSING_CHAR, singleTokenEvent);
						break;
					case MATCH:
						writeSingleTokenDefinition(FORMAT_SUBCOMMAND_MATCH_CHAR, singleTokenEvent);
						break;
					default:  // OTHER
						break;  // Nothing to do.
				}
			}
			return true;			
		}
		else if (event.getType().getContentType().equals(EventContentType.CHARACTER_SET_INTERVAL) && (getParentEvent() == null)) {  // Such events are only allowed on the top level.
			//TODO Store position information somewhere, if MrBayes extension for having multiple token sets is supported.
			return true;
		}
		else {  // No other events would be valid here.
			throw IllegalEventException.newInstance(this, getParentEvent(), event);
		}
	}
}
