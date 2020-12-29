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


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterAlignmentInformation;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * Receiver that is used to write sequence tokens.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLSequenceTokensReceiver extends NeXMLHandleSequenceDataReceiver {


	public NeXMLSequenceTokensReceiver(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameterMap, boolean longTokens) {
		super(streamDataProvider, parameterMap, longTokens);
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		if (isNestedUnderSingleToken()) {
			AbstractNeXMLDataReceiverMixin.handleLiteralMeta(getStreamDataProvider(), event);
		}
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		if (isNestedUnderSingleToken()) {
			AbstractNeXMLDataReceiverMixin.handleLiteralContentMeta(getStreamDataProvider(), getParameterMap(), event);
		}
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		if (isNestedUnderSingleToken()) {
			AbstractNeXMLDataReceiverMixin.handleResourceMeta(getStreamDataProvider(), event);
		}
	}
	
	
	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		if (isNestedUnderSingleToken()) {
			AbstractNeXMLDataReceiverMixin.handleMetaEndEvent(getStreamDataProvider(), event);
		}
	}


	@Override
	protected void handleComment(CommentEvent event) throws IOException, XMLStreamException {
		if (!isNestedUnderSingleToken()) {
			AbstractNeXMLDataReceiverMixin.handleComment(getStreamDataProvider(), event);
		}
	}


	@Override
	protected void handleToken(String token, String label) throws XMLStreamException {
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();
		String translatedToken;
		
		if (alignmentInfo.getAlignmentType().equals(CharacterStateSetType.DISCRETE)) {
			translatedToken = alignmentInfo.getIDToTokenSetInfoMap().get(alignmentInfo.getColumnIndexToStatesMap().get(getTokenIndex())).getTokenTranslationMap().get(token);
		}
		else if (alignmentInfo.getAlignmentType().equals(CharacterStateSetType.AMINO_ACID) && (token.length() == 3)) {		
			translatedToken = Character.toString(SequenceUtils.oneLetterAminoAcidByThreeLetter(token));			
		}
		else {
			translatedToken = token;
		}
		
		if (alignmentInfo.isWriteCellsTags()) {
			getStreamDataProvider().getWriter().writeStartElement(TAG_CELL.getLocalPart());
			if (label != null) {
				getStreamDataProvider().getWriter().writeAttribute(ATTR_LABEL.getLocalPart(), label);
			}
			getStreamDataProvider().setSingleToken(translatedToken);
		}		
		else {
			getStreamDataProvider().getWriter().writeCharacters(translatedToken);
			if (isLongTokens()) {
				getStreamDataProvider().getWriter().writeCharacters(" ");
			}
		}
		
		setTokenIndex(getTokenIndex() + 1);
	}


	@Override
	protected void handleTokenEnd() throws XMLStreamException {
		if (getStreamDataProvider().getCurrentAlignmentInfo().isWriteCellsTags()) {
			String token = getStreamDataProvider().getSingleToken();			
			if (token != null) {
				getStreamDataProvider().getWriter().writeCharacters(token);
				getStreamDataProvider().setSingleToken(null);
			}			
			getStreamDataProvider().getWriter().writeEndElement();
		}		
	}
}
