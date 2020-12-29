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
package info.bioinfweb.jphyloio.formats.phylip;


import java.io.IOException;
import java.util.Iterator;

import info.bioinfweb.jphyloio.AbstractSingleMatrixEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.text.TextSequenceContentReceiver;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;
import info.bioinfweb.jphyloio.utils.LabelEditingReporter;



/**
 * Event based writer for the <i>Phylip</i> format.
 * <p>
 * This write is able to write sequence data to <i>Phylip</i> formatted streams. It will ignore any data for phylogenetic
 * trees and networks that are provided by {@link DocumentDataAdapter#getTreeNetworkIterator(ReadWriteParameterMap)}, because the <i>Phylip</i> 
 * format does not support such data.
 * <p>
 * Note that sequence names may have to be edited according to the (length) constrains the <i>Phylip</i> format imposes.
 * According edits can be obtained using the {@link LabelEditingReporter} which is returned via the parameters map
 * (using {@link ReadWriteParameterNames#KEY_LABEL_EDITING_REPORTER}).
 * <p>
 * Since <i>Phylip</i> does not support OTU or taxon lists as well, such a list (if provided by 
 * {@link DocumentDataAdapter#getOTUListIterator(ReadWriteParameterMap)}) will also not be written. OTU definitions (if present) will though 
 * be used, if a sequence with a linked OTU ID but without a label is specified. In such cases 
 * {@link OTUListDataAdapter#getOTUStartEvent(String)} will be used to determine the according OTU label. If that OTU
 * label is also {@code null}, the sequence ID will be used as the sequence name in <i>Phylip</i>.
 * <p>
 * Comments and metadata nested in any of the supported elements will be ignored. 
 * <p>
 * <b>Recognized parameters:</b>
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_SEQUENCE_EXTENSION_TOKEN}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_MAXIMUM_NAME_LENGTH}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LABEL_EDITING_REPORTER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LINE_SEPARATOR}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_MAXIMUM_NAME_LENGTH} (If this parameter is omitted, {@link PhylipConstants#DEFAULT_NAME_LENGTH} will be used.)</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LABEL_EDITING_REPORTER}</li>
 * </ul>
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class PhylipEventWriter extends AbstractSingleMatrixEventWriter<TextWriterStreamDataProvider<PhylipEventWriter>> implements PhylipConstants {
	//TODO Check if documentation is still valid, when implementation is finished (especially regarding label editing). (Do the same for the FASTA writer.)

	/**
	 * Creates a new instance of this class. (Instances may be reused for different documents in subsequent calls of
	 * the different {@code #writeDocument()} methods.)
	 */
	public PhylipEventWriter() {
		super(PHYLIP_FORMAT_NAME);
	}


	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.PHYLIP_FORMAT_ID;
	}
	
	
	@Override
	protected TextWriterStreamDataProvider<PhylipEventWriter> createStreamDataProvider() {
		return new TextWriterStreamDataProvider<PhylipEventWriter>(this);
	}


	
	/**
	 * Replaces characters that may not occur in Phylip labels according to 
	 * <a href="http://evolution.genetics.washington.edu/phylip/doc/main.html#inputfiles>">this definition</a>.
	 * <p>
	 * The following replacements are performed:
	 * <ul>
	 *   <li>{@code '('} &rarr; {@code '<'}</li>
	 *   <li>{@code ')'} &rarr; {@code '>'}</li>
	 *   <li>{@code '['} &rarr; {@code '<'}</li>
	 *   <li>{@code ']'} &rarr; {@code '>'}</li>
	 *   <li>{@code ':'} &rarr; {@code '|'}</li>
	 *   <li>{@code ';'} &rarr; {@code '|'}</li>
	 *   <li>{@code ','} &rarr; {@code '|'}</li>
	 * </ul>
	 * 
	 * @param label the label to be edited
	 * @return the edited label (not containing any reserved characters anymore)
	 */
	public static String maskReservedPhylipLabelCharacters(String label) {
		return label.replace('(', '<').replace(')', '>').replace('[', '<').replace(']', '>').
				replace(':', '|').replace(';', '|').replace(',', '|');
	}
	

	@Override
	protected String maskReservedLabelCharacters(String label) {
		return maskReservedPhylipLabelCharacters(label);
	}


	@Override
	protected void writeSingleMatrix(DocumentDataAdapter document, MatrixDataAdapter matrix, 
			Iterator<String> sequenceIDIterator, ReadWriteParameterMap parameters) throws IOException {

		int nameLength = parameters.getInteger(ReadWriteParameterMap.KEY_MAXIMUM_NAME_LENGTH, DEFAULT_NAME_LENGTH);
		String extensionToken = parameters.getString(ReadWriteParameterMap.KEY_SEQUENCE_EXTENSION_TOKEN);
		long maxSequenceLength = determineMaxSequenceLength(matrix, parameters);
		OTUListDataAdapter otuList = null;
		LinkedLabeledIDEvent matrixStartEvent = matrix.getStartEvent(parameters);
		if (matrixStartEvent.hasLink()) {
			otuList = document.getOTUList(parameters, matrixStartEvent.getLinkedID());
			if (otuList == null) {
				parameters.getLogger().addWarning("The matrix with the ID " + matrixStartEvent.getID() 
						+	" references an OTU list with the ID " + matrixStartEvent.getLinkedID() 
						+	", but the document data adapter does not provide an OTU list with this ID. "
						+ "OTU references of writtes sequences will be ignored.");
				//TODO Would it be better to throw an InconsistentDataException here?
			}
		}
		
		// Write heading:
		getWriter().write("\t" + matrix.getSequenceCount(parameters) + "\t" + maxSequenceLength);
    writeLineBreak(getWriter(), parameters);
    if ((matrix.getColumnCount(parameters) == -1) && (extensionToken == null)) {
    	parameters.getLogger().addWarning("The provided sequences have inequal lengths and filling up sequences was not "
    			+ "specified. The column count written to the Phylip document is the length of the longest sequence. Some "
    			+ "programs may not be able to parse Phylip files with unequal sequence lengths.");
    }
    
    while (sequenceIDIterator.hasNext()) {
    	String id = sequenceIDIterator.next();
    	
    	// Write label:
    	String label = editSequenceOrNodeLabel(matrix.getSequenceStartEvent(parameters, id), parameters, otuList);
    	getWriter().write(label);
    	for (int i = label.length(); i < nameLength; i++) {
    		getWriter().write(' ');
			}
    	
    	// Write sequence:
    	TextSequenceContentReceiver<TextWriterStreamDataProvider<PhylipEventWriter>> receiver = 
    			new TextSequenceContentReceiver<TextWriterStreamDataProvider<PhylipEventWriter>>(getStreamDataProvider(), parameters, 
    					matrix.containsLongTokens(parameters), null, null);
    	matrix.writeSequencePartContentData(parameters, receiver, id, 0, matrix.getSequenceLength(parameters, id));
    	extendSequence(matrix, parameters, id, maxSequenceLength, extensionToken, receiver);
    	
    	writeLineBreak(getWriter(), parameters);
    }
	}
}
