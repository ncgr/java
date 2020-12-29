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



/**
 * Classes implementing this interface are used to modify a node label read from a <i>Newick</i> string
 * (e.g., by using information from a taxon list or translation table.)
 * 
 * @author Ben St&ouml;ver
 */
public interface NewickReaderNodeLabelProcessor {
	/**
	 * Processes a label according to the format modeled by this implementation.
	 * 
	 * @param originalLabel the label as it was read from the Newick string
	 * @param isInternal defines whether the node carrying this label is an internal node or not
	 * @return the processed label
	 */
	public String processLabel(String originalLabel, boolean isInternal);
	
	/**
	 * Returns the OTU ID that is associated with the specified label.
	 * 
	 * @param processedLabel the processed label as it was returned by a call if {@link #processLabel(String)}
	 * @return the linked OTU ID or {@code null} if no associated OTU ID exists
	 */
	public String getLinkedOTUID(String processedLabel);
}
