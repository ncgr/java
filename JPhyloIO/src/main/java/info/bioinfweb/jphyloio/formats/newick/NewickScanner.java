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
package info.bioinfweb.jphyloio.formats.newick;


import java.io.IOException;
import java.util.NoSuchElementException;

import info.bioinfweb.commons.io.PeekReader;
import info.bioinfweb.commons.io.StreamLocation;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees.TreeReader;



/**
 * Reads Newick tokens from a stream. (This class is used internally by JPhyloIO reader reading Newick tree definitions.)
 * 
 * @author Ben St&ouml;ver
 * @see NewickStringReader
 * @see NewickEventReader
 * @see TreeReader
 */
public class NewickScanner implements NewickConstants {
	private PeekReader reader; 
	private boolean readSequence;
	private NewickToken next = null;
	private NewickToken previous = null;
	private boolean beforeFirstAccess = true;
	private boolean branchLengthExpected = false;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param reader the underlying reader to read the tokens from
	 * @param readSequence Specify {@code true} here, if this reader shall read multiple Newick strings separated by ';' from
	 *        the underlying stream or {@code false} if the reader shall stop at the first encountered tree end (';').
	 */
	public NewickScanner(PeekReader reader, boolean readSequence) {
		super();
		this.reader = reader;
		this.readSequence = readSequence;
	}


