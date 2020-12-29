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
package info.bioinfweb.jphyloio.formats.nexml.elementreader;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.collections.PackedObjectArrayList;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLConstants;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexml.TokenTranslationStrategy;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.AbstractXMLElementReader;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLElementReader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Processes a NeXML tag without any of its subelements.
 * <p>
 * Methods provided in this class are commonly used by different element readers.
 * 
 * @author Sarah Wiechers
 */
public abstract class AbstractNeXMLElementReader extends AbstractXMLElementReader<NeXMLReaderStreamDataProvider> 
		implements XMLElementReader<NeXMLReaderStreamDataProvider>, NeXMLConstants, ReadWriteConstants {
	
	
	/**
	 * Contains information about a {@link LabeledIDEvent}.
	 */
	protected static class LabeledIDEventInformation {
		public String id;
		public String label;
	}
	
	
	/**
	 * Contains information about a {@link LinkedLabeledIDEvent}.
	 */
	protected static class OTUorOTUsEventInformation extends LabeledIDEventInformation {
		public String otuOrOtusID;
	}
	
	
	/**
	 * Parses a sequence string to a list of single tokens by using the provided {@link TokenTranslationStrategy}.
	 * <p>
	 * The sequence may contain tokens longer than one character in case of continuous or standard data.
	 *
	 * @param streamDataProvider the stream data provider of the calling {@link NeXMLEventReader}
	 * @param sequence the sequence as a string of tokens
	 * @param translateTokens the {@link TokenTranslationStrategy} to be applied 
	 * @return the list of tokens obtained from the sequence
	 * @throws JPhyloIOReaderException
	 * @throws XMLStreamException
	 */
	protected List<String> readSequence(NeXMLReaderStreamDataProvider streamDataProvider, String sequence, TokenTranslationStrategy translateTokens) throws JPhyloIOReaderException, XMLStreamException {		
		List<String> tokenList = new ArrayList<String>();
		String lastToken = "";
   	String currentToken = "";
		Character currentChar;
		
		if (streamDataProvider.isAllowLongTokens()) {  // Continuous and standard data
			if (streamDataProvider.getIncompleteToken() != null) {
				currentToken = streamDataProvider.getIncompleteToken();
				streamDataProvider.setIncompleteToken(null);
			}
			
			for (int i = 0; i < sequence.length(); i++) {
	 			currentChar = sequence.charAt(i);	 			
	 			if (!Character.isWhitespace(currentChar)) {
	 				currentToken += currentChar;
	 			}
	 			else {
	 				if (!currentToken.isEmpty()) {
	 					tokenList.add(currentToken);
					}		 				
	 				currentToken = "";
	 			}	   		
	 		}
			lastToken = currentToken;
			
			if (!Character.isWhitespace(sequence.charAt(sequence.length() - 1))) {
				//TODO Had the catch block here (deleted in r1600) another function? 
				XMLEvent nextEvent = streamDataProvider.getXMLReader().peek();
				if ((nextEvent != null) && (nextEvent.getEventType() == XMLStreamConstants.CHARACTERS) && 
						!Character.isWhitespace(nextEvent.asCharacters().getData().charAt(0))) {
					
					streamDataProvider.setIncompleteToken(lastToken);
				}
				else if (!currentToken.isEmpty()) {
					tokenList.add(currentToken);
				}
			}			

			if (streamDataProvider.getCharacterSetType().equals(CharacterStateSetType.DISCRETE) && !translateTokens.equals(TokenTranslationStrategy.NEVER)) {  // Standard data
				for (int i = 0; i < tokenList.size(); i++) {
					if (!tokenList.get(i).equals("" + SequenceUtils.GAP_CHAR) && !tokenList.get(i).equals("" + SequenceUtils.MISSING_DATA_CHAR)) {
			 			try {
			 				int standardToken = Integer.parseInt(tokenList.get(i));
			 				tokenList.set(i, streamDataProvider.getTokenSets().get(streamDataProvider.getCharIDToStatesMap().get(
			 						streamDataProvider.getCharIDs().get(i))).getSymbolTranslationMap().get(standardToken));
			 			}
			 			catch (NumberFormatException e) {
			 				throw new JPhyloIOReaderException("The symbol \"" + tokenList.get(i) + 
			 						"\" of a standard data token definition must be of type Integer.", streamDataProvider.getXMLReader().peek().getLocation());
			 			}
					}
				}		 		
			}
		}
		
		else {  // DNA, RNA, AA & restriction data
			for (int i = 0; i < sequence.length(); i++) {
				currentChar = sequence.charAt(i);
				if (!Character.isWhitespace(currentChar)) {
					tokenList.add(currentChar.toString());
				}
	 		}
		}

   	return tokenList;
	}
	
	
	/**
	 * Creates a series of {@link CharacterSetIntervalEvent}s from an array of IDs referencing {@link CharacterDefinitionEvent}s 
	 * and adds them to the current event collection.
	 * 
	 * @param streamDataProvider the stream data provider of the calling {@link NeXMLEventReader}
	 * @param charIDs an array of IDs referencing {@link CharacterDefinitionEvent}s
	 * @throws JPhyloIOReaderException
	 * @throws XMLStreamException
	 */
	protected void createIntervalEvents(NeXMLReaderStreamDataProvider streamDataProvider, String[] charIDs) throws JPhyloIOReaderException, XMLStreamException {
		PackedObjectArrayList<Boolean> columns = new PackedObjectArrayList<Boolean>(2, streamDataProvider.getCharIDs().size());
		for (int i = 0; i < streamDataProvider.getCharIDs().size(); i++) {
			columns.add(false);
		}
		
		for (String charID: charIDs) {
			if (streamDataProvider.getCharIDToIndexMap().containsKey(charID)) {
				columns.set(streamDataProvider.getCharIDToIndexMap().get(charID), true);
			}
			else {
				throw new JPhyloIOReaderException("A character set referenced the ID \"" + charID + "\" of a character that was not specified before.", streamDataProvider.getXMLReader().peek().getLocation());
			}
		}
		
		int currentIndex = -1;
		int startIndex = -1;
		
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i)) {
				currentIndex = i;
			}
			
			if ((i == 0) || (columns.get(i) && !columns.get(i - 1))) {
				startIndex = i;
			}
			
			if (columns.get(i) && ((i + 1 == columns.size()) || !columns.get(i + 1))) {
				streamDataProvider.getCurrentEventCollection().add(new CharacterSetIntervalEvent(startIndex, currentIndex + 1)); //the end of a character set interval is specified as the first index after the end of the sequence segment to be add to the specified character set
			}
		}
	}
	
	
	/**
	 * Obtains information about the ID and label associated with a {@link StartElement} by reading its attributes. 
	 * <p>
	 * If no ID can be found a default ID is created in case of {@link NeXMLConstants#TAG_META} and {@link NeXMLConstants#TAG_CHAR}. 
	 * In all other cases an ID attribute needs to provide a valid ID or a {@link JPhyloIOReaderException} will be thrown.
	 * 
	 * @param streamDataProvider the stream data provider of the calling {@link NeXMLEventReader}
	 * @param element the {@link StartElement} to obtain the information from
	 * @return the {@link LabeledIDEventInformation} containing information about the given {@link StartElement}
	 * @throws JPhyloIOReaderException
	 */
	protected LabeledIDEventInformation getLabeledIDEventInformation(NeXMLReaderStreamDataProvider streamDataProvider, StartElement element) throws JPhyloIOReaderException {
		LabeledIDEventInformation labeledIDEventInformation = new LabeledIDEventInformation();
		labeledIDEventInformation.id = XMLUtils.readStringAttr(element, ATTR_ID, null);
		labeledIDEventInformation.label = XMLUtils.readStringAttr(element, ATTR_LABEL, null);		
		
		if ((labeledIDEventInformation.id == null) || !org.semanticweb.owlapi.io.XMLUtils.isNCName(labeledIDEventInformation.id)) {
			if (element.getName().equals(TAG_META)) {  // NeXML meta elements are not required to specify a valid ID (though they usually do)
				labeledIDEventInformation.id = RESERVED_ID_PREFIX + DEFAULT_META_ID_PREFIX + streamDataProvider.getIDManager().createNewID();
			}
			else if (element.getName().equals(TAG_CHAR)) {  // In some cases NeXML char elements might only specify a character index instead of an ID
				labeledIDEventInformation.id = RESERVED_ID_PREFIX + DEFAULT_CHARACTER_DEFINITION_ID_PREFIX + streamDataProvider.getIDManager().createNewID();
			}
		}
		
		if (labeledIDEventInformation.id != null) {
			return labeledIDEventInformation;
		}
		else {			
			throw new JPhyloIOReaderException("The element \"" + element.getName().getLocalPart() + "\" must specify an ID.", element.getLocation());			
		}
	}
	
	
	/**
	 * Obtains information about the ID, label and linked OTU or OTU list associated with a {@link StartElement} by reading its attributes. 
	 * <p>
	 * If no label is associated with this start element, the label of the linked element will be returned as a label. 
	 * If the linked element does not have a label either, the elemnent's ID will can used as a label, depending on 
	 * {@link ReadWriteParameterMap#KEY_USE_OTU_LABEL}.
	 *  
	 * @param streamDataProvider the stream data provider of the calling {@link NeXMLEventReader}
	 * @param element the {@link StartElement} to obtain the information from
	 * @return the {@link OTUorOTUsEventInformation} containing information about the given {@link StartElement}
	 * @throws JPhyloIOReaderException
	 */
	protected OTUorOTUsEventInformation getOTUorOTUsEventInformation(NeXMLReaderStreamDataProvider streamDataProvider, StartElement element) throws JPhyloIOReaderException {
		LabeledIDEventInformation labeledIDEventInformation = getLabeledIDEventInformation(streamDataProvider, element);
		OTUorOTUsEventInformation otuEventInformation = new OTUorOTUsEventInformation();
		
		otuEventInformation.id = labeledIDEventInformation.id;
		otuEventInformation.label = labeledIDEventInformation.label;
		otuEventInformation.otuOrOtusID = XMLUtils.readStringAttr(element, ATTR_SINGLE_OTU_LINK, null);
		
		if (otuEventInformation.otuOrOtusID == null) {
			otuEventInformation.otuOrOtusID = XMLUtils.readStringAttr(element, ATTR_OTUS, null);
		}
		
		// If no label is present in the element, the OTU label (if present) can be used as a label
		if ((otuEventInformation.label == null) && (otuEventInformation.otuOrOtusID != null) 
				&& streamDataProvider.getParameters().getBoolean(ReadWriteParameterMap.KEY_USE_OTU_LABEL, false)) {
			otuEventInformation.label = streamDataProvider.getOTUIDToLabelMap().get(otuEventInformation.otuOrOtusID);
		}
		
		return otuEventInformation;
	}
	
	
	public static CharacterSymbolMeaning parseStateMeaning(String symbol) {
		CharacterSymbolMeaning result = null;
		if (symbol.length() == 1) {
			result = CharacterSymbolMeaning.meaningByDefaultSymbol(symbol.charAt(0));
		}
		if (result == null) {
			result = CharacterSymbolMeaning.CHARACTER_STATE;
		}
		return result;
	}
}
