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



/**
 * Indicates the end of a part of a sequence or character set definition.
 * <p>
 * Sequences or character set definitions may be split across several sequences of events (each 
 * surrounded by a start and a end event), especially when interleaved formats 
 * are read. {@link #isTerminated()} determines whether another part of the same 
 * sequence may follow.
 * <p>
 * (Note that this class differs from {@link ContinuedEvent}, which has a property indicating if a single value 
 * is separates among a sequence of identical events. No direct instances from {@link ContinuedEvent} can be created.)
 * 
 * @author Ben St&ouml;ver
 */
public class PartEndEvent extends ConcreteJPhyloIOEvent {
	private boolean terminated;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param contentType the content type of this end event (currently either {@link EventContentType#SEQUENCE}
	 *        or {@link EventContentType#CHARACTER_SET})
	 * @param terminated Specifies whether another part of the same sequence may follow.
	 */
	public PartEndEvent(EventContentType contentType, boolean terminated) {
		super(contentType, EventTopologyType.END);
		this.terminated = terminated;
	}


	/**
	 * Specifies whether another part of the same sequence or character set definition may follow.
	 * <p>
	 * Sequences or character set definitions may be split across several sequences of events (each 
	 * surrounded by a start and a end event), especially when interleaved formats 
	 * are read.
	 * 
	 * @return {@code true} if this sequence or character set definition is definitely terminated or 
	 *         {@code false} if another part may (or may not) follow 
	 */
	public boolean isTerminated() {
		return terminated;
	}
}