	public static boolean isCharAfterLength(char c) {
		return Character.isWhitespace(c) || (c == ELEMENT_SEPERATOR) || (c == SUBTREE_END) || (c == COMMENT_START)
				|| (c == TERMINAL_SYMBOL);
	}
	
	
	public static boolean isFreeNameChar(char c) {
		return (c != SUBTREE_END) && (c != LENGTH_SEPERATOR) && (c != ELEMENT_SEPERATOR) && (c != COMMENT_START) 
				&& (c != TERMINAL_SYMBOL) && !Character.isWhitespace(c); 
	}
	
	
	private NewickToken readDelimitedName(char delimiter) throws IOException {
		reader.read();  // Skip initial delimiter.
		StringBuilder result = new StringBuilder();
		do {
			while ((reader.peek() != -1) && (reader.peekChar() != delimiter)) {
				result.append(reader.readChar());
			}
			if ((reader.peek(1) != -1) && (reader.peekChar(1) == delimiter)) {
				result.append(delimiter);  // Allow 'abc'''
				reader.read();
				reader.read();
			}
		} while ((reader.peek() != -1) && (reader.peekChar() != delimiter));
		
		if (reader.peek() == -1) {
			throw new JPhyloIOReaderException("Unterminated Newick token.", reader);
		}
		else {
			reader.read();  // Skip terminal delimiter.
			NewickToken token = new NewickToken(NewickTokenType.NAME, reader);
			token.setText(result.toString());
			token.setDelimited(true);
			return token;
		}
	}
	
	
	private NewickToken readFreeName() throws IOException {
		StringBuilder result = new StringBuilder();
		result.append(reader.readChar());
		while ((reader.peek() != -1) && isFreeNameChar(reader.peekChar())) {
			char c = reader.readChar();
			if (c == FREE_NAME_BLANK) {
				result.append(' ');
			}
			else {
				result.append(c);
			}
		}
		
		if (reader.peek() == -1) {
			throw new JPhyloIOReaderException("Unterminated Newick name.", reader);
		}
		else {
			return new NewickToken(reader, result.toString(), false);
		}
	}
	
	
	/**
	 * Reads a length statement in an Newick string or a comment before a branch length definition.
	 */
	private NewickToken readBranchLength() throws IOException {
		reader.readRegExp("\\s*", true);  // Skip whitespace.
		if (reader.peek() == -1) {
			throw new JPhyloIOReaderException("Unexpected end of file in a Newick branch length definition.", reader);
		}
		else {
			StreamLocation location = new StreamLocation(reader);
			if (reader.peekChar() == COMMENT_START) {
				return readComment();
			}
			else {
				StringBuilder text = new StringBuilder();
				while ((reader.peek() != -1) && !isCharAfterLength(reader.peekChar())) {
					text.append(reader.readChar());
				}
	
				double value; 
				try {
					value = Double.parseDouble(text.toString());
				}
				catch (NumberFormatException e) {
					throw new JPhyloIOReaderException("\"" + text + "\" is not a valid Newick branch length.", location, e);
				}
				
				NewickToken token = new NewickToken(NewickTokenType.LENGTH, location);
				token.setLength(value);
				branchLengthExpected = false;
				return token;
			}
		}
	}

	
	private NewickToken readComment() throws IOException {
		StreamLocation location = new StreamLocation(reader);
		reader.read();  // Skip COMMENT_START.
		StringBuilder buffer = new StringBuilder();
		while ((reader.peek() != -1) && (reader.peekChar() != COMMENT_END)) {
			buffer.append(reader.readChar());
		}
		
		if (reader.peek() == -1) {
			throw new JPhyloIOReaderException("Unexpected end of file inside a Newick comment.", reader);
		}
		else {
			reader.read();  // Skip COMMENT_END.
			String text = buffer.toString();
			String lowerCase = text.trim().toLowerCase();
			if (lowerCase.equals(UNROOTED_HOT_COMMENT)) {
				return new NewickToken(NewickTokenType.UNROOTED_COMMAND, location);
			}
			else if (lowerCase.equals(ROOTED_HOT_COMMENT)) {
				return new NewickToken(NewickTokenType.ROOTED_COMMAND, location);
			}
			else {
				NewickToken result = new NewickToken(NewickTokenType.COMMENT, location);
				result.setText(text);
				return result;
			}
		}
	}
	
	
	private NewickToken readNextToken() throws IOException {
		reader.readRegExp("\\s*", true);  // Skip whitespace. Can be removed, when ConsumeWhiteSpaceAndComments is called above.
		
		if (reader.peek() == -1) {
			return null; 
		}
		else if (branchLengthExpected) {
			return readBranchLength();
		}
		else {
			switch (reader.peekChar()) {
			  case SUBTREE_START:
			  	reader.read();
			  	return new NewickToken(NewickTokenType.SUBTREE_START, reader);
			  case SUBTREE_END:
			  	reader.read();
			  	return new NewickToken(NewickTokenType.SUBTREE_END, reader);
			  case LENGTH_SEPERATOR:
			  	branchLengthExpected = true;
			  	reader.read(); // skip LENGTH_SEPERATOR
			  	return readBranchLength();
			  case NAME_DELIMITER:
			  case ALTERNATIVE_NAME_DELIMITER:
			  	return readDelimitedName(reader.peekChar());
			  case TERMINAL_SYMBOL:
			  	reader.read();
			  	return new NewickToken(NewickTokenType.TERMNINAL_SYMBOL, reader);
			  case ELEMENT_SEPERATOR:
			  	reader.read();  // Skip element separator
			  	return new NewickToken(NewickTokenType.ELEMENT_SEPARATOR, reader);
			  case COMMENT_START:
			  	return readComment();
			  default:
			    if (isFreeNameChar(reader.peekChar())) {
			    	return readFreeName();
			    }
			    else {  // Whitespaces have been consumed before.
			    	throw new JPhyloIOReaderException("Unexpected token '" + reader.peekChar() + "'.", reader);
			    }
			}
		}
	}
	
	
	public NewickToken peek() throws IOException {
		// ensureFirstEvent() is called in hasMoreTokens()
		if (!hasMoreTokens()) {  //
			throw new NoSuchElementException("The end of the document was already reached.");
		}
		else {
			return next;
		}
	}


	private void ensureFirstEvent() throws IOException {
		if (beforeFirstAccess) {
			next = readNextToken();
			beforeFirstAccess = false;
		}
	}
	
	
	public boolean hasMoreTokens() throws IOException {
		ensureFirstEvent();
		return next != null;
	}

	
	public NewickToken nextToken() throws IOException {
		// ensureFirstEvent() is called in hasMoreTokens()
		if (!hasMoreTokens()) {  //
			throw new NoSuchElementException("The end of the document was already reached.");
		}
		else {
			previous = next;  // previous needs to be set before readNextEvent() is called, because it could be accessed in there.
			if (!readSequence && NewickTokenType.TERMNINAL_SYMBOL.equals(previous.getType())) {
				next = null;
			}
			else {
				next = readNextToken();
			}
			return previous;
		}
	}
}
