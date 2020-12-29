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


import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLColorTranslator;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLEventWriter;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLPredicateInfo;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLPredicateTreatment;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLPrivateConstants;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLWriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.phyloxml.PropertyOwner;
import info.bioinfweb.jphyloio.formats.xml.XMLReadWriteUtils;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



/**
 * Receiver that is used to translate between meta events with certain predicates indicating 
 * they contain information about a <i>PhyloXML</i> tag and the actual tag.
 * <p>
 * This receiver does not fully  validate the contents of the meta events. An {@link InconsistentAdapterDataException}
 * will be thrown if incorrect predicates are nested under a phylogeny, a node or an edge or if any predicate is nested 
 * in an incorrect order. Which predicates are allowed in which order can be obtained from the documentation of {@link PhyloXMLEventWriter}
 * or the <a href="http://www.phyloxml.org/documentation/version_1.10/phyloxml.xsd.html">PhyloXML schema</a>.
 * 
 * @see {@link PhyloXMLEventWriter}
 * 
 * @author Sarah Wiechers
 *
 */
public class PhyloXMLSpecificPredicatesDataReceiver extends PhyloXMLMetaDataReceiver {
	private Stack<QName> predicates = new Stack<QName>();
	private Stack<Integer> childIndices = new Stack<Integer>();	
	private QName currentDatatype;
	private boolean writeAppliesTo = false;
	private boolean writeElement = true;
	

	public PhyloXMLSpecificPredicatesDataReceiver(PhyloXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, PropertyOwner propertyOwner, QName parentPredicate) {
		super(streamDataProvider, parameterMap, propertyOwner);
		predicates.push(parentPredicate);
		childIndices.push(0);
	}


