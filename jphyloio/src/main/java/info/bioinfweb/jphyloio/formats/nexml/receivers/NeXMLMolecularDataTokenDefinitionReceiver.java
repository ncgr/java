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
package info.bioinfweb.jphyloio.formats.nexml.receivers;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;



/**
 * Receiver that writes token definitions for molecular data, such as DNA, RNA or protein sequences.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public class NeXMLMolecularDataTokenDefinitionReceiver extends NeXMLMetaDataReceiver {
	private NeXMLTokenSetEventReceiver receiver;
	private Set<Character> tokens = new HashSet<Character>();


	public NeXMLMolecularDataTokenDefinitionReceiver(NeXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, String tokenSetID) {
		
		super(streamDataProvider, parameterMap);
		this.receiver = new NeXMLTokenSetEventReceiver(streamDataProvider, parameterMap, tokenSetID);
	}


	/**
	 * Determines which tokens are missing in the current DNA token set.
	 * 
	 * @param receiver the receiver to add the missing events to
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void writeDNATokenDefinitions(NeXMLTokenSetEventReceiver receiver) throws IOException, XMLStreamException {
		LinkedHashSet<Character> remainingTokens = new LinkedHashSet<Character>();
		
		// Add atomic states first
		for (int i = 0; i < SequenceUtils.DNA_CHARS.length(); i++) {  // This is done to have the atomic states at the beginning of the list, although they would anyway be added in the next loop.
			remainingTokens.add(SequenceUtils.DNA_CHARS.charAt(i));
		}
		
		// Ambiguity codes second: (Otherwise constituent references would be invalid.)
		for (Character state : SequenceUtils.getNucleotideCharacters()) {
			remainingTokens.add(state);
		}
		
		remainingTokens.removeAll(tokens);
		remainingTokens.remove('U');

		List<String> states = new ArrayList<String>();
		for (int i = 0; i < SequenceUtils.DNA_CHARS.length(); i++) {
			states.add(Character.toString(SequenceUtils.DNA_CHARS.charAt(i)));
		}
		states.add(Character.toString(SequenceUtils.GAP_CHAR));

		writeTokenDefinitionEvents(receiver, remainingTokens, CharacterStateSetType.DNA, states, 
				!tokens.contains(SequenceUtils.GAP_CHAR), !tokens.contains(SequenceUtils.MISSING_DATA_CHAR));
	}

	
	/**
	 * Determines which tokens are missing in the current RNA token set.
	 * 
	 * @param receiver the receiver to add the missing events to
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void writeRNATokenDefinitions(NeXMLTokenSetEventReceiver receiver) throws IOException, XMLStreamException {
		LinkedHashSet<Character> remainingTokens = new LinkedHashSet<Character>();
		
		// Add atomic states first
		for (int i = 0; i < SequenceUtils.RNA_CHARS.length(); i++) {
			remainingTokens.add(SequenceUtils.RNA_CHARS.charAt(i));
		}
		
		// Ambiguity codes second: (Otherwise constituent references would be invalid.)
		for (Character state : SequenceUtils.getNucleotideCharacters()) {
			remainingTokens.add(state);
		}
		
		remainingTokens.removeAll(tokens);
		remainingTokens.remove('T');

		List<String> states = new ArrayList<String>();
		for (int i = 0; i < SequenceUtils.RNA_CHARS.length(); i++) {
			states.add(Character.toString(SequenceUtils.RNA_CHARS.charAt(i)));
		}
		states.add(Character.toString(SequenceUtils.GAP_CHAR));

		writeTokenDefinitionEvents(receiver, remainingTokens, CharacterStateSetType.RNA, states,
				!tokens.contains(SequenceUtils.GAP_CHAR), !tokens.contains(SequenceUtils.MISSING_DATA_CHAR));
		}


	/**
	 * Determines which tokens are missing in the current amino acid token set.
	 * 
	 * @param receiver the receiver to add the missing events to
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void writeAminoAcidTokenDefinitions(NeXMLTokenSetEventReceiver receiver) throws IOException, XMLStreamException {
		LinkedHashSet<Character> remainingTokens = new LinkedHashSet<Character>();
		
		// Add atomic states first:
		for (Character state : SequenceUtils.getAminoAcidOneLetterCodes(false)) {
			remainingTokens.add(state);
		}
		remainingTokens.add(SequenceUtils.STOP_CODON_CHAR);
		
		// Ambiguity codes second: (Otherwise constituent references would be invalid.)
		for (Character state : SequenceUtils.getAminoAcidOneLetterCodes(true)) {  //TODO Isn't there a more efficient method (to be possibly added to SequenceUtils) returning only the ambiguity codes? 
			remainingTokens.add(state);
		}
		
		remainingTokens.removeAll(tokens);
		remainingTokens.remove('J');  // This is not supported in the current version of the NeXML schema.

		List<String> states = new ArrayList<String>();
		for (Character state : SequenceUtils.getAminoAcidOneLetterCodes(false)) {
			states.add(Character.toString(state));
		}
		states.add(Character.toString(SequenceUtils.STOP_CODON_CHAR));
		states.add(Character.toString(SequenceUtils.GAP_CHAR));

		writeTokenDefinitionEvents(receiver, remainingTokens, CharacterStateSetType.AMINO_ACID, states,
				!tokens.contains(SequenceUtils.GAP_CHAR), !tokens.contains(SequenceUtils.MISSING_DATA_CHAR));
	}


	/**
	 * Creates a series of {@link SingleTokenDefinitionEvent}s and respective end events from the tokens that are 
	 * missing in the current token set and adds them to the given receiver.
	 * 
	 * @param receiver the receiver to add the created events to
	 * @param remainingTokens the tokens missing in the current token set
	 * @param alignmentType the {@link CharacterStateSetType} of the current alignment
	 * @param missingConstituents the atomic states that are the constituents of the missing data symbol
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	private void writeTokenDefinitionEvents(NeXMLTokenSetEventReceiver receiver, Set<Character> remainingTokens, 
			CharacterStateSetType alignmentType, Collection<String> missingConstituents, boolean addGap, boolean addMissing) 
			throws IOException, XMLStreamException {
		
		//TODO Calling receiver.doAdd() directly instead of receiver.add() seems to be currently working in this method, but is it necessary? Would unwanted side effects occur otherwise or would on the other hand problem occur of BasicEventReceiver.add() gains additional functionality in the future, which would than not be used here?
		
		List<String> constituents;
		CharacterSymbolType type;

		for (Character token : remainingTokens) {
			constituents = null;
			type = CharacterSymbolType.ATOMIC_STATE;
			char[] constituentChars = new char[0];

			if (alignmentType.equals(CharacterStateSetType.DNA)) {
				constituentChars = SequenceUtils.nucleotideConstituents(token);
			}
			else if (alignmentType.equals(CharacterStateSetType.RNA)) {
				constituentChars = SequenceUtils.rnaConstituents(token);
			}
			else if (alignmentType.equals(CharacterStateSetType.AMINO_ACID)) {
				constituentChars = SequenceUtils.oneLetterAminoAcidConstituents(Character.toString(token));
			}

			if (constituentChars.length > 1) {
				constituents = new ArrayList<String>();
				type = CharacterSymbolType.UNCERTAIN;
				for (int i = 0; i < constituentChars.length; i++) {
					constituents.add(Character.toString(constituentChars[i]));
				}
			}
			
			String tokenDefinitionID = getStreamDataProvider().createNewID(ReadWriteConstants.DEFAULT_TOKEN_DEFINITION_ID_PREFIX);
			getStreamDataProvider().addToDocumentIDs(tokenDefinitionID);
			receiver.doAdd(new SingleTokenDefinitionEvent(tokenDefinitionID, null, Character.toString(token), 
					CharacterSymbolMeaning.CHARACTER_STATE, type, constituents));
			receiver.doAdd(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_TOKEN_DEFINITION));
		}
		
		if (addGap) {
			String tokenDefinitionID = getStreamDataProvider().createNewID(ReadWriteConstants.DEFAULT_TOKEN_DEFINITION_ID_PREFIX);
			getStreamDataProvider().addToDocumentIDs(tokenDefinitionID);
			receiver.doAdd(new SingleTokenDefinitionEvent(tokenDefinitionID, "gap", Character.toString(SequenceUtils.GAP_CHAR), 
					CharacterSymbolMeaning.GAP, CharacterSymbolType.ATOMIC_STATE, null));
			receiver.doAdd(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_TOKEN_DEFINITION));
		}

		if (addMissing) {
			String tokenDefinitionID = getStreamDataProvider().createNewID(ReadWriteConstants.DEFAULT_TOKEN_DEFINITION_ID_PREFIX);
			getStreamDataProvider().addToDocumentIDs(tokenDefinitionID);
			receiver.doAdd(new SingleTokenDefinitionEvent(tokenDefinitionID, "missing data", Character.toString(SequenceUtils.MISSING_DATA_CHAR), 
					CharacterSymbolMeaning.MISSING, CharacterSymbolType.UNCERTAIN, missingConstituents));
			receiver.doAdd(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_TOKEN_DEFINITION));
		}
	}


	/**
	 * Determines the type of alignment to which additional token definitions need to be add.
	 * 
	 * @param type the {@link CharacterStateSetType} of the current token set
	 * @throws IOException
	 * @throws XMLStreamException
	 */
	public void addRemainingEvents(CharacterStateSetType type) throws IOException, XMLStreamException {
		switch (type) {
			case NUCLEOTIDE:
			case DNA:
				writeDNATokenDefinitions(receiver);
				break;
			case RNA:
				writeRNATokenDefinitions(receiver);
				break;
			case AMINO_ACID:
				writeAminoAcidTokenDefinitions(receiver);
				break;
			default:
				break;
		}
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		receiver.add(event);
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		receiver.add(event);
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		receiver.add(event);
	}


	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		receiver.add(event);
	}


	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		receiver.add(event);
	}


	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IOException, XMLStreamException {
		switch (event.getType().getContentType()) {
			case SINGLE_TOKEN_DEFINITION:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					SingleTokenDefinitionEvent tokenDefinitionEvent = event.asSingleTokenDefinitionEvent();

					if (getStreamDataProvider().getCurrentAlignmentInfo().getAlignmentType().equals(CharacterStateSetType.AMINO_ACID)) {
						if (tokenDefinitionEvent.getTokenName().length() > 1) {  //only one letter codes can be written to NeXML
							tokens.add(SequenceUtils.oneLetterAminoAcidByThreeLetter(tokenDefinitionEvent.getTokenName()));  //Token must be a valid three letter code which was already checked in NeXMLCollectTokenSetDefinitionDataReceiver
						}
					}
					else {
						tokens.add(tokenDefinitionEvent.getTokenName().charAt(0));
					}
				}
				receiver.doAdd(event);
			default:
				break;
		}
		return true;
	}
}
