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


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.receivers.AbstractSequenceContentReceiver;
import info.bioinfweb.jphyloio.events.CommentEvent;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * Receiver that writes all encountered sequence tokens and comments to the current line and ignores all metadata.
 * Comments can also be ignored, of {@code null} is specified as the comment start token.
 * <p>
 * It will log an according warning, if comments need to be edited (e.g. because they contain character that are
 * reserved in the target format).
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class TextSequenceContentReceiver<P extends TextWriterStreamDataProvider<? extends AbstractTextEventWriter<P>>> 
		extends AbstractSequenceContentReceiver<P> {
	
	private String commentStart;
	private String commentEnd;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param writer the writer to write the sequence to
	 * @param parameterMap the parameter map specified to the calling event writer
	 * @param commentStart a comment start token or {@code null}, if this writer shall ignore comment events
	 * @param commentEnd a comment end token or {@code null}, if this writer shall ignore comment events
	 * @param longTokens Specify {@code true} here if sequence tokens shall be separated by spaces (if one token may
	 *        be longer than one character) or {@code false} otherwise.
	 */	
	protected void handleToken(String token, String label) throws IOException {
		getStreamDataProvider().getWriter().write(token);
		if (isLongTokens()) {
			getStreamDataProvider().getWriter().write(' ');
		}
	}


	public TextSequenceContentReceiver(P streamDataProvider, ReadWriteParameterMap parameterMap, boolean longTokens,
			String commentStart, String commentEnd) {
		
		super(streamDataProvider, parameterMap, longTokens);
		this.commentStart = commentStart;
		this.commentEnd = commentEnd;
	}


	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		if (commentStart != null) {
			BasicTextCommentEventReceiver.writeComment(this, event, commentStart, commentEnd);
		}
		else {
			addIgnoredComments(1);
		}		
	}
}
