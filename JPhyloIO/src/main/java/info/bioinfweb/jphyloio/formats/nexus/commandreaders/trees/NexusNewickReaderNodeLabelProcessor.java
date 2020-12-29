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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees;


import java.util.Collections;
import java.util.List;

import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.newick.NewickReaderNodeLabelProcessor;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;



public class NexusNewickReaderNodeLabelProcessor implements NewickReaderNodeLabelProcessor, NexusConstants {
	public static final boolean DEFAULT_TRANSLATE_INTERNAL_NODE_NAMES = false;
	
	
	private NexusReaderStreamDataProvider streamDataProvider;
	
	
	public NexusNewickReaderNodeLabelProcessor(NexusReaderStreamDataProvider streamDataProvider) {
		super();
		this.streamDataProvider = streamDataProvider;
	}


	@Override
	public String processLabel(String originalLabel, boolean isInternal) {
		if (originalLabel == null) {  // e.g., for some internal nodes
			return null;
		}
		else if (isInternal && !streamDataProvider.getParameters().getBoolean(ReadWriteParameterNames.KEY_TRANSLATE_INTERNAL_NODE_NAMES, DEFAULT_TRANSLATE_INTERNAL_NODE_NAMES)) {
			return originalLabel;  // Return name without applying any translation.
		}
		else {
			NexusTranslationTable table = streamDataProvider.getTreesTranslationTable();
			String linkedOTUsID = streamDataProvider.getCurrentLinkedBlockID(BLOCK_NAME_TAXA);
			List<String> taxaList;
			if (linkedOTUsID == null) {
				taxaList = Collections.emptyList();
			}
			else {
				taxaList = streamDataProvider.getElementList(EventContentType.OTU, linkedOTUsID);
			}
			
			String result = table.get(originalLabel);
			if (result == null) {
				try {
					int index = Integer.parseInt(originalLabel) - 1;  // Nexus indices start with 1.
					if (index < table.size()) {
						result = table.get(index);
					}
					else if (index < taxaList.size()) {
						result = taxaList.get(index);
					}
				}
				catch (NumberFormatException e) {}
				
				if (result == null) {  // No replacement was successful.
					result = originalLabel;
				}
			}
			return result;
		}
	}

	
	@Override
	public String getLinkedOTUID(String processedLabel) {
		if (processedLabel == null) {
			return null;
		}
		else {
			String linkedOTUsID = streamDataProvider.getCurrentLinkedBlockID(BLOCK_NAME_TAXA);
			if (linkedOTUsID == null) {
				return null;
			}
			else {
				return streamDataProvider.getNexusNameToIDMap(EventContentType.OTU, linkedOTUsID).get(processedLabel);
			}
		}
	}
}
