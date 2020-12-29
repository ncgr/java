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

import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexus.NexusWriterStreamDataProvider;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;



public class ReferenceOnlySetReceiver extends AbstractNexusSetsEventReceiver {
	private Set<EventContentType> allowedTypes;
	private Set<EventContentType> ignoredTypes;
	
	
	public ReferenceOnlySetReceiver(NexusWriterStreamDataProvider streamDataProvider,	Set<EventContentType> allowedTypes, 
			Set<EventContentType> ignoredTypes) {
		
		super(streamDataProvider);
		this.allowedTypes = allowedTypes;
		this.ignoredTypes = ignoredTypes;
	}


	public ReferenceOnlySetReceiver(NexusWriterStreamDataProvider streamDataProvider,	Set<EventContentType> allowedTypes) {
		this(streamDataProvider, allowedTypes, EnumSet.noneOf(EventContentType.class));
	}


	@Override
	protected boolean handleSetElement(SetElementEvent event) throws IOException {
		boolean result = allowedTypes.contains(event.getLinkedObjectType()); 
		if (result) {
			writeElementReference(event);
		}
		else {
			result = ignoredTypes.contains(event.getLinkedObjectType());
		}
		return result;
	}
}
