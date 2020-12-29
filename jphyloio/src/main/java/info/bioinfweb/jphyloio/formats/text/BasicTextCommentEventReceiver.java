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


import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.receivers.BasicEventReceiver;
import info.bioinfweb.jphyloio.events.CommentEvent;



/**
 * Basic event receiver implementation that writes comments to text formats that are delimited by a start and an end string. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class BasicTextCommentEventReceiver<P extends TextWriterStreamDataProvider<? extends AbstractTextEventWriter<P>>> extends BasicEventReceiver<P> {
	private String commentStart;
	private String commentEnd;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param writer the writer to write data to
	 * @param parameterMap the parameter map of the event writer that will be using this receiver instance
	 * @param commentStart a string indicating the start of a comment in the target format
	 * @param commentEnd a string indicating the end of a comment in the target format
	 */
	public BasicTextCommentEventReceiver(P streamDataProvider, ReadWriteParameterMap parameterMap, String commentStart, String commentEnd) {
		super(streamDataProvider, parameterMap);
		this.commentStart = commentStart;
		this.commentEnd = commentEnd;
	}


	public String getCommentStart() {
		return commentStart;
	}


	public String getCommentEnd() {
		return commentEnd;
	}


	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		writeComment(this, event, commentStart, commentEnd);
	}
	
	
	public static void writeComment(BasicEventReceiver<?> receiver, CommentEvent event, String commentStart, String commentEnd) throws IOException {
		Writer writer = ((TextWriterStreamDataProvider<?>)receiver.getStreamDataProvider()).getWriter();
		if (!receiver.isInComment()) {
			writer.write(commentStart);
		}
		
		String content = event.getContent();
		String editedContent = content.replaceAll(Pattern.quote(commentEnd), "");
		writer.write(editedContent);
		if (!content.equals(editedContent)) {
			receiver.getLogger().addWarning("A comment inside a sequence contained one or more comment end symbols used by the target "
					+ "format. The according parts were removed from the comment.");
		}

		if (!event.isContinuedInNextEvent()) {
			writer.write(commentEnd);
		}
	}
}