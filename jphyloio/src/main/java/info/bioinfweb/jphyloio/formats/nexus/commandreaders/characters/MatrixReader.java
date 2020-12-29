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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters;


import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.PartEndEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.exception.UnsupportedFormatFeatureException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractNexusCommandEventReader;
import info.bioinfweb.jphyloio.utils.IDToNameManager;



/**
 * Parser for the {@code MATRIX} command in a {@code CHARACTERS}, {@code UNALIGNED} or {@code DATA} block.
 * 
 * @author Ben St&ouml;ver
 */
public class MatrixReader extends AbstractNexusCommandEventReader implements NexusConstants, ReadWriteConstants {
	private String currentSequenceLabel = null;
	private int currentSequenceIndex = 0;
	private long currentSequencePosition = 0;
	private IDToNameManager idToNameManager = new IDToNameManager(DEFAULT_SEQUENCE_ID_PREFIX);
	
	
	public MatrixReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_MATRIX, new String[]{BLOCK_NAME_CHARACTERS, BLOCK_NAME_UNALIGNED, BLOCK_NAME_DATA}, nexusDocument);
	}

	
	private String readDelimitedToken(char start, char end) throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		reader.skip(1);  // Consume "start".
		char c = reader.peekChar();
		StringBuilder result = new StringBuilder();
		while (c != end) {
			if (c == COMMENT_START) {
				reader.skip(1);  // Consume '['.
				getStreamDataProvider().readComment();
			}
			else {
				result.append(c);
				reader.skip(1);  // Consume c.
			}
			c = reader.peekChar();
		}
		reader.skip(1);  // Consume "end".
		return start + result.toString() + end;
	}
	
	
	private String readToken(boolean longTokens) throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		char c = reader.peekChar();
		if (!longTokens && (c != MATRIX_POLYMORPHIC_TOKEN_START) && (c != MATRIX_UNCERTAINS_TOKEN_START)) {
			return Character.toString(reader.readChar());
		}
		else {
			if (c == MATRIX_POLYMORPHIC_TOKEN_START) {
				return readDelimitedToken(MATRIX_POLYMORPHIC_TOKEN_START, MATRIX_POLYMORPHIC_TOKEN_END);
			}
			else if (c == MATRIX_UNCERTAINS_TOKEN_START) {
				return readDelimitedToken(MATRIX_UNCERTAINS_TOKEN_START, MATRIX_UNCERTAINS_TOKEN_END);
			}
			else {
				return getStreamDataProvider().readNexusWord();  // Also parses ' delimited tokens although they are formally not allowed in Nexus. 
			}
		}
	}
	
	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		ParameterMap map = getStreamDataProvider().getSharedInformationMap();
		if (map.getBoolean(FormatReader.INFO_KEY_TRANSPOSE, false)) {
			throw new UnsupportedFormatFeatureException("Transposed Nexus matrices are currently not supported by JPhyloIO.", 
					getStreamDataProvider().getDataReader());
		}
		else {
			boolean longTokens = map.getBoolean(FormatReader.INFO_KEY_TOKENS_FORMAT, false);
			boolean interleaved = map.getBoolean(FormatReader.INFO_KEY_INTERLEAVE, false);
			boolean noLabels = !map.getBoolean(FormatReader.INFO_KEY_LABELS, true);
			long alignmentLength = map.getLong(DimensionsReader.INFO_KEY_CHAR, Long.MAX_VALUE);
			PeekReader reader = getStreamDataProvider().getDataReader();
			try {
				if (currentSequenceLabel == null) {
					getStreamDataProvider().consumeWhiteSpaceAndComments();  // This must be done before c == COMMAND_END is tested.
				}
				char c = reader.peekChar();
				if (c == COMMAND_END) {
					reader.skip(1);  // Consume ';'.
					setAllDataProcessed(true);
					return false;
				}
				else {
					// Read name:
					if (currentSequenceLabel == null) {
						if (getStreamDataProvider().eventsUpcoming()) {
							return true;  // Immediately return comment in front of sequence name, if it was not added to the final event list.
						}
						
						String linkedOTUsID = getStreamDataProvider().getCurrentLinkedBlockID(BLOCK_NAME_TAXA);
						if (noLabels) {
							if (linkedOTUsID == null) {
								throw new JPhyloIOReaderException("A MATRIX command with the NOLABELS option was found, but no preceding TAXA "
										+ "block is present", getStreamDataProvider().getDataReader());  //TODO Can NOLABELS also be used, if NEWTAXA is used?
							}
							
							if (currentSequenceIndex >= getStreamDataProvider().getElementList(EventContentType.OTU, linkedOTUsID).size()) {
								throw new JPhyloIOReaderException("A MATRIX command contains more sequences than defined in the TAXA block. "
										+ "This is invalid, if NOLABELS was specified. An alternative cause could be an invalid sequence "
										+ "length definition.", getStreamDataProvider().getDataReader());
							}
							else {
								currentSequenceLabel = getStreamDataProvider().getElementList(EventContentType.OTU, linkedOTUsID).get(currentSequenceIndex);
							}
						}
						else {
							currentSequenceLabel = getStreamDataProvider().readNexusWord();
						}
						currentSequenceIndex++;
						
						String otuID = null;
						if (linkedOTUsID != null) {
							otuID = getStreamDataProvider().getNexusNameToIDMap(EventContentType.OTU, linkedOTUsID).get(currentSequenceLabel);  // Returns the OTU ID or null, if it is not found in the map.
						}
						getStreamDataProvider().getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.SEQUENCE, 
								idToNameManager.getID(currentSequenceLabel), currentSequenceLabel, otuID));
						currentSequencePosition = 0;  // getStreamDataProvider().getSequenceTokensEventManager().getCurrentBlockStartPosition() does not work here, because it does not return the updated value for the first sequence of the second and following blocks, since the event is processed after this command.
					}
					
					// Read tokens:
					List<String> tokens = new ArrayList<String>();
					c = reader.peekChar();
					boolean result = false;
					boolean tokenListComplete = false;
					while ((c != COMMAND_END) && (tokens.size() < getStreamDataProvider().getParameters().getMaxTokensToRead()) && 
							!tokenListComplete) {
						
						if ((c == ELEMENT_SEPARATOR) || (interleaved && StringUtils.isNewLineChar(c)) || 
								(currentSequencePosition >= alignmentLength)) {  // Position from SequenceTokensEventManager cannot be used here, because it is not valid until all read tokens have been passed to createEvent().
							
							if (c == ELEMENT_SEPARATOR) {  // If ',' should be allowed as a token in CHARACTERS and DATA blocks, an additional check whether the current block is UNALIGNED would be needed here.
								reader.skip(1);  // Consume ','.
							}
							reader.consumeNewLine();
							if (!tokens.isEmpty()) {  //TODO What about events for empty sequences?
								getStreamDataProvider().getCurrentEventCollection().add(
										getStreamDataProvider().getSequenceTokensEventManager().createEvent(currentSequenceLabel, tokens));
								result = true;
							}
							currentSequenceLabel = null;  // Read new label next time.
							tokenListComplete = true;
							getStreamDataProvider().getCurrentEventCollection().add(new PartEndEvent(EventContentType.SEQUENCE, !interleaved || 
									(getStreamDataProvider().getSequenceTokensEventManager().getCurrentPosition() >= alignmentLength)));  // Since the event has already been added, the position should be valid here.
						}
						else if (c == COMMENT_START) {
							if (!tokens.isEmpty()) {
								getStreamDataProvider().getCurrentEventCollection().add(  // Make sure to add token event before comment event.
										getStreamDataProvider().getSequenceTokensEventManager().createEvent(currentSequenceLabel, tokens));
							}
							
							reader.skip(1);  // Consume '['.
							getStreamDataProvider().readComment();
							return true;  // Return comment that was just read.
						}
						else if (Character.isWhitespace(c)) {  // consumeWhitespaceAndComments() cannot be used here, because line breaks are relevant.
							reader.skip(1);  // Consume white space.
						}
						else {
							String token = readToken(longTokens);
							if (!"".equals(token)) {
								tokens.add(token);
								currentSequencePosition++;
							}
						}
						c = reader.peekChar();
					}
					
					// Return event:
					if (!tokens.isEmpty() && (currentSequenceLabel != null)) {  // Max number of tokens was reached.
						getStreamDataProvider().getCurrentEventCollection().add(
								getStreamDataProvider().getSequenceTokensEventManager().createEvent(currentSequenceLabel, tokens));
						result = true;
					}
					if (c == COMMAND_END) {
						setAllDataProcessed(true);
						reader.skip(1);  // Consume ';'.
						if (currentSequenceLabel != null) {
							getStreamDataProvider().getCurrentEventCollection().add(new PartEndEvent(EventContentType.SEQUENCE, true));
						}
					}
					return result;
				}
			}
			catch (EOFException e) {
				throw new JPhyloIOReaderException("Unexpected end of file in Nexus " + getCommandName() + " command.", reader, e);
			}
		}
	}
}
