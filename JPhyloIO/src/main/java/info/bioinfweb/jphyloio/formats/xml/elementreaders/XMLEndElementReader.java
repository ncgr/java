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
package info.bioinfweb.jphyloio.formats.xml.elementreaders;


import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;



/**
 * Element reader that is used to process XML end elements.
 * 
 * @author Sarah Wiechers
 *
 */
@SuppressWarnings("rawtypes")
public class XMLEndElementReader extends AbstractXMLElementReader {
	private boolean createLiteralEnd;
	private boolean createResourceEnd;
	private boolean isEdgeMeta;
	
	
	public XMLEndElementReader(boolean createLiteralEnd, boolean createResourceEnd, boolean isEdgeMeta) {
		super();
		this.createLiteralEnd = createLiteralEnd;
		this.createResourceEnd = createResourceEnd;
		this.isEdgeMeta = isEdgeMeta;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readEvent(XMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException,
			XMLStreamException {
		if (createLiteralEnd) {
			streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.LITERAL_META));
		}
		
		if (streamDataProvider.isCustomXMLStartWritten()) {
			streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
			streamDataProvider.setCustomXMLStartWritten(false);
		}
		
		if (createResourceEnd) {
			streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
		}
		
		if (isEdgeMeta && streamDataProvider.hasSpecialEventCollection()) {
			streamDataProvider.resetCurrentEventCollection();
		}
	}	
}