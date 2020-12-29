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
package info.bioinfweb.jphyloio.formats.newick;


import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.jphyloio.AbstractEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.formats.nexus.NexusEventWriter;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;
import info.bioinfweb.jphyloio.utils.TopoplogicalNodeInfo;
import info.bioinfweb.jphyloio.utils.TreeTopologyExtractor;

import java.io.IOException;
import java.util.Iterator;



/**
 * Implementation to write Newick tree definitions to be used by {@link NewickEventWriter} and {@link NexusEventWriter}.
 * 
 * @author Ben St&ouml;ver
 * @see NewickEventWriter
 * @see NexusEventWriter
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class NewickStringWriter implements NewickConstants {
	private TextWriterStreamDataProvider<?> streamDataProvider;
	private TreeNetworkDataAdapter tree;
	private ObjectListDataAdapter<EdgeEvent> edges;
	private ObjectListDataAdapter<NodeEvent> nodes;
	private TreeTopologyExtractor topologyExtractor;
	private NewickWriterNodeLabelProcessor nodeLabelProcessor;
	private ReadWriteParameterMap parameters;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param writer the writer to write the Newick string to
	 * @param tree the tree data adapter providing the tree data to be written
	 * @param otuList the list of OTU definitions to be used to label unlabeled tree nodes (Maybe {@code null}.)
	 * @param useOTUFirst Specify {@code true} here, if 
	 *        {@link AbstractEventWriter#getLinkedOTUNameOTUFirst(LinkedLabeledIDEvent, OTUListDataAdapter)}
	 *        shall be used to determine node names (e.g. for writing Nexus) or {@code false} if
	 *        {@link AbstractEventWriter#getLinkedOTUNameOwnFirst(LinkedLabeledIDEvent, OTUListDataAdapter)}
	 *        should be used instead (e.g. for writing Newick).
	 * @param parameters the write parameter map specified to the calling reader
	 */
	public NewickStringWriter(TextWriterStreamDataProvider<?> streamDataProvider, TreeNetworkDataAdapter tree,
			NewickWriterNodeLabelProcessor nodeLabelProcessor, ReadWriteParameterMap parameters) {
		
		super();
		this.streamDataProvider = streamDataProvider;
		this.tree = tree;
		this.nodeLabelProcessor = nodeLabelProcessor;
		this.parameters = parameters;
	}
	
	
	public static boolean isFreeNameCharForWriting(char c) {
		return NewickScanner.isFreeNameChar(c) && (c != NAME_DELIMITER) && (c != ALTERNATIVE_NAME_DELIMITER);
	}
	
	
	private static boolean isFreeName(String name) {
		if (name.length() == 0) {
			return true;
		}
		else {
			for (int i = 0; i < name.length(); i++) {
				if (!isFreeNameCharForWriting(name.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	}
	
	
	public static String formatToken(String token, char delimiter) {
		boolean containsUnderscores = token.contains("" + FREE_NAME_BLANK);
		if (!containsUnderscores && isFreeName(token)) {  // Do not write strings as free names, which contain underscores, because they would become spaces when they are read again.
			return token;
		}
		else {
			if (!containsUnderscores) {
				String editedName = token.replace(' ', FREE_NAME_BLANK);
				if (isFreeName(editedName)) { 
					return editedName;  // Replace spaces by underscores, if no underscore was present in the original name.
				}
			}
			
			StringBuffer result = new StringBuffer(token.length() * 2);
			result.append(delimiter);
			for (int i = 0; i < token.length(); i++) {
				if (token.charAt(i) == delimiter) {
					result.append(delimiter);  // Second time 
				}
				result.append(token.charAt(i));
			}
			result.append(delimiter);
			return result.toString();
		}
	}
	
	
	private void writeSubtree(String nodeID) throws IOException {
		TopoplogicalNodeInfo nodeInfo = topologyExtractor.getIDToNodeInfoMap().get(nodeID);
		NewickNodeEdgeEventReceiver<EdgeEvent> edgeReceiver = new NewickNodeEdgeEventReceiver<EdgeEvent>(streamDataProvider, parameters);
		edges.writeContentData(parameters, edgeReceiver, nodeInfo.getAfferentBranchID());  //TODO It would theoretically possible to save memory, if only the node ID would be processed here and the associated metadata and comments would be processed after the recursion.
		Iterator<String> childNodeIDIterator = nodeInfo.getChildNodeIDs().iterator();
		if (childNodeIDIterator.hasNext()) {
			streamDataProvider.getWriter().write(SUBTREE_START);
			writeSubtree(childNodeIDIterator.next());
			while (childNodeIDIterator.hasNext()) {
				streamDataProvider.getWriter().write(ELEMENT_SEPERATOR + " ");
				writeSubtree(childNodeIDIterator.next());
			}
			streamDataProvider.getWriter().write(SUBTREE_END);
		}
		
		NewickNodeEdgeEventReceiver<LinkedLabeledIDEvent> nodeReceiver = 
				new NewickNodeEdgeEventReceiver<LinkedLabeledIDEvent>(streamDataProvider, parameters);
		nodes.writeContentData(parameters, nodeReceiver, nodeID);
		
		// Write node data:
		streamDataProvider.getWriter().write(formatToken(nodeLabelProcessor.createNodeName(tree.getNodes(parameters).getObjectStartEvent(parameters, nodeID)), NAME_DELIMITER));
		nodeReceiver.writeMetadata();
		nodeReceiver.writeComments();
		
		// Write edge data:
		if (edges.getObjectStartEvent(parameters, nodeInfo.getAfferentBranchID()).hasLength()) {
			streamDataProvider.getWriter().write(LENGTH_SEPERATOR);
			streamDataProvider.getWriter().write(Double.toString(edges.getObjectStartEvent(parameters, nodeInfo.getAfferentBranchID()).getLength()));
		}
		else if (!nodeReceiver.hasMetadataToWrite() && edgeReceiver.hasMetadataToWrite()) {  // If no node annotations and not branch length were written, an empty hot comment needs to be placed before the edge metadata. Otherwise it would become node metadata, when the output is read again.
			streamDataProvider.getWriter().write(COMMENT_START);
			streamDataProvider.getWriter().write(HOT_COMMENT_START_SYMBOL);
			streamDataProvider.getWriter().write(COMMENT_END);
		}
		edgeReceiver.writeMetadata();
		edgeReceiver.writeComments();
	}
	
	
	private void writeRootedInformation() throws IOException {
		streamDataProvider.getWriter().write(COMMENT_START);
		if (nodes.getObjectStartEvent(parameters, topologyExtractor.getPaintStartID()).isRootNode()) {
			streamDataProvider.getWriter().write(ROOTED_HOT_COMMENT.toUpperCase());
		}
		else {
			streamDataProvider.getWriter().write(UNROOTED_HOT_COMMENT.toUpperCase());
		}
		streamDataProvider.getWriter().write(COMMENT_END);
		streamDataProvider.getWriter().write(" ");
	}

	
	/**
	 * Writes the tree data specified in the constructor to the specified stream.
	 * <p>
	 * If the specified tree/network data adapter models a phylogenetic network and not a tree,
	 * nothing is written and an according warning is logged. Additionally warnings are logged,
	 * if the tree adapter provides metadata or if multiple root edges are available.
	 * <p>
	 * If an empty tree definition (with no root edge) is specified, the written Newick string
	 * only consists of the terminal symbol {@code ';'}.
	 * 
	 * @throws IOException if an I/O error occurs while writing to specified writer
	 */
	public void write() throws IOException {
		ApplicationLogger logger = parameters.getLogger();
		if (tree.isTree(parameters)) {
//			if (tree.getMetadataAdapter() != null) {  //TODO Use receiver to check for metadata instead
//				logger.addWarning(
//						"A tree definition contains tree metadata, which cannot be written to Newick/NHX and is therefore ignored.");
//			}
			
			edges = tree.getEdges(parameters);
			nodes = tree.getNodes(parameters);
			topologyExtractor = new TreeTopologyExtractor(tree, parameters);
			
			writeRootedInformation();
			writeSubtree(topologyExtractor.getPaintStartID());
			streamDataProvider.getWriter().write(TERMINAL_SYMBOL);
			AbstractEventWriter.writeLineBreak(streamDataProvider.getWriter(), parameters);
		}
		else {
			logger.addWarning("A provided network definition was ignored, because the Newick/NHX format only supports trees.");  //TODO Reference network label or ID of the network, when available.
		}
	}
}