	/** 
	 * In this method LiteralMetadataEvents are processed. It is validated if the specific predicates are given in the correct order 
	 * and if they are nested in the corrected way, so valid <i>PhyloXML</i> elements can be written. It is also checked if only the 
	 * correct predicates are nested under nodes and edges. If either the order or the way 
	 * the elements are nested is not correct, an {@link InconsistentAdapterDataException} is thrown. It is not validated if the number of 
	 * child elements is correct (i. e. if they are present too often or not often enough).
	 */
	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		if (getStreamDataProvider().getMetaIDs().contains(event.getID()) && writeElement) {
			int currentIndex = 0;
			writeElement = true;
			PhyloXMLPredicateInfo predicateInfo = getStreamDataProvider().getPredicateInfoMap().get(event.getPredicate().getURI());
	
			if (event.getPredicate().getURI() != null) {
				
				if (event.getPredicate().getURI().getNamespaceURI().equals(PHYLOXML_PREDICATE_NAMESPACE) 
						&& !Arrays.asList(getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getAllowedChildren()).contains(event.getPredicate().getURI())) {
					throw new InconsistentAdapterDataException("The element \"" + event.getPredicate().getURI().getLocalPart() + "\" is not allowed to occur under the element \"" 
							+ predicates.peek().getLocalPart() + "\".");
				}
				
				for (QName child : getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getAllowedChildren()) {
					currentIndex++;
					
					if (child.equals(event.getPredicate().getURI()) 
							|| ((child.equals(PhyloXMLPrivateConstants.IDENTIFIER_CUSTOM_XML) || child.equals(PhyloXMLPrivateConstants.IDENTIFIER_ANY_PREDICATE)) 
									&& (predicateInfo == null))) {
						
						// Attributes and values can not be repeated (if the content is split, more than one content event should be nested instead of repeating the literal meta)
						boolean writeAttribute = (predicateInfo != null) && predicateInfo.getTreatment().equals(PhyloXMLPredicateTreatment.ATTRIBUTE) 
								&& currentIndex > childIndices.peek();
						boolean writeValue = (predicateInfo != null) && predicateInfo.getTreatment().equals(PhyloXMLPredicateTreatment.VALUE) 
								&& currentIndex > childIndices.peek();
						boolean writeTag = ((predicateInfo != null) && !predicateInfo.getTreatment().equals(PhyloXMLPredicateTreatment.ATTRIBUTE) 
								&& !predicateInfo.getTreatment().equals(PhyloXMLPredicateTreatment.VALUE) && currentIndex >= childIndices.peek());
						boolean writeOtherContent = ((predicateInfo == null) && currentIndex >= childIndices.peek());
						
						if (writeAttribute || writeValue || writeTag || writeOtherContent) {
							childIndices.pop();
							childIndices.push(currentIndex);
							
							if (predicateInfo != null) {
								predicates.push(event.getPredicate().getURI());
								getStreamDataProvider().getMetaIDs().remove(event.getID());
								
								switch (predicateInfo.getTreatment()) {
									case TAG_AND_VALUE:
										QName tagName = predicateInfo.getTranslation();
										getStreamDataProvider().getWriter().writeStartElement(tagName.getNamespaceURI(), tagName.getLocalPart());
										
										if (event.getOriginalType() != null) {
											currentDatatype = event.getOriginalType().getURI();
										}
										break;
									default:
										break;
								}
							}
							else if (child.equals(PhyloXMLPrivateConstants.IDENTIFIER_CUSTOM_XML)) {
								predicates.push(PhyloXMLPrivateConstants.IDENTIFIER_CUSTOM_XML);
								getStreamDataProvider().getMetaIDs().remove(event.getID());
							}
							else if (child.equals(PhyloXMLPrivateConstants.IDENTIFIER_ANY_PREDICATE) && !predicates.peek().equals(PhyloXMLPrivateConstants.IDENTIFIER_NODE)) {
								predicates.push(PhyloXMLPrivateConstants.IDENTIFIER_ANY_PREDICATE);
								getStreamDataProvider().getMetaIDs().remove(event.getID());
								writeAppliesTo = true;
								
								if (event.getOriginalType() != null) {
									currentDatatype = event.getOriginalType().getURI();
								}
								
								if (predicates.peek().equals(PREDICATE_ANNOTATION)) {
									getStreamDataProvider().getWriter().writeStartElement(TAG_PROPERTY.getLocalPart());
								}
								
								getStreamDataProvider().getWriter().writeAttribute(ATTR_REF.getLocalPart(), 
										getStreamDataProvider().getWriter().getPrefix(event.getPredicate().getURI().getNamespaceURI()) 
										+ XMLUtils.QNAME_SEPARATOR + event.getPredicate().getURI().getLocalPart());
								
								getStreamDataProvider().getWriter().writeAttribute(ATTR_DATATYPE.getLocalPart(), XMLReadWriteUtils.XSD_DEFAULT_PRE 
										+ XMLUtils.QNAME_SEPARATOR + event.getOriginalType().getURI().getLocalPart());						
							}					
						}
						else {
							throw new InconsistentAdapterDataException("Metaevents with PhyloXML-specific predicates must be given in the correct order. "
									+ "Attributes can only be written once.");
						}
					}
				}
			}
		}
		else {
			writeElement = false;
		}
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		if (writeElement) {
			switch (getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getTreatment()) {
				case VALUE:
					getStreamDataProvider().getWriter().writeCharacters(event.toString());
					predicates.pop();
					break;
				case TAG_AND_VALUE:
					if (writeAppliesTo) {
						getStreamDataProvider().getWriter().writeAttribute(ATTR_APPLIES_TO.getLocalPart(), getPropertyOwner().toString().toLowerCase());
						writeAppliesTo = false;
					}
					
					if (event.hasObjectValue() && predicates.peek().equals(PREDICATE_COLOR)) {
						PhyloXMLColorTranslator colorTranslator = new PhyloXMLColorTranslator();
						colorTranslator.writeXMLRepresentation(getStreamDataProvider().getWriter(), event.getObjectValue(), getStreamDataProvider());
					}
					else {
						ObjectTranslator<?> translator = getParameterMap().getObjectTranslatorFactory().getDefaultTranslatorWithPossiblyInvalidNamespace(currentDatatype);
						String value = processLiteralContent(event, translator, currentDatatype);
						
						if (value != null) {
							getStreamDataProvider().getWriter().writeCharacters(value);
						}
					}
					
					break;
				case ATTRIBUTE:
					QName attribute = getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getTranslation();
					getStreamDataProvider().getWriter().writeAttribute(attribute.getPrefix(), attribute.getNamespaceURI(), attribute.getLocalPart(), event.toString());
					predicates.pop();
					
					if (attribute.equals(ATTR_APPLIES_TO)) {
						writeAppliesTo = false;
					}				
					break;
				case CUSTOM_XML:
					if (event.hasXMLEventValue()) {					
						writeCustomXMLTag(event.getXMLEvent());
					}
					break;
				default:
					break;
			}
		}
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		if (getStreamDataProvider().getMetaIDs().contains(event.getID())) {
			int currentIndex = 0;
			writeElement = true;
			
			if (event.getRel().getURI().getNamespaceURI().equals(PHYLOXML_PREDICATE_NAMESPACE) 
					&& !Arrays.asList(getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getAllowedChildren()).contains(event.getRel().getURI())) {
				throw new InconsistentAdapterDataException("The element \"" + event.getRel().getURI().getLocalPart() + "\" is not allowed to occur under the element \"" 
						+ predicates.peek().getLocalPart() + "\".");
			}
			
			for (QName child : getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getAllowedChildren()) {
				currentIndex++;
				
				if (child.equals(event.getRel().getURI())) {
					if (currentIndex >= childIndices.peek()) {
						childIndices.pop();
						childIndices.push(currentIndex);
						
						getStreamDataProvider().getMetaIDs().remove(event.getID());
						predicates.push(event.getRel().getURI());
						childIndices.push(-1);
						
						switch (getStreamDataProvider().getPredicateInfoMap().get(event.getRel().getURI()).getTreatment()) {
							case TAG:
								QName tagName = getStreamDataProvider().getPredicateInfoMap().get(event.getRel().getURI()).getTranslation();
								getStreamDataProvider().getWriter().writeStartElement(tagName.getNamespaceURI(), tagName.getLocalPart());							
								break;
							case VALUE:
								getStreamDataProvider().getWriter().writeCharacters(event.getHRef().toString());
								break;
							default:
								break;
						}
					}
					else {
						throw new InconsistentAdapterDataException("The metaevent with the predicate \"" + event.getRel().getURI().getLocalPart() 
								+ "\" was not nested under the element \"" + predicates.peek() + "\" in the correct order.");
					}
				}
			}
		}
		else {
			writeElement = false;
		}
	}


	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		if (getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getTreatment().equals(PhyloXMLPredicateTreatment.TAG_AND_VALUE)
				&& event.getType().getContentType().equals(EventContentType.LITERAL_META)) {
			getStreamDataProvider().getWriter().writeEndElement();
			predicates.pop();
		}
		else if (getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getTreatment().equals(PhyloXMLPredicateTreatment.TAG)
				&& event.getType().getContentType().equals(EventContentType.RESOURCE_META) && (predicates.size() > 1)) {
			
			if (!predicates.peek().equals(PREDICATE_PROPERTY)) {				
				getStreamDataProvider().getWriter().writeEndElement();
			}
			predicates.pop();
			childIndices.pop();			
		}
		else if (getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getTreatment().equals(PhyloXMLPredicateTreatment.VALUE)
			&& event.getType().getContentType().equals(EventContentType.RESOURCE_META) && (predicates.size() > 1)) {
				predicates.pop();
				childIndices.pop();
		}
		else if (getStreamDataProvider().getPredicateInfoMap().get(predicates.peek()).getTreatment().equals(PhyloXMLPredicateTreatment.CUSTOM_XML)) {
			predicates.pop();
			
			if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
				childIndices.pop();
			}
		}
	}
}