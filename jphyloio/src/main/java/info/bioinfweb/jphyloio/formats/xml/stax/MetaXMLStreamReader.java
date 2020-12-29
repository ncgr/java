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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Adapter class that allows reading a sequence of {@link LiteralMetadataContentEvent}s using an {@link XMLStreamReader}.
 * Instances of this class should not (and cannot) be created directly in application code, but 
 * {@link JPhyloIOXMLEventReader#createMetaXMLStreamReader()} should be used instead.
 * <p>
 * Since it is registered which events are read from the event stream, it is possible to read only a part of the 
 * custom XML tree with this reader, while the rest is read using the original {@link JPhyloIOEventReader}.
 * <p>
 * Note that all methods intended to obtain information about the document start refer to the start of the whole 
 * document, not only the custom XML tree.
 * <p>
 * Methods of this reader referring to element text as a character array , e.g. {@link #getTextCharacters()}, all work 
 * on a string representation obtained from a characters event. The usual performance benefits in using these character 
 * array based methods do not apply for this implementation, since it works on an underlying instance of 
 * {@link JPhyloIOEventReader}.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public class MetaXMLStreamReader extends AbstractMetaXMLReader implements XMLStreamReader {
	private XMLEvent currentEvent;
	private StartElement currentStartElement;
	private List<Attribute> currentAttributes = new ArrayList<Attribute>();
	private List<Namespace> currentNamespaces = new ArrayList<Namespace>();
	

	/**
	 * Creates a new instance of this class. Application code should not use this constructor directly, but use
	 * {@link JPhyloIOXMLEventReader#createMetaXMLStreamReader()} instead.
	 * 
	 * @param streamDataProvider the stream data provider of the underlying {@link JPhyloIOEventReader} 
	 */
	public MetaXMLStreamReader(XMLReaderStreamDataProvider<?> streamDataProvider) {
		super(streamDataProvider);
	}

	
	@Override
	public int getAttributeCount() {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.size();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}	
	}
	

	@Override
	public String getAttributeLocalName(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).getName().getLocalPart();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}
	

	@Override
	public QName getAttributeName(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).getName();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}


	@Override
	public String getAttributeNamespace(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).getName().getNamespaceURI();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}
	

	@Override
	public String getAttributePrefix(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).getName().getPrefix();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}
	

	@Override
	public String getAttributeType(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).getDTDType();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}

	
	@Override
	public String getAttributeValue(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).getValue();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}
	

	@Override
	public String getAttributeValue(String namespaceURI, String localName) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentStartElement.getAttributeByName(new QName(namespaceURI, localName)).getValue();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}
	}

	
	@Override
	public String getCharacterEncodingScheme() {
		if ((getEventType() == XMLStreamConstants.START_DOCUMENT)) {
			return getStreamDataProvider().getStartDocumentEvent().getCharacterEncodingScheme();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start document event.");
		}
	}

	
	@Override
	public String getElementText() throws XMLStreamException {
		if (getEventType() != XMLStreamConstants.START_ELEMENT) {			
				throw new XMLStreamException("To read the next element text this reader must be positioned on a start element.");			
		}		
		
		int eventType = next();
		StringBuffer content = new StringBuffer();
		
		while (eventType != XMLStreamConstants.END_ELEMENT) {
			switch (eventType) {
				case XMLStreamConstants.CHARACTERS:
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.SPACE:
				case XMLStreamConstants.ENTITY_REFERENCE:
					content.append(getText());
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
				case XMLStreamConstants.COMMENT:
					// Skip events of these types
					break;
				case XMLStreamConstants.END_DOCUMENT:
					throw new XMLStreamException("The end of the document was reached while reading element text content.");
				case XMLStreamConstants.START_ELEMENT:
					throw new XMLStreamException("Only text elements are allowed to be nested while reading element text content.");
				default:
					throw new XMLStreamException("An unexpected event type was encountered while reading element text content.");
			}		
			eventType = next();
		}
		
		return content.toString();
	}

	
	@Override
	public String getEncoding() {
		if ((getEventType() == XMLStreamConstants.START_DOCUMENT)) {
			return null;  // Encoding is unknown to this reader
		}
		else {
			throw new IllegalStateException("This method can only be called on a start document event.");
		}
	}
	

	@Override
	public int getEventType() {
		return currentEvent.getEventType();
	}
	

	@Override
	public String getLocalName() {
		switch (getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				return currentStartElement.getName().getLocalPart();
			case XMLStreamConstants.END_ELEMENT:
				return currentEvent.asEndElement().getName().getLocalPart();			
			case XMLStreamConstants.ENTITY_REFERENCE:
				return ((EntityReference)currentEvent).getName();
			default:
				throw new IllegalStateException("This method can only be called on a start element, end element or entity reference.");
		}
	}

	
	@Override
	public Location getLocation() {		
		return currentEvent.getLocation(); 
	}
	

	@Override
	public QName getName() {
		switch (getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				return currentStartElement.getName();
			case XMLStreamConstants.END_ELEMENT:
				return currentEvent.asEndElement().getName();			
			default:
				throw new IllegalStateException("This method can only be called on a start element or an end element.");
		}
	}
	

	@Override
	public NamespaceContext getNamespaceContext() {
		return getJPhyloIOEventReader().getNamespaceContext(); // Returns the current namespace context of the underlying JPhyloIOEventReader, not the XMLEventReader
	}

	
	@Override
	public int getNamespaceCount() {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.END_ELEMENT)
				 || (getEventType() == XMLStreamConstants.NAMESPACE)) {
			
			return currentNamespaces.size();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element, end element or namespace.");
		}
	}
	

	@Override
	public String getNamespacePrefix(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.END_ELEMENT)
				 || (getEventType() == XMLStreamConstants.NAMESPACE)) {
			
			return currentNamespaces.get(index).getPrefix();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element, end element or namespace.");
		}
	}
	

	@Override
	public String getNamespaceURI() {
		switch (getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				return currentStartElement.getName().getNamespaceURI();
			case XMLStreamConstants.END_ELEMENT:
				return currentEvent.asEndElement().getName().getNamespaceURI();			
			default:
				throw new IllegalStateException("This method can only be called on a start element or an end element.");
		}
	}
	

	@Override
	public String getNamespaceURI(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.END_ELEMENT)
				 || (getEventType() == XMLStreamConstants.NAMESPACE)) {

			return currentNamespaces.get(index).getNamespaceURI();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element, end element or namespace.");
		}
	}
	

	@Override
	public String getNamespaceURI(String prefix) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.END_ELEMENT)
				 || (getEventType() == XMLStreamConstants.NAMESPACE)) {
			
			return currentStartElement.getNamespaceURI(prefix);
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element, end element or namespace.");
		}
	}
	

	@Override
	public String getPIData() {
		if (getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION) {			
			return ((ProcessingInstruction)currentEvent).getData();
		}
		else {
			throw new IllegalStateException("This method can only be called on a processing instruction.");
		}
	}

	
	@Override
	public String getPITarget() {
		if (getEventType() == XMLStreamConstants.PROCESSING_INSTRUCTION) {			
			return ((ProcessingInstruction)currentEvent).getTarget();
		}
		else {
			throw new IllegalStateException("This method can only be called on a processing instruction.");
		}
	}
	

	@Override
	public String getPrefix() {
		switch (getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				return currentStartElement.getName().getPrefix();
			case XMLStreamConstants.END_ELEMENT:
				return currentEvent.asEndElement().getName().getPrefix();			
			default:
				throw new IllegalStateException("This method can only be called on a start element or an end element.");
		}
	}

	
	@Override
	public String getText() {
		switch (getEventType()) {
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.SPACE:
				return currentEvent.asCharacters().getData();
			case XMLStreamConstants.COMMENT:
				return ((Comment)currentEvent).getText();
			case XMLStreamConstants.ENTITY_REFERENCE:
				return ((EntityReference)currentEvent).getDeclaration().getReplacementText();
			case XMLStreamConstants.DTD:
				return ((DTD)currentEvent).getDocumentTypeDeclaration();
			default:
				throw new IllegalStateException("This method can only be called on an element containing text "
						+ "(characters, cData, space, comment, entity reference or document type declaration).");
		}
	}
	

	@Override
	public char[] getTextCharacters() {
		switch (getEventType()) {
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.SPACE:
				return currentEvent.asCharacters().getData().toCharArray();
			case XMLStreamConstants.COMMENT:
				return ((Comment)currentEvent).getText().toCharArray();
			default:
				throw new IllegalStateException("This method can only be called on an element containing text (characters, cData, space, comment).");
		}
	}
	

	@Override
	public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
		switch (getEventType()) {
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.COMMENT:
				System.arraycopy(getTextCharacters(), sourceStart, target, targetStart, length);
				return length;
			default:
				throw new IllegalStateException("This method can only be called on an element containing text (characters, cData, space, comment).");
		}
	}
	

	@Override
	public int getTextLength() {
		switch (getEventType()) {
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.COMMENT:
				return getText().length();
			default:
				throw new IllegalStateException("This method can only be called on an element containing text (characters, cData, space, comment).");
		}
	}

	
	@Override
	public int getTextStart() {
		switch (getEventType()) {
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.COMMENT:
				return 0; // Since no buffer is used the text always starts at the first position of the according char array
			default:
				throw new IllegalStateException("This method can only be called on an element containing text (characters, cData, space, comment).");
		}
	}

	
	@Override
	public String getVersion() {
		if ((getEventType() == XMLStreamConstants.START_DOCUMENT)) {
			return getStreamDataProvider().getStartDocumentEvent().getVersion();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start document event.");
		}
	}
	

	@Override
	public boolean hasName() {
		switch (getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
			case XMLStreamConstants.END_ELEMENT:
				return true;
			default:
				return false;
		}
	}
	

	@Override
	public boolean hasText() {
		switch (getEventType()) {
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
			case XMLStreamConstants.SPACE:
			case XMLStreamConstants.COMMENT:
			case XMLStreamConstants.ENTITY_REFERENCE:
			case XMLStreamConstants.DTD:
				return true;
			default:
				return false;
		}
	}
	

	@Override
	public boolean isAttributeSpecified(int index) {
		if ((getEventType() == XMLStreamConstants.START_ELEMENT) || (getEventType() == XMLStreamConstants.ATTRIBUTE)) {
			return currentAttributes.get(index).isSpecified();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start element or attribute.");
		}	
	}

	@Override
	public boolean isCharacters() {
		if (getEventType() == XMLStreamConstants.CHARACTERS) {
			return true;
		}
		else {
			return false;
		}
	}
	

	@Override
	public boolean isEndElement() {
		if (getEventType() == XMLStreamConstants.END_ELEMENT) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isStandalone() {
		if ((getEventType() == XMLStreamConstants.START_DOCUMENT)) {
			return getStreamDataProvider().getStartDocumentEvent().isStandalone();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start document event.");
		}
	}

	@Override
	public boolean isStartElement() {
		if (getEventType() == XMLStreamConstants.START_ELEMENT) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isWhiteSpace() {
		if ((getEventType() == XMLStreamConstants.CHARACTERS) && getText().trim().isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
	

	@Override
	public int next() throws XMLStreamException {
		XMLEvent result = null;
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
						throw new XMLStreamException("No XML event could be obtained from the underlying reader.");
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
		
		currentEvent = result;
		evaluateStartElement(result);
		
		return result.getEventType();
	}

	
	@Override
	public int nextTag() throws XMLStreamException {		
		int eventType = next();
		
		while(((eventType == XMLStreamConstants.CHARACTERS) && isWhiteSpace()) 
				|| ((eventType == XMLStreamConstants.CDATA) && isWhiteSpace()) 
				|| (eventType == XMLStreamConstants.SPACE) || (eventType == XMLStreamConstants.PROCESSING_INSTRUCTION) 
				|| (eventType == XMLStreamConstants.COMMENT)) {
			 
			eventType = next();
		}
    
		if ((eventType != XMLStreamConstants.START_ELEMENT) && (eventType != XMLStreamConstants.END_ELEMENT)) {
			throw new XMLStreamException("An element that could not be skipped was encountered before the next start or end element.");
		}
		
		return eventType;
	}
	

	@Override
	public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
		if (type != getEventType()) {
			throw new XMLStreamException("The specified event type did not match the type of the current event.");
		}
		
		if (namespaceURI != null && !namespaceURI.equals(getNamespaceURI())) {
			throw new XMLStreamException("The specified namespace URI did not match the namespace URI of the current event.");
		}
		
		if(localName != null && !localName.equals(getLocalName())) {
			throw new XMLStreamException("The specified local name did not match the local name of the current event.");
		}
	}
	

	@Override
	public boolean standaloneSet() {
		if ((getEventType() == XMLStreamConstants.START_DOCUMENT)) {
			return getStreamDataProvider().getStartDocumentEvent().standaloneSet();
		}
		else {
			throw new IllegalStateException("This method can only be called on a start document event.");
		}
	}
	
	
	/**
	 * This method fills lists with attributes and namespaces to allow methods to obtain information 
	 * about either of those with just an index as a parameter. To ensure that all events are added to the according list,
	 * whether they are included in a start element or occur as separate events, these lists are filled continuously 
	 * and not only if needed.
	 * 
	 * @param the current XML event
	 */
	private void evaluateStartElement(XMLEvent event) {
		int eventType = event.getEventType();
		
		switch (eventType) {
			case XMLStreamConstants.START_ELEMENT:
				currentStartElement = event.asStartElement();
				currentAttributes.clear();
				currentNamespaces.clear();
				
				@SuppressWarnings("unchecked")
				Iterator<Attribute> attributes = currentStartElement.getAttributes(); 
				while (attributes.hasNext()) {
					currentAttributes.add(attributes.next());
				}
				
				@SuppressWarnings("unchecked")
				Iterator<Namespace> namespaces = currentStartElement.getNamespaces(); 
				while (namespaces.hasNext()) {
					currentNamespaces.add(namespaces.next());
				}
				
				break;
			case XMLStreamConstants.ATTRIBUTE:
				currentAttributes.add(((Attribute)event));
				break;
			case XMLStreamConstants.NAMESPACE:
				currentNamespaces.add(((Namespace)event));
				break;
			default:
				break;
		}
	}
}
