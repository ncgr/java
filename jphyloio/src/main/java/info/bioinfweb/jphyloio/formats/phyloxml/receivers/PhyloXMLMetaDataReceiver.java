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
package info.bioinfweb.jphyloio.formats.phyloxml.receivers;


import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLConstants;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLMetaEventInfo;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLWriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.phyloxml.PropertyOwner;
import info.bioinfweb.jphyloio.formats.xml.XMLReadWriteUtils;
import info.bioinfweb.jphyloio.formats.xml.receivers.AbstractXMLDataReceiver;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Receiver that implements a general strategy of writing metadata to a <i>PhyloXML</i> document. 
 * It also implements some functionality that is used by inheriting receivers, e.g. to write custom XML.
 * 
 * @author Sarah Wiechers
 *
 */
public class PhyloXMLMetaDataReceiver extends AbstractXMLDataReceiver<PhyloXMLWriterStreamDataProvider> implements PhyloXMLConstants {
	public static final Set<QName> VALID_XSD_TYPES = new HashSet<QName>();
	
	
	private PropertyOwner propertyOwner;
	private boolean hasSimpleContent;
	private URIOrStringIdentifier literalPredicate;
	private URIOrStringIdentifier originalType;
	private boolean writeContent;
	private boolean writePropertyStart;
	private String currentLiteralMetaID;


	public PhyloXMLMetaDataReceiver(PhyloXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, PropertyOwner propertyOwner) {
		super(streamDataProvider, parameterMap);
		this.propertyOwner = propertyOwner;
		
		fillValidXSDTypes();
	}


	public PropertyOwner getPropertyOwner() {
		return propertyOwner;
	}


	public boolean hasSimpleContent() {
		return hasSimpleContent;
	}


	public boolean isWriteContent() {
		return writeContent;
	}


	public URIOrStringIdentifier getOriginalType() {
		return originalType;
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		writeContent = determineWriteMeta(event.getID(), event.getPredicate());
		
		if (writeContent) {
			hasSimpleContent = event.getSequenceType().equals(LiteralContentSequenceType.SIMPLE);
			literalPredicate = event.getPredicate();
			originalType = event.getOriginalType();
			currentLiteralMetaID = event.getID();
			writePropertyStart = true;
		}
	}
	

	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		if (writeContent) {
			if (hasSimpleContent()) {
				String value = null;
				
				if ((originalType == null) || (originalType.getURI() == null)) {
					originalType = new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_STRING);				
				}
				
				ObjectTranslator<?> translator = getParameterMap().getObjectTranslatorFactory()
						.getDefaultTranslatorWithPossiblyInvalidNamespace(originalType.getURI());				

				if (((translator == null) && !VALID_XSD_TYPES.contains(originalType.getURI()))
						|| ((translator != null) && translator.hasStringRepresentation() && !VALID_XSD_TYPES.contains(originalType.getURI()))){
					originalType = new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_STRING);	
				}
				
				value = processLiteralContent(event, translator, originalType.getURI());				
								
				if (value != null) {
					if (writePropertyStart) {
						writePropertyTag(literalPredicate, originalType, null, false);
						writePropertyStart = false;
					}
					
					getStreamDataProvider().getWriter().writeCharacters(value);
					
					if (!event.isContinuedInNextEvent()) {
						getStreamDataProvider().getWriter().writeEndElement();		
					}
					
					getStreamDataProvider().getMetaIDs().remove(currentLiteralMetaID);
				}
				
