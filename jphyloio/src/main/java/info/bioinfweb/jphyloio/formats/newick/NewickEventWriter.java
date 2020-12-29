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
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.text.AbstractTextEventWriter;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;



/**
 * Event writer for the Newick format.
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_WRITER_INSTANCE}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LINE_SEPARATOR}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_MAXIMUM_NAME_LENGTH} (If this parameter is omitted, any name length is possible.)</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LABEL_EDITING_REPORTER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}</li>
 * </ul>
 * 
 * @author Ben St&ouml;ver
 * @see 0.0.0
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class NewickEventWriter extends AbstractTextEventWriter<TextWriterStreamDataProvider<NewickEventWriter>> implements NewickConstants {
	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.NEWICK_FORMAT_ID;
	}
	
	
	@Override
	protected TextWriterStreamDataProvider<NewickEventWriter> createStreamDataProvider() {
		return new TextWriterStreamDataProvider<NewickEventWriter>(this);
	}
	
	
	@Override
	protected void doWriteDocument(DocumentDataAdapter document, Writer writer,	ReadWriteParameterMap parameters) throws IOException {
		super.doWriteDocument(document, writer, parameters);
		
		ApplicationLogger logger = parameters.getLogger();
		int treeCount = 0;
		
		logIngnoredOTULists(document, logger, parameters, "Newick/NHX", "tree nodes"); 
		if (document.getMatrixIterator(parameters).hasNext()) {
			logger.addWarning(
					"The specified matrix (matrices) will not be written, since the Newick/NHX format does not support such data."); 
		}
		
		Iterator<TreeNetworkGroupDataAdapter> treeNetworkGroupIterator = document.getTreeNetworkGroupIterator(parameters);
		while (treeNetworkGroupIterator.hasNext()) {
			TreeNetworkGroupDataAdapter treeNetworkGroup = treeNetworkGroupIterator.next();
			OTUListDataAdapter otuList = getReferencedOTUList(document, treeNetworkGroup, parameters);
			
			Iterator<TreeNetworkDataAdapter> treeNetworkIterator = treeNetworkGroup.getTreeNetworkIterator(parameters);
			while (treeNetworkIterator.hasNext()) {
				treeCount++;
				TreeNetworkDataAdapter treeNetwork = treeNetworkIterator.next();
				new NewickStringWriter(getStreamDataProvider(), treeNetwork, new DefaultNewickWriterNodeLabelProcessor(otuList, parameters), parameters).write();
			}			
		}
		
		if (treeCount == 0) {
			logger.addWarning(
					"An empty document was written, since no tree definitions were offered by the specified document adapter.");  //TODO Use message, that would be more understandable by application users (which does not use library-specific terms)?
		}
	}
}
