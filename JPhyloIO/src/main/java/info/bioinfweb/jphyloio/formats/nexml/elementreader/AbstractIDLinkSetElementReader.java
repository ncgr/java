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
package info.bioinfweb.jphyloio.formats.nexml.elementreader;


import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLReaderStreamDataProvider;

import javax.xml.namespace.QName;



/**
 * Element reader for NeXML sets that are represented as a sequence of {@link EventContentType#SET_ELEMENT} events.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AbstractIDLinkSetElementReader extends AbstractSetElementReader {
	/**
	 * Creates a new instance of this class.
	 *
	 * @param setType the type of the set to be read
	 * @param linkedIDType the type of the elements contained in the set to be read
	 * @param linkedIDsAttributes one or more names of attribute that contain links to other objects (events)
	 */
	public AbstractIDLinkSetElementReader(EventContentType setType, EventContentType linkedIDType, QName... linkedIDsAttributes) {
		super(setType, linkedIDType, linkedIDsAttributes);
	}
	

	@Override
	protected void processIDs(NeXMLReaderStreamDataProvider streamDataProvider, String[] linkedIDs, QName attributeName) {
		EventContentType objectType = determineObjectType(attributeName);
		for (int i = 0; i < linkedIDs.length; i++) {
			streamDataProvider.getCurrentEventCollection().add(new SetElementEvent(linkedIDs[i], objectType));
		}
	}
	
	
	protected abstract EventContentType determineObjectType(QName attributeName);
}
