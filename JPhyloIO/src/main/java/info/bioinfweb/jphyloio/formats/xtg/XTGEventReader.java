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
package info.bioinfweb.jphyloio.formats.xtg;


import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.NodeEdgeInfo;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLEventReader;
import info.bioinfweb.jphyloio.formats.xml.AttributeInfo;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.AbstractXMLElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.CommentElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.StartDocumentElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLElementReaderKey;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLEndElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLNoCharactersAllowedElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLStartElementReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Event reader for the <a href="http://bioinfweb.info/xmlns/xtg">extensible TreeGraph 2 format (XTG)</a> used by the 
 * phylogenetic tree editor <a href="http://treegraph.bioinfweb.info/">TreeGraph 2</a>.
 * <p>
 * This reader supports reading pyhlogenetic trees and nested annotations. Reading scale bar and legend information 
 * is not supported. In order to serialize the hierarchical structure of {@code node} and {@code branch} tags, 
 * according events are fired at the end of a node or the start of a new node, depending on what happens first. 
 * This avoids buffering large amounts of data longer than necessary. 
 * <p>
 * Predefined elements as well as attributes are represented by metaevents. If necessary these events are grouped by 
 * {@link ResourceMetadataEvent}s. Textual or decimal annotation values (according to the {@code is_decimal} attribute} 
 * are parsed to the according Java objects.
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_ALLOW_DEFAULT_NAMESPACE}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 * </ul>
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class XTGEventReader extends AbstractXMLEventReader<XMLReaderStreamDataProvider<XTGEventReader>> implements XTGConstants {	
	public XTGEventReader(File file, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		super(file, parameters);
	}


	public XTGEventReader(InputStream stream, ReadWriteParameterMap parameters)	throws IOException, XMLStreamException {
		super(stream, parameters);
	}


	public XTGEventReader(Reader reader, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		super(reader, parameters);
	}


	public XTGEventReader(XMLEventReader xmlReader,	ReadWriteParameterMap parameters) {
		super(xmlReader, parameters);
	}


	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.XTG_FORMAT_ID;
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void fillMap() {
		AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>> nodeStartReader = new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();				
				String id = DEFAULT_NODE_ID_PREFIX + streamDataProvider.getIDManager().createNewID();
				String label = XMLUtils.readStringAttr(element, ATTR_TEXT, null);
				
				createNodeEvents(streamDataProvider);  // Create node events for previous node to avoid buffering large amounts of meta data
				
				// Add node info for this node
				NodeEdgeInfo nodeInfo = new NodeEdgeInfo(id, Double.NaN, new ArrayList<JPhyloIOEvent>(), new ArrayList<JPhyloIOEvent>());				
				if ((label != null) && !label.isEmpty()) {
					nodeInfo.setLabel(label);
				}				
				if (streamDataProvider.getParentName().equals(TAG_TREE.getLocalPart()) && streamDataProvider.isRootedPhylogeny()) {
					nodeInfo.setIsRoot(true);
				}				
				streamDataProvider.getSourceNode().add(nodeInfo);
				streamDataProvider.setCreateNodeStart(true);
				
				// Add edge info for this level
				streamDataProvider.getEdgeInfos().add(new ArrayDeque<NodeEdgeInfo>());
				
				streamDataProvider.setCurrentEventCollection(streamDataProvider.getSourceNode().peek().getNestedNodeEvents());
				
				readAttributes(getStreamDataProvider(), event.asStartElement(), "",	
						new AttributeInfo(ATTR_TEXT_IS_DECIMAL, PREDICATE_IS_DECIMAL, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_TEXT_COLOR, PREDICATE_TEXT_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_TEXT_HEIGHT, PREDICATE_TEXT_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_TEXT_STYLE, PREDICATE_TEXT_STYLE, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_FONT_FAMILY, PREDICATE_FONT_FAMILY, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_DECIMAL_FORMAT, PREDICATE_DECIMAL_FORMAT, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_LANG, PREDICATE_LOCALE_LANG, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_COUNTRY, PREDICATE_LOCALE_COUNTRY, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_VARIANT, PREDICATE_LOCALE_VARIANT, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LINE_COLOR, PREDICATE_LINE_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_LINE_WIDTH, PREDICATE_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_UNIQUE_NAME, PREDICATE_NODE_UNIQUE_NAME, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_EDGE_RADIUS, PREDICATE_EDGE_RADIUS, W3CXSConstants.DATA_TYPE_FLOAT));
			}
		};
		
		AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>> nodeEndReader = new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				createNodeEvents(streamDataProvider);
				streamDataProvider.setCreateNodeStart(false);
				
				createEdgeEvents(streamDataProvider);
				
				streamDataProvider.getSourceNode().pop();
			}
		};
		
		XMLEndElementReader resourceEndReader = new XMLEndElementReader(false, true, false);
		
		XMLEndElementReader edgeResourceEndReader = new XMLEndElementReader(false, true, true);
		
		XMLStartElementReader labelMarginStartReader = new XMLStartElementReader(null, PREDICATE_LABEL_MARGIN, null, true, 
				new AttributeInfo(ATTR_LEFT, PREDICATE_MARGIN_LEFT, W3CXSConstants.DATA_TYPE_FLOAT),
				new AttributeInfo(ATTR_TOP, PREDICATE_MARGIN_TOP, W3CXSConstants.DATA_TYPE_FLOAT),
				new AttributeInfo(ATTR_RIGHT, PREDICATE_MARGIN_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
				new AttributeInfo(ATTR_BOTTOM, PREDICATE_MARGIN_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT));

		putElementReader(new XMLElementReaderKey(TAG_LABEL_MARGIN, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>> emptyReader = new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {}
		};
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.START_DOCUMENT), new StartDocumentElementReader<XMLReaderStreamDataProvider<XTGEventReader>>());
		
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.END_DOCUMENT), new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.DOCUMENT));
			}
		});
		
		putElementReader(new XMLElementReaderKey(null, TAG_ROOT, XMLStreamConstants.START_ELEMENT), emptyReader);
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(null, TAG_ROOT, XMLStreamConstants.END_ELEMENT), emptyReader);
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_TREE, XMLStreamConstants.START_ELEMENT), new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getEdgeInfos().add(new ArrayDeque<NodeEdgeInfo>());
				streamDataProvider.setCreateNodeStart(false);  // Prevents creation of node events at start of root node
				
				streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.TREE_NETWORK_GROUP, 
						DEFAULT_TREE_NETWORK_GROUP_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null, null));  // Since there can only be one tree in an XTG document, the tree group start event can be fired here
				
				streamDataProvider.getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.TREE, 
						DEFAULT_TREE_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null, null));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_TREE, XMLStreamConstants.END_ELEMENT), new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				createEdgeEvents(streamDataProvider);
				
				streamDataProvider.getSourceNode().clear();
				streamDataProvider.getEdgeInfos().clear();
				
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.TREE));
				
				streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.TREE_NETWORK_GROUP));  // Since there can only be one tree in an XTG document, the tree group end event can be fired here
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_NODE, XMLStreamConstants.START_ELEMENT), nodeStartReader);
		
		putElementReader(new XMLElementReaderKey(TAG_NODE, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_NODE, XMLStreamConstants.END_ELEMENT), nodeEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_NODE, XMLStreamConstants.START_ELEMENT), nodeStartReader);
		
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_NODE, XMLStreamConstants.END_ELEMENT), nodeEndReader);

		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_BRANCH, XMLStreamConstants.START_ELEMENT), new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				StartElement element = event.asStartElement();
				double branchLength = XMLUtils.readDoubleAttr(element, ATTR_BRANCH_LENGTH, Double.NaN);
				
				streamDataProvider.getSourceNode().peek().setLength(branchLength);
				streamDataProvider.setCurrentEventCollection(streamDataProvider.getSourceNode().peek().getNestedEdgeEvents());
				
				readAttributes(getStreamDataProvider(), event.asStartElement(), "", 
						new AttributeInfo(ATTR_LINE_COLOR, PREDICATE_LINE_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_LINE_WIDTH, PREDICATE_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_CONSTANT_WIDTH, PREDICATE_BRANCH_CONSTANT_WIDTH, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_MIN_BRANCH_LENGTH, PREDICATE_BRANCH_MIN_LENGTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_MIN_SPACE_ABOVE, PREDICATE_BRANCH_MIN_SPACE_ABOVE, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_MIN_SPACE_BELOW, PREDICATE_BRANCH_MIN_SPACE_BELOW, W3CXSConstants.DATA_TYPE_FLOAT));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_BRANCH, XMLStreamConstants.END_ELEMENT), new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {				
				if (streamDataProvider.hasSpecialEventCollection()) {
					streamDataProvider.resetCurrentEventCollection();
				}
			}
		});
		
		// TreegraphDocument.GlobalFormats
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_GLOBAL_FORMATS, XMLStreamConstants.START_ELEMENT), 
				new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
				@Override
				public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {				
					StartElement element = event.asStartElement();
					
					streamDataProvider.setRootedPhylogeny(XMLUtils.readBooleanAttr(element, ATTR_SHOW_ROOTED, false));
					
					streamDataProvider.getCurrentEventCollection().add(
							new ResourceMetadataEvent(ReadWriteConstants.DEFAULT_META_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null, 
									new URIOrStringIdentifier(null, PREDICATE_GLOBAL_FORMATS), null, null));

					readAttributes(streamDataProvider, element, null, 
							new AttributeInfo(ATTR_BG_COLOR, PREDICATE_GLOBAL_FORMATS_BG_COLOR, DATA_TYPE_COLOR),
							new AttributeInfo(ATTR_BRANCH_LENGTH_SCALE, PREDICATE_GLOBAL_FORMATS_BRANCH_LENGTH_SCALE, W3CXSConstants.DATA_TYPE_DOUBLE),
							new AttributeInfo(ATTR_SHOW_SCALE_BAR, PREDICATE_GLOBAL_FORMATS_SHOW_SCALE_BAR, W3CXSConstants.DATA_TYPE_BOOLEAN),
							new AttributeInfo(ATTR_ALIGN_TO_SUBTREE, PREDICATE_GLOBAL_FORMATS_ALIGN_TO_SUBTREE, W3CXSConstants.DATA_TYPE_BOOLEAN),
							new AttributeInfo(ATTR_POSITION_LABELS_TO_LEFT, PREDICATE_GLOBAL_FORMATS_POSITION_LABELS_TO_LEFT, W3CXSConstants.DATA_TYPE_BOOLEAN));
				}
			});
		
		putElementReader(new XMLElementReaderKey(TAG_GLOBAL_FORMATS, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_GLOBAL_FORMATS, TAG_DOCUMENT_MARGIN, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_DOCUMENT_MARGIN, null, false, 
						new AttributeInfo(ATTR_LEFT, PREDICATE_MARGIN_LEFT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_TOP, PREDICATE_MARGIN_TOP, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_RIGHT, PREDICATE_MARGIN_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_BOTTOM, PREDICATE_MARGIN_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT)));	
		putElementReader(new XMLElementReaderKey(TAG_DOCUMENT_MARGIN, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());		
		putElementReader(new XMLElementReaderKey(TAG_GLOBAL_FORMATS, TAG_DOCUMENT_MARGIN, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_GLOBAL_FORMATS, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		// TreegraphDocument.NodeBranchDataAdapters
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_NODE_BRANCH_DATA_ADAPTERS, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_NODE_BRANCH_DATA_ADAPTERS, null,	false));		
		putElementReader(new XMLElementReaderKey(TAG_NODE_BRANCH_DATA_ADAPTERS, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_NODE_BRANCH_DATA_ADAPTERS, TAG_ADAPTER, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER, null, false, 
						new AttributeInfo(ATTR_ADAPTER_NAME, PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_NAME, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_ADAPTER_ID, PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_ID, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_ADAPTER_PURPOSE, PREDICATE_NODE_BRANCH_DATA_ADAPTERS_ADAPTER_PURPOSE, W3CXSConstants.DATA_TYPE_STRING)));
		
		putElementReader(new XMLElementReaderKey(TAG_ADAPTER, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());		
		putElementReader(new XMLElementReaderKey(TAG_NODE_BRANCH_DATA_ADAPTERS, TAG_ADAPTER, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_ROOT, TAG_NODE_BRANCH_DATA_ADAPTERS, XMLStreamConstants.END_ELEMENT), resourceEndReader);
	
//		// Tree.ScaleBar
//		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_SCALE_BAR, XMLStreamConstants.START_ELEMENT), 
//				new XMLStartElementReader(null, PREDICATE_SCALE_BAR, null,	false, ATTR_TEXT, PREDICATE_SCALE_BAR_ATTR_TEXT, 
//						ATTR_TEXT_IS_DECIMAL, PREDICATE_SCALE_BAR_ATTR_IS_DECIMAL, ATTR_TEXT_COLOR, PREDICATE_SCALE_BAR_ATTR_TEXT_COLOR, 
//						ATTR_TEXT_HEIGHT, PREDICATE_SCALE_BAR_ATTR_TEXT_HEIGHT, ATTR_TEXT_STYLE, PREDICATE_SCALE_BAR_ATTR_TEXT_STYLE, 
//						ATTR_FONT_FAMILY, PREDICATE_SCALE_BAR_ATTR_FONT_FAMILY, ATTR_DECIMAL_FORMAT, PREDICATE_SCALE_BAR_ATTR_DECIMAL_FORMAT, 
//						ATTR_LOCALE_LANG, PREDICATE_SCALE_BAR_ATTR_LOCALE_LANG, ATTR_LOCALE_COUNTRY, PREDICATE_SCALE_BAR_ATTR_LOCALE_COUNTRY, 
//						ATTR_LOCALE_VARIANT, PREDICATE_SCALE_BAR_ATTR_LOCALE_VARIANT, ATTR_LINE_COLOR, PREDICATE_SCALE_BAR_ATTR_LINE_COLOR, 
//						ATTR_LINE_WIDTH, PREDICATE_SCALE_BAR_ATTR_LINE_WIDTH, ATTR_SCALE_BAR_ALIGN, PREDICATE_SCALE_BAR_ATTR_ALIGN, 
//						ATTR_SCALE_BAR_DISTANCE, PREDICATE_SCALE_BAR_ATTR_TREE_DISTANCE, ATTR_SCALE_BAR_WIDTH, PREDICATE_SCALE_BAR_ATTR_WIDTH, 
//						ATTR_SCALE_BAR_HEIGHT, PREDICATE_SCALE_BAR_ATTR_HEIGHT, ATTR_SMALL_INTERVAL, PREDICATE_SCALE_BAR_ATTR_SMALL_INTERVAL, 
//						ATTR_LONG_INTERVAL, PREDICATE_SCALE_BAR_ATTR_LONG_INTERVAL, ATTR_SCALE_BAR_START, PREDICATE_SCALE_BAR_ATTR_START_LEFT, 
//						ATTR_SCALE_BAR_INCREASE, PREDICATE_SCALE_BAR_ATTR_INCREASING));
//		putElementReader(new XMLElementReaderKey(TAG_SCALE_BAR, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());		
//		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_SCALE_BAR, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		// Node.LeafMargin
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_LEAF_MARGIN, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_LEAF_MARGIN, null, false, 
						new AttributeInfo(ATTR_LEFT, PREDICATE_MARGIN_LEFT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_TOP, PREDICATE_MARGIN_TOP, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_RIGHT, PREDICATE_MARGIN_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_BOTTOM, PREDICATE_MARGIN_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT)));	
		
		putElementReader(new XMLElementReaderKey(TAG_LEAF_MARGIN, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());		
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_LEAF_MARGIN, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		// Branch.TextLabel		
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_TEXT_LABEL, XMLStreamConstants.START_ELEMENT), 
			new XTGFormattedTextElementReader(null, PREDICATE_TEXT_LABEL, null, true, 
					new AttributeInfo(ATTR_TEXT_COLOR, PREDICATE_TEXT_COLOR, DATA_TYPE_COLOR),
					new AttributeInfo(ATTR_TEXT_HEIGHT, PREDICATE_TEXT_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
					new AttributeInfo(ATTR_TEXT_STYLE, PREDICATE_TEXT_STYLE, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_FONT_FAMILY, PREDICATE_FONT_FAMILY, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_DECIMAL_FORMAT, PREDICATE_DECIMAL_FORMAT, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_LOCALE_LANG, PREDICATE_LOCALE_LANG, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_LOCALE_COUNTRY, PREDICATE_LOCALE_COUNTRY, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_LOCALE_VARIANT, PREDICATE_LOCALE_VARIANT, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_ID, PREDICATE_COLUMN_ID, W3CXSConstants.DATA_TYPE_STRING),
					new AttributeInfo(ATTR_LABEL_ABOVE, PREDICATE_LABEL_ABOVE, W3CXSConstants.DATA_TYPE_BOOLEAN),
					new AttributeInfo(ATTR_LINE_NO, PREDICATE_LABEL_LINE_NO, W3CXSConstants.DATA_TYPE_INT),
					new AttributeInfo(ATTR_LINE_POS, PREDICATE_LABEL_LINE_POS, W3CXSConstants.DATA_TYPE_DOUBLE)));
		
		putElementReader(new XMLElementReaderKey(TAG_TEXT_LABEL, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_TEXT_LABEL, TAG_LABEL_MARGIN, XMLStreamConstants.START_ELEMENT), labelMarginStartReader);		
		// Element reader for character content of label margin tag was registered before
		putElementReader(new XMLElementReaderKey(TAG_TEXT_LABEL, TAG_LABEL_MARGIN, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_TEXT_LABEL, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		// Branch.IconLabel
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_ICON_LABEL, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_ICON_LABEL, null,	true, 
						new AttributeInfo(ATTR_LINE_COLOR, PREDICATE_LINE_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_LINE_WIDTH, PREDICATE_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_WIDTH, PREDICATE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_HEIGHT, PREDICATE_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_ICON, PREDICATE_ICON_LABEL_ICON, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_ICON_FILLED, PREDICATE_ICON_LABEL_ICON_FILLED, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_ID, PREDICATE_COLUMN_ID, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LABEL_ABOVE, PREDICATE_LABEL_ABOVE, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_LINE_NO, PREDICATE_LABEL_LINE_NO, W3CXSConstants.DATA_TYPE_INT),
						new AttributeInfo(ATTR_LINE_POS, PREDICATE_LABEL_LINE_POS, W3CXSConstants.DATA_TYPE_DOUBLE)));

		putElementReader(new XMLElementReaderKey(TAG_ICON_LABEL, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_ICON_LABEL, TAG_LABEL_MARGIN, XMLStreamConstants.START_ELEMENT), labelMarginStartReader);
		// Element reader for character content of label margin tag was registered before
		putElementReader(new XMLElementReaderKey(TAG_ICON_LABEL, TAG_LABEL_MARGIN, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_ICON_LABEL, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		// Branch.PieChartLabel
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_PIE_CHART_LABEL, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_PIE_CHART_LABEL, null,	true, 
						new AttributeInfo(ATTR_LINE_COLOR, PREDICATE_LINE_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_LINE_WIDTH, PREDICATE_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_LABEL_WIDTH, PREDICATE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_LABEL_HEIGHT, PREDICATE_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_SHOW_INTERNAL_LINES, PREDICATE_PIE_CHART_LABEL_INTERNAL_LINES, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_SHOW_NULL_LINES, PREDICATE_PIE_CHART_LABEL_NULL_LINES, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_SHOW_TITLE, PREDICATE_PIE_CHART_LABEL_SHOW_TITLE, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_CAPTION_TYPE, PREDICATE_PIE_CHART_LABEL_CAPTION_TYPE, DATA_TYPE_PIE_CHART_LABEL_CAPTION_TYPE),
						new AttributeInfo(ATTR_CAPTION_LINK_TYPE, PREDICATE_PIE_CHART_LABEL_CAPTION_LINK_TYPE, DATA_TYPE_PIE_CHART_LABEL_CAPTION_LINK_TYPE),
						new AttributeInfo(ATTR_ID, PREDICATE_COLUMN_ID, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LABEL_ABOVE, PREDICATE_LABEL_ABOVE, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_LINE_NO, PREDICATE_LABEL_LINE_NO, W3CXSConstants.DATA_TYPE_INT),
						new AttributeInfo(ATTR_LINE_POS, PREDICATE_LABEL_LINE_POS, W3CXSConstants.DATA_TYPE_DOUBLE)));
			
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_LABEL, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_LABEL, TAG_LABEL_MARGIN, XMLStreamConstants.START_ELEMENT), labelMarginStartReader);
		// Element reader for character content of label margin tag was registered before
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_LABEL, TAG_LABEL_MARGIN, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_LABEL, TAG_PIE_CHART_IDS, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_DATA_IDS, null, true));
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_IDS, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_IDS, TAG_PIE_CHART_ID, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(PREDICATE_DATA_ID_VALUE, PREDICATE_DATA_ID, new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_STRING), true, 
						new AttributeInfo(ATTR_PIE_COLOR, PREDICATE_PIE_COLOR, DATA_TYPE_COLOR)));
		
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_ID, null, XMLStreamConstants.CHARACTERS), new AbstractXMLElementReader<XMLReaderStreamDataProvider<XTGEventReader>>() {			
			@Override
			public void readEvent(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
				streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(event.asCharacters().getData(), 
						streamDataProvider.getXMLReader().peek().isCharacters()));
			}
		});
		
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_IDS, TAG_PIE_CHART_ID, XMLStreamConstants.END_ELEMENT), new XMLEndElementReader(true, true, true));
		
		putElementReader(new XMLElementReaderKey(TAG_PIE_CHART_LABEL, TAG_PIE_CHART_IDS, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_PIE_CHART_LABEL, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		// Node.InvisibleData
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_HIDDEN_DATA, XMLStreamConstants.START_ELEMENT), 
				new XTGFormattedTextElementReader(null, PREDICATE_INVISIBLE_DATA, null, false, 
						new AttributeInfo(ATTR_ID, PREDICATE_COLUMN_ID, W3CXSConstants.DATA_TYPE_STRING)));
		
		putElementReader(new XMLElementReaderKey(TAG_HIDDEN_DATA, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_NODE, TAG_HIDDEN_DATA, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		// Branch.InvisibleData
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_HIDDEN_DATA, XMLStreamConstants.START_ELEMENT), 
				new XTGFormattedTextElementReader(null, PREDICATE_INVISIBLE_DATA, null, true, 
						new AttributeInfo(ATTR_ID, PREDICATE_COLUMN_ID, W3CXSConstants.DATA_TYPE_STRING)));
		
		// Element reader for character content of hidden data tag was registered before
		
		putElementReader(new XMLElementReaderKey(TAG_BRANCH, TAG_HIDDEN_DATA, XMLStreamConstants.END_ELEMENT), edgeResourceEndReader);
		
		// Tree.Legend
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_LEGEND, XMLStreamConstants.START_ELEMENT), 
				new XTGFormattedTextElementReader(null, PREDICATE_LEGEND, null, false,
						new AttributeInfo(ATTR_TEXT_COLOR, PREDICATE_TEXT_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_TEXT_HEIGHT, PREDICATE_TEXT_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_TEXT_STYLE, PREDICATE_TEXT_STYLE, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_FONT_FAMILY, PREDICATE_FONT_FAMILY, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_DECIMAL_FORMAT, PREDICATE_DECIMAL_FORMAT, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_LANG, PREDICATE_LOCALE_LANG, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_COUNTRY, PREDICATE_LOCALE_COUNTRY, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_VARIANT, PREDICATE_LOCALE_VARIANT, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LINE_COLOR, PREDICATE_LINE_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_LINE_WIDTH, PREDICATE_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_LEGEND_POS, PREDICATE_LEGEND_POSITION, W3CXSConstants.DATA_TYPE_INT),
						new AttributeInfo(ATTR_MIN_TREE_DISTANCE, PREDICATE_LEGEND_MIN_TREE_DISTANCE, W3CXSConstants.DATA_TYPE_FLOAT), 
						new AttributeInfo(ATTR_LEGEND_SPACING, PREDICATE_LEGEND_SPACING, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_LEGEND_STYLE, PREDICATE_LEGEND_STYLE, DATA_TYPE_LEGEND_STYLE),
						new AttributeInfo(ATTR_TEXT_ORIENTATION, PREDICATE_LEGEND_ORIENTATION, DATA_TYPE_TEXT_ORIENTATION),
						new AttributeInfo(ATTR_EDGE_RADIUS, PREDICATE_EDGE_RADIUS, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_ANCHOR_0, PREDICATE_LEGEND_ANCHOR_0, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_ANCHOR_1, PREDICATE_LEGEND_ANCHOR_1, W3CXSConstants.DATA_TYPE_STRING)));
		putElementReader(new XMLElementReaderKey(TAG_LEGEND, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		
		putElementReader(new XMLElementReaderKey(TAG_LEGEND, TAG_LEGEND_MARGIN, XMLStreamConstants.START_ELEMENT), 
				new XMLStartElementReader(null, PREDICATE_LEGEND_MARGIN, null, false, 
						new AttributeInfo(ATTR_LEFT, PREDICATE_MARGIN_LEFT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_TOP, PREDICATE_MARGIN_TOP, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_RIGHT, PREDICATE_MARGIN_RIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_BOTTOM, PREDICATE_MARGIN_BOTTOM, W3CXSConstants.DATA_TYPE_FLOAT)));	
		putElementReader(new XMLElementReaderKey(TAG_LEGEND_MARGIN, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());		
		putElementReader(new XMLElementReaderKey(TAG_LEGEND, TAG_LEGEND_MARGIN, XMLStreamConstants.END_ELEMENT), resourceEndReader);
		
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_LEGEND, XMLStreamConstants.END_ELEMENT), resourceEndReader);		
		
		// Scale bar
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_SCALE_BAR, XMLStreamConstants.START_ELEMENT), 
				new XTGFormattedTextElementReader(null, PREDICATE_SCALE_BAR, null, false,
						new AttributeInfo(ATTR_TEXT_COLOR, PREDICATE_TEXT_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_TEXT_HEIGHT, PREDICATE_TEXT_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_TEXT_STYLE, PREDICATE_TEXT_STYLE, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_FONT_FAMILY, PREDICATE_FONT_FAMILY, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_DECIMAL_FORMAT, PREDICATE_DECIMAL_FORMAT, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_LANG, PREDICATE_LOCALE_LANG, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_COUNTRY, PREDICATE_LOCALE_COUNTRY, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LOCALE_VARIANT, PREDICATE_LOCALE_VARIANT, W3CXSConstants.DATA_TYPE_STRING),
						new AttributeInfo(ATTR_LINE_COLOR, PREDICATE_LINE_COLOR, DATA_TYPE_COLOR),
						new AttributeInfo(ATTR_LINE_WIDTH, PREDICATE_LINE_WIDTH, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_SCALE_BAR_ALIGN, PREDICATE_SCALE_BAR_ALIGN, DATA_TYPE_SCALE_BAR_ALIGNMENT), 
						new AttributeInfo(ATTR_SCALE_BAR_DISTANCE, PREDICATE_SCALE_BAR_TREE_DISTANCE, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_SCALE_BAR_START, PREDICATE_SCALE_BAR_START_LEFT, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_SCALE_BAR_INCREASE, PREDICATE_SCALE_BAR_INCREASING, W3CXSConstants.DATA_TYPE_BOOLEAN),
						new AttributeInfo(ATTR_SCALE_BAR_WIDTH, PREDICATE_WIDTH, DATA_TYPE_SCALE_VALUE),
						new AttributeInfo(ATTR_SCALE_BAR_HEIGHT, PREDICATE_HEIGHT, W3CXSConstants.DATA_TYPE_FLOAT),
						new AttributeInfo(ATTR_SCALE_BAR_LONG_INTERVAL, PREDICATE_SCALE_BAR_LONG_INTERVAL, W3CXSConstants.DATA_TYPE_INT),
						new AttributeInfo(ATTR_SCALE_BAR_SMALL_INTERVAL, PREDICATE_SCALE_BAR_SMALL_INTERVAL, W3CXSConstants.DATA_TYPE_FLOAT)));
		putElementReader(new XMLElementReaderKey(TAG_LEGEND, null, XMLStreamConstants.CHARACTERS), new XMLNoCharactersAllowedElementReader());
		putElementReader(new XMLElementReaderKey(TAG_TREE, TAG_SCALE_BAR, XMLStreamConstants.END_ELEMENT), resourceEndReader);
				
		// Comments
		putElementReader(new XMLElementReaderKey(null, null, XMLStreamConstants.COMMENT), new CommentElementReader<XMLReaderStreamDataProvider<XTGEventReader>>());
	}
	
	
	private void createNodeEvents(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider) {
		if (streamDataProvider.hasSpecialEventCollection()) {
			streamDataProvider.resetCurrentEventCollection();
		}
		
		if (streamDataProvider.isCreateNodeStart()) {
			NodeEdgeInfo nodeInfo = streamDataProvider.getSourceNode().peek();
			
			getStreamDataProvider().getCurrentEventCollection().add(new NodeEvent(nodeInfo.getID(), nodeInfo.getLabel(), null, nodeInfo.isRoot()));
			for (JPhyloIOEvent nextEvent : nodeInfo.getNestedNodeEvents()) {				
				getStreamDataProvider().getCurrentEventCollection().add(nextEvent);  // Might lead to an exception, if nodeInfo.getNestedEvents() is the currentEventCollection at the time this method is called
			}
			
			streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.NODE));
		}
	}
	
	
	private void createEdgeEvents(XMLReaderStreamDataProvider<XTGEventReader> streamDataProvider) {
		Queue<NodeEdgeInfo> edgeInfos = streamDataProvider.getEdgeInfos().pop();  // All edges leading to children of this node
		String sourceID = null;
		NodeEdgeInfo edgeInfo;
		
		if (!streamDataProvider.getEdgeInfos().isEmpty()) {
			streamDataProvider.getEdgeInfos().peek().add(streamDataProvider.getSourceNode().peek());  // Add info for this node to top level queue
		}
		
		if (!streamDataProvider.getSourceNode().isEmpty()) {
			sourceID = streamDataProvider.getSourceNode().peek().getID();
		}
		
		while (!edgeInfos.isEmpty()) {
			edgeInfo = edgeInfos.poll();
			
			if (!((sourceID == null) && Double.isNaN(edgeInfo.getLength()) && edgeInfo.getNestedEdgeEvents().isEmpty())) {  // Do not add root edge if no information about it is present
				getStreamDataProvider().getCurrentEventCollection().add(new EdgeEvent(DEFAULT_EDGE_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null, 
						sourceID, edgeInfo.getID(), edgeInfo.getLength()));

				for (JPhyloIOEvent nextEvent : edgeInfo.getNestedEdgeEvents()) {
					getStreamDataProvider().getCurrentEventCollection().add(nextEvent);
				}
				
				streamDataProvider.getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(
						sourceID == null ? EventContentType.ROOT_EDGE : EventContentType.EDGE, EventTopologyType.END));
			}
		}		
	}
}
