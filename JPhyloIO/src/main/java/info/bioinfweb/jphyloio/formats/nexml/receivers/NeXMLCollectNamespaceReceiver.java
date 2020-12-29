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


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;


/**
 * Receiver that collects namespace definitions occurring in document metadata by calling the according methods of 
 * the {@link AbstractNeXMLDataReceiverMixin}.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLCollectNamespaceReceiver extends AbstractNeXMLDataReceiver {
	
	
	public NeXMLCollectNamespaceReceiver(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameterMap) {
		super(streamDataProvider, parameterMap);
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		AbstractNeXMLDataReceiverMixin.checkLiteralMeta(getStreamDataProvider(), event);
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		AbstractNeXMLDataReceiverMixin.checkResourceMeta(getStreamDataProvider(), event);
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		AbstractNeXMLDataReceiverMixin.checkLiteralContentMeta(getStreamDataProvider(), getParameterMap(), event);
	}


	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {}


	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {}
}
