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
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterAlignmentInformation;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;


/**
 * Receiver that collects information about the sequence tokens contained in a document.
 * <p>
 * It is checked if the document contains tokens with additional information attached (labels or metadata) 
 * (and therefore cells tags will have to be written to the document later on)
 * and which {@link CharacterStateSetType} these tokens indicate.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLCollectSequenceDataReceiver extends NeXMLHandleSequenceDataReceiver {


	public NeXMLCollectSequenceDataReceiver(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameterMap, boolean longTokens) {
		super(streamDataProvider, parameterMap, longTokens);
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		AbstractNeXMLDataReceiverMixin.checkResourceMeta(getStreamDataProvider(), event);
		if (isNestedUnderSingleToken()) {
			getStreamDataProvider().getCurrentAlignmentInfo().setWriteCellsTags(true);
		}
	}


	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		AbstractNeXMLDataReceiverMixin.checkLiteralMeta(getStreamDataProvider(), event);
		if (isNestedUnderSingleToken()) {
			getStreamDataProvider().getCurrentAlignmentInfo().setWriteCellsTags(true);
		}
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		AbstractNeXMLDataReceiverMixin.checkLiteralContentMeta(getStreamDataProvider(), getParameterMap(), event);
		if (isNestedUnderSingleToken()) {
			getStreamDataProvider().getCurrentAlignmentInfo().setWriteCellsTags(true);
		}
	}


	@Override
	protected void handleToken(String token, String label) throws JPhyloIOWriterException {
		//TODO Why is the label parameter never used?
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();
		
		if (!alignmentInfo.hasTokenDefinitionSet()) {  // No token set was contained in the data adapter
			alignmentInfo.getIDToTokenSetInfoMap().get(alignmentInfo.getColumnIndexToStatesMap().get(getTokenIndex())).getOccuringTokens().add(token);
			
			if (alignmentInfo.getTokenSetType().equals(CharacterStateSetType.CONTINUOUS)) {
				try {
					Double.parseDouble(token);
					alignmentInfo.setTokenType(CharacterStateSetType.CONTINUOUS);
				}
				catch (NumberFormatException e) {
					alignmentInfo.setTokenType(CharacterStateSetType.DISCRETE);
				}
			}
			else if (!alignmentInfo.getTokenSetType().equals(CharacterStateSetType.DISCRETE)) {  // Molecular data				
				if (!alignmentInfo.getDefinedTokens().contains(token)) {
					alignmentInfo.setTokenType(CharacterStateSetType.DISCRETE);
				}
			}
		}
		else {
			if (alignmentInfo.getTokenSetType().equals(CharacterStateSetType.DISCRETE)) {
				alignmentInfo.getIDToTokenSetInfoMap().get(alignmentInfo.getColumnIndexToStatesMap().get(getTokenIndex())).getOccuringTokens().add(token);
			}
			else if (alignmentInfo.getTokenSetType().equals(CharacterStateSetType.CONTINUOUS)) {
				try {
					Double.parseDouble(token);  // NeXML schema restricts continuous data to xsd:double. Therefore parsing BigDecimal is unnecessary.
				}
				catch (NumberFormatException e) {
					throw new JPhyloIOWriterException("All tokens in a continuous data characters tag must be numbers.");
				}
			}
			else if (alignmentInfo.getTokenSetType().equals(CharacterStateSetType.AMINO_ACID)) {
				alignmentInfo.getIDToTokenSetInfoMap().get(alignmentInfo.getColumnIndexToStatesMap().get(getTokenIndex())).getOccuringTokens().add(token);
				
				if (!alignmentInfo.getDefinedTokens().contains(token)) { // Token set definitions were read already, so any new tokens here were not defined previously
					if (!((token.length() == 3) && alignmentInfo.getDefinedTokens().contains
							(Character.toString(SequenceUtils.oneLetterAminoAcidByThreeLetter(token))))) {
						alignmentInfo.setTokenType(CharacterStateSetType.DISCRETE);
					}
				}
			}
			else {
				alignmentInfo.getIDToTokenSetInfoMap().get(alignmentInfo.getColumnIndexToStatesMap().get(getTokenIndex())).getOccuringTokens().add(token);

				if (!alignmentInfo.getDefinedTokens().contains(token)) { // Token set definitions were read already, so any new tokens here were not defined previously
					alignmentInfo.setTokenType(CharacterStateSetType.DISCRETE);
				}
			}
		}
		
		setTokenIndex(getTokenIndex() + 1);
	}
}
