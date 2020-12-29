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


import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLReaderStreamDataProvider;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Abstract element reader that processes NeXML set tags that contain one or more attributes of the type {@code xs:IDREFS}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AbstractSetElementReader extends AbstractNeXMLElementReader {
	private EventContentType setType;
	private QName[] linkedIDsAttributes;
	private EventContentType linkedIDType;
	
	
	protected AbstractSetElementReader(EventContentType setType, EventContentType linkedIDType, QName... linkedIDsAttributes) {
		super();
		this.setType = setType;
		this.linkedIDType = linkedIDType;
		this.linkedIDsAttributes = linkedIDsAttributes;
	}


	public EventContentType getSetType() {
		return setType;
	}


	@Override
	public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider,	XMLEvent event) throws IOException, XMLStreamException {
		StartElement element = event.asStartElement();
		LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
		
		streamDataProvider.setCurrentSetIsSupported(true);
		streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(setType, info.id, info.label, 
				streamDataProvider.getElementTypeToCurrentIDMap().get(linkedIDType)));

		for (int i = 0; i < linkedIDsAttributes.length; i++) {
			String linkedIDString = XMLUtils.readStringAttr(element, linkedIDsAttributes[i], null);
			if (linkedIDString != null) {
				String[] linkedIDs = linkedIDString.split("\\s+");  // IDs are not allowed to contain spaces
				linkedIDString = null;  // Free memory in case of large lists before events are add to the queue.
				processIDs(streamDataProvider, linkedIDs, linkedIDsAttributes[i]);
			}
		}
	}
	
	
	/**
	 * Method that is called to process a list of IDs that has been found in an according attribute of the tag processed by this
	 * instance.
	 * 
	 * @param streamDataProvider the stream data provider of the associated event reader
	 * @param linkedIDs the list of IDs linked by the read tag
	 * @param attribute the attribute that contained the linked IDs
	 */
	protected abstract void processIDs(NeXMLReaderStreamDataProvider streamDataProvider, String[] linkedIDs, QName attribute) 
			throws JPhyloIOReaderException, XMLStreamException ;
}
