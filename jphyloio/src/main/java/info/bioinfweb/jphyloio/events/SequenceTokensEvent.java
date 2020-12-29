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
package info.bioinfweb.jphyloio.events;


import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;

import java.util.Collections;
import java.util.List;



/**
 * Indicates that one or more sequence tokens have been read from an alignment. This event can be triggered one or more
 * times between {@link EventContentType#ALIGNMENT} and {@link EventContentType#ALIGNMENT_END} events.
 * <p>
 * It depends on the implementation of the format specific reader how many tokens are contained in a single event. For 
 * performance reasons most applications will group several tokens together in one event, but not necessarily a whole
 * sequence.  
 * 
 * @author Ben St&ouml;ver
 */
public class SequenceTokensEvent extends ConcreteJPhyloIOEvent {
	private List<String> tokens;
	
	
	public SequenceTokensEvent(List<String> tokens) {
		super(EventContentType.SEQUENCE_TOKENS, EventTopologyType.SOLE);
		
		this.tokens = Collections.unmodifiableList(tokens);
	}

	
	public List<String> getTokens() {
		return tokens;
	}
}