				getStreamDataProvider().setLiteralContentIsContinued(event.isContinuedInNextEvent());				
			}			
		}
	}
	

	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		if (determineWriteMeta(event.getID(), event.getRel()) 
				&& !((event.getRel().getURI() != null) && event.getRel().getURI().equals(ReadWriteConstants.PREDICATE_HAS_CUSTOM_XML))) {
			String uri = null;
			
			if (event.getHRef() != null) {
				uri = event.getHRef().toString();				
			}
			
			writePropertyTag(event.getRel(), new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_ANY_URI), uri, true);
			
			getStreamDataProvider().getMetaIDs().remove(event.getID());
		}
	}
	

	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		if (event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
			originalType = null;
			writePropertyStart = false;
			if (getStreamDataProvider().isLiteralContentContinued()) {
				throw new InconsistentAdapterDataException("A literal meta end event was encounterd, although the last literal meta content "
						+ "event was marked to be continued in a subsequent event.");
			}
		}
	}
	
	
	protected void writePropertyTag(URIOrStringIdentifier predicate, URIOrStringIdentifier datatype, String value, boolean writeEndElement) throws XMLStreamException, JPhyloIOWriterException {			
		getStreamDataProvider().getWriter().writeStartElement(TAG_PROPERTY.getLocalPart());
		
		if (predicate.getURI() == null) {
			predicate = new URIOrStringIdentifier(null, ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA);
		}
		
		getStreamDataProvider().getWriter().writeAttribute(ATTR_REF.getLocalPart(), 
				getStreamDataProvider().getWriter().getPrefix(predicate.getURI().getNamespaceURI()) + ":" + predicate.getURI().getLocalPart());		
		
		if ((datatype == null) || (datatype.getURI() == null)) {
			datatype = new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_STRING);
		}
	
		getStreamDataProvider().getWriter().writeAttribute(ATTR_DATATYPE.getLocalPart(), XMLReadWriteUtils.XSD_DEFAULT_PRE 
				+ ":" + datatype.getURI().getLocalPart());
		
		getStreamDataProvider().getWriter().writeAttribute(ATTR_APPLIES_TO.getLocalPart(), propertyOwner.toString());
		
		if (value != null) {
			getStreamDataProvider().getWriter().writeCharacters(value);
		}

		if (writeEndElement) {
			getStreamDataProvider().getWriter().writeEndElement();		
		}
	}
	
	
	protected void writeCustomXMLTag(XMLEvent event) throws XMLStreamException {
		boolean writeCustomXML = true;
		
		switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				StartElement element = event.asStartElement();				
				
				if (!element.getName().getNamespaceURI().equals(PhyloXMLConstants.PHYLOXML_NAMESPACE)) {
					getStreamDataProvider().getCustomXMLElements().push(event.asStartElement().getName().getLocalPart());
				}
				else {  // Do not write known PhyloXML-Tags as custom XML
					throw new InconsistentAdapterDataException("The element \"" + element.getName().getLocalPart() + "\" was not nested correctly.");
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				try {
					getStreamDataProvider().getCustomXMLElements().pop();
				}
				catch (EmptyStackException e) {					
					throw new InconsistentAdapterDataException("One more end element than start elements was found in the nested custom XML.");
				}
				break;
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.CDATA:
				if (getStreamDataProvider().getCustomXMLElements().isEmpty()) {
					writeCustomXML = false;
					getParameterMap().getLogger().addWarning("A character or CDATA element that was not nested in any custom XML tag was found, "
							+ "but could not be written since PhyloXML does not support this.");
				}
				break;
			default:
				break;
		}
		
		if (writeCustomXML) {
			XMLReadWriteUtils.writeCustomXML(getStreamDataProvider().getWriter(), getParameterMap(), event);
		}
	}
	
	
	protected boolean determineWriteMeta(String id, URIOrStringIdentifier predicate) {
		boolean writeMeta = false;
		PhyloXMLMetaEventInfo metaInfo = getStreamDataProvider().getMetaEvents().get(id);
		
		if (getStreamDataProvider().getMetaIDs().contains(id)) {
			if (!(predicate.getURI() != null && predicate.getURI().getNamespaceURI().equals(PHYLOXML_PREDICATE_NAMESPACE))) {  //TODO Should really all non-PhyloXML attributes that are not null cause an exception? Or should the condition be: (predicate.getURI() != null) && !predicate.getURI().getNamespaceURI().equals(PHYLOXML_PREDICATE_NAMESPACE)?
				switch (getParameterMap().getPhyloXMLMetadataTreatment()) {
					case NONE:
						writeMeta = false;
						break;
					case LEAVES_ONLY:
						writeMeta = metaInfo.getChildIDs().isEmpty();
						break;
					case SEQUENTIAL:
						writeMeta = true;
						break;
					case TOP_LEVEL_WITH_CHILDREN:
						writeMeta = metaInfo.isTopLevel() && !metaInfo.getChildIDs().isEmpty();
						break;
					case TOP_LEVEL_WITHOUT_CHILDREN:
						writeMeta = metaInfo.isTopLevel() && metaInfo.getChildIDs().isEmpty();
						break;
				}
				
				return writeMeta;
			}
			else {
				throw new InconsistentAdapterDataException("The meta event \"" + id + "\" with the PhyloXML-specific predicate \"" 
						+ predicate.getURI().getLocalPart() + "\" was not nested correctly.");
			}
		}
		else {
			return false;
		}
	}
	
	
	protected String processLiteralContent(LiteralMetadataContentEvent event, ObjectTranslator<?> translator, QName datatype) 
			throws IOException, XMLStreamException {
		
		String value = null;
		
		if (event.hasObjectValue()) {
			if ((translator != null) && translator.hasStringRepresentation()) {
				try {
					value = translator.javaToRepresentation(event.getObjectValue(), getStreamDataProvider());
				}
				catch (ClassCastException e) {
					throw new JPhyloIOWriterException("The original type of the object declared in this event did not match the actual object type. "
							+ "Therefore it could not be parsed.");
				}				
			}
			else if (event.hasStringValue()) {
				value = event.getStringValue();  // It is not validated if the string value has the correct format as required by the datatype
			}
			else {
				value = event.getObjectValue().toString();
			}
		}
		else {
			value = event.getStringValue();  // It is not validated if the string value has the correct format as required by the datatype
		}
		
		return value;
	}
	
	
	private static void fillValidXSDTypes() {
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_STRING);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_BOOLEAN);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_DECIMAL);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_FLOAT);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_DOUBLE);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_DURATION);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_DATE_TIME);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_TIME);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_DATE);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_G_YEAR_MONTH);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_G_YEAR);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_G_MONTH_DAY);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_G_DAY);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_G_MONTH);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_HEX_BINARY);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_BASE_64_BINARY);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_ANY_URI);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_NORMALIZED_STRING);  // Currently no object translator for this type exists
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_TOKEN);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_INTEGER);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_NON_POSITIVE_INTEGER);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_NEGATIVE_INTEGER);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_LONG);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_INT);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_SHORT);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_BYTE);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_NON_NEGATIVE_INTEGER);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_UNSIGNED_LONG);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_UNSIGNED_INT);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_UNSIGNED_SHORT);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_UNSIGNED_BYTE);
		VALID_XSD_TYPES.add(W3CXSConstants.DATA_TYPE_POSITIVE_INTEGER);
	}
}
