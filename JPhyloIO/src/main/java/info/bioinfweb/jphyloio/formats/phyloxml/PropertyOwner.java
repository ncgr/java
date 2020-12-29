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



/**
 * This enum is used by {@link PhyloXMLEventWriter} to give data receivers information about the data element 
 * the contents of a meta event belong to.
 * 
 * @author Sarah Wiechers
 *
 */
public enum PropertyOwner {
	
	/**
	 * The metadata belongs to a phylogenetic tree or network.
	 */
	PHYLOGENY,
	
	/**
	 * The metadata belongs to a node or edge.
	 */
	CLADE,
	
	/**
	 * The metadata belongs to a node.
	 */
	NODE,
	
	/**
	 * The metadata belongs to a sequence annotation.
	 */
	ANNOTATION,
	
	/**
	 * The metadata belongs to an edge.
	 */
	PARENT_BRANCH,
	
	/**
	 * The metadata belongs to an element different from those mentioned above.
	 */
	OTHER;
	
	
	@Override
	public String toString() {
		switch (this) {
			case PHYLOGENY:
				return PhyloXMLConstants.APPLIES_TO_PHYLOGENY;
			case CLADE:
				return PhyloXMLConstants.APPLIES_TO_CLADE;
			case NODE:
				return PhyloXMLConstants.APPLIES_TO_NODE;
			case ANNOTATION:
				return PhyloXMLConstants.APPLIES_TO_ANNOTATION;
			case PARENT_BRANCH:
				return PhyloXMLConstants.APPLIES_TO_PARENT_BRANCH;
			case OTHER:
				return PhyloXMLConstants.APPLIES_TO_OTHER;
			default:
				return super.toString();
		}
	}
}
