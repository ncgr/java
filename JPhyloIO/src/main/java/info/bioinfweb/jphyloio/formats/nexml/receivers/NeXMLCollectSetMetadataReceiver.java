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
import info.bioinfweb.jphyloio.events.SetElementEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * Receiver that collects information about sets contained in a document.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLCollectSetMetadataReceiver extends NeXMLCollectNamespaceReceiver {
	private String setID;
	private boolean ignoreMetadata;
	

	public NeXMLCollectSetMetadataReceiver(NeXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, String setID, boolean ignoreMetadata) {
		super(streamDataProvider, parameterMap);
		this.setID = setID;
		this.ignoreMetadata = ignoreMetadata;
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		if (!ignoreMetadata) {
			super.handleLiteralMetaStart(event);
		}
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		if (!ignoreMetadata) {
			super.handleResourceMetaStart(event);
		}
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		if (!ignoreMetadata) {
			super.handleLiteralContentMeta(event);
		}
	}


	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		if (!ignoreMetadata) {
			super.handleMetaEndEvent(event);
		}
	}


	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		if (!ignoreMetadata) {
			super.handleComment(event);
		}
	}


	@Override
	protected boolean doAdd(JPhyloIOEvent event) throws IOException, XMLStreamException {
		switch (event.getType().getContentType()) {
			case SET_ELEMENT:
				SetElementEvent setElementEvent = event.asSetElementEvent();
				if (getStreamDataProvider().getSetIDToSetElementsMap().get(setID).containsKey(setElementEvent.getLinkedObjectType())) {
					getStreamDataProvider().getSetIDToSetElementsMap().get(setID).get(setElementEvent.getLinkedObjectType()).add(setElementEvent.getLinkedID());
				}
				else {
					throw new JPhyloIOWriterException("The element \"" + setElementEvent.getLinkedID() + "\" with the type \"" + setElementEvent.getLinkedObjectType() 
							+ "\" can not be written to the current set.");
				}
				break;
			default:
				break;
		}
		return true;
	}
}
