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
package info.bioinfweb.jphyloio.formats.text;


import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.commons.text.StringUtils;
import info.bioinfweb.jphyloio.AbstractEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.PartEndEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



/**
 * Abstract base class for all readers that read their data from a file using a {@link PeekReader}.
 * <p>
 * Those are currently readers all reader reading non-XML formats from text files.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractTextEventReader<P extends TextReaderStreamDataProvider<? extends AbstractTextEventReader<P>>> 
		extends AbstractEventReader<P> {
	
	private PeekReader reader;
	protected boolean lineConsumed = true;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param reader the reader providing the document data to be read
	 * @param parameters the parameter map for this reader instance 
	 * @param matchToken the match token to be replaced in sequences or {@code null} if no replacement shall be performed
	 */
	public AbstractTextEventReader(PeekReader reader, ReadWriteParameterMap parameters, String matchToken) {
		super(parameters, matchToken);
		this.reader = reader;
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param reader the reader providing the document data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @param matchToken the match token to be replaced in sequences or {@code null} if no replacement shall be performed
	 */
	public AbstractTextEventReader(Reader reader, ReadWriteParameterMap parameters, String matchToken) throws IOException {
		super(parameters, matchToken);
		if (!(reader instanceof BufferedReader)) {
			reader = new BufferedReader(reader);
		}
		this.reader = new PeekReader(reader);
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param stream the stream providing the document data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @param matchToken the match token to be replaced in sequences or {@code null} if no replacement shall be performed
	 */
	public AbstractTextEventReader(InputStream stream, ReadWriteParameterMap parameters, String matchToken) throws IOException {
		this(new InputStreamReader(stream), parameters, matchToken);
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param file the document file to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @param matchToken the match token to be replaced in sequences or {@code null} if no replacement shall be performed
	 */
	public AbstractTextEventReader(File file, ReadWriteParameterMap parameters, String matchToken) throws IOException{
		this(new FileReader(file), parameters, matchToken);
	}


	@Override
	protected P createStreamDataProvider() {
		return (P)new TextReaderStreamDataProvider(this);
	}


	protected List<String> createTokenList(CharSequence sequence) {
		List<String> result = new ArrayList<String>(sequence.length());
		for (int i = 0; i < sequence.length(); i++) {
			char c = sequence.charAt(i);
			if (!Character.isWhitespace(c)) {  // E.g. Phylip and MEGA allow white spaces in between sequences
				result.add(Character.toString(c));
			}
		}
		return result;
	}
	
	
	protected void consumeWhiteSpaceAndComments(char commentStart, char commentEnd) throws IOException {
		int c = getReader().peek();
		while ((c != -1) && (Character.isWhitespace(c) || ((char)c == commentStart))) {
			if (((char)c == commentStart)) {
				getReader().skip(1);  // Consume comment start.
				readComment(commentStart, commentEnd);
			}
			else {
				getReader().skip(1);  // Consume white space.
			}
			c = getReader().peek();
		}
	}
	
	
	/**
	 * Reads a single comment from the reader. Only the first one of subsequent comments (e.g. 
	 * {@code [comment 1][comment 2]}) would be read. Nested comments are included in the
	 * parsed comment.
	 * <p>
	 * This method assumes that the comment start symbol has already been consumed. 
	 * 
	 * @throws IOException
	 */
	protected void readComment(char commentStart, char commentEnd) throws IOException {
		StringBuilder content = new StringBuilder();
		int nestedComments = 0;
		try {
			char c = getReader().readChar();
			int length = 0;
			int maxCommentLength = getParameters().getMaxCommentLength();
			while (!((nestedComments == 0) && (c == commentEnd))) {
				if (c == commentStart) {
					nestedComments++;
				}
				else if (c == commentEnd) {
					nestedComments--;
				}
				content.append(c);
				length++;
				if (length >= maxCommentLength) {
					c = getReader().peekChar();
					getCurrentEventCollection().add(new CommentEvent(content.toString(), (c == -1) || (c != commentEnd)));
					content.delete(0, content.length());
					length = 0;
				}
				c = getReader().readChar();
			}
			if (content.length() > 0) {
				getCurrentEventCollection().add(new CommentEvent(content.toString(), false));
			}
		}
		catch (EOFException e) {
			throw new JPhyloIOReaderException("Unexpected end of file inside a comment.", getReader());
		}
	}
	
	
	private JPhyloIOEvent eventFromCharacters(String currentSequenceName, CharSequence content) throws IOException {
		List<String> characters = createTokenList(content);
		if (characters.isEmpty()) {  // The rest of the line was consisting only of spaces
			return null;
		}
		else {
			return getSequenceTokensEventManager().createEvent(currentSequenceName, characters);
		}
	}
	
	
	protected JPhyloIOEvent readCharacters(String currentSequenceName) throws IOException {
		PeekReader.ReadResult readResult = getReader().readLine(getParameters().getMaxTokensToRead());
		lineConsumed = readResult.isCompletelyRead();
		return eventFromCharacters(currentSequenceName, readResult.getSequence());
	}
	
	
	/**
	 * Reads characters from the stream and adds a respective sequence tokens event to the queue. Additionally comment events
	 * are added to the queue, if comments are found.
	 * 
	 * @param currentSequenceName
	 * @param commentStart
	 * @param commentEnd
	 * @return the sequence tokens event that was added to the event queue
	 * @throws Exception
	 */
	protected JPhyloIOEvent readCharacters(String currentSequenceName, char commentStart, char commentEnd) throws IOException {
		final Pattern pattern = Pattern.compile(".*(\\n|\\r|\\" + commentStart + ")");
		PeekReader.ReadResult readResult = getReader().readRegExp(getParameters().getMaxTokensToRead(), pattern, false);  // In greedy mode the start of a nested comment could be consumed.
		char lastChar = StringUtils.lastChar(readResult.getSequence());
		
		JPhyloIOEvent result = eventFromCharacters(currentSequenceName, StringUtils.cutEnd(readResult.getSequence(), 1));
		if (result != null) {
			getCurrentEventCollection().add(result);
		}
		if (lastChar == commentStart) {
			readComment(commentStart, commentEnd);
		}
		else if (StringUtils.isNewLineChar(lastChar)) {
		  // Consume rest of line break:
			int nextChar = getReader().peek();
			if ((nextChar != -1) && (lastChar == '\r') && ((char)nextChar == '\n')) {
				getReader().skip(1);
			}
			lineConsumed = true;
		}
		else {  // Maximum length was reached.
			lineConsumed = false;
		}
		return result;
	}

	
	protected String readToken(char commandEnd, char commentStart, char commentEnd, char keyValueSeparator) throws IOException {
		//TODO Should delimited names be supported? Combine this method with NexusEventReader.readNexusWord()?
		
		PeekReader reader = getReader();
		StringBuilder result = new StringBuilder();
		char c = reader.peekChar();
		while (!Character.isWhitespace(c) && (c != commandEnd) && (c != keyValueSeparator)) {
			if ((char)c == commentStart) {
				reader.skip(1);  // Consume comment start.
				readComment(commentStart, commentEnd);
			}
			else {
				result.append(c);
				reader.skip(1);
			}
			c = reader.peekChar();
		}
		return result.toString();
	}
	
	
	protected KeyValueInformation readKeyValueInformation(char commandEnd, char commentStart,	char commentEnd, char keyValueSeparator) 
			throws IOException {
		
		PeekReader reader = getReader();
		
		// Read key:
		String key = readToken(commandEnd, commentStart, commentEnd, keyValueSeparator).toLowerCase();
		consumeWhiteSpaceAndComments(commentStart, commentEnd);
		
		// Read value:
		String value = "";
		char valueDelimiter = ' ';
		if (reader.peekChar() == keyValueSeparator) {
			reader.skip(1);  // Consume '='.
			consumeWhiteSpaceAndComments(commentStart, commentEnd);
			
			char c = reader.peekChar();
			if (c == '\'') {
				valueDelimiter = '\'';
			}
			else if (c == '"') {
				valueDelimiter = '"';
			}
			
			if (valueDelimiter != ' ') {
				reader.skip(1);  // Consume '"'.
				value = reader.readUntil(Character.toString(valueDelimiter)).getSequence().toString();
			}
			else {
				value = readToken(commandEnd, commentStart, commentEnd, keyValueSeparator);
			}
			consumeWhiteSpaceAndComments(commentStart, commentEnd);
		}
		return new KeyValueInformation(key, value, valueDelimiter);
	}
	
	
	/**
	 * Returns the reader providing the document contents.
	 * 
	 * @return the reader to read the document data from
	 */
	protected PeekReader getReader() {
		return reader;
	}


	@Override
	public void close() throws IOException {
		super.close();
		reader.close();
	}
}
