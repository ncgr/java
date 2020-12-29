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
package info.bioinfweb.jphyloio.demo.simplealignment;


import info.bioinfweb.commons.collections.NumberedStringsIterator;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.NoCharDefsNoSetsMatrixDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;

import java.io.IOException;
import java.util.Iterator;



/**
 * This application's implementation of {@link MatrixDataAdapter}. It provides the data stored in the application business model
 * ({@link ApplicationModel}) to <i>JPhyloIO</i> writers.
 * <p>
 * Since this application does not handle any other data than alignments, no additional adapter implementations are necessary.
 * 
 * @author Ben St&ouml;ver
 */
public class MatrixDataAdapterImpl extends NoCharDefsNoSetsMatrixDataAdapter implements MatrixDataAdapter {
	/** A link to the application business model represented by this adapter. */
	private ApplicationModel model;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param model the application model containing the data to be represented by the new instance
	 */
	public MatrixDataAdapterImpl(ApplicationModel model) {
		super();
		this.model = model;
	}


	/**
	 * Returns the alignment start event of the alignment to be written. It will carry the label that is
	 * stored in the application model represented by this adapter.
	 * 
	 * @return the start event
	 */
	@Override
	public LinkedLabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return new LinkedLabeledIDEvent(EventContentType.ALIGNMENT, "alignment", model.getLabel(), null);  
				// Since this application support only one alignment at a time, a static ID may be used.
	}


	@Override
	public long getSequenceCount(ReadWriteParameterMap parameters) {
		return model.size();
	}

	
	@Override
	public long getColumnCount(ReadWriteParameterMap parameters) {
		return -1;  // -1 as a return value means that sequences may differ in length. If sequences should be filled up with e.g. gaps 
		            // to form an alignment, a concrete value should be returned here.
	}

	
	@Override
	public boolean containsLongTokens(ReadWriteParameterMap parameters) {
		return true;  // Since our application in principle supports tokens longer than one character, we always return true here. A
		              // more advanced implementation could check here, if long tokens are really currently present.
	}
	

	@Override
	public Iterator<String> getSequenceIDIterator(ReadWriteParameterMap parameters) {
		// The sequence IDs used in this adapter will all start with a common prefix followed by the index the respective sequence has
		// in the application business model.
		
		return new NumberedStringsIterator(ReadWriteConstants.DEFAULT_SEQUENCE_ID_PREFIX, model.size());
	}
	
	
	/**
	 * Extracts the index from a sequence ID. (Sequence IDs used by this adapter all start with a common prefix followed by the index 
	 * the respective sequence has in the application business model.
	 * 
	 * @param sequenceID the <i>JPhyloIO</i> sequence index
	 * @return the respective index in the application business model
	 */
	private int sequenceIndexByID(String sequenceID) {
		return NumberedStringsIterator.extractIntIndexFromString(sequenceID, ReadWriteConstants.DEFAULT_SEQUENCE_ID_PREFIX);
	}
	

	@Override
	public LinkedLabeledIDEvent getSequenceStartEvent(ReadWriteParameterMap parameters, String sequenceID) {
		return new LinkedLabeledIDEvent(EventContentType.SEQUENCE, sequenceID,
				model.getSequenceLabel(sequenceIndexByID(sequenceID)),  // The sequence label is fetched from the application model. 
				null);  // No linked OTU is specified.
		// Note that this method does not check, whether the specified ID is valid. More advanced implementations could do this.
	}
	

	@Override
	public long getSequenceLength(ReadWriteParameterMap parameters, String sequenceID) throws IllegalArgumentException {
		return model.getSequenceTokens(sequenceIndexByID(sequenceID)).size();  // The sequence length is fetched from the application model.
		// Note that this method does not check, whether the specified ID is valid. More advanced implementations could do this.
	}
	

	@Override
	public void writeSequencePartContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String sequenceID, 
			long startColumn,	long endColumn) throws IOException, IllegalArgumentException {
		
		receiver.add(new SequenceTokensEvent(  // Writes a sequence tokens event into the receiver.
				model.getSequenceTokens(sequenceIndexByID(sequenceID)).subList((int)startColumn, (int)endColumn)));
		// Note that no range check is performed in the specified column indices. More advanced implementations catch such problems.
	}
}
