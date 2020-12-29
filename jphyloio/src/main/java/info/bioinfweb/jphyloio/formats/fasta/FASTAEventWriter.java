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
package info.bioinfweb.jphyloio.formats.fasta;


import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.jphyloio.AbstractSingleMatrixEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;

import java.io.IOException;
import java.util.Iterator;



/**
 * Event based writer for the <i>FASTA</i> format.
 * <p>
 * This write is able to write sequence data to <i>FASTA</i> formatted streams. It will ignore any data for phylogenetic
 * trees and networks that are provided by {@link DocumentDataAdapter#getTreeNetworkIterator(ReadWriteParameterMap)}, because the 
 * <i>FASTA</i> format does not support such data. 
 * <p>
 * Since <i>FASTA</i> does not support OTU or taxon lists as well, such a list (if provided by 
 * {@link DocumentDataAdapter#getOTUListIterator(ReadWriteParameterMap)}) will also not be written. OTU definitions (if present) 
 * will though be used, if a sequence with a linked OTU ID but without a label is specified. In such cases
 * {@link OTUListDataAdapter#getOTUStartEvent(String)} will be used to determine the according OTU label. If that OTU
 * label is also {@code null}, the sequence ID will be used as the sequence name in <i>FASTA</i>.
 * <p>
 * Comments and metadata nested in any of the supported elements will be ignored, with the only exception of comments
 * before the first token of a sequence. Such comments will be included in <i>FASTA</i>, since this is only valid position
 * for comments in the format. 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_SEQUENCE_EXTENSION_TOKEN}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LINE_LENGTH}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_WRITER_INSTANCE}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LINE_SEPARATOR}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_MAXIMUM_NAME_LENGTH} (If this parameter is omitted, any name length is possible.)</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LABEL_EDITING_REPORTER}</li>
 * </ul>
 * 
 * @author Ben St&ouml;ver
 */
public class FASTAEventWriter extends AbstractSingleMatrixEventWriter<TextWriterStreamDataProvider<FASTAEventWriter>> implements FASTAConstants {
	public FASTAEventWriter() {
		super(FASTA_FORMAT_NAME);
	}


	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.FASTA_FORMAT_ID;
	}
	
	
	@Override
	protected TextWriterStreamDataProvider<FASTAEventWriter> createStreamDataProvider() {
		return new TextWriterStreamDataProvider<FASTAEventWriter>(this);
	}
	

	@Override
	protected String maskReservedLabelCharacters(String label) {
		return label;
	}


	private void writeSequenceName(String sequenceName, FASTASequenceEventReceiver receiver, ReadWriteParameterMap parameters) 
			throws IOException {
		
		if (receiver.getCharsPerLineWritten() > 0) {
			receiver.writeNewLine(getStreamDataProvider().getWriter());
		}
		getStreamDataProvider().getWriter().write(NAME_START_CHAR);
		getStreamDataProvider().getWriter().write(sequenceName);
		writeLineBreak(getStreamDataProvider().getWriter(), parameters);
	}
	
	
	@Override
	protected void writeSingleMatrix(DocumentDataAdapter document, MatrixDataAdapter matrix, 
			Iterator<String> sequenceIDIterator, ReadWriteParameterMap parameters) throws IOException {
		
		FASTASequenceEventReceiver eventReceiver = new FASTASequenceEventReceiver(getStreamDataProvider(), parameters, matrix, 
				parameters.getLong(ReadWriteParameterNames.KEY_LINE_LENGTH, DEFAULT_LINE_LENGTH));
		String extensionToken = parameters.getString(ReadWriteParameterNames.KEY_SEQUENCE_EXTENSION_TOKEN);
		long maxSequenceLength = determineMaxSequenceLength(matrix, parameters);
		OTUListDataAdapter otuList = getReferencedOTUList(document, matrix, parameters);
		
		while (sequenceIDIterator.hasNext()) {
			String id = sequenceIDIterator.next();
			
			// Write name and tokens:
			writeSequenceName(editSequenceOrNodeLabel(matrix.getSequenceStartEvent(parameters, id), parameters, otuList), 
					eventReceiver, parameters);
			eventReceiver.setAllowCommentsBeforeTokens(true);  // Writing starts with 0 each time.
			matrix.writeSequencePartContentData(parameters, eventReceiver, id, 0, matrix.getSequenceLength(parameters, id));
			
			extendSequence(matrix, parameters, id, maxSequenceLength, extensionToken, eventReceiver);  // Event receiver manages line length.
		}
		
		ApplicationLogger logger = parameters.getLogger();
		if (eventReceiver.didIgnoreComments()) {
			logger.addWarning(eventReceiver.getIgnoredComments() + " comment events inside the matrix could not be written, "
					+ "because FASTA supports only comments at the beginning of sequences.");
		}
		if (eventReceiver.didIgnoreMetadata()) {
			logger.addWarning(eventReceiver.getIgnoredMetadata() + " metadata events inside the matrix could not be written, "
					+ "because FASTA does not support metadata.");
		}
	}
}
