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
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.events.type.EventType;
import info.bioinfweb.jphyloio.formats.xml.JPhyloIOXMLEventReader;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.push.JPhyloIOEventListener;

import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;



/**
 * Implements shared functionality for custom XML readers.
 * <p>
 * Each created reader instance will create their own start and end document events. If multiple instances are used for the same literal 
 * meta subsequence this can lead to multiple start and end document events being generated. However, start document events are only 
 * created if no literal content event was consumed yet.
 * 
 * @author Sarah Wiechers
 */
public abstract class AbstractMetaXMLReader {
	private XMLReaderStreamDataProvider<?> streamDataProvider;

	private MetaEventListener listener = new MetaEventListener();
	private XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	
	private boolean endReached = false;
	private boolean startDocumentFired;
	private boolean endDocumentFired;
	
	
	private class MetaEventListener implements JPhyloIOEventListener { 
		@Override
		public void processEvent(JPhyloIOEventReader source, JPhyloIOEvent event) throws IOException {
			if (source.peek().getType().equals(new EventType(EventContentType.LITERAL_META, EventTopologyType.END))) {
				setEndReached();
			}
		}
	}


	public AbstractMetaXMLReader(XMLReaderStreamDataProvider<?> streamDataProvider) {
		super();
		this.streamDataProvider = streamDataProvider;
		getJPhyloIOEventReader().addEventListener(listener);
	}
	
	
	protected void setEndReached() {
		endReached = true;
		getJPhyloIOEventReader().removeEventListener(listener);
	}


	protected JPhyloIOXMLEventReader getJPhyloIOEventReader() {
		return (JPhyloIOXMLEventReader)streamDataProvider.getEventReader();
	}


	protected XMLReaderStreamDataProvider<?> getStreamDataProvider() {
		return streamDataProvider;
	}


	protected XMLEventFactory getEventFactory() {
		return eventFactory;
	}


	protected boolean isEndReached() {
		return endReached;
	}


	protected boolean isStartDocumentFired() {
		return startDocumentFired;
	}


	protected void setStartDocumentFired(boolean startDocumentFired) {
		this.startDocumentFired = startDocumentFired;
	}


	protected boolean isEndDocumentFired() {
		return endDocumentFired;
	}


	protected void setEndDocumentFired(boolean endDocumentFired) {
		this.endDocumentFired = endDocumentFired;
	}


	/**
	 * This method has no effect in this reader. Both the <i>JPhyloIO</i> and the XML event stream will
	 * still be open, if they were before calling this method. Freeing resources of this reader is not
	 * necessary, since it just delegates to another reader.
	 */
	public void close() throws XMLStreamException {}
	

	public boolean hasNext() {
		return !isEndDocumentFired();
	}
	

	// This method is declared by the reader and writer interfaces that are implemented by the inherited classes.
	public Object getProperty(String name) throws IllegalArgumentException {
		return streamDataProvider.getXMLReader().getProperty(name);  // It is possible that implementation specific objects have properties that can be changed by the application in a way that our code does not work anymore
	}
	
	
	protected XMLEvent obtainXMLContentEvent(JPhyloIOEvent jPhyloIOEvent) throws XMLStreamException {
		XMLEvent result;
		
		switch (jPhyloIOEvent.getType().getContentType()) {
			case COMMENT:
				result = getEventFactory().createComment(jPhyloIOEvent.asCommentEvent().getContent());
				break;
			case LITERAL_META_CONTENT:
				LiteralMetadataContentEvent contentEvent = jPhyloIOEvent.asLiteralMetadataContentEvent();
				
				if (contentEvent.hasXMLEventValue()) {
					result = contentEvent.getXMLEvent();
				}
				else {
					throw new XMLStreamException("No XML event could be obtained from the current metadata content event.");
				}
				break;
			default:
				throw new XMLStreamException("An event with the unexpected content type \"" + jPhyloIOEvent.getType().getContentType() 
						+ "\" was encountered in the literal meta subsequence.");
		}
		
		return result;
	}
}
