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


import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Abstract element reader that processes start elements of NeXML meta tags.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLMetaStartElementReader extends AbstractNeXMLElementReader {
	@Override
	public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException,
			XMLStreamException {
		
		StartElement element = event.asStartElement();
  	LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
  	QName type = streamDataProvider.getEventReader().parseQName(XMLUtils.readStringAttr(element, ATTR_XSI_TYPE, null), element);
  	URIOrStringIdentifier predicate;  	
		
		if (type.getLocalPart().equals(TYPE_LITERAL_META)) {
			streamDataProvider.getMetaType().push(EventContentType.LITERAL_META);
			
			predicate = new URIOrStringIdentifier(null, 
					streamDataProvider.getEventReader().parseQName(XMLUtils.readStringAttr(element, ATTR_PROPERTY, null), element));
			QName datatype = streamDataProvider.getEventReader().parseQName(XMLUtils.readStringAttr(element, ATTR_DATATYPE, null), element);		  			
			String content = XMLUtils.readStringAttr(element, ATTR_CONTENT, null);
			
			URIOrStringIdentifier originalType = null;
			if (datatype != null) {
				originalType = new URIOrStringIdentifier(null, datatype);
			}
			
			streamDataProvider.setAlternativeStringRepresentation(content);
			
			LiteralContentSequenceType contentType = LiteralContentSequenceType.XML;
			
			ObjectTranslator<?> translator = streamDataProvider.getParameters().getObjectTranslatorFactory()
					.getDefaultTranslatorWithPossiblyInvalidNamespace(datatype);
			if (translator != null) {
				contentType = LiteralContentSequenceType.SIMPLE;
			}
			
			streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataEvent(info.id, info.label, predicate, content, originalType, contentType));
			streamDataProvider.setCurrentLiteralContentSequenceType(contentType);
			
			XMLEvent nextEvent = streamDataProvider.getXMLReader().peek();
			if (((nextEvent.getEventType() == XMLStreamConstants.END_ELEMENT)) && (content != null)) {  // If no character data or custom XML is nested under this literal meta event the value of the content-attribute is used to create a LiteralMetadataContentEvent
				Object objectValue;
				if (translator != null) {
  				try {
						objectValue = translator.representationToJava(content, streamDataProvider);
						streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(objectValue, content));
					}
					catch (InvalidObjectSourceDataException e) {
						throw new JPhyloIOReaderException("The content of this meta tag could not be parsed to class " + translator.getObjectClass().getSimpleName() + ".", event.getLocation());
					}
				}
				else {
					streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(content, content));
				}
			}
			else if ((datatype != null) && !datatype.equals(W3CXSConstants.DATA_TYPE_TOKEN) && !datatype.equals(W3CXSConstants.DATA_TYPE_STRING) && (translator != null)) {
				Object objectValue = null;
				
				if (translator.hasStringRepresentation()) {
					String nestedContent = XMLUtils.readCharactersAsString(streamDataProvider.getXMLReader());
					
					if (nestedContent != null) {
						try {
							if (!"".equals(nestedContent.trim())) {
								objectValue = translator.representationToJava(nestedContent, streamDataProvider);
								streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(objectValue, nestedContent));
							}
							else if ((content != null) && !content.isEmpty()) {
								objectValue = translator.representationToJava(content, streamDataProvider);
								streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(objectValue, content));
							}
						}
						catch (InvalidObjectSourceDataException e) {
							throw new JPhyloIOReaderException("The nested content of this meta tag could not be parsed to class " + translator.getObjectClass().getSimpleName() + ".", event.getLocation());
						}
					}
				}
				else {
					try {
						objectValue = translator.readXMLRepresentation(streamDataProvider.getXMLReader(), streamDataProvider);
						streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(objectValue, null));
					}
					catch (InvalidObjectSourceDataException e) {
						throw new JPhyloIOReaderException("The nested XML content of this meta tag could not be parsed to class " + translator.getObjectClass().getSimpleName() + ".", event.getLocation());
					}
				}
			}
		}
		else if (type.getLocalPart().equals(TYPE_RESOURCE_META)) {
			streamDataProvider.getMetaType().push(EventContentType.RESOURCE_META);
			predicate = new URIOrStringIdentifier(null, 
					streamDataProvider.getEventReader().parseQName(XMLUtils.readStringAttr(element, ATTR_REL, null), element));
			String about = XMLUtils.readStringAttr(element, ATTR_ABOUT, null);
			String uri = XMLUtils.readStringAttr(element, ATTR_HREF, null);
			URI href = null;
			if (uri != null) {
  			try {
  				href = new URI(uri);
  			}
  			catch (URISyntaxException e) {
  				throw new JPhyloIOReaderException("An \"href\"-attribute element must specify a valid URI. Instead the string \"" + uri + "\" was found.", event.getLocation());
  			}
			}
			
			streamDataProvider.getCurrentEventCollection().add(new ResourceMetadataEvent(info.id, info.label, predicate, href, about));	  			
		}
		else {
			throw new JPhyloIOReaderException("Meta annotations can only be of type \"" + TYPE_LITERAL_META + "\" or \"" + 
					TYPE_RESOURCE_META + "\".", element.getLocation());
		}
	}
}
