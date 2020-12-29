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
package info.bioinfweb.jphyloio.formats.nexml;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.collections.NonStoringCollection;
import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.PartEndEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.SingleSequenceTokenEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.BufferedEventInfo;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.AbstractIDLinkSetElementReader;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.AbstractNeXMLElementReader;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.AbstractSetElementReader;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.NeXMLMetaEndElementReader;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.NeXMLMetaStartElementReader;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.NeXMLSetEndElementReader;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.NeXMLStartDocumentElementReader;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLEventReader;
import info.bioinfweb.jphyloio.formats.xml.AttributeInfo;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.CommentElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLElementReaderKey;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * An event reader for the <a href="http://nexml.org/">NeXML format</a>. The majority of <i>NeXML</i> elements are supported by 
 * this reader, since the data model of <i>JPhyloIO</i> is heavily influenced by the data model of <i>NeXML</i>.
 * <p>
 * Element IDs found in <i>NeXML</i> documents are directly used as IDs of <i>JPhyloIO</i> events. All {@code meta} tags
 * of <i>NeXML</i> are represented by according {@link ResourceMetadataEvent}s or {@link LiteralMetadataEvent}s. Since there are no
 * equivalent <i>JPhyloIO</i> events for the {@code format} or {@code matrix} tags of <i>NeXML</i>, metadata nested under these tags
 * is translated into meta-events between the start and end events of the type {@link EventContentType#ALIGNMENT}. To distinguish 
 * them from meta-events generated from {@code meta} tags nested directly under the {@code characters} tag (which are are also fired 
 * at this position), these meta-events are grouped by resource meta start and end events with the predicates 
 * {@link NeXMLConstants#PREDICATE_FORMAT} or {@link NeXMLConstants#PREDICATE_MATRIX} around them. (Note that these events are 
 * generated by the reader only to group according metadata and do not have any equivalent {@code meta} tag in the document. 
 * {@link NeXMLEventWriter} can handle such metadata structures accordingly.) <i>NeXML</i> specific tags found nested under a 
 * literal {@code meta} tag are processed in the same manner as custom <i>XML</i> elements in this position.
 * <p>
 * <i>NeXML</i> can store different types of sequence data. While DNA, RNA, amino acid and continuous data is processed 
 * straightforwardly, restriction and standard data is both processed as {@link CharacterStateSetType#DISCRETE}. Standard data
 * represented by integers is translated according to the {@ code TokenTranslationStrategy} specified, either to the ID or label 
 * specified in the token definition or not at all. {@link SingleSequenceTokenEvent}s generated from tokens in {@code cell} tags are 
 * fired in the order of the alignment columns the tokens belong to. If they are not in the correct order in the file, some 
 * buffering is necessary to sort them.
 * <p>
 * Most of the sets modeled in <i>NeXML</i> are supported by <i>JPhyloIO</i> as well, however sets of cells and sets of character 
 * states are currently not. These sets will be ignored while reading a <i>NeXML</i> document. 
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_ALLOW_DEFAULT_NAMESPACE}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_NEXML_TOKEN_TRANSLATION_STRATEGY}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_USE_OTU_LABEL}</li>
 * </ul>
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class NeXMLEventReader extends AbstractXMLEventReader<NeXMLReaderStreamDataProvider> implements NeXMLConstants {

	
	private int currentMetaLiteralStartLevel = -1;
	
	
	public NeXMLEventReader(InputStream stream, ReadWriteParameterMap parameters)	throws IOException, XMLStreamException {
		super(stream, parameters);
	}


	public NeXMLEventReader(Reader reader, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		super(reader, parameters);
	}


	public NeXMLEventReader(XMLEventReader xmlReader,	ReadWriteParameterMap parameters) {
		super(xmlReader, parameters);
	}


	public NeXMLEventReader(File file, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		super(file, parameters);
	}
	
	
	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.NEXML_FORMAT_ID;
	}
	
	
	@Override
	protected NeXMLReaderStreamDataProvider createStreamDataProvider() {
		return new NeXMLReaderStreamDataProvider(this);
	}
	
	
	public TokenTranslationStrategy getTranslateTokens() {
		return getParameters().getTranslateTokens();
	}


	@Override
	protected void fillMap() {
		AbstractNeXMLElementReader readMetaStart = new NeXMLMetaStartElementReader();
		
		AbstractNeXMLElementReader readMetaEnd = new NeXMLMetaEndElementReader();
		
		AbstractNeXMLElementReader readMetaWithPredicateStart = new NeXMLMetaStartElementReader() {
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				if (streamDataProvider.getParentName().equals(TAG_SET.getLocalPart()) && !streamDataProvider.isCurrentSetSupported()) {					
					streamDataProvider.setCurrentEventCollection(new NonStoringCollection<JPhyloIOEvent>());					
				}
				
				if (streamDataProvider.getAdditionalResourceMetaRel() == null) {
					URIOrStringIdentifier predicate = null;
					
					if (streamDataProvider.getParentName().equals(TAG_FORMAT.getLocalPart())) {
						predicate = new URIOrStringIdentifier(null, PREDICATE_FORMAT);
					}
					else if (streamDataProvider.getParentName().equals(TAG_MATRIX.getLocalPart())) {
						predicate = new URIOrStringIdentifier(null, PREDICATE_MATRIX);
					}					
					// Add cases if element reader is registered for more parent tags
					
					if (predicate != null) {
			    	streamDataProvider.getCurrentEventCollection().add(new ResourceMetadataEvent(RESERVED_ID_PREFIX + DEFAULT_META_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), 
			    			null, predicate, null, null));  // ID conflict theoretically possible
			    	streamDataProvider.setAdditionalResourceMetaRel(predicate);
					}
				}
				
				super.readEvent(streamDataProvider, event);
			}
		};
		
		AbstractNeXMLElementReader readMetaWithPredicateEnd = new NeXMLMetaEndElementReader() {
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				super.readEvent(streamDataProvider, event);
				
				if (streamDataProvider.getParentName().equals(TAG_SET.getLocalPart()) && !streamDataProvider.isCurrentSetSupported()) {
					streamDataProvider.resetCurrentEventCollection();
				}
			}			
		};
		
		AbstractNeXMLElementReader readNodeStart = new AbstractNeXMLElementReader() {
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				OTUorOTUsEventInformation info = getOTUorOTUsEventInformation(streamDataProvider, element);
				boolean isRoot = XMLUtils.readBooleanAttr(element, ATTR_ROOT, false);
				
				streamDataProvider.getCurrentEventCollection().add(new NodeEvent(info.id,	info.label, info.otuOrOtusID, isRoot));
			}
		};
		
		AbstractNeXMLElementReader readNodeEnd = new AbstractNeXMLElementReader() {
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.NODE));
			}
		};
		
		AbstractNeXMLElementReader readEdgeStart = new AbstractNeXMLElementReader() {
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				try {
					LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
					String targetID = XMLUtils.readStringAttr(element, ATTR_TARGET, null);
					double length = XMLUtils.readDoubleAttr(element, ATTR_LENGTH, Double.NaN); // It is not a problem for JPhyloIO, if floating point values are specified for IntTrees.

					if (targetID == null) {
						throw new JPhyloIOReaderException("The \"target\" attribute of an edge or rootedge definition in NeXML must not be omitted.", 
								element.getLocation());
					}
					else {
						streamDataProvider.getCurrentEventCollection().add(new EdgeEvent(info.id, info.label, 
								XMLUtils.readStringAttr(element, ATTR_SOURCE, null), targetID, length)); // The source ID will be null for rootedges, which is valid.
					}
				}
				catch (NumberFormatException e) {
					throw new JPhyloIOReaderException("The attribute value \"" + element.getAttributeByName(ATTR_LENGTH).getValue() + 
							"\" is not a valid branch length.", element.getLocation(), e);
				}
			}
		};
		
		AbstractNeXMLElementReader readEdgeEnd = new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.EDGE));
			}
		};
		
		AbstractNeXMLElementReader readStateSetStart = new AbstractNeXMLElementReader() {		
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				String symbol = XMLUtils.readStringAttr(element, ATTR_SYMBOL, null);
				
	  		if (symbol == null) {
	  			throw new JPhyloIOReaderException("State tag must have an attribute called \"" + ATTR_SYMBOL + "\".", element.getLocation());
	  		}
	  		
	  		streamDataProvider.setCurrentSingleTokenDefinition(new NeXMLSingleTokenDefinitionInformation(info.id, info.label, symbol, new ArrayList<String>()));
				streamDataProvider.setCurrentEventCollection(new ArrayList<JPhyloIOEvent>());  // Meta events nested under this state set are buffered here
			}
		};
		
		AbstractNeXMLElementReader readStateSetEnd = new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				NeXMLSingleTokenDefinitionInformation info = streamDataProvider.getCurrentSingleTokenDefinition();
				String symbol = info.getSymbol();
				String translation = symbol;
				
				if (streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.DISCRETE)) {					
					if (!streamDataProvider.getEventReader().getTranslateTokens().equals(TokenTranslationStrategy.NEVER)) {
		   			if (streamDataProvider.getEventReader().getTranslateTokens().equals(TokenTranslationStrategy.SYMBOL_TO_LABEL) 
		   					&& (info.getLabel() != null)) {
		   				translation = info.getLabel();
		   			}
		   			else {  // SYMBOL_TO_ID or label was null
		   				translation = info.getID();
		   			}
		   		}
					
					if (!symbol.equals("" + SequenceUtils.GAP_CHAR) && !symbol.equals("" + SequenceUtils.MISSING_DATA_CHAR)) {
						try {
							streamDataProvider.getTokenSets().get(streamDataProvider.getElementTypeToCurrentIDMap().get(EventContentType.TOKEN_SET_DEFINITION))
									.getSymbolTranslationMap().put(Integer.parseInt(symbol), translation);
						}
						catch (NumberFormatException e) {
							throw new JPhyloIOReaderException("The symbol \"" + symbol + 
									"\" of a standard data token definition must be of type integer.", event.getLocation());
						}
					}
				}				
				
	  		if (symbol != null) {
					CharacterSymbolType tokenType;				
	  			if (streamDataProvider.getElementName().equals(TAG_POLYMORPHIC.getLocalPart())) {
						tokenType = CharacterSymbolType.POLYMORPHIC;
					}
					else {
						tokenType = CharacterSymbolType.UNCERTAIN;
					}
	  			
	  			streamDataProvider.getTokenDefinitionIDToSymbolMap().put(info.getID(), symbol);
	  			
	  			Collection<JPhyloIOEvent> nestedEvents = streamDataProvider.resetCurrentEventCollection();
	  			
	  			streamDataProvider.getCurrentEventCollection().add(new SingleTokenDefinitionEvent(info.getID(), info.getLabel(), symbol, 
	  					parseStateMeaning(symbol), tokenType, info.getConstituents()));	  		
	  			
	  			for (JPhyloIOEvent nestedEvent : nestedEvents) {
	  				streamDataProvider.getCurrentEventCollection().add(nestedEvent);
	  			}
	  			
	  			streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_TOKEN_DEFINITION));
	  		}				
			}
		};
		
		AbstractNeXMLElementReader readMemberStart = new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				String state = XMLUtils.readStringAttr(element, ATTR_SINGLE_STATE_LINK, null);
				
				if (streamDataProvider.getTokenDefinitionIDToSymbolMap().containsKey(state)) {
					streamDataProvider.getCurrentSingleTokenDefinition().getConstituents().add(streamDataProvider.getTokenDefinitionIDToSymbolMap().get(state));
				}
				else {
					throw new JPhyloIOReaderException("A single token definition referenced the ID \"" + state + "\" of a state that was not specified before.", element.getLocation()); 
				}
			}
		};
		
		AbstractNeXMLElementReader nodeAndEdgeSetEndReader = new NeXMLSetEndElementReader(EventContentType.NODE_EDGE_SET);
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_META, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_META, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_SET, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaWithPredicateStart);
		putElementReader(new XMLElementReaderKey(TAG_SET, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaWithPredicateEnd);
	
		putElementReader(new XMLElementReaderKey(TAG_OTUS, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_OTUS, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_OTU, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_OTU, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		
		putElementReader(new XMLElementReaderKey(TAG_CHARACTERS, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_CHARACTERS, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaWithPredicateStart);
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaWithPredicateEnd);		
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_STATE, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_STATE, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_UNCERTAIN, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_UNCERTAIN, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_POLYMORPHIC, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_POLYMORPHIC, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_CHAR, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_CHAR, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_MATRIX, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaWithPredicateStart);
		putElementReader(new XMLElementReaderKey(TAG_MATRIX, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaWithPredicateEnd);
		putElementReader(new XMLElementReaderKey(TAG_ROW, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_ROW, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_CELL, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_CELL, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_EDGE, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_EDGE, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		putElementReader(new XMLElementReaderKey(TAG_ROOTEDGE, TAG_META, XMLStreamConstants.START_ELEMENT), readMetaStart);
		putElementReader(new XMLElementReaderKey(TAG_ROOTEDGE, TAG_META, XMLStreamConstants.END_ELEMENT), readMetaEnd);
		
		putElementReader(new XMLElementReaderKey(TAG_META, null, XMLStreamConstants.CHARACTERS), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				if ((streamDataProvider.getMetaType() != null) && streamDataProvider.getMetaType().peek().equals(EventContentType.LITERAL_META)) {  // Content events are only allowed under literal meta events
					String content = event.asCharacters().getData();
					boolean isContinued = streamDataProvider.getXMLReader().peek().equals(XMLStreamConstants.CHARACTERS);
					
					if (streamDataProvider.getCurrentLiteralContentSequenceType().equals(LiteralContentSequenceType.SIMPLE)) {						
						if (!content.matches("\\s+")) {
							if (isContinued) {
								streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(content, isContinued));
							}
							else {
								streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(content, content));
							}
						}
						else if (streamDataProvider.getAlternativeStringRepresentation() != null) {
							content = streamDataProvider.getAlternativeStringRepresentation();
							if (!isContinued) {
								streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(content, content));
							}
						}
					}
					else {  // XML and simple values with unknown original type that have a content attribute and only whitespace as nested content (Reason: For unknown types, it cannot be determined whether they are simple or XML.)
						streamDataProvider.getCurrentEventCollection().add(
								new LiteralMetadataContentEvent(event.asCharacters(), isContinued));
					}
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				if (!streamDataProvider.getMetaType().isEmpty() && streamDataProvider.getMetaType().peek().equals(EventContentType.LITERAL_META)) {  // Content events are only allowed under literal meta events					
					streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(event.asStartElement(), false));
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.CHARACTERS), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {				
				if (!streamDataProvider.getMetaType().isEmpty() && streamDataProvider.getMetaType().peek().equals(EventContentType.LITERAL_META)) {  // Content events are only allowed under literal meta events
					if (!event.asCharacters().getData().matches("\\s+")) {
						streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(event.asCharacters(), 
								streamDataProvider.getXMLReader().peek().equals(XMLStreamConstants.CHARACTERS)));
					}
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				if (!streamDataProvider.getMetaType().isEmpty() && streamDataProvider.getMetaType().peek().equals(EventContentType.LITERAL_META)) {  // Content events are only allowed under literal meta events					
					streamDataProvider.getCurrentEventCollection().add(
							new LiteralMetadataContentEvent(event.asEndElement(), false));
				}
			}
		});

		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.START_DOCUMENT), new NeXMLStartDocumentElementReader());
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.END_DOCUMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.DOCUMENT));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_OTUS, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				streamDataProvider.getCurrentEventCollection().add(new LabeledIDEvent(EventContentType.OTU_LIST, info.id,	info.label));
				streamDataProvider.getElementTypeToCurrentIDMap().put(EventContentType.OTU_LIST, info.id);
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_OTUS, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.OTU_LIST));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_OTUS, TAG_OTU, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				
				streamDataProvider.getOTUIDToLabelMap().put(info.id, info.label);
				streamDataProvider.getCurrentEventCollection().add(new LabeledIDEvent(EventContentType.OTU, info.id, info.label));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_OTUS, TAG_OTU, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.OTU));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_OTUS, TAG_SET, XMLStreamConstants.START_ELEMENT), 
				new AbstractIDLinkSetElementReader(EventContentType.OTU_SET, EventContentType.OTU_LIST, ATTR_OTU_SET_LINKED_IDS) {
			
			@Override
			protected EventContentType determineObjectType(QName attributeName) {
				return EventContentType.OTU;
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_OTUS, TAG_SET, XMLStreamConstants.END_ELEMENT), new NeXMLSetEndElementReader(EventContentType.OTU_SET));
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_CHARACTERS, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {		
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				OTUorOTUsEventInformation info = getOTUorOTUsEventInformation(streamDataProvider, element);
				
				String tokenSetType = XMLUtils.readStringAttr(element, ATTR_XSI_TYPE, null);
				if (tokenSetType != null) {
					tokenSetType = parseQName(tokenSetType, element).getLocalPart();
				}
				else {
					throw new JPhyloIOReaderException("Character tag must have an attribute called \"" + ATTR_XSI_TYPE + "\".", element.getLocation());
				}				

				CharacterStateSetType setType = null;
				
				if (tokenSetType.equals(TYPE_DNA_SEQS) || tokenSetType.equals(TYPE_DNA_CELLS)) {
					setType = CharacterStateSetType.DNA;  // Standard IUPAC nucleotide symbols
					streamDataProvider.setAllowLongTokens(false);
				}
				else if (tokenSetType.equals(TYPE_RNA_SEQS) || tokenSetType.equals(TYPE_RNA_CELLS)) {
					setType = CharacterStateSetType.RNA;  // Standard IUPAC nucleotide symbols
					streamDataProvider.setAllowLongTokens(false);
				}
				else if (tokenSetType.equals(TYPE_PROTEIN_SEQS) || tokenSetType.equals(TYPE_PROTEIN_CELLS)) {
					setType = CharacterStateSetType.AMINO_ACID;  // Standard IUPAC amino acid symbols
					streamDataProvider.setAllowLongTokens(false);
				}
				else if (tokenSetType.equals(TYPE_CONTIN_SEQ) || tokenSetType.equals(TYPE_CONTIN_CELLS)) {
					setType = CharacterStateSetType.CONTINUOUS; 
					streamDataProvider.setAllowLongTokens(true);
				}
				else if (tokenSetType.equals(TYPE_RESTRICTION_SEQS) || tokenSetType.equals(TYPE_RESTRICTION_CELLS)) {
					setType = CharacterStateSetType.DISCRETE; 
					streamDataProvider.setAllowLongTokens(false);
				}
				else {  // Type of character block is StandardSeqs or StandardCells
					setType = CharacterStateSetType.DISCRETE;
					streamDataProvider.setAllowLongTokens(true);
				}
				streamDataProvider.setCharacterSetType(setType);
				
				
				streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.ALIGNMENT, info.id, info.label, info.otuOrOtusID));
				streamDataProvider.getElementTypeToCurrentIDMap().put(EventContentType.ALIGNMENT, info.id);
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_CHARACTERS, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {		
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.ALIGNMENT));
				streamDataProvider.getTokenSets().clear();
				streamDataProvider.getCharIDs().clear();
				streamDataProvider.getCharIDToIndexMap().clear();
				streamDataProvider.getTokenDefinitionIDToSymbolMap().clear();
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_CHARACTERS, TAG_FORMAT, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {		
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				// Fire resource end event if format meta events are present
				if (streamDataProvider.getAdditionalResourceMetaRel() != null) {
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
					streamDataProvider.setAdditionalResourceMetaRel(null);
				}
				
				// Token set definitions
				if (streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.CONTINUOUS)) { // Continuous data character tags usually do not specify a token set so a new is generated here
					streamDataProvider.getCurrentEventCollection().add(new TokenSetDefinitionEvent(
							CharacterStateSetType.CONTINUOUS, RESERVED_ID_PREFIX + DEFAULT_TOKEN_DEFINITION_SET_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null));
					streamDataProvider.getCurrentEventCollection().add(new CharacterSetIntervalEvent(0, streamDataProvider.getCharIDs().size()));					
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.TOKEN_SET_DEFINITION));
				}
				else {
					Iterator<String> tokenSetIDIterator = streamDataProvider.getTokenSets().keySet().iterator();
					while (tokenSetIDIterator.hasNext()) {
						String tokenSetID = tokenSetIDIterator.next();
						NeXMLReaderTokenSetInformation info = streamDataProvider.getTokenSets().get(tokenSetID);
						String[] columnIDs; 
						
						if (!info.getNestedEvents().isEmpty()) {						
							streamDataProvider.getCurrentEventCollection().add(new TokenSetDefinitionEvent(info.getSetType(), info.getID(), info.getLabel()));						
							
							for (JPhyloIOEvent nestedEvent : info.getNestedEvents()) {
								streamDataProvider.getCurrentEventCollection().add(nestedEvent);
							}
							
							columnIDs = streamDataProvider.getTokenSetIDtoColumnsMap().get(tokenSetID).toArray(new String[streamDataProvider.getTokenSetIDtoColumnsMap().get(tokenSetID).size()]);
							createIntervalEvents(streamDataProvider, columnIDs);
							
							streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.TOKEN_SET_DEFINITION));
						}
					}
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_STATES, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				// Fire resource end event if format meta events are present
				if (streamDataProvider.getAdditionalResourceMetaRel() != null) {
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
					streamDataProvider.setAdditionalResourceMetaRel(null);
				}				
				
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				
				if (info.label == null) {
					info.label = streamDataProvider.getCharacterSetType().toString();
				}

				streamDataProvider.getElementTypeToCurrentIDMap().put(EventContentType.TOKEN_SET_DEFINITION, info.id);
				streamDataProvider.getTokenSetIDtoColumnsMap().put(info.id, new ArrayList<String>());
				streamDataProvider.getTokenSets().put(info.id, new NeXMLReaderTokenSetInformation(info.id, info.label, streamDataProvider.getCharacterSetType()));
				streamDataProvider.setCurrentEventCollection(streamDataProvider.getTokenSets().get(info.id).getNestedEvents());
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_STATES, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.resetCurrentEventCollection();
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_STATE, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {		
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				String symbol = XMLUtils.readStringAttr(element, ATTR_SYMBOL, null);
				String translation = symbol;
				
				if (streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.DISCRETE)) {					
					if (!streamDataProvider.getEventReader().getTranslateTokens().equals(TokenTranslationStrategy.NEVER)) {
		   			if (streamDataProvider.getEventReader().getTranslateTokens().equals(TokenTranslationStrategy.SYMBOL_TO_LABEL) 
		   					&& (info.label != null)) {
		   				translation = info.label;
		   			}
		   			else {  // SYMBOL_TO_ID or label was null
		   				translation = info.id;
		   			}
		   		}
					
					try {
						streamDataProvider.getTokenSets().get(streamDataProvider.getElementTypeToCurrentIDMap().get(
								EventContentType.TOKEN_SET_DEFINITION)).getSymbolTranslationMap().put(Integer.parseInt(symbol), translation);
						//TODO '-' and '?' must also be accepted. getSymbolTranslationMap() should probably not use integers as keys anymore. (Would parsing numbers be necessary at all then?)
					}
					catch (NumberFormatException e) {
						throw new JPhyloIOReaderException("The symbol (\"" + symbol + 
								"\") of a standard data token definition must be of type Integer.", event.getLocation());
					}	  			
				}
				
	  		if (symbol != null) {	  			
	  			streamDataProvider.getTokenDefinitionIDToSymbolMap().put(info.id, symbol);
	  			streamDataProvider.getCurrentEventCollection().add(new SingleTokenDefinitionEvent(info.id, info.label, symbol, parseStateMeaning(symbol), CharacterSymbolType.ATOMIC_STATE));
	  		}
	  		else {
	  			throw new JPhyloIOReaderException("State tag must have an attribute called \"" + ATTR_SYMBOL + "\".", element.getLocation());
	  		}
			}
		});		
		
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_STATE, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_TOKEN_DEFINITION));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_UNCERTAIN, XMLStreamConstants.START_ELEMENT), readStateSetStart);
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_UNCERTAIN, XMLStreamConstants.END_ELEMENT), readStateSetEnd);
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_POLYMORPHIC, XMLStreamConstants.START_ELEMENT), readStateSetStart);
		putElementReader(new XMLElementReaderKey(TAG_STATES, TAG_POLYMORPHIC, XMLStreamConstants.END_ELEMENT), readStateSetEnd);
		
		putElementReader(new XMLElementReaderKey(TAG_UNCERTAIN, TAG_MEMBER, XMLStreamConstants.START_ELEMENT), readMemberStart);
		putElementReader(new XMLElementReaderKey(TAG_POLYMORPHIC, TAG_MEMBER, XMLStreamConstants.START_ELEMENT), readMemberStart);

		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_CHAR, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				String states =	XMLUtils.readStringAttr(element, ATTR_STATES, null);
				
				streamDataProvider.getCharIDToIndexMap().put(info.id, streamDataProvider.getCharIDs().size());
				streamDataProvider.getCharIDs().add(info.id);
				streamDataProvider.getCharIDToStatesMap().put(info.id, states);
				
				if (!streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.CONTINUOUS)) {
					if (streamDataProvider.getTokenSetIDtoColumnsMap().get(states) != null) {
						streamDataProvider.getTokenSetIDtoColumnsMap().get(states).add(info.id);
					}
					else {
						throw new JPhyloIOReaderException("A character referenced the ID \"" + states + "\" of a token set that was not specified before.", element.getLocation()); 
					}
				}
				
				streamDataProvider.getCurrentEventCollection().add(new CharacterDefinitionEvent(info.id, info.label, streamDataProvider.getCharIDs().size() - 1));

				readAttributes(streamDataProvider, element, RESERVED_ID_PREFIX, 
						new AttributeInfo(ATTR_TOKENS, PREDICATE_CHAR_ATTR_TOKENS, W3CXSConstants.DATA_TYPE_POSITIVE_INTEGER), 
						new AttributeInfo(ATTR_CODON_POSITION, PREDICATE_CHAR_ATTR_CODON_POSITION, W3CXSConstants.DATA_TYPE_NON_NEGATIVE_INTEGER));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_CHAR, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.CHARACTER_DEFINITION));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_SET, XMLStreamConstants.START_ELEMENT), 
				new AbstractSetElementReader(EventContentType.CHARACTER_SET, EventContentType.ALIGNMENT, ATTR_CHAR_SET_LINKED_IDS) {			
			@Override
			protected void processIDs(NeXMLReaderStreamDataProvider streamDataProvider,	String[] linkedIDs, QName attribute) throws JPhyloIOReaderException, XMLStreamException {
				createIntervalEvents(streamDataProvider, linkedIDs);
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_FORMAT, TAG_SET, XMLStreamConstants.END_ELEMENT), new NeXMLSetEndElementReader(EventContentType.CHARACTER_SET));
		
		putElementReader(new XMLElementReaderKey(TAG_MATRIX, TAG_ROW, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				// Fire resource end event if matrix meta events are present
				if (streamDataProvider.getAdditionalResourceMetaRel() != null) {
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
					streamDataProvider.setAdditionalResourceMetaRel(null);
				}
				
				StartElement element = event.asStartElement();
				OTUorOTUsEventInformation otuInfo = getOTUorOTUsEventInformation(streamDataProvider, element);
				
	  		streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.SEQUENCE, otuInfo.id, otuInfo.label, otuInfo.otuOrOtusID));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_MATRIX, TAG_ROW, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(new PartEndEvent(EventContentType.SEQUENCE, true));
				
				// Related to handling cell tags that were not in the order of the columns:
				if (!streamDataProvider.getCurrentCellsBuffer().isEmpty()) {
					// All waiting events should have been consumed in the last call of the cell tag element reader.
					throw new JPhyloIOReaderException(streamDataProvider.getCurrentCellsBuffer().size() + 
							" cell tag(s) referencing an undeclared column ID was/were found.", event.getLocation());
				}
				
				streamDataProvider.clearCurrentRowInformation();
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROW, TAG_CELL, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();				
				String label = XMLUtils.readStringAttr(element, ATTR_LABEL, null);
				String tokenState = XMLUtils.readStringAttr(element, ATTR_SINGLE_STATE_LINK, null);
				String columnID = XMLUtils.readStringAttr(element, ATTR_SINGLE_CHAR_LINK, null);
				String token = tokenState;
				
				if (!streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.CONTINUOUS)) {
					if (streamDataProvider.getTokenDefinitionIDToSymbolMap().containsKey(tokenState)) {
						token = streamDataProvider.getTokenDefinitionIDToSymbolMap().get(tokenState);
					}
					else {
						throw new JPhyloIOReaderException("A cell referenced the ID \"" + tokenState + "\" of a token definition that was not specified before.", event.getLocation());
					}
				}

				if (streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.DISCRETE) 
						&& !streamDataProvider.getEventReader().getTranslateTokens().equals(TokenTranslationStrategy.NEVER)) {

					String translatedToken = null;
					String currentStates = streamDataProvider.getCharIDToStatesMap().get(columnID);
					
					if (!token.equals("" + SequenceUtils.GAP_CHAR) && !token.equals("" + SequenceUtils.MISSING_DATA_CHAR)) {
			 			try {
			 				int standardToken = Integer.parseInt(token);
			 				translatedToken = streamDataProvider.getTokenSets().get(currentStates).getSymbolTranslationMap().get(standardToken);
			 			}
			 			catch (NumberFormatException e) {
			 				throw new JPhyloIOReaderException("The symbol \"" + token + 
			 						"\" of a standard data token definition must be of type Integer.", streamDataProvider.getXMLReader().peek().getLocation());
			 			}
					}
					
		 	 		if (translatedToken != null) {
		 	 			token = translatedToken;
		 	 		}
		 	 		
		 		}
				
				SingleSequenceTokenEvent currentTokenEvent = new SingleSequenceTokenEvent(label, token);
				
				// Handle cell tags that are not in the order of the columns:
				String expectedID = streamDataProvider.getCurrentExpectedCharID();
				if (expectedID == null) {
					throw new JPhyloIOReaderException("A row contained more cell tags than previously declared columns.", event.getLocation());
				}
				else if (expectedID.equals(columnID)) {  // Fire the current event:
					streamDataProvider.getCurrentEventCollection().add(currentTokenEvent);
					expectedID = streamDataProvider.nextCharID();  // Move iterator forward for next call of this method.
					streamDataProvider.setCurrentCellBuffered(false);
				}
				else {  // Buffer current events for later use:
					BufferedEventInfo<SingleSequenceTokenEvent> info = new BufferedEventInfo<SingleSequenceTokenEvent>(currentTokenEvent);
					streamDataProvider.getCurrentCellsBuffer().put(columnID, info);
					streamDataProvider.setCurrentEventCollection(info.getNestedEvents());
					streamDataProvider.setCurrentCellBuffered(true);
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROW, TAG_CELL, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				if (streamDataProvider.isCurrentCellBuffered()) {
					streamDataProvider.resetCurrentEventCollection();  // Remove current buffer list.
				}
				else {
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_SEQUENCE_TOKEN));
				}
				
				// Fire all waiting events from the buffer that fit to the current position:
				BufferedEventInfo<SingleSequenceTokenEvent> info = streamDataProvider.getCurrentCellsBuffer().get(streamDataProvider.getCurrentExpectedCharID());
				while (info != null) {
					streamDataProvider.getCurrentEventCollection().add(info.getStartEvent());
					streamDataProvider.getCurrentEventCollection().addAll(info.getNestedEvents());
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_SEQUENCE_TOKEN));
					streamDataProvider.getCurrentCellsBuffer().remove(streamDataProvider.getCurrentExpectedCharID());
					
					info = streamDataProvider.getCurrentCellsBuffer().get(streamDataProvider.nextCharID());  // Move iterator forward for next iteration or next call of this method.
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_SEQ, null, XMLStreamConstants.CHARACTERS), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				String tokens = event.asCharacters().getData();
		   	streamDataProvider.getCurrentEventCollection().add(new SequenceTokensEvent(readSequence(streamDataProvider, tokens, streamDataProvider.getEventReader().getTranslateTokens())));				
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_MATRIX, TAG_SET, XMLStreamConstants.START_ELEMENT), 
				new AbstractIDLinkSetElementReader(EventContentType.SEQUENCE_SET, EventContentType.ALIGNMENT, ATTR_SEQUENCE_SET_LINKED_IDS) {
			
			@Override
			protected EventContentType determineObjectType(QName attributeName) {
				return EventContentType.SEQUENCE;
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_MATRIX, TAG_SET, XMLStreamConstants.END_ELEMENT), new NeXMLSetEndElementReader(EventContentType.SEQUENCE_SET));
		
		putElementReader(new XMLElementReaderKey(TAG_CHARACTERS, TAG_MATRIX, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException,
				XMLStreamException {
					// Fire resource end event if matrix meta events are present and were not fired yet
					if (streamDataProvider.getAdditionalResourceMetaRel() != null) {
						streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.RESOURCE_META));
						streamDataProvider.setAdditionalResourceMetaRel(null);
					}				
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_TREES, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				OTUorOTUsEventInformation info = getOTUorOTUsEventInformation(streamDataProvider, element);				
				
				streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.TREE_NETWORK_GROUP, info.id, info.label, info.otuOrOtusID));
				streamDataProvider.getElementTypeToCurrentIDMap().put(EventContentType.TREE_NETWORK_GROUP, info.id);
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_TREES, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.TREE_NETWORK_GROUP));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_TREE, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();				
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				String treeType = XMLUtils.readStringAttr(element, ATTR_XSI_TYPE, null);
				
				streamDataProvider.getCurrentEventCollection().add(new LabeledIDEvent(EventContentType.TREE, info.id,	info.label));
				streamDataProvider.getElementTypeToCurrentIDMap().put(EventContentType.TREE, info.id);
				
				if (treeType == null) {
					throw new JPhyloIOReaderException("Tree tag must have an attribute called \"" + ATTR_XSI_TYPE + "\".", element.getLocation());
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_TREE, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.TREE));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_NETWORK, XMLStreamConstants.START_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				LabeledIDEventInformation info = getLabeledIDEventInformation(streamDataProvider, element);
				
	  		streamDataProvider.getCurrentEventCollection().add(new LabeledIDEvent(EventContentType.NETWORK, info.id, info.label));
	  		streamDataProvider.getElementTypeToCurrentIDMap().put(EventContentType.NETWORK, info.id);
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_NETWORK, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.NETWORK));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_NODE, XMLStreamConstants.START_ELEMENT), readNodeStart);
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_NODE, XMLStreamConstants.END_ELEMENT), readNodeEnd);
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_NODE, XMLStreamConstants.START_ELEMENT), readNodeStart);
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_NODE, XMLStreamConstants.END_ELEMENT), readNodeEnd);

		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_EDGE, XMLStreamConstants.START_ELEMENT), readEdgeStart);
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_EDGE, XMLStreamConstants.END_ELEMENT), readEdgeEnd);
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_ROOTEDGE, XMLStreamConstants.START_ELEMENT), readEdgeStart);
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_ROOTEDGE, XMLStreamConstants.END_ELEMENT), new AbstractNeXMLElementReader() {			
			@Override
			public void readEvent(NeXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.ROOT_EDGE));
			}
		});
				
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_EDGE, XMLStreamConstants.START_ELEMENT), readEdgeStart);
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_EDGE, XMLStreamConstants.END_ELEMENT), readEdgeEnd);
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_SET, XMLStreamConstants.START_ELEMENT), 
				new AbstractIDLinkSetElementReader(EventContentType.NODE_EDGE_SET, EventContentType.TREE, 
						ATTR_NODE_EDGE_SET_LINKED_NODE_IDS, ATTR_NODE_EDGE_SET_LINKED_ROOTEDGE_IDS, ATTR_NODE_EDGE_SET_LINKED_EDGE_IDS) {			
				@Override
				protected EventContentType determineObjectType(QName attributeName) {
					if (attributeName.equals(ATTR_NODE_EDGE_SET_LINKED_NODE_IDS)) {
						return EventContentType.NODE;
					}
					else if (attributeName.equals(ATTR_NODE_EDGE_SET_LINKED_EDGE_IDS)) {
						return EventContentType.EDGE;
					}
					else if (attributeName.equals(ATTR_NODE_EDGE_SET_LINKED_ROOTEDGE_IDS)) {
						return EventContentType.ROOT_EDGE;
					}
					else {
						throw new IllegalArgumentException("No content type for the attribute name \"" + attributeName.getLocalPart() + "\" available.");
					}
				}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_SET, XMLStreamConstants.END_ELEMENT), nodeAndEdgeSetEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_SET, XMLStreamConstants.START_ELEMENT), 
				new AbstractIDLinkSetElementReader(EventContentType.NODE_EDGE_SET, EventContentType.NETWORK, 
						ATTR_NODE_EDGE_SET_LINKED_NODE_IDS, ATTR_NODE_EDGE_SET_LINKED_EDGE_IDS) {			
				@Override
				protected EventContentType determineObjectType(QName attributeName) {
					if (attributeName.equals(ATTR_NODE_EDGE_SET_LINKED_NODE_IDS)) {
						return EventContentType.NODE;
					}
					else if (attributeName.equals(ATTR_NODE_EDGE_SET_LINKED_EDGE_IDS)) {
						return EventContentType.EDGE;
					}
					else {
						throw new IllegalArgumentException("No content type for the attribute name \"" + attributeName.getLocalPart() + "\" available.");
					}
				}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_NETWORK, TAG_SET, XMLStreamConstants.END_ELEMENT), nodeAndEdgeSetEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_SET, XMLStreamConstants.START_ELEMENT), 
				new AbstractIDLinkSetElementReader(EventContentType.TREE_NETWORK_SET, EventContentType.TREE_NETWORK_GROUP, 
						ATTR_TREE_SET_LINKED_TREE_IDS, ATTR_TREE_SET_LINKED_NETWORK_IDS) {
			
			@Override
			protected EventContentType determineObjectType(QName attributeName) {
				if (attributeName.equals(ATTR_TREE_SET_LINKED_TREE_IDS)) {
					return EventContentType.TREE;
				}
				else if (attributeName.equals(ATTR_TREE_SET_LINKED_NETWORK_IDS)) {
					return EventContentType.NETWORK;
				}
				else {
					throw new IllegalArgumentException("No content type for the attribute name \"" + attributeName.getLocalPart() + "\" available.");
				}
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREES, TAG_SET, XMLStreamConstants.END_ELEMENT), new NeXMLSetEndElementReader(EventContentType.TREE_NETWORK_SET));
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.COMMENT), new CommentElementReader<NeXMLReaderStreamDataProvider>());
	}


	@Override
	protected XMLElementReader<NeXMLReaderStreamDataProvider> getElementReader(QName parentTag, QName elementTag, int eventType) {
		if (!getStreamDataProvider().getMetaType().isEmpty() && getStreamDataProvider().getMetaType().peek().equals(EventContentType.LITERAL_META) 
				&& (currentMetaLiteralStartLevel == -1) && getStreamDataProvider().getCurrentLiteralContentSequenceType().equals(LiteralContentSequenceType.XML)) {
			currentMetaLiteralStartLevel = getEncounteredTags().size();
		}
		
		if ((currentMetaLiteralStartLevel != -1) && (currentMetaLiteralStartLevel <= getEncounteredTags().size())) {  // Read custom XML.
			return super.getElementReader(null, null, eventType);  // Always returns a custom XML element reader if unknown tags are found nested under a literal meta event
		}
		else {
			currentMetaLiteralStartLevel = -1;
			return super.getElementReader(parentTag, elementTag, eventType);
		}
	}
}