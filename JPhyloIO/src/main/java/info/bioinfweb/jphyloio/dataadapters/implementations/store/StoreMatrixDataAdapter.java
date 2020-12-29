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
package info.bioinfweb.jphyloio.dataadapters.implementations.store;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.events.type.EventType;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;



public class StoreMatrixDataAdapter extends StoreAnnotatedDataAdapter<LinkedLabeledIDEvent> implements MatrixDataAdapter {
	private LinkedLabeledIDEvent startEvent;
	private StoreObjectListDataAdapter<LinkedLabeledIDEvent> matrix = new StoreObjectListDataAdapter<LinkedLabeledIDEvent>();
	private StoreObjectListDataAdapter<CharacterDefinitionEvent> characterDefinitions = new StoreObjectListDataAdapter<CharacterDefinitionEvent>();
	private StoreObjectListDataAdapter<LinkedLabeledIDEvent> sequenceSets = new StoreObjectListDataAdapter<LinkedLabeledIDEvent>();
	private StoreObjectListDataAdapter<TokenSetDefinitionEvent> tokenSets = new StoreObjectListDataAdapter<TokenSetDefinitionEvent>();
	private StoreObjectListDataAdapter<LinkedLabeledIDEvent> characterSets = new StoreObjectListDataAdapter<LinkedLabeledIDEvent>();
	private boolean longTokens;
	
	
	public StoreMatrixDataAdapter(LinkedLabeledIDEvent alignmentStartEvent, boolean longTokens, List<JPhyloIOEvent> annotations) {
		super(annotations);
		this.startEvent = alignmentStartEvent;
		this.longTokens = longTokens;
	}


	@Override
	public LinkedLabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return startEvent;
	}



	@Override
	public long getSequenceCount(ReadWriteParameterMap parameters) {
		return matrix.getObjectMap().size();
	}
	

	@Override
	public long getColumnCount(ReadWriteParameterMap parameters) { //TODO do not go through all sequences in case they are very long
		long previousLength = 0;
		long currentLength = 0;
		
		Iterator<String> sequences = getSequenceIDIterator(parameters);
		while (sequences.hasNext()) {
			previousLength = currentLength;
			currentLength = getSequenceLength(parameters, sequences.next());
			if ((previousLength != 0) && (previousLength != currentLength)) {
				return -1;
			}
		}
	
		return currentLength;
	}
	

	@Override
	public boolean containsLongTokens(ReadWriteParameterMap parameters) {
		return longTokens;
	}


	public StoreObjectListDataAdapter<LinkedLabeledIDEvent> getMatrix() {
		return matrix;
	}


	@Override
	public StoreObjectListDataAdapter<LinkedLabeledIDEvent> getCharacterSets(ReadWriteParameterMap parameters) {
		return characterSets;
	}
	

	@Override
	public StoreObjectListDataAdapter<TokenSetDefinitionEvent> getTokenSets(ReadWriteParameterMap parameters) {
		return tokenSets;
	}
	

	@Override
	public Iterator<String> getSequenceIDIterator(ReadWriteParameterMap parameters) {
		return matrix.getObjectMap().keyList().iterator();
	}
	

	@Override
	public LinkedLabeledIDEvent getSequenceStartEvent(ReadWriteParameterMap parameters, String sequenceID) throws IllegalArgumentException {
		if (matrix.getObjectMap().keyList().contains(sequenceID)) {
			return matrix.getObjectStartEvent(parameters, sequenceID);
		}
		else {
			throw new IllegalArgumentException("The alignment does not contain a sequence with the ID \"" + sequenceID +"\".");
		}
	}
	

	@Override
	public long getSequenceLength(ReadWriteParameterMap parameters, String sequenceID) throws IllegalArgumentException {
		if (matrix.getObjectMap().keyList().contains(sequenceID)) {
			int sequenceLength = 0;
			for (JPhyloIOEvent event : matrix.getObjectContent(sequenceID)) {
				if (event.getType().equals(new EventType(EventContentType.SINGLE_SEQUENCE_TOKEN, EventTopologyType.START))) {
					sequenceLength++;
				}
				else if (event.getType().getContentType().equals(EventContentType.SEQUENCE_TOKENS)) {
					sequenceLength += event.asSequenceTokensEvent().getTokens().size();
				}
			}
			return sequenceLength;
		}
		else {
			throw new IllegalArgumentException("The alignment does not contain a sequence with the ID \"" + sequenceID +"\".");
		}
	}
	

	@Override
	public void writeSequencePartContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String sequenceID, 
			long startColumn, long endColumn) throws IOException, IllegalArgumentException {
		if (matrix.getObjectMap().keyList().contains(sequenceID)) {
			for (JPhyloIOEvent event : matrix.getObjectContent(sequenceID)) {
				receiver.add(event);
			}
		}
		else {
			throw new IllegalArgumentException("The alignment does not contain a sequence with the ID \"" + sequenceID +"\".");
		}		
	}


	@Override
	public StoreObjectListDataAdapter<CharacterDefinitionEvent> getCharacterDefinitions(ReadWriteParameterMap parameters) {
		return characterDefinitions;
	}


	@Override
	public StoreObjectListDataAdapter<LinkedLabeledIDEvent> getSequenceSets(ReadWriteParameterMap parameters) {		
		return sequenceSets;
	}
}
