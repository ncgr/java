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
package info.bioinfweb.jphyloio.events;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Event indicating a node in a tree or network.
 * <p>
 * This event is a start event, which is followed by an end event of the same content type. Comment
 * and metainformation events maybe nested between this and its according end event. (See the description
 * of {@link JPhyloIOEventReader} for the complete grammar definition of JPhyloIO event streams.)
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public class NodeEvent extends LinkedLabeledIDEvent {
	private boolean rootNode;

	
	public NodeEvent(String id, String label, String linkedOTUID, boolean isRootNode) {
		super(EventContentType.NODE, id, label, linkedOTUID);
		this.rootNode = isRootNode;
	}

	
	/**
	 * Indicates whether this node is a root of its tree or network. One tree or network may contain alternative
	 * root nodes, but not all formats will support this.
	 * <p>
	 * Note that in case of trees this property indicates that this node can be one (alternative) root in a semantic
	 * sense. The topological representation of the tree is independent of this and only determined by how its nodes 
	 * are linked by edge events. The presence of a root edge defining a length of a tree branch leading to the node 
	 * that is topologically at the root of the tree is also fully independent of this property.
	 * <p>
	 * In <i>NeXML</i> root nodes are identified by having a {@code root="true"} 
	 * <a href="http://nexml.org/doc/schema-1/trees/abstracttrees/#AbstractNode">attribute</a>. In <i>Newick</i> and 
	 * <i>Nexus</i> only the property of the topological root node is modeled by the {@code [&R]} or {@code [&U]}
	 * hot comments preceding a <i>Newick</i> string. The same applies for the {@code rooted} attribute of the {@code phylogeny}
	 * <a href="http://www.phyloxml.org/documentation/version_1.10/phyloxml.xsd.html#h535307528">tag</a> in <i>PhyloXML</i>.
	 * 
	 * @return {@code true} if this node is a possible root or {@code false} otherwise
	 */
	public boolean isRootNode() {
		return rootNode;
	}
}
