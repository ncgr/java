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
package info.bioinfweb.jphyloio.formats.phyloxml;


import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.phyloxml.receivers.PhyloXMLCollectMetadataDataReceiver;
import info.bioinfweb.jphyloio.formats.phyloxml.receivers.PhyloXMLMetaDataReceiver;
import info.bioinfweb.jphyloio.formats.phyloxml.receivers.PhyloXMLOnlyCustomXMLDataReceiver;
import info.bioinfweb.jphyloio.formats.phyloxml.receivers.PhyloXMLPropertyMetadataReceiver;
import info.bioinfweb.jphyloio.formats.phyloxml.receivers.PhyloXMLSpecificPredicatesDataReceiver;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLEventWriter;
import info.bioinfweb.jphyloio.formats.xml.XMLReadWriteUtils;
import info.bioinfweb.jphyloio.utils.TreeTopologyExtractor;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamException;



/**
 * Event writer for the <a href="http://phyloxml.org/">PhyloXML</a> format.
 * 
 * <h3><a id="treesNetworks"></a>Phylogenetic trees or networks</h3>
 *  
 * This writer supports writing phylogenetic trees and rooted networks. Phylogenetic networks are represented using 
 * the {@code clade_rel} tag in <i>PhyloXML</i>. To be able to write a hierarchical tree structure, the topology is 
 * reconstructed in the tool class {@link TreeTopologyExtractor} from the node and edge lists provided by 
 * {@link TreeNetworkDataAdapter}. This writer does not support writing phylogenetic networks with multiple roots 
 * due to the way topologies are reconstructed from the sequential lists of nodes and edges in <i>JPhyloIO</i>.
 * 
 * <h3><a id="ids"></a>Element IDs</h3>
 * 
 * Some elements in a <i>PhyloXML</i> document have IDs given either via the {@link PhyloXMLConstants#ATTR_ID_SOURCE} 
 * or nested <code>ID</code> tags. These IDs are not necessarily identical with according <i>JPhyloIO</i> event IDs.
 * If information about either type of ID is present in meta events with PhyloXML specific predicates, these IDs will be
 * written to the file. All <code>id_ref</code> attributes (e.g. of a <code>clade_rel</code> tag) reference IDs given 
 * previously in an <code>id_source</code> attribute, never those from an <code>ID</code> tag.
 * If a phylogeny has no previously specified ID, the according <i>JPhyloIO</i> event ID is written to the nested 
 * <code>ID</code> tag. If a <code>clade</code> tag has no previously specified ID, it is checked whether the <i>JPhyloIO</i> 
 * event ID was already used in another <code>id_source</code> attribute in the document (since all such values have 
 * to be unique docuemnt-wide). If so the ID is modified by adding a numerical suffix and then written to the file, 
 * otherwise the event ID is directly written.
 * 
 * <h3><a id="simpleLiteralMetadata"></a>Simple literal metadata</h3>
 * 
 * Metadata with literal values that belong to a tree, network, node or edge can be written to {@code property} tags nested under
 * {@code phylogeny} or {@code clade}. Since these can not be nested in each other, the user can define a strategy
 * to deal with nested meta-events with a parameter of the type {@link PhyloXMLMetadataTreatment}. This allows
 * to e.g. write all meta-event values sequentially or ignore any nested metadata.
 * 
 * <h3><a id="specificMetadata"></a><i>PhyloXML</i>-specific metadata</h3>
 * 
 * <i>PhyloXML</i> specific metadata can be written using metadata events with respective predicates, which are declared
 * in {@link PhyloXMLConstants}. The nesting of the tags to be written must be reflected in the nesting of the metadata
 * events, where parent tags are modeled with {@link ResourceMetadataEvent}s using respective predicates and textual data
 * of terminal tags and attribute values are modeled with {@link LiteralMetadataEvent}s also using respective predicates.
 * If terminal tags have attributes, there is a predicate to be used for a parent resource metadata event and additional
 * predicates for each attribute. The textual value nested in this tag has the form {@code XXX_VALUE}. (An example would
 * be {@link PhyloXMLConstants#PREDICATE_NODE_ID_VALUE}, which would be nested under 
 * {@link PhyloXMLConstants#PREDICATE_NODE_ID} together with {@link PhyloXMLConstants#PREDICATE_NODE_ID_ATTR_PROVIDER}.)
 * <p>
 * Since the <i>PhyloXML</i> schema defines a fixed order of tags, only meta-events with certain predicates
 * are allowed in the content of different data elements and they also need to be in a specific order. Otherwise
 * an {@link InconsistentAdapterDataException} will be thrown.
 * 
 * <h4><a id="specificMetadataPredicates"></a>Where to use which predicates</h4>
 * 
 * In the following the allowed predicates nested under an event with a certain content type are listed. (Which 
 * predicates are allowed to be present in the content of these metadata events results from the information in the
 * <i>PhyloXML</i> schema (version <a href="http://www.phyloxml.org/documentation/version_1.10/phyloxml.xsd.html">1.10</a> and 
 * <a href="http://www.phyloxml.org/documentation/version_1.20/phyloxml.html">1.20</a>) and the accordingly named predicates 
 * declared in {@link PhyloXMLConstants}.)
 * <p>
 * Predicates allowed nested under events with {@link EventContentType#TREE} or {@link EventContentType#NETWORK}:
 * <ul>
 *   <li>{@link PhyloXMLConstants#PREDICATE_PHYLOGENY_ATTR_REROOTABLE}</li> 
 *   <li>{@link PhyloXMLConstants#PREDICATE_PHYLOGENY_ATTR_BRANCH_LENGTH_UNIT}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_PHYLOGENY_ATTR_TYPE}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_PHYLOGENY_DESCRIPTION}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_PHYLOGENY_DATE}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_CONFIDENCE}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_PROPERTY}</li>
 * </ul>
 * <p>
 * Predicates allowed nested under events with {@link EventContentType#EDGE} or {@link EventContentType#ROOT_EDGE}: 
 * <ul>
 *   <li>{@link PhyloXMLConstants#PREDICATE_CONFIDENCE}, 
 *   <li>{@link PhyloXMLConstants#PREDICATE_WIDTH}, 
 *   <li>{@link PhyloXMLConstants#PREDICATE_COLOR}
 * </ul>
 * Predicates allowed nested under events with {@link EventContentType#NODE}: 
 *   <li>{@link PhyloXMLConstants#PREDICATE_NODE_ID}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_TAXONOMY}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_SEQUENCE}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_EVENTS}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_BINARY_CHARACTERS}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_DISTRIBUTION}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_DATE}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_REFERENCE}</li>
 *   <li>{@link PhyloXMLConstants#PREDICATE_PROPERTY}</li>
 * </ul>
 * 
 * <h3><a id="customXML"></a>Custom XML annotations</h3>
 * 
 * Custom XML can be written nested under the {@code clade} and the {@code phylogeny} tag if it does not consist of character 
 * data that is not nested under any tags or tags that are already defined in <i>PhyloXML</i>. <i>XML</i> metadata not fulfilling
 * these conditions will be ignored.
 * <p>
 * Namespaces used or declared in custom XML elements are managed according to 
 * {@link ReadWriteParameterNames#KEY_CUSTOM_XML_NAMESPACE_HANDLING}. 
 * More information about this can be found in the documentation of {@link XMLReadWriteUtils#manageLiteralContentMetaNamespaces()}.
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_WRITER_INSTANCE}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_APPLICATION_NAME}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_APPLICATION_VERSION}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_APPLICATION_URL}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_PHYLOXML_METADATA_TREATMENT}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_CUSTOM_XML_NAMESPACE_HANDLING}</li>
 * </ul>
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see PhyloXMLConstants
 * @see PhyloXMLMetadataTreatment
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class PhyloXMLEventWriter extends AbstractXMLEventWriter<PhyloXMLWriterStreamDataProvider> implements PhyloXMLConstants, PhyloXMLPrivateConstants {
	
	
	public PhyloXMLEventWriter() {
		super();
	}
	

	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.PHYLOXML_FORMAT_ID;
	}
	
	
	@Override
	protected PhyloXMLWriterStreamDataProvider createStreamDataProvider() {
		return new PhyloXMLWriterStreamDataProvider(this);
	}

	
	@Override
	protected void doWriteDocument() throws IOException, XMLStreamException {
		PhyloXMLOnlyCustomXMLDataReceiver receiver = new PhyloXMLOnlyCustomXMLDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.OTHER);
		
		// Bind default prefixes here to avoid having to change them if an application tries to use them later on
		getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.XSD_DEFAULT_PRE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
		getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getXSIPrefix(getXMLWriter()), XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
//			getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getRDFPrefix(getXMLWriter()), XMLReadWriteUtils.NAMESPACE_RDF);
		
		checkDocument();
		
		getXMLWriter().writeStartElement(TAG_ROOT.getLocalPart());
		
		// Write namespace declarations
		getXMLWriter().writeDefaultNamespace(PHYLOXML_NAMESPACE);
		for (String prefix : getStreamDataProvider().getNamespacePrefixes()) {
			getXMLWriter().writeNamespace(prefix, getXMLWriter().getNamespaceContext().getNamespaceURI(prefix));
		}
		
		// Write schema location
		getXMLWriter().writeAttribute(XMLReadWriteUtils.getXSIPrefix(getXMLWriter()), XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, XMLReadWriteUtils.SCHEMA_LOCATION, PHYLOXML_SCHEMA_LOCATION_URI);
		
		getXMLWriter().writeComment(" " + getFileStartInfo(getParameters()) + " ");
		
		if (getStreamDataProvider().isDocumentHasMetadata() || getStreamDataProvider().isDocumentHasPhylogeny()) {			
			writePhylogenyTags();
			getDocument().writeMetadata(getParameters(), receiver);
		}
		else {
			getXMLWriter().writeStartElement(TAG_PHYLOGENY.getLocalPart());
			getXMLWriter().writeEndElement();
			
			getParameters().getLogger().addWarning("The document did not contain any data that could be written to the file.");
		}
		
		getXMLWriter().writeEndElement();		
	}
	
	
	private void checkDocument() throws IOException {
		PhyloXMLCollectMetadataDataReceiver receiver = new PhyloXMLCollectMetadataDataReceiver(getStreamDataProvider(), getParameters());
		
		getDocument().writeMetadata(getParameters(), receiver);		
		getStreamDataProvider().setDocumentHasMetadata(receiver.hasMetadata());
		
		Iterator<TreeNetworkGroupDataAdapter> treeNetworkGroupIterator = getDocument().getTreeNetworkGroupIterator(getParameters());		
		while (treeNetworkGroupIterator.hasNext()) {
			TreeNetworkGroupDataAdapter treeNetworkGroup = treeNetworkGroupIterator.next();
			
			receiver.resetHasMetadata();
			treeNetworkGroup.writeMetadata(getParameters(), receiver);
			getStreamDataProvider().setTreeGroupHasMetadata(receiver.hasMetadata());
			
			Iterator<TreeNetworkDataAdapter> treeNetworkIterator = treeNetworkGroup.getTreeNetworkIterator(getParameters());
			while (treeNetworkIterator.hasNext()) {
				TreeNetworkDataAdapter tree = treeNetworkIterator.next();					
				tree.writeMetadata(getParameters(), receiver);
				
				getStreamDataProvider().setDocumentHasPhylogeny(true);
				
				Iterator<String> edgeIDIterator = tree.getEdges(getParameters()).getIDIterator(getParameters());
				while (edgeIDIterator.hasNext()) {
					tree.getEdges(getParameters()).writeContentData(getParameters(), receiver, edgeIDIterator.next());
				}
				
				Iterator<String> nodeIDIterator = tree.getNodes(getParameters()).getIDIterator(getParameters());
				while (nodeIDIterator.hasNext()) {
					String nodeID = nodeIDIterator.next();
					
					tree.getNodes(getParameters()).writeContentData(getParameters(), receiver, nodeID);
					
					getStreamDataProvider().getNodeEventIDToIDSourceMap().put(nodeID, getStreamDataProvider().getCurrentCladeIDSource());
					getStreamDataProvider().setCurrentCladeIDSource(null);
				}
			}
		}
	}
	
	
	private void writePhylogenyTags() throws XMLStreamException, IOException {		
		Iterator<TreeNetworkGroupDataAdapter> treeNetworkGroupIterator = getDocument().getTreeNetworkGroupIterator(getParameters());		
		while (treeNetworkGroupIterator.hasNext()) {
			TreeNetworkGroupDataAdapter treeNetworkGroup = treeNetworkGroupIterator.next();
			
			Iterator<TreeNetworkDataAdapter> treeNetworkIterator = treeNetworkGroup.getTreeNetworkIterator(getParameters());
			while (treeNetworkIterator.hasNext()) {
				TreeNetworkDataAdapter tree = treeNetworkIterator.next();
				
				writePhylogenyTag(tree);  // Networks are written using the clade_relation element
			}
			
			if (getStreamDataProvider().hasTreeGroupMetadata()) {
				getLogger().addWarning("No metadata for the tree or network group with the ID \"" + treeNetworkGroup.getStartEvent(getParameters()).getID() + 
						"\" was written, because the PhyloXML format does not support this.");
			}
		}
	}
	
	
	private void writePhylogenyTag(TreeNetworkDataAdapter tree) throws XMLStreamException, IOException {
		PhyloXMLMetaDataReceiver receiver = new PhyloXMLSpecificPredicatesDataReceiver(getStreamDataProvider(), getParameters(), 
				PropertyOwner.PHYLOGENY, IDENTIFIER_PHYLOGENY);
		LabeledIDEvent startEvent = tree.getStartEvent(getParameters());
		TreeTopologyExtractor topologyExtractor = new TreeTopologyExtractor(tree, getParameters());
		
		String rootNodeID = topologyExtractor.getPaintStartID();		
		boolean rooted = tree.getNodes(getParameters()).getObjectStartEvent(getParameters(), rootNodeID).isRootNode();
		
		getXMLWriter().writeStartElement(TAG_PHYLOGENY.getLocalPart());
		getXMLWriter().writeAttribute(ATTR_ROOTED.getLocalPart(), Boolean.toString(rooted));
		getXMLWriter().writeAttribute(ATTR_BRANCH_LENGTH_UNIT.getLocalPart(), 
				XMLReadWriteUtils.getXSDPrefix(getXMLWriter()) + XMLUtils.QNAME_SEPARATOR + "double");
		
		writeSimpleTag(TAG_NAME.getLocalPart(), startEvent.getLabel());
		
		// Write ID element
		String phylogenyID = getStreamDataProvider().getPhylogenyID();		
		if (phylogenyID == null) {
			phylogenyID = startEvent.getID();
		}
		
		getXMLWriter().writeStartElement(TAG_ID.getLocalPart());
		
		if (getStreamDataProvider().getPhylogenyIDProvider() != null) {
			getXMLWriter().writeAttribute(ATTR_ID_PROVIDER.getLocalPart(), getStreamDataProvider().getPhylogenyIDProvider());
		}
		
		getXMLWriter().writeCharacters(phylogenyID);
		getXMLWriter().writeEndElement();
		
		// Write metadata with PhyloXML-specific predicates
		tree.writeMetadata(getParameters(), receiver);
		
		writeCladeTag(tree, topologyExtractor, rootNodeID);  // It is ensured by the TreeTopologyExtractor that the root node ID is not null		
		
		for (String networkEdgeID : topologyExtractor.getNetworkEdgeIDs()) {
			EdgeEvent networkEdgeEvent = tree.getEdges(getParameters()).getObjectStartEvent(getParameters(), networkEdgeID);
			getXMLWriter().writeStartElement(TAG_CLADE_RELATION.getLocalPart());
			getXMLWriter().writeAttribute(ATTR_ID_REF_0.getLocalPart(), getStreamDataProvider().getNodeEventIDToIDSourceMap().get(networkEdgeEvent.getSourceID()));
			getXMLWriter().writeAttribute(ATTR_ID_REF_1.getLocalPart(), getStreamDataProvider().getNodeEventIDToIDSourceMap().get(networkEdgeEvent.getTargetID()));
			getXMLWriter().writeAttribute(ATTR_DISTANCE.getLocalPart(), Double.toString(networkEdgeEvent.getLength()));
			getXMLWriter().writeAttribute(ATTR_TYPE.getLocalPart(), TYPE_NETWORK_EDGE);
		}
		
		// Write property tags from PhyloXML-specific predicates
		receiver = new PhyloXMLPropertyMetadataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.PHYLOGENY);
		tree.writeMetadata(getParameters(), receiver);
		
		// Write general meta data
		receiver = new PhyloXMLMetaDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.PHYLOGENY);	
		tree.writeMetadata(getParameters(), receiver);
		
		// Write custom XML
		receiver = new PhyloXMLOnlyCustomXMLDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.PHYLOGENY);	
		tree.writeMetadata(getParameters(), receiver);
		
		getXMLWriter().writeEndElement();
	}
	
	
	private void writeCladeTag(TreeNetworkDataAdapter tree, TreeTopologyExtractor topologyExtractor, String rootNodeID) throws XMLStreamException, IOException {	
		PhyloXMLMetaDataReceiver nodeReceiver = new PhyloXMLSpecificPredicatesDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.NODE, IDENTIFIER_NODE);
		PhyloXMLMetaDataReceiver edgeReceiver = new PhyloXMLSpecificPredicatesDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.PARENT_BRANCH, IDENTIFIER_EDGE);
		
		NodeEvent rootNode = tree.getNodes(getParameters()).getObjectStartEvent(getParameters(), rootNodeID);
		EdgeEvent afferentEdge = tree.getEdges(getParameters()).getObjectStartEvent(getParameters(), 
				topologyExtractor.getIDToNodeInfoMap().get(rootNodeID).getAfferentBranchID());
		
		getXMLWriter().writeStartElement(TAG_CLADE.getLocalPart());
		
		String idSource = getStreamDataProvider().getNodeEventIDToIDSourceMap().get(rootNodeID);
		if (idSource != null) {
			getXMLWriter().writeAttribute(ATTR_ID_SOURCE.getLocalPart(), idSource);
		}
		else {
			int idSuffix = 1;
			idSource = rootNodeID;
			while (!getStreamDataProvider().getIdSources().add(idSource)) {
				idSource = rootNodeID + idSuffix;
				idSuffix++;
			}
			
			getStreamDataProvider().getNodeEventIDToIDSourceMap().put(rootNodeID, idSource);
			getXMLWriter().writeAttribute(ATTR_ID_SOURCE.getLocalPart(), idSource);
		}
		
		if (!Double.isNaN(afferentEdge.getLength())) {
			getXMLWriter().writeAttribute(ATTR_BRANCH_LENGTH.getLocalPart(), Double.toString(afferentEdge.getLength()));
		}
		
		writeSimpleTag(TAG_NAME.getLocalPart(), rootNode.getLabel());
		
		// Write PhyloXML-specific metadata
		tree.getEdges(getParameters()).writeContentData(getParameters(), edgeReceiver, afferentEdge.getID());
		tree.getNodes(getParameters()).writeContentData(getParameters(), nodeReceiver, rootNodeID);
		
		// Write general metadata
		nodeReceiver = new PhyloXMLMetaDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.NODE);
		edgeReceiver = new PhyloXMLMetaDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.PARENT_BRANCH);		
		tree.getNodes(getParameters()).writeContentData(getParameters(), nodeReceiver, rootNodeID);
		tree.getEdges(getParameters()).writeContentData(getParameters(), edgeReceiver, afferentEdge.getID());
		
		// Write subtree
		for (String childID : topologyExtractor.getIDToNodeInfoMap().get(rootNodeID).getChildNodeIDs()) {		
			writeCladeTag(tree, topologyExtractor, childID);
		}
	
		// Write custom XML
		nodeReceiver = new PhyloXMLOnlyCustomXMLDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.NODE);
		edgeReceiver = new PhyloXMLOnlyCustomXMLDataReceiver(getStreamDataProvider(), getParameters(), PropertyOwner.PARENT_BRANCH);
		tree.getNodes(getParameters()).writeContentData(getParameters(), nodeReceiver, rootNodeID);
		tree.getEdges(getParameters()).writeContentData(getParameters(), edgeReceiver, afferentEdge.getID());
		
		getXMLWriter().writeEndElement();
	}
	
	
	private void writeSimpleTag(String tagName, String characters) throws XMLStreamException {
		if ((characters != null) && !characters.isEmpty()) {
			getXMLWriter().writeStartElement(tagName);
			getXMLWriter().writeCharacters(characters);
			getXMLWriter().writeEndElement();
		}
	}
}