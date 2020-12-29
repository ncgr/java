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


import info.bioinfweb.jphyloio.AbstractEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.receivers.BasicEventReceiver;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;



/**
 * Used internally by {@link FASTAEventWriter} to write sequence tokens.
 * 
 * @author Ben St&ouml;ver
 */
class FASTASequenceEventReceiver extends BasicEventReceiver<TextWriterStreamDataProvider<FASTAEventWriter>> implements JPhyloIOEventReceiver, FASTAConstants {
	private int charsPerLineWritten = 0;
	private MatrixDataAdapter matrixDataAdapter;
	private long lineLength;
	private boolean allowCommentsBeforeTokens = false;
	private boolean continuedCommentExpected = false;


	public FASTASequenceEventReceiver(TextWriterStreamDataProvider<FASTAEventWriter> streamDataProvider,
			ReadWriteParameterMap parameterMap, MatrixDataAdapter matrixDataAdapter, long lineLength) {
		super(streamDataProvider, parameterMap);
		this.matrixDataAdapter = matrixDataAdapter;
		this.lineLength = lineLength;
	}


	public int getCharsPerLineWritten() {
		return charsPerLineWritten;
	}


	public boolean isAllowCommentsBeforeTokens() {
		return allowCommentsBeforeTokens;
	}


	public void setAllowCommentsBeforeTokens(boolean allowCommentsBeforeTokens) {
		this.allowCommentsBeforeTokens = allowCommentsBeforeTokens;
	}


	protected void writeNewLine(Writer writer) throws IOException {
		AbstractEventWriter.writeLineBreak(writer, getParameterMap());
		charsPerLineWritten = 0;
	}
	
	
	private void writeToken(String token) throws IOException {
		if (matrixDataAdapter.containsLongTokens(getParameterMap())) {
			token += " "; 
		}
		else if (token.length() > 1) {
			throw new IllegalArgumentException("The specified string representation of one or more of token(s) is longer "
					+ "than one character, although this reader is set to not allow longer tokens.");
		}
		if (charsPerLineWritten + token.length() > lineLength) {
			writeNewLine(getStreamDataProvider().getWriter());
		}
		getStreamDataProvider().getWriter().write(token);
		charsPerLineWritten += token.length();
	}
	
	
	private void writeTokens(Collection<String> tokens) throws IOException {
		Iterator<String> tokenIterator = tokens.iterator();
		while (tokenIterator.hasNext()) {
			writeToken(tokenIterator.next());
		}
	}
	
	
	private void writeComment(CommentEvent commentEvent) throws IOException {
		if (!continuedCommentExpected) {  // Writing starts in the previous comment line
			getStreamDataProvider().getWriter().write(COMMENT_START_CHAR);
		}
		getStreamDataProvider().getWriter().write(commentEvent.getContent());
		continuedCommentExpected = commentEvent.isContinuedInNextEvent();
		if (!continuedCommentExpected) {
			writeNewLine(getStreamDataProvider().getWriter());
		}
	}
	
	
	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		if (isAllowCommentsBeforeTokens()) {
			writeComment(event.asCommentEvent());
		}
		else {
			addIgnoredComments(1);
		}
	}


	@Override
	public boolean doAdd(JPhyloIOEvent event) throws IllegalArgumentException, IOException {
		if (continuedCommentExpected && !EventContentType.COMMENT.equals(event.getType().getContentType())) {
			throw new IllegalArgumentException("The previous event was a comment event indicating that it would be continued in this "
					+ "event, but this event was of type " + event.getType());
		}
		
		boolean tokenWritten = false;
		switch (event.getType().getContentType()) {
			case SEQUENCE_TOKENS:
				SequenceTokensEvent tokensEvent = event.asSequenceTokensEvent();
				if (!tokensEvent.getTokens().isEmpty()) {
					writeTokens(event.asSequenceTokensEvent().getTokens());
					tokenWritten = true;
				}
				break;
			case SINGLE_SEQUENCE_TOKEN:
				if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
					writeToken(event.asSingleSequenceTokenEvent().getToken());
					tokenWritten = true;
				}  // End events can be ignored.
				break;
			default:
				throw new IllegalArgumentException("Events of the type " + event.getType().getContentType() + 
						" are not allowed in a sequence content subsequence.");
		}
		
		if (tokenWritten) {  // Allow comments only at the beginning of a sequence. (If writeSequencePartContentData() was called with a start index > 0, allowCommentsBeforeTokens should have been false before this receiver was passed.
			setAllowCommentsBeforeTokens(false);
		}
		return true;
	}
}
