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

import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexus.NexusWriterStreamDataProvider;



public class CharacterSetEventReceiver extends AbstractNexusSetsEventReceiver {
	public CharacterSetEventReceiver(NexusWriterStreamDataProvider streamDataProvider) {
		super(streamDataProvider);
	}

	
	@Override
	protected boolean handleCharacterSetInterval(CharacterSetIntervalEvent event) throws IOException {
		getStreamDataProvider().getWriter().write(' ');
		getStreamDataProvider().getWriter().write(Long.toString(event.getStart() + 1));
		if (event.getEnd() - event.getStart() > 1) {
			getStreamDataProvider().getWriter().write(SET_TO_SYMBOL);
			getStreamDataProvider().getWriter().write(Long.toString(event.getEnd()));
		}
		return true;
	}


	@Override
	protected boolean handleSetElement(SetElementEvent event) throws IOException {
		boolean result = event.getLinkedObjectType().equals(EventContentType.CHARACTER_SET); 
		if (result) {
			writeElementReference(event);
		}
		return result;
	}
}
