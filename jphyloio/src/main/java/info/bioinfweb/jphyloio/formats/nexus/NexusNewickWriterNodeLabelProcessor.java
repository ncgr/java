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
package info.bioinfweb.jphyloio.formats.nexus;


import java.util.Map;

import info.bioinfweb.jphyloio.AbstractEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.formats.newick.DefaultNewickWriterNodeLabelProcessor;
import info.bioinfweb.jphyloio.utils.LabelEditingReporter;



/**
 * The node label processor used by {@link NexusEventWriter}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class NexusNewickWriterNodeLabelProcessor extends DefaultNewickWriterNodeLabelProcessor {
	private Map<String, Long> indexMap;
	
	
	public NexusNewickWriterNodeLabelProcessor(OTUListDataAdapter otuList, Map<String, Long> indexMap, 
			ReadWriteParameterMap parameters) {
		
		super(otuList, parameters);
		this.indexMap = indexMap;
	}


	@Override
	public String createNodeName(LinkedLabeledIDEvent nodeEvent) {
		LabelEditingReporter reporter = getParameters().getLabelEditingReporter();
		String result;
		if ((indexMap != null) && nodeEvent.hasLink()) {
			Long index = indexMap.get(nodeEvent.getLinkedID());
			if (index == null) {
				throw new InconsistentAdapterDataException("Error when writing tree: The node with the ID " + nodeEvent.getID() + 
						" references an OTU with the ID " + nodeEvent.getLinkedID() + 
						", which could not be found in the OTU list associated with this tree.");
			}
			else {
				result = index.toString();
				reporter.addEdit(nodeEvent, reporter.getEditedLabel(EventContentType.OTU, nodeEvent.getLinkedID()));
			}
		}
		else {
			result = AbstractEventWriter.getLinkedOTUNameOTUFirst(nodeEvent, getOTUList(), getParameters());
			reporter.addEdit(nodeEvent, result);  // Collisions between labels of nodes that do not reference an OTU are legal. (Otherwise it would not be possible to write support values as internal node names.)
		}
		return result;
	}
}
