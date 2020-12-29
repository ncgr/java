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


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLConstants;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLMetaEventInfo;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLWriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.XMLReadWriteUtils;
import info.bioinfweb.jphyloio.formats.xml.receivers.AbstractXMLDataReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



/**
 * Receiver that is used to collect information from meta events (e.g. used namespaces).
 * 
 * @author Sarah Wiechers
 *
 */
public class PhyloXMLCollectMetadataDataReceiver extends AbstractXMLDataReceiver<PhyloXMLWriterStreamDataProvider> implements PhyloXMLConstants {
	private Stack<String> metaIDs = new Stack<String>();
	private boolean isPhylogenyIDValue = false;
	private boolean isPhylogenyIDProvider = false;
	private boolean isIDSource = false;
	private boolean hasMetadata = false;
	
	
	public PhyloXMLCollectMetadataDataReceiver(PhyloXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap) {
		super(streamDataProvider, parameterMap);
	}
	
	
	public boolean hasMetadata() {
		return hasMetadata;
	}


	public void resetHasMetadata() {
		hasMetadata = false;
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		String id = event.getID();		
		QName resourceIdentifier;		
		hasMetadata = true;
		
		if (!metaIDs.isEmpty()) {
			String parentID = metaIDs.peek();
			getStreamDataProvider().getMetaEvents().get(parentID).getChildIDs().add(id);
		}
		
		getStreamDataProvider().getMetaEvents().put(id, new PhyloXMLMetaEventInfo(id, new ArrayList<String>(), metaIDs.isEmpty()));
		getStreamDataProvider().getMetaIDs().add(id);
		metaIDs.add(id);
		
		// Original type namespace does not need to be added, since only XSD types are allowed
			
		if (event.getPredicate().getURI() != null) {
			resourceIdentifier = event.getPredicate().getURI();
			getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(getStreamDataProvider().getWriter(), resourceIdentifier.getPrefix(), 
					resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			
			if (resourceIdentifier.equals(PREDICATE_PHYLOGENY_ID_ATTR_PROVIDER)) {
				isPhylogenyIDProvider = true;
				getStreamDataProvider().getMetaIDs().remove(event.getID());
			}
			else if (resourceIdentifier.equals(PREDICATE_PHYLOGENY_ID_VALUE)) {
				isPhylogenyIDValue = true;
				getStreamDataProvider().getMetaIDs().remove(event.getID());
			}
			else if (resourceIdentifier.equals(PREDICATE_ATTR_ID_SOURCE)) {
				isIDSource = true;
				getStreamDataProvider().getMetaIDs().remove(event.getID());
			}
		}
		else {
			resourceIdentifier = ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA;
			getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(getStreamDataProvider().getWriter(), resourceIdentifier.getPrefix(), 
					resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			
//			if (event.getSequenceType().equals(LiteralContentSequenceType.XML)) {
//				resourceIdentifier = ReadWriteConstants.ATTRIBUTE_STRING_KEY;
//				getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getNamespacePrefix(getStreamDataProvider().getWriter(), resourceIdentifier.getPrefix(), 
//						resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
//			}
		}
	}
	

	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		XMLReadWriteUtils.manageLiteralContentMetaNamespaces(getStreamDataProvider(), getParameterMap(), event);
		
		if (event.hasStringValue()) {
			String value = event.getStringValue();
			
			if (isPhylogenyIDProvider) {
				getStreamDataProvider().setPhylogenyIDProvider(value);
				isPhylogenyIDProvider = false;
			}
			else if (isPhylogenyIDValue) {
				getStreamDataProvider().setPhylogenyID(value);
				isPhylogenyIDValue = false;
			}
			else if (isIDSource) {
				if (!getStreamDataProvider().getIdSources().add(value)) {
					throw new InconsistentAdapterDataException("Duplicate value \"" + value + "\" found in attribute \"id_source\". "
							+ "All values of such an attribute need to be unique in the document.");
				}
				else {
					getStreamDataProvider().setCurrentCladeIDSource(value);
				}
				
				isIDSource = false;
			}
		}
	}

	
	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		String id = event.getID();
		QName resourceIdentifier;
		hasMetadata = true;
		
		if (!metaIDs.isEmpty()) {
			String parentID = metaIDs.peek();
			getStreamDataProvider().getMetaEvents().get(parentID).getChildIDs().add(id);
		}		
		
		getStreamDataProvider().getMetaEvents().put(id, new PhyloXMLMetaEventInfo(id, new ArrayList<String>(), metaIDs.isEmpty()));
		getStreamDataProvider().getMetaIDs().add(id);
		metaIDs.add(id);
		
		if (event.getRel().getURI() != null) {
			resourceIdentifier = event.getRel().getURI();
			getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(getStreamDataProvider().getWriter(), resourceIdentifier.getPrefix(), 
					resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
			
			if (resourceIdentifier.equals(PREDICATE_PHYLOGENY_ID)) {
				getStreamDataProvider().getMetaIDs().remove(event.getID());
			}
		}
		else {
			resourceIdentifier = ReadWriteConstants.PREDICATE_HAS_LITERAL_METADATA;
			getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getDefaultNamespacePrefix(getStreamDataProvider().getWriter(), resourceIdentifier.getPrefix(), 
					resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());				
		}
	}

	
	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		hasMetadata = true;
		
		metaIDs.pop();
		isPhylogenyIDProvider = false;
		isPhylogenyIDValue = false;
		isIDSource = false;
	}

	
	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {}	
}
