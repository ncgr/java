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
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterAlignmentInformation;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;
import java.util.Collection;

import javax.xml.stream.XMLStreamException;



/**
 * Receiver that is used to check the contents of a token set.
 * <p>
 * The {@link CharacterStateSetType} of the token set will be different from the one given in the start event of the according set,
 * if a {@link SingleTokenDefinitionEvent} defines a token that is not allowed under the current type of token set 
 * or if the original token set type was {@link CharacterStateSetType#NUCLEOTIDE}.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLCollectTokenSetDefinitionDataReceiver extends NeXMLCollectNamespaceReceiver {
	private String tokenSetDefinitionID;


	public NeXMLCollectTokenSetDefinitionDataReceiver(NeXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, String tokenSetDefinitionID) {
		
		super(streamDataProvider, parameterMap);
		this.tokenSetDefinitionID = tokenSetDefinitionID;
	}

	
	private void logTokenSetWarning(SingleTokenDefinitionEvent event, CharacterStateSetType specifiedType, 
			CharacterStateSetType newType) {
		
		getLogger().addWarning("Switching from the specified " + specifiedType + " token set to " + newType + 
				" token set, since a token with the ID " + event.getID() + " (\"" + event.getTokenName() + "\", " + event.getTokenType() + 
				", " + event.getMeaning() +	") was encountered that is invalid for a " + specifiedType + 
				" token set in NeXML or such a token set does not exist in this format. "
				+ "(Note that additional switches within the same token set may follow.)");
	}
	

	private void checkSingleTokenDefinition(SingleTokenDefinitionEvent event) throws JPhyloIOWriterException {
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();
		
		switch (alignmentInfo.getTokenSetType()) {
			case DNA:
				if (!isDNAToken(event)) {
					if (getStreamDataProvider().getCurrentTokenSetInfo().isNucleotideType() && isRNAToken(event) && !alignmentInfo.getDefinedTokens().contains("T")) {
						logTokenSetWarning(event, alignmentInfo.getTokenSetType(), CharacterStateSetType.RNA);
						alignmentInfo.setTokenSetType(CharacterStateSetType.RNA);
					}
					else {
						logTokenSetWarning(event, alignmentInfo.getTokenSetType(), CharacterStateSetType.DISCRETE);
						alignmentInfo.setTokenSetType(CharacterStateSetType.DISCRETE);
					}
				}
				break;
			case RNA:
				if (!isRNAToken(event)) {
					if (getStreamDataProvider().getCurrentTokenSetInfo().isNucleotideType() && isDNAToken(event) && !alignmentInfo.getDefinedTokens().contains("U")) {
						logTokenSetWarning(event, alignmentInfo.getTokenSetType(), CharacterStateSetType.DNA);
						alignmentInfo.setTokenSetType(CharacterStateSetType.DNA);
					}
					else {
						logTokenSetWarning(event, alignmentInfo.getTokenSetType(), CharacterStateSetType.DISCRETE);
						alignmentInfo.setTokenSetType(CharacterStateSetType.DISCRETE);
					}
				}
				break;
			case NUCLEOTIDE:
				if (isDNAToken(event)) {
					getLogger().addMessage("Switching from a NUCLEOTIDE to a DNA token set, since NeXML does not allow general nucleotide token sets. "
							+ "(Note that additional switches within the same token set may follow.)");
					alignmentInfo.setTokenSetType(CharacterStateSetType.DNA);
				}
				else if (isRNAToken(event)) {
					getLogger().addMessage("Switching from a NUCLEOTIDE to a RNA token set, since NeXML does not allow general nucleotide token sets. "
							+ "(Note that additional switches within the same token set may follow.)");
					alignmentInfo.setTokenSetType(CharacterStateSetType.RNA);
				}
				else {
					logTokenSetWarning(event, alignmentInfo.getTokenSetType(), CharacterStateSetType.DISCRETE);
					alignmentInfo.setTokenSetType(CharacterStateSetType.DISCRETE);
				}
				break;
			case AMINO_ACID:
				if (!isAAToken(event)) {
					logTokenSetWarning(event, alignmentInfo.getTokenSetType(), CharacterStateSetType.DISCRETE);
					alignmentInfo.setTokenSetType(CharacterStateSetType.DISCRETE);
				}
				break;
			case CONTINUOUS:
				throw new InconsistentAdapterDataException("A continuous data token set can not specify single token definitions.");
			default:
				break;
		}
	}


	private boolean isDNAToken(SingleTokenDefinitionEvent event) {
		if (event.getTokenName().length() == 1) {
			char token = event.getTokenName().charAt(0);
			if (token != 'U') {
				if (event.getTokenType().equals(CharacterSymbolType.ATOMIC_STATE)) {					
					if (SequenceUtils.isNonAmbiguityNucleotide(token) || isMissingChar(event) || isGapChar(event)) {
						return true;
					}
				}
				else if (event.getTokenType().equals(CharacterSymbolType.UNCERTAIN)) {
					if (SequenceUtils.isNucleotideAmbuguityCode(token)) {
						return checkConstituents(event.getConstituents(), SequenceUtils.nucleotideConstituents(token));
					}
					else if (isGapChar(event) || isMissingChar(event)) {
						return true;
					}
				}				
			}
		}
		return false;
	}


	private boolean isRNAToken(SingleTokenDefinitionEvent event) {
		if (event.getTokenName().length() == 1) {
			char token = event.getTokenName().charAt(0);
			if (token != 'T') {
				if (event.getTokenType().equals(CharacterSymbolType.ATOMIC_STATE)) {
					if (SequenceUtils.isNonAmbiguityNucleotide(token) || isMissingChar(event) || isGapChar(event)) {
						return true;
					}
				}
				else if (event.getTokenType().equals(CharacterSymbolType.UNCERTAIN)) {
					if (SequenceUtils.isNucleotideAmbuguityCode(token)) {
						return checkConstituents(event.getConstituents(), SequenceUtils.rnaConstituents(token));
					}
					else if (isGapChar(event) || isMissingChar(event)) {
						return true;
					}
				}
			}
		}

		return false;
	}


	private boolean isAAToken(SingleTokenDefinitionEvent event) {
		String token = event.getTokenName();

		if (event.getTokenType().equals(CharacterSymbolType.ATOMIC_STATE)) {
			if (SequenceUtils.isNonAmbiguityAminoAcid(token) || isMissingChar(event) || isGapChar(event) ||
				(event.getMeaning().equals(CharacterSymbolMeaning.CHARACTER_STATE) && token.equals(SequenceUtils.STOP_CODON_CHAR))) {
			
					return true;
			}
		}
		else if (event.getTokenType().equals(CharacterSymbolType.UNCERTAIN)) {
			if (SequenceUtils.isAminoAcidAmbiguityCode(token)) {
				if (!(token.equals("J") || token.equals("Xle"))) {
					Collection<String> constituents = event.getConstituents();

					if (SequenceUtils.getAminoAcidOneLetterCodes(true).contains(token.charAt(0))) {
						checkConstituents(constituents, SequenceUtils.oneLetterAminoAcidConstituents(token));
					}
					else if (SequenceUtils.getAminoAcidThreeLetterCodes(true).contains(token)) {
						checkConstituents(constituents, SequenceUtils.threeLetterAminoAcidConstituents(token));
					}
					return true;
				}
			}
			else if (isGapChar(event) || isMissingChar(event)) {
				return true;
			}
		}

		return false;
	}


	private boolean isGapChar(SingleTokenDefinitionEvent event) {
		return (event.getMeaning().equals(CharacterSymbolMeaning.GAP) && event.getTokenName().equals(Character.toString(SequenceUtils.GAP_CHAR)));
	}


	private boolean isMissingChar(SingleTokenDefinitionEvent event) {
		return (event.getMeaning().equals(CharacterSymbolMeaning.MISSING) && event.getTokenName().equals(Character.toString(SequenceUtils.MISSING_DATA_CHAR)));
	}


	private boolean checkConstituents(Collection<String> constituents, char[] expectedConstituents) {
		String[] expectedConstituentsString = new String[expectedConstituents.length];

		for (int i = 0; i < expectedConstituents.length; i++) {
			expectedConstituentsString[i] = Character.toString(expectedConstituents[i]);
		}

		return checkConstituents(constituents, expectedConstituentsString);
	}


	private boolean checkConstituents(Collection<String> constituents, String[] expectedConstituents) {
		if ((constituents != null) && !constituents.isEmpty()) {
			if (constituents.size() == expectedConstituents.length) {
				boolean isContained = true;
				for (int i = 0; i < expectedConstituents.length; i++) {
					isContained = constituents.contains(expectedConstituents[i]);
					if (!isContained) {
						return false;
					}
				}
				if (isContained) {
					return true;
				}
			}
		}
		else {
			return true;
		}

		return false;
	}


	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IOException, XMLStreamException {
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();
		
		switch (event.getType().getContentType()) {
			case SINGLE_TOKEN_DEFINITION:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					SingleTokenDefinitionEvent tokenDefinitionEvent = event.asSingleTokenDefinitionEvent();
					if (!tokenDefinitionEvent.getMeaning().equals(CharacterSymbolMeaning.MATCH)) {
						getStreamDataProvider().addToDocumentIDs(tokenDefinitionEvent.getID());
						checkSingleTokenDefinition(tokenDefinitionEvent);				
					
						alignmentInfo.getIDToTokenSetInfoMap().get(tokenSetDefinitionID).getSingleTokenDefinitions().add(tokenDefinitionEvent.getTokenName());
						alignmentInfo.getDefinedTokens().add(tokenDefinitionEvent.getTokenName());
					}
				}
				break;
			case CHARACTER_SET_INTERVAL:
				CharacterSetIntervalEvent intervalEvent = event.asCharacterSetIntervalEvent();
				for (long i = intervalEvent.getStart(); i < intervalEvent.getEnd(); i++) {
					if (!alignmentInfo.getColumnIndexToStatesMap().containsKey(i)) {
						alignmentInfo.getColumnIndexToStatesMap().put(i, tokenSetDefinitionID);  // Token sets are not allowed to overlap
					}
					else {
						throw new InconsistentAdapterDataException("More than one token set was assigned to the alignment column " + i + ".");
					}
				}
				break;
			default:
				break;
		}
		return true;
	}
}