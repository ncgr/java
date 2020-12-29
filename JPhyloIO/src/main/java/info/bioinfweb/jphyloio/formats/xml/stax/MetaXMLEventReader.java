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
package info.bioinfweb.jphyloio.formats.xml.stax;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.events.type.EventType;
import info.bioinfweb.jphyloio.formats.xml.JPhyloIOXMLEventReader;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.XMLEvent;



/**
 * Adapter class that allows reading a sequence of {@link LiteralMetadataContentEvent}s using an {@link XMLEventReader}.
 * Instances of this class should not (and cannot) be created directly in application code, but 
 * {@link JPhyloIOXMLEventReader#createMetaXMLEventReader()} should be used instead.
 * <p>
 * Since it is registered which events are read from the event stream, it is possible to read only a part of the 
 * custom <i>XML</i> tree with this reader, while the rest is read using the original {@link JPhyloIOEventReader}.
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers 
 */
public class MetaXMLEventReader extends AbstractMetaXMLReader implements XMLEventReader {
	/**
	 * Creates a new instance of this class. Application code should not use this constructor directly, but use
	 * {@link JPhyloIOXMLEventReader#createMetaXMLEventReader()} instead.
	 * 
	 * @param streamDataProvider the stream data provider of the underlying {@link JPhyloIOEventReader} 
	 */
	public MetaXMLEventReader(XMLReaderStreamDataProvider<?> streamDataProvider) {
		super(streamDataProvider);
	}


	@Override
	public Object next() throws NoSuchElementException {
		try {
			return nextEvent();
		}
		catch (XMLStreamException e) {
			throw new NoSuchElementException(e.getLocalizedMessage());
		}
	}

	
	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("A passed event stream cannot be modified.");
	}
	
	
	@Override
	public String getElementText() throws XMLStreamException {
		StringBuffer content = new StringBuffer();
		
		// Check if the reader is currently located at a start element
		if (getJPhyloIOEventReader().getPreviousEvent().getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
			XMLEvent lastEvent = getJPhyloIOEventReader().getPreviousEvent().asLiteralMetadataContentEvent().getXMLEvent();
			
			if (lastEvent.getEventType() != XMLStreamConstants.START_ELEMENT) {
				throw new XMLStreamException("To read the next element text this reader must be positioned on a start element.");
			}
		}	
		
		XMLEvent event = nextEvent();
		int eventType = event.getEventType();
		
		while (eventType != XMLStreamConstants.END_ELEMENT) {
			switch (eventType) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
					content.append(event.asCharacters().getData());
					break;
				case XMLStreamConstants.ENTITY_REFERENCE:
					content.append(((EntityReference)event).getDeclaration().getReplacementText());
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				case XMLStreamConstants.COMMENT:
					// Ignore events of these types
					break;
				case XMLStreamConstants.END_DOCUMENT:
					throw new XMLStreamException("The end of the document was reached while reading element text content.");
				case XMLStreamConstants.START_ELEMENT:
					throw new XMLStreamException("Only text elements are allowed to be nested while reading element text content.");
				default:
					throw new XMLStreamException("An unexpected event type was encountered while reading element text content.");
			}
			
			event = nextEvent();
			eventType = event.getEventType();
		}			
		
		return content.toString();
	}
	

	@Override
	public XMLEvent nextEvent() throws XMLStreamException {
		XMLEvent result;
		
		if (!isEndReached()) {
			if (getJPhyloIOEventReader().getPreviousEvent().getType().equals(new EventType(EventContentType.LITERAL_META, EventTopologyType.START)) 
					&& !isStartDocumentFired()) {
				
				result = getEventFactory().createStartDocument();
				setStartDocumentFired(true);
			}
			else {
				try {
					result = obtainXMLContentEvent(getJPhyloIOEventReader().next());
				}
				catch (IOException e) {
					if (e.getCause() != null) {
						throw new XMLStreamException(e.getCause());
					}
					else {
						throw new XMLStreamException("No event could be obtained from the underlying reader.");
					}
				}
			}
		}
		else if (!isEndDocumentFired()) {
			result = getEventFactory().createEndDocument();
			setEndDocumentFired(true);
		}
		else {
			throw new NoSuchElementException("The end of this XML metadata stream was already reached.");
		}		
		
		return result;
	}
	

	@Override
	public XMLEvent nextTag() throws XMLStreamException {
		XMLEvent event = nextEvent();
		int eventType = event.getEventType();
		
		while((event.isCharacters() && event.asCharacters().isWhiteSpace()) || (eventType == XMLStreamConstants.PROCESSING_INSTRUCTION)
				|| (eventType == XMLStreamConstants.COMMENT)) {
			 
			event = nextEvent();
			eventType = event.getEventType();
		}
    
		if ((eventType != XMLStreamConstants.START_ELEMENT) && (eventType != XMLStreamConstants.END_ELEMENT)) {
			throw new XMLStreamException("An element that could not be skipped was encountered before the next start or end element.");
		}
		
		return event;
	}
	

	@Override
	public XMLEvent peek() throws XMLStreamException {
		XMLEvent result = null;		

		if (!isEndReached()) {
			if (getJPhyloIOEventReader().getPreviousEvent().getType().equals(new EventType(EventContentType.LITERAL_META, EventTopologyType.START)) 
					&& !isStartDocumentFired()) {
				
				result = getEventFactory().createStartDocument();
			}
			else {
				try {
					result = obtainXMLContentEvent(getJPhyloIOEventReader().peek());
				}
				catch (IOException e) {
					if (e.getCause() != null) {
						throw new XMLStreamException(e.getCause());
					}
					else {
						throw new XMLStreamException("No XML event could be obtained from the underlying reader.");
					}
				}
			}
		}
		else if (!isEndDocumentFired()) {
			result = getEventFactory().createEndDocument();
		}		
		
		return result;  // If the end of the custom XML was already reached this method returns null
	}
}
