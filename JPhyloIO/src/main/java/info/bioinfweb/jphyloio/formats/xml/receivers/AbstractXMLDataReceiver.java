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
package info.bioinfweb.jphyloio.formats.xml.receivers;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.receivers.BasicEventReceiver;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLEventWriter;
import info.bioinfweb.jphyloio.formats.xml.XMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * Abstract implementation of a data receiver that can be used to write XML formats.
 * <p>
 * Functionality implemented here is commonly used by inheriting receivers.
 * 
 * @author Sarah Wiechers
 *
 */
public abstract class AbstractXMLDataReceiver<P extends XMLWriterStreamDataProvider<? extends AbstractXMLEventWriter<P>>> 
		extends BasicEventReceiver<P> {
	
	public AbstractXMLDataReceiver(P streamDataProvider, ReadWriteParameterMap parameterMap) {
		super(streamDataProvider, parameterMap);
	}

	
	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		String comment = event.getContent();
		
		if (!comment.isEmpty()) {
			getStreamDataProvider().getCommentContent().append(comment);
		}
		
		if (!event.isContinuedInNextEvent()) {
			getStreamDataProvider().getWriter().writeComment(getStreamDataProvider().getCommentContent().toString());
			getStreamDataProvider().getCommentContent().delete(0, getStreamDataProvider().getCommentContent().length());			
		}
	}
}
