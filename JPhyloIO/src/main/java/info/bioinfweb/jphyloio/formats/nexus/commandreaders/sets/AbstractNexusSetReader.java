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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.sets;


import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.PartEndEvent;
import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.exception.UnsupportedFormatFeatureException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractNexusCommandEventReader;



/**
 * General reader implementation to read the Nexus commands that define different kinds of sets.
 * <p>
 * It supports reading sets in Nexus standard and vector format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AbstractNexusSetReader extends AbstractNexusCommandEventReader implements NexusConstants, ReadWriteConstants {
	//TODO Implement support for Nexus name references (to both objects and other sets). Calling the createEventsForName() method will be necessary.
	//TODO Document in JavaDoc if any feature of the standard formats is not supported, when version 1.0 is released (e.g. "REMINDER").
	
	private static final String NULL_LINKED_ID = "$$$$$";
	
	
	private EventContentType setType;
	private String name = null;
	private boolean isVectorFormat = false;
	private long currentColumn = 0;
	
	
	public AbstractNexusSetReader(EventContentType setType, String commandName, String[] validBlocks, NexusReaderStreamDataProvider nexusDocument) {
		super(commandName, validBlocks, nexusDocument);
		this.setType = setType;
	}
	
	
	/**
	 * Returns the ID if the object that is linked to this set (e.g. an alignment start event related to a character set
	 * or an OTU list related to an OTU set)
	 * 
	 * @return the ID of the linked start element
	 * @throws IOException
	 */
	protected abstract String getLinkedID();
	
	/**
	 * Returns the number of elements which can potentially be contained in this set (e.g. the number of declared taxa or the number
	 * of alignment columns).
	 * 
	 * @return the number of elements or -1 if the number is currently undefined (e.g. if no block is linked to the current 
	 *         {@code SETS} block
	 */
	protected abstract long getElementCount();
	
	/**
	 * Creates the event(s) representing the specified interval in the current set type
	 * 
	 * @param start the fist index of the interval
	 * @param end the first index after the end of the interval
	 * @throws IOException if an I/O error occurs while writing the event(s)
	 */
	protected abstract void createEventsForInterval(long start, long end) throws IOException;
	
	/**
	 * Converts the Nexus name of a set element to its index.
	 * 
	 * @param name the Nexus name of the element
	 * @return the index of the specified element or -1 if an element with the specified name could not be found
	 */
	protected abstract long elementIndexByName(String name);
	
	
	private boolean checkFormatName(PeekReader reader, String name) {
		return reader.peekString(name.length()).toUpperCase().equals(name);
	}
	
	
	private String getKeyFromLinkedID() {
		String result = getLinkedID();
		if (result == null) {
			result = NULL_LINKED_ID;  // Just for the case that no linked and no default linked block is present. 
		}
		return result;
	}
	

	private void addStartEvent(String name) throws IOException {
		LinkedLabeledIDEvent event = new LinkedLabeledIDEvent(setType, DEFAULT_GENERAL_ID_PREFIX + 
				getStreamDataProvider().getIDManager().createNewID(), name,	getLinkedID());
		getStreamDataProvider().getCurrentEventCollection().add(event);
		
		getStreamDataProvider().getNexusNameToIDMap(setType, getKeyFromLinkedID()).put(event.getLabel(), event.getID());
	}
	
	
	private boolean readNameAndFormat() throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		
		// Read name:
		getStreamDataProvider().consumeWhiteSpaceAndComments();
		name = getStreamDataProvider().readNexusWord();
		if (name.length() == 0) {  // Can only happen if end of file was reached. (Otherwise at least ';' or '[' must be in name.)
			throw new JPhyloIOReaderException("Unexpected end of file in a Nexus CHARSET command.", reader);
		}
		char end = reader.readChar();  //StringUtils.lastChar(name);
		
		if (end == COMMENT_START) {
			getStreamDataProvider().readComment();
		}
		else if (end == COMMAND_END) {  // character set definition incomplete
			setAllDataProcessed(true);
			if (name.length() > 1) {
				addStartEvent(StringUtils.cutEnd(name, 1));
				getStreamDataProvider().getCurrentEventCollection().add(new PartEndEvent(setType, true));  // Empty character sets are not valid in Nexus but are anyway supported here.
				return true;
			}
			else {
				throw new JPhyloIOReaderException("Empty Nexus CHARSET command. At least a set name must be specified.", reader);
			}
		}
		else if (end != KEY_VALUE_SEPARATOR) {
			// Determine format:
			getStreamDataProvider().consumeWhiteSpaceAndComments();
			isVectorFormat = false;
			if (checkFormatName(reader, FORMAT_NAME_STANDARD)) {
				reader.skip(FORMAT_NAME_STANDARD.length());
			}
			else if (checkFormatName(reader, FORMAT_NAME_VECTOR)) {
				isVectorFormat = true;
				reader.skip(FORMAT_NAME_VECTOR.length());
			}
			
			// Consume '=':
			getStreamDataProvider().consumeWhiteSpaceAndComments();
			char c = reader.readChar();
			if (c == COMMAND_END) {
				setAllDataProcessed(true);
				addStartEvent(name);
				getStreamDataProvider().getCurrentEventCollection().add(new PartEndEvent(setType, true));  // Empty character sets are not valid in Nexus but are anyway supported here.
				return true;
			}
			else if (c != KEY_VALUE_SEPARATOR) {
				throw new JPhyloIOReaderException("Unexpected token '" + c + "' found in CharSet command.", reader);
			}
		}
		
		addStartEvent(name);
		return false;
	}

	
	private JPhyloIOReaderException createUnknownElementCountException(PeekReader reader) {
		return new JPhyloIOReaderException("A set was referencing the maximum index (using '.'), which could not be determined. "
				+ "A possible cause can be that no block containing the set elements (e.g. TAXA or CHARACTERS) was previously defined.", reader);
	}
	
	
	private long readIndex(String word, long finalIndex) {
		if (word.equals(Character.toString(SET_END_INDEX_SYMBOL))) {
			return finalIndex;
		}
		else {
			long result = elementIndexByName(word);
			if (result == -1) {
				try {
					result = Long.parseLong(word) - 1;  // Nexus indices start with 1.
				}
				catch (NumberFormatException e) {
					return -1;
				}
			}
			return result;
		}
	}
	
	
	private void readStandardFormat() throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		long start;
		long end = -1;
		Collection<JPhyloIOEvent> savedCommentEvents = new ArrayList<JPhyloIOEvent>();
		long finalIndex = getElementCount() - 1;
		
		try {
			if (reader.isNext(SET_KEY_WORD_ALL)) {
				reader.skip(SET_KEY_WORD_ALL.length());
				if (finalIndex < 0) {
					throw createUnknownElementCountException(reader);
				}
				else {
					start = 0;
					end = finalIndex;
					//TODO Consume remaining characters until command end (but process comments), since additional intervals would be valid but unnecessary.
				}
			}
			else if (reader.isNext(SET_KEY_WORD_REMAINING)) {
				throw new UnsupportedFormatFeatureException("The encountered keyword " + SET_KEY_WORD_REMAINING + 
						" is currently not supported in JPhyloIO.", reader);
			}
			else {
				String word = getStreamDataProvider().readNexusWord();  //TODO Does this really read '.' and will it continue to do so in the future? (Possibly check for this manually.)
				start = readIndex(word, -2);
				if (start == -2) {
					throw new JPhyloIOReaderException("The placeholder '" + SET_END_INDEX_SYMBOL + 
							"' is not allowed as the first index of an interval in a Nexus set defintion.", reader);
				}
				else if (start == -1) {
					String setID = getStreamDataProvider().getNexusNameToIDMap(setType, getKeyFromLinkedID()).get(word); 
					if (setID != null) {
						getStreamDataProvider().getCurrentEventCollection().add(new SetElementEvent(setID, setType));
						consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
						if (reader.peekChar() == SET_REGULAR_INTERVAL_SYMBOL) {
							throw new UnsupportedFormatFeatureException("Specifying regular intervals for a referenced Nexus set (using '" + 
									SET_REGULAR_INTERVAL_SYMBOL + "') is currently not supported by this reader.", reader);
						}
					}
					else {
						throw new JPhyloIOReaderException("The token \"" + word + 
								"\" could not be recognized as an element of this set or a reference to another set of the same type.", reader);
					}
				}
				else {
					consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
					end = start;  // Definitions like "1-2 4 6-7" are allowed.
					if (reader.peekChar() == SET_TO_SYMBOL) {
						reader.skip(1);  // Consume '-'
						consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
						word = getStreamDataProvider().readNexusWord();
						end = readIndex(word, finalIndex);
						if (end == -1) {
							throw createUnknownElementCountException(reader);
						}
						consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
					}
					
				}
			}
			
			if (start >= 0) {  // Otherwise a set reference was already written.
				if (reader.peekChar() == SET_REGULAR_INTERVAL_SYMBOL) {  //TODO Throw exception, if this construct is used together with a reference to another set. (A special UnsupportedFeatureException could be implemented for such cases.)
					reader.skip(1);  // Consume '\'
					consumeWhiteSpaceAndCommentsToBuffer(savedCommentEvents);
					long interval = getStreamDataProvider().readPositiveInteger(-1);
					if (end == -2) {
						throw createUnknownElementCountException(reader);
					}
					else if (interval < 0) {
						throw new JPhyloIOReaderException("Unexpected token found in Nexus set definition.", reader);  //TODO More concrete message?
					}
					for (long i = start; i <= end; i += interval) {
						createEventsForInterval(i, i + 1);
					}
				}
				else {
					createEventsForInterval(start, end + 1);
				}
			}
			getStreamDataProvider().getCurrentEventCollection().addAll(savedCommentEvents);
		}
		catch (EOFException e) {  // Would the thrown by peekChar().
			throw new JPhyloIOReaderException("Unexpected end of file inside a set definition.", reader, e);
		}
	}
	
	
	private void readVectorFormat() throws IOException {
		PeekReader reader = getStreamDataProvider().getDataReader();
		Collection<JPhyloIOEvent> queue = getStreamDataProvider().getCurrentEventCollection();
		
		char c = reader.readChar();
		long currentStartColumn = -1;
		while (c != COMMAND_END) {
			if (!Character.isWhitespace(c)) {
				switch (c) {
					case COMMENT_START:
						if (currentStartColumn != -1) {
							createEventsForInterval(currentStartColumn, currentColumn);
						}
						getStreamDataProvider().readComment();
						return;
					case SET_VECTOR_CONTAINED:
						if (currentStartColumn == -1) {
							currentStartColumn = currentColumn;
						}
						currentColumn++;
						break;
					case SET_VECTOR_NOT_CONTAINED:
						currentColumn++;
						if (currentStartColumn != -1) {
							createEventsForInterval(currentStartColumn, currentColumn - 1);
							return;
						}
						break;
					default:
						throw new JPhyloIOReaderException("Invalid CHARSET vector symbol '" + c + "' found.", reader);
				}
			}
			c = reader.readChar();
		}
		
		setAllDataProcessed(true);
		if (currentStartColumn != -1) {  // No terminal '0' was found.
			createEventsForInterval(currentStartColumn, currentColumn);
		}
		queue.add(new PartEndEvent(setType, true));
	}
	
	
	@Override
	protected boolean doReadNextEvent() throws IOException {
		int initialEventCount = getStreamDataProvider().getCurrentEventCollection().size();
		PeekReader reader = getStreamDataProvider().getDataReader();
		
		// Read set name:
		boolean isFirstCall = (name == null);  // Save for later use
		if (isFirstCall) {
			if (readNameAndFormat()) {
				return true;
			}
		}

		// Read position information:
		getStreamDataProvider().consumeWhiteSpaceAndComments();
		int nextChar = reader.peek();
		if (nextChar == -1) {
			throw new JPhyloIOReaderException("Unexpected end of file in Nexus CHARSET command.", reader);  // At least ';' end "END Sets" would be still to come.
		}
		else if ((char)nextChar == COMMAND_END) {
			reader.skip(1);  // Consume ';'.
			setAllDataProcessed(true);
			getStreamDataProvider().getCurrentEventCollection().add(new PartEndEvent(setType, true));
			return isFirstCall;
		}
		else {
			if (isVectorFormat) {
				readVectorFormat();
			}
			else {
				readStandardFormat();
			}
			getStreamDataProvider().consumeWhiteSpaceAndComments();  // Consume upcoming comments to have the fired before the next character set event.
			return initialEventCount < getStreamDataProvider().getCurrentEventCollection().size();
		}
	}
}
