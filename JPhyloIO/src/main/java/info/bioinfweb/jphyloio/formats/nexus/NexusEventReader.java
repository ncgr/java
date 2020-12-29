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
package info.bioinfweb.jphyloio.formats.nexus;


import info.bioinfweb.commons.LongIDManager;
import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.commons.io.PeekReader.ReadResult;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.UnknownCommandEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.exception.UnsupportedFormatFeatureException;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.newick.NewickStringReader;
import info.bioinfweb.jphyloio.formats.nexus.blockhandlers.ENewickNetworksBlockHandler;
import info.bioinfweb.jphyloio.formats.nexus.blockhandlers.NexusBlockHandler;
import info.bioinfweb.jphyloio.formats.nexus.blockhandlers.NexusBlockHandlerMap;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.DefaultCommandReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandEventReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandReaderFactory;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.FormatReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.MatrixReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.sets.AbstractNexusSetReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees.ENewickNetworkReader;
import info.bioinfweb.jphyloio.formats.text.AbstractTextEventReader;
import info.bioinfweb.jphyloio.formats.text.KeyValueInformation;
import info.bioinfweb.jphyloio.utils.SequenceTokensEventManager;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;



/**
 * Event based reader for <i>Nexus</i> files.
 * <p>
 * This reader as able to read data from the <i>Nexus</i> {@code TAXA}, {@code CHARACTERS}, {@code UNALIGNED}, {@code DATA}, 
 * {@code TREES} and {@code SETS} blocks, although not all commands of the {@code SETS} block are currently supported (see below).
 * Furthermore it can read metadata provided in hot comments in trees and the <i>eNexus</i> network format as described in the 
 * documentation of {@link NewickStringReader}. Reading phylogenetic networks from one type of custom {@code NETWORKS} block
 * is optionally also supported, if {@link #addENewickNetworksBlockSupport()} is called after construction.
 * It will also handle <i>Nexus</i> comments as comment events and optionally unknown commands as {@link UnknownCommandEvent}s.
 * 
 * <h3><a id="sets"></a>Supported sets</h3>
 * <p>
 * The commands for {@code CHARSET}, {@code TAXSET} and {@code TREESET} are currently supported both in standard and vector format. 
 * {@code STATESET} and {@code CHANGESET} are currently ignored, since <i>Nexus</i> state sets are currently not modeled by known other 
 * software and change sets are not part of the data model of <i>JPhyloIO</i>. For the same reason, the respective partition commands 
 * (e.g. {@code CHARPARTITION} that would allow to divide a character set into separate subsets) are currently not read as  well.
 *  
 * <h3><a id="linking"></a>Links between blocks</h3> 
 * <p>
 * In addition to the core <i>Nexus</i> standards, this reader also supports the {@code TITLE} and {@code LINK} commands
 * introduced by <a href="http://mesquiteproject.org/">Mesquite</a> e.g. to assign {@code TAXA} blocks to character 
 * or tree data, if more than one {@code TAXA} block is present.
 * <p>
 * Block linking is modeled on <i>JPhyloIO</i> using the {@link LinkedLabeledIDEvent#getLinkedID()} property. The information from
 * {@code TITLE} and {@code LINK} commands is e.g. used to link tree groups to OTU lists, alignments to OTU lists, character sets to 
 * alignments or indirectly to link sequences and tree nodes to OTUs. 
 * <p>
 * Note that this reader requires referenced blocks to occur before the block that contains the reference. Linking blocks that occur 
 * later in the file is not supported, since it would not allow direct event based parsing. If a {@code LINK} command is encountered 
 * that links a block that was not declared before (or not anywhere in the file), a {@link JPhyloIOReaderException} is be thrown.
 * <p>
 * If a block without a {@code LINK} command, that would usually link another block is encountered, the first block of the according 
 * type found in the file will be assumed as linked. If no according block was encountered before the {@code LINK}, nothing will be linked
 * ({@link LinkedLabeledIDEvent#getLinkedID()} of the according event will return {@code null}).
 * 
 * <h3><a id="unsupportedFeatures"></a>Unsupported features</h3> 
 * <p>
 * Currently this implementation (if used with the default command readers) does not support the following <i>Nexus</i> features and will throw
 * an {@link UnsupportedFormatFeatureException} if one of them is encountered:
 * <ul>
 *   <li>Character matrices using the {@code FORMAT} subcommand {@code TRANSPOSE}, indicating that columns and rows are shifted can 
 *       currently not be read by {@link MatrixReader}.</li>
 *   <li>Set definitions in a {@code SETS} block using the {@code REMAINING} keyword to indicate that they contain all elements that
 *       were not contained in a previous set are currently not supported by {@link AbstractNexusSetReader} and its descendants.</li>
 * </ul>
 * Note that this list only contains <i>Nexus</i> features that trigger an exception, if encountered. There may be additional featured (e.g.
 * the {@code NOTES} block) that are currently not supported, but are just ignored by the reader without throwing an exception.
 * 
 * <h3><a id="extending"></a>Extending this implementation</h3> 
 * <p>
 * It is possible to extend the functionality of this reader by adding custom implementations of {@link NexusBlockHandler}
 * or {@link NexusCommandEventReader}.
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_EXPECT_E_NEWICK}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_NEXUS_BLOCK_HANDLER_MAP}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_NEXUS_COMMAND_READER_FACTORY}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_CREATE_UNKNOWN_COMMAND_EVENTS}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_MAXIMUM_TOKENS_TO_READ}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_MAXIMUM_COMMENT_LENGTH}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_REPLACE_MATCH_TOKENS}</li>
 * </ul>
 * If custom or third parts block handlers are used together with an instance of this reader, these may support additional 
 * custom parameters.
 * 
 * @author Ben St&ouml;ver
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class NexusEventReader extends AbstractTextEventReader<NexusReaderStreamDataProvider> implements NexusConstants {
	private NexusBlockHandlerMap blockHandlerMap;
	private NexusCommandReaderFactory factory;
	private boolean createUnknownCommandEvents = false;
	private String currentBlockName;
	private NexusCommandEventReader currentCommandReader = null;
	private boolean documentEndReached = false;
	
	
	/**
	 * Creates a new instance of this class with a default block handler map and command reader factory. These contain all
	 * handlers and command readers available in the core module of <i>JPhyloIO</i>. 
	 * 
	 * @param file the <i>Nexus</i> file to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NexusEventReader(File file, ReadWriteParameterMap parameters) throws IOException {
		super(file, parameters, parameters.getMatchToken());
		init();
	}
	

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param stream the stream providing the <i>Nexus</i> data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NexusEventReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException {
		super(stream, parameters, parameters.getMatchToken());
		init();
	}
	

	/**
	 * Creates a new instance of this class with a default block handler map and command reader factory. These contain all
	 * handlers and command readers available in the core module of <i>JPhyloIO</i>. 
	 * 
	 * @param reader the reader providing the <i>Nexus</i> data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NexusEventReader(PeekReader reader, ReadWriteParameterMap parameters) throws IOException {
		super(reader, parameters, parameters.getMatchToken());
		init();
	}
	

	/**
	 * Creates a new instance of this class with a default block handler map and command reader factory. These contain all
	 * handlers and command readers available in the core module of <i>JPhyloIO</i>. 
	 * 
	 * @param reader the reader providing the <i>Nexus</i> data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NexusEventReader(Reader reader, ReadWriteParameterMap parameters) throws IOException {
		super(reader, parameters, parameters.getMatchToken());
		init();
	}
	
	
	private void init() {
		blockHandlerMap = getParameters().getObject(ReadWriteParameterMap.KEY_NEXUS_BLOCK_HANDLER_MAP, 
				NexusBlockHandlerMap.newJPhyloIOInstance(), NexusBlockHandlerMap.class);
		factory = getParameters().getObject(ReadWriteParameterMap.KEY_NEXUS_COMMAND_READER_FACTORY, 
				NexusCommandReaderFactory.newJPhyloIOInstance(), NexusCommandReaderFactory.class);
		createUnknownCommandEvents = getParameters().getBoolean(
				ReadWriteParameterMap.KEY_CREATE_UNKNOWN_COMMAND_EVENTS, false);
	}
	
	
	/**
	 * Adds an instance of {@link ENewickNetworksBlockHandler} and {@link ENewickNetworkReader} to the maps
	 * of this instance in order to be able to read custom {@code NETWORK} blocks containing phylogenetic
	 * networks in the <a href="http://dx.doi.org/10.1186/1471-2105-9-532">eNewick</i> format.
	 * <p>
	 * Both maps (ReadWriteParameterNames#KEY_NEXUS_BLOCK_HANDLER_MAP and ReadWriteParameterNames#KEY_NEXUS_COMMAND_READER_FACTORY) 
	 * are contained in the parameters map of this instance and may also be modified there.
	 * <p>
	 * Note that calling this method or not affects id {@code NETWORK} blocks are processed, while the 
	 * {@link ReadWriteParameterNames#KEY_EXPECT_E_NEWICK} parameter determines whether 
	 * <a href="http://dx.doi.org/10.1186/1471-2105-9-532">eNewick</i> strings are expected in the 
	 * {@code TREE} commands of {@code TREES} blocks. Both features can be used independently.
	 * <p>
	 * This method should not be called more than once on each instance.
	 * 
	 * @see ENewickNetworksBlockHandler
	 * @see ENewickNetworkReader
	 * @see ReadWriteParameterNames#KEY_EXPECT_E_NEWICK
	 * @see ReadWriteParameterNames#KEY_NEXUS_BLOCK_HANDLER_MAP
	 * @see ReadWriteParameterNames#KEY_NEXUS_COMMAND_READER_FACTORY
	 */
	public void addENewickNetworksBlockSupport() {
		blockHandlerMap.addHandler(new ENewickNetworksBlockHandler());
		factory.addReaderClass(ENewickNetworkReader.class);
	}
	

	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.NEXUS_FORMAT_ID;
	}


	@Override
	protected NexusReaderStreamDataProvider createStreamDataProvider() {
		return new NexusReaderStreamDataProvider(this);
	}


	/**
	 * Specifies whether {@link UnknownCommandEvent}s will be fired for all <i>Nexus</i> commands with no according reader
	 * stored in the factory. The key will be the name of the command and the value will be its contents.
	 * <p>
	 * Note that e.g. {@link FormatReader} will fire additional <i>Nexus</i> specific meta events, even if this property
	 * is set to {@code false}.  
	 * 
	 * @return {@code true} if unknown command events will be fired, {@code false} otherwise
	 */
	protected boolean getCreateUnknownCommandEvents() {
		return createUnknownCommandEvents;
	}


	/**
	 * Returns the name of the current <i>Nexus</i> block (e.g. {@code TAXA}). Note that this is different from a possible title of
	 * this block.
	 * 
	 * @return the current block name or {@code null} if the reader is currently not located inside a block
	 */
	public String getCurrentBlockName() {
		return currentBlockName;
	}


	@Override
	protected LongIDManager getIDManager() {  // Enable package visibility
		return super.getIDManager();
	}

	
	@Override
	protected SequenceTokensEventManager getSequenceTokensEventManager() {  // Enable package visibility
		return super.getSequenceTokensEventManager();
	}
	

	/**
	 * Calls {@link #consumeWhiteSpaceAndComments(char, char)} with according parameters and
	 * ensures visibility for {@link NexusReaderStreamDataProvider}.
	 * 
	 * @throws IOException if an I/O error occurs during the read operation
	 */
	protected void consumeWhiteSpaceAndComments() throws IOException {
		consumeWhiteSpaceAndComments(COMMENT_START, COMMENT_END);
	}
	
	
	protected String readNexusWord() throws IOException {
		try {
			StringBuilder result = new StringBuilder();
			char c = getReader().peekChar();
			if (c == WORD_DELIMITER) {
				getReader().skip(1);  // Consume '.
				c = getReader().peekChar();
				while (true) {
					if (c == WORD_DELIMITER) {
						getReader().skip(1);  // Consume ' (either the first of two inside a word or the terminal delimiter).
						if (getReader().peekChar() != WORD_DELIMITER) {  // Otherwise go on parsing and add one ' to the result.
							break;
						}
					}
					result.append(c);
					getReader().skip(1);  // Consume last character.
					c = getReader().peekChar();
				}
			}
			else {
				while (!Character.isWhitespace(c) && (c != COMMENT_START) && (c != COMMAND_END) && (c != KEY_VALUE_SEPARATOR) 
						&& (c != ELEMENT_SEPARATOR) && (c != CHARACTER_NAME_STATES_SEPARATOR) && ((c != SET_TO_SYMBOL) || (result.length() == 0))) {  // Allow words to start with '-' to read negative numbers.  
					  //TODO Add more special characters
					
					result.append(c);
					getReader().skip(1);
					c = getReader().peekChar();
				}
			}
			return result.toString();
		}
		catch (EOFException e) {
			throw new JPhyloIOReaderException("Unexpected end of file inside a Nexus word.", getReader(), e);
		}
	}
	
	
	/**
	 * Tries to read a positive integer from the current position of the underlying stream-
	 * 
	 * @param startOrEndIndex the value to be returned, if {@code '.'} is encountered (It is used as a placeholder 
	 *        for the highest possible index in some commands.)
	 * @return the read positive integer or {@code startOrEndIndex} or -2 if neither a positive integer nor {@code '.'} was found
	 * @throws IOException
	 */
	protected long readPositiveInteger(long startOrEndIndex) throws IOException {
		if (getReader().peekChar() == SET_END_INDEX_SYMBOL) {
			getReader().skip(1);
			return startOrEndIndex;
		}
		else {
			StringBuilder number = new StringBuilder();
			while (Character.isDigit(getReader().peekChar())) {
				number.append(getReader().readChar());
			}
			
			if (number.length() > 0) {
				return Long.parseLong(number.toString());
			}
			else {
				return -2;  // Returns -2 instead of -1 to distinguish it from the return value of getElementCount().
			}
		}
	}
	
	
	/**
	 * Calls {@link #readComment(char, char)} with according parameters and
	 * ensures visibility for {@link NexusReaderStreamDataProvider}.
	 * 
	 * @throws IOException if an I/O error occurs during the read operation
	 */
	protected void readComment() throws IOException {
		readComment(COMMENT_START, COMMENT_END);
	}
	
	
	protected KeyValueInformation readKeyValueMetaInformation() throws IOException {
		return readKeyValueInformation(COMMAND_END, COMMENT_START, COMMENT_END, KEY_VALUE_SEPARATOR);
	}
	
	
	private void checkStart() throws IOException {
		if (!FIRST_LINE.equals(getReader().readString(FIRST_LINE.length()).toUpperCase())) { 
			throw new JPhyloIOReaderException("All Nexus files must start with \"" + FIRST_LINE + "\".", 0, 0, 0);
		}
	}
	
	
	private void processBlockStartEnd(EventTopologyType topologyType) {
		getStreamDataProvider().clearBlockInformation();
		NexusBlockHandler handler = blockHandlerMap.getHandler(currentBlockName);
		if (handler != null) {
			if (EventTopologyType.START.equals(topologyType)) {
				handler.handleBegin(getStreamDataProvider());
			}
			else {
				handler.handleEnd(getStreamDataProvider());
			}
		}
	}
	
	
	private boolean readNextCommand() throws IOException {
		consumeWhiteSpaceAndComments();  // Needs to be done before the loop once, if the rest of the file only consists of whitespace and comments.
		while (getUpcomingEvents().isEmpty() && (getReader().peek() != -1)) {  // Read commands until an event is produced.  
			String commandName = getReader().readRegExp(UNTIL_WHITESPACE_COMMENT_COMMAND_PATTERN, false).getSequence().toString();
			char lastChar = StringUtils.lastChar(commandName);
			commandName = StringUtils.cutEnd(commandName, 1).toUpperCase();
			
			if (lastChar == COMMENT_START) {
				readComment();
			}
			
			if (BEGIN_COMMAND.equals(commandName)) {
				if (currentBlockName == null) {
					consumeWhiteSpaceAndComments();
					currentBlockName = getReader().readRegExp(
							UNTIL_WHITESPACE_COMMENT_COMMAND_PATTERN, false).getSequence().toString().toUpperCase();
					lastChar = StringUtils.lastChar(currentBlockName);
					currentBlockName = StringUtils.cutEnd(currentBlockName, 1);
					processBlockStartEnd(EventTopologyType.START);
					if (lastChar != COMMAND_END) {
						consumeWhiteSpaceAndComments();
						if (getReader().peekChar() == COMMAND_END) {
							getReader().skip(1);  // Consume ';'.
						}
						else {
							throw new JPhyloIOReaderException("Invalid character '" + getReader().peekChar() + "' in " + BEGIN_COMMAND + 
									" command.", getReader());
						}
					}
				}
				else {
					throw new JPhyloIOReaderException("Nested blocks are not allowed in Nexus.", getReader());
				}
			}
			else if (END_COMMAND.equals(commandName) || ALTERNATIVE_END_COMMAND.equals(commandName)) {
				processBlockStartEnd(EventTopologyType.END);  // Must be called before currentBlockName is set to null.
				currentBlockName = null;
				
				consumeWhiteSpaceAndComments();
				if ((getReader().peek() != -1) && (getReader().peekChar() == COMMAND_END)) {
					getReader().read();  // Skip COMMAND_END. (Necessary if ';' is not located directly behind the "END".)
				}
			}
			else if (lastChar == COMMAND_END) {  //	If a command of the form "COMMAND;" is encountered.
				//TODO Fire according event? (Unknown command event would be possible. Is this code also called on "COMMAND ;"?)
//				getCurrentEventCollection().add(new MetaInformationEvent(commandName, null, ""));
//				getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.META_INFORMATION, EventTopologyType.END));
			}
			else {
				currentCommandReader = factory.createReader(currentBlockName, commandName, getStreamDataProvider());
				NexusBlockHandler handler = blockHandlerMap.getHandler(currentBlockName);
				if (handler != null) {
					handler.beforeCommand(getStreamDataProvider(), commandName, currentCommandReader);
				}
				if (currentCommandReader == null) {
					if (getCreateUnknownCommandEvents()) {  // Create unknown command event from command content.
						currentCommandReader = new DefaultCommandReader(commandName, getStreamDataProvider());
					}
					else if (lastChar != COMMAND_END) {  // Consume content of command without loading large commands into a CharacterSequence as a whole.
						ReadResult readResult;
						do {
							readResult = getReader().readUntil(getParameters().getMaxTokensToRead(), Character.toString(COMMAND_END));
						} while (!readResult.isCompletelyRead());
					}
				}
				if (currentCommandReader != null) {
					return currentCommandReader.readNextEvent();
				}
			}
			consumeWhiteSpaceAndComments();
		}
		return getReader().peek() != -1;
	}
	
	
	@Override
	protected void readNextEvent() throws IOException {
		if (!documentEndReached) { 
			if (isBeforeFirstAccess()) {
				checkStart();
				getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.DOCUMENT, EventTopologyType.START));
				consumeWhiteSpaceAndComments();
			}
			else {
				boolean eventCreated = false;
				if (currentCommandReader == null) {
					eventCreated = readNextCommand();
				}
				else {
					eventCreated = currentCommandReader.readNextEvent();
				}
				if (!eventCreated) {
					do {
						eventCreated = readNextCommand();
						consumeWhiteSpaceAndComments();
					} while (!eventCreated && (getReader().peek() != -1));
				}
				if (!eventCreated) {
					documentEndReached = true;
					getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.DOCUMENT, EventTopologyType.END));
				}
			}
		}
	}
}
