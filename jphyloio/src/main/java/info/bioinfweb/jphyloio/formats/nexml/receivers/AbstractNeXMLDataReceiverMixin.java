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
package info.bioinfweb.jphyloio.formats.nexml.receivers;


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLConstants;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.XMLReadWriteUtils;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



/**
 * Provides a set of static methods to be used as static imports when needed
 * across multiple NeXML data receivers. Since not all of these classes allow direct access to a 
 * NeXMLWriterStreamDataProvider, an instance of it is a parameter of the static methods instead of an 
 * instance of AbstractNeXMLDataReceiver.
 * 
 * @author Sarah Wiechers
 * @since 0.0.0
 */
public class AbstractNeXMLDataReceiverMixin implements NeXMLConstants {
	
	
	public static void handleLiteralMeta(NeXMLWriterStreamDataProvider streamDataProvider, LiteralMetadataEvent event) throws XMLStreamException, JPhyloIOWriterException {
		XMLStreamWriter writer = streamDataProvider.getWriter();
		String metaType = streamDataProvider.getNeXMLPrefix(streamDataProvider.getWriter()) + ":" + TYPE_LITERAL_META;
		QName predicate;
		
		writer.writeStartElement(TAG_META.getLocalPart());
		streamDataProvider.writeLabeledIDAttributes(event, null);
		
		writer.writeAttribute(XMLReadWriteUtils.getXSIPrefix(streamDataProvider.getWriter()), ATTR_XSI_TYPE.getNamespaceURI(), 
				ATTR_XSI_TYPE.getLocalPart(), metaType);
		
		if (event.getPredicate().getURI() != null) { //TODO add entry to readWriteParameterMap to allow not writing metadata without QName predicate at all
			predicate = event.getPredicate().getURI();			
		}
		else {
			predicate = ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA;
		}
		
		if (event.getPredicate().getStringRepresentation() != null) {  // URIORStringIdentifier checks if either a string representation or an URI are present, both can not be null.
			writer.writeAttribute(ReadWriteConstants.ATTRIBUTE_STRING_KEY.getNamespaceURI(), ReadWriteConstants.ATTRIBUTE_STRING_KEY.getLocalPart(), event.getPredicate().getStringRepresentation());
		}
		
		writer.writeAttribute(ATTR_PROPERTY.getLocalPart(), obtainPrefix(streamDataProvider, predicate.getNamespaceURI()) + ":" + predicate.getLocalPart());
		
		if ((event.getOriginalType() != null) && (event.getOriginalType().getURI() != null)) { // Attribute is optional
			writer.writeAttribute(ATTR_DATATYPE.getLocalPart(), obtainPrefix(streamDataProvider, event.getOriginalType().getURI().getNamespaceURI()) 
					+ ":" + event.getOriginalType().getURI().getLocalPart());
		}
		
		if (event.getAlternativeStringValue() != null) {  // Attribute is optional
			writer.writeAttribute(ATTR_CONTENT.getLocalPart(), event.getAlternativeStringValue());
		}
		
		streamDataProvider.setCurrentLiteralMetaSequenceType(event.getSequenceType());
		streamDataProvider.setCurrentLiteralMetaDatatype(event.getOriginalType());
	}
	
	
	public static void checkLiteralMeta (NeXMLWriterStreamDataProvider streamDataProvider, LiteralMetadataEvent event) 
			throws XMLStreamException, JPhyloIOWriterException {
		if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
			QName resourceIdentifier;
			
			streamDataProvider.addToDocumentIDs(event.getID());
			
			if (event.getPredicate().getURI() != null) {
				resourceIdentifier = event.getPredicate().getURI();
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			}
			else {
				resourceIdentifier = ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA;
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			}
			
			if (event.getPredicate().getStringRepresentation() != null)  {				
				resourceIdentifier = ReadWriteConstants.ATTRIBUTE_STRING_KEY;
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			}
			
			if ((event.getOriginalType() != null) && (event.getOriginalType().getURI() != null)) {
				resourceIdentifier = event.getOriginalType().getURI();
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			}
		}
	}
	

	public static void handleLiteralContentMeta(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameters, 
			LiteralMetadataContentEvent event) throws XMLStreamException, ClassCastException, IOException {
		
		XMLStreamWriter writer = streamDataProvider.getWriter();
		
		switch (streamDataProvider.getCurrentLiteralMetaSequenceType()) {
			case SIMPLE:
				QName datatype = null;
				if (streamDataProvider.getCurrentLiteralMetaDatatype() != null) {
					datatype = streamDataProvider.getCurrentLiteralMetaDatatype().getURI();
				}
				
				ObjectTranslator<?> translator = parameters.getObjectTranslatorFactory()
						.getDefaultTranslatorWithPossiblyInvalidNamespace(datatype);
				if ((event.hasObjectValue())) {
					if (translator != null) {
						translator.writeXMLRepresentation(writer, event.getObjectValue(), null);
					}
					else if (event.hasStringValue()) {		
						writer.writeCharacters(event.getStringValue());
					}
					else {
						writer.writeCharacters(event.getObjectValue().toString());
					}
				}
				else if (event.hasStringValue()) {					
					writer.writeCharacters(event.getStringValue());
				}
				break;
			case XML:
				if (event.hasXMLEventValue()) {					
					XMLReadWriteUtils.writeCustomXML(writer, parameters, event.getXMLEvent());
				}
				break;			
		}
		
		streamDataProvider.setLiteralContentIsContinued(event.isContinuedInNextEvent());
	}
	
	
	public static void checkLiteralContentMeta(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameters, 
			LiteralMetadataContentEvent event) throws XMLStreamException {
		
		XMLReadWriteUtils.manageLiteralContentMetaNamespaces(streamDataProvider,parameters, event);
	}
	
	
	public static void handleResourceMeta(NeXMLWriterStreamDataProvider streamDataProvider, ResourceMetadataEvent event) throws ClassCastException, XMLStreamException, JPhyloIOWriterException {
		XMLStreamWriter writer = streamDataProvider.getWriter();
		String metaType = streamDataProvider.getNeXMLPrefix(streamDataProvider.getWriter()) + ":" + TYPE_RESOURCE_META;
		QName predicate;
		
		writer.writeStartElement(TAG_META.getLocalPart());		
		streamDataProvider.writeLabeledIDAttributes(event, event.getAbout());
		
		writer.writeAttribute(XMLReadWriteUtils.getXSIPrefix(streamDataProvider.getWriter()), ATTR_XSI_TYPE.getNamespaceURI(), 
				ATTR_XSI_TYPE.getLocalPart(), metaType);
		
		if (event.getRel().getURI() != null) {  //TODO add entry to readWriteParameterMap to allow not writing metadata without QName predicate at all
			predicate = event.getRel().getURI();		
		}
		else {
			predicate = ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA;
		}
		
		if (event.getRel().getStringRepresentation() != null)  {  // URIORSTringIdentifier checks if either string representation or URI are present, both can not be null
			writer.writeAttribute(ReadWriteConstants.ATTRIBUTE_STRING_KEY.getNamespaceURI(), ReadWriteConstants.ATTRIBUTE_STRING_KEY.getLocalPart(), 
					event.getRel().getStringRepresentation());
		}
		
		writer.writeAttribute(ATTR_REL.getLocalPart(), obtainPrefix(streamDataProvider, predicate.getNamespaceURI()) + ":" + predicate.getLocalPart());
		
		if (event.getHRef() != null) { // Attribute is optional
			writer.writeAttribute(ATTR_HREF.getLocalPart(), event.getHRef().toString());
		}		
	}
	
	
	public static void checkResourceMeta(NeXMLWriterStreamDataProvider streamDataProvider, ResourceMetadataEvent event) throws IOException, XMLStreamException {
		if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
			QName resourceIdentifier;
			streamDataProvider.addToDocumentIDs(event.getID());
			
			if (event.getRel().getURI() != null) {
				resourceIdentifier = event.getRel().getURI();
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			}
			else {
				resourceIdentifier = ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA;
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());				
			}
			
			if (event.getRel().getStringRepresentation() != null) {
				resourceIdentifier = ReadWriteConstants.ATTRIBUTE_STRING_KEY;
				streamDataProvider.setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			}
		}
	}
	
	
	public static void handleMetaEndEvent(NeXMLWriterStreamDataProvider streamDataProvider, JPhyloIOEvent event) throws IOException, XMLStreamException {
		if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
			if (streamDataProvider.isLiteralContentContinued()) {
				throw new InconsistentAdapterDataException("A literal meta end event was encountered, although the last literal meta content "
						+ "event was marked to be continued in a subsequent event.");
			}
			
			streamDataProvider.setCurrentLiteralMetaSequenceType(null);
		}		
		
		streamDataProvider.getWriter().writeEndElement();
	}
	
	
	public static void handleComment(NeXMLWriterStreamDataProvider streamDataProvider, CommentEvent event) throws ClassCastException, XMLStreamException {
		String comment = event.getContent();
		
		if (!comment.isEmpty()) {
			streamDataProvider.getCommentContent().append(comment);
		}
		
		if (!event.isContinuedInNextEvent()) {
			streamDataProvider.getWriter().writeComment(streamDataProvider.getCommentContent().toString());
			streamDataProvider.getCommentContent().delete(0, streamDataProvider.getCommentContent().length());			
		}
	}
	
	
	private static String obtainPrefix(NeXMLWriterStreamDataProvider streamDataProvider, String namespaceURI) throws XMLStreamException, JPhyloIOWriterException {
		String prefix = streamDataProvider.getWriter().getPrefix(namespaceURI);
		
		if (prefix == null) {
			throw new JPhyloIOWriterException("The namespace \"" + namespaceURI + "\" is not bound to a prefix.");
		}
		
		return prefix;
	}
}
