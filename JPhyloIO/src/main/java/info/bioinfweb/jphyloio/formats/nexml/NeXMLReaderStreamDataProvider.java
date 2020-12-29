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
package info.bioinfweb.jphyloio.formats.nexml;


import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleSequenceTokenEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.BufferedEventInfo;
import info.bioinfweb.jphyloio.formats.nexml.elementreader.AbstractNeXMLElementReader;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;


/**
 * Stores data that shall be shared among different implementations of {@link AbstractNeXMLElementReader}
 * reading from the same document.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLReaderStreamDataProvider extends XMLReaderStreamDataProvider<NeXMLEventReader> {	
	private boolean allowLongTokens;
	
	private EnumMap<EventContentType, String> elementTypeToCurrentIDMap = new EnumMap<EventContentType, String>(EventContentType.class);
	
	private Stack<EventContentType> metaType = new Stack<EventContentType>();
	private LiteralContentSequenceType currentLiteralContentSequenceType;
	private String alternativeStringRepresentation;
	private URIOrStringIdentifier additionalResourceMetaRel;
	
	private boolean currentSetIsSupported;
	
	private Map<String, String> otuIDToLabelMap = new TreeMap<String, String>();
	
	private Map<String, NeXMLReaderTokenSetInformation> tokenSets = new TreeMap<String, NeXMLReaderTokenSetInformation>();
	private Map<String, String> tokenDefinitionIDToSymbolMap = new HashMap<String, String>();
	private NeXMLSingleTokenDefinitionInformation currentSingleTokenDefinition;	

	private Map<String, List<String>> tokenSetIDtoColumnsMap = new HashMap<String, List<String>>();
	private List<String> charIDs = new ArrayList<String>();
	private Map<String, Integer> charIDToIndexMap = new HashMap<String, Integer>();
	private Map<String, String> charIDToStatesMap = new HashMap<String, String>();
	
	private Map<String, BufferedEventInfo<SingleSequenceTokenEvent>> currentCellsBuffer = new HashMap<String, BufferedEventInfo<SingleSequenceTokenEvent>>();
	private Iterator<String> currentCharIDIterator = null;
	private String currentExpectedCharID = null;
	private boolean currentCellBuffered = false;
	
	
	public NeXMLReaderStreamDataProvider(NeXMLEventReader nexmlEventReader) {
		super(nexmlEventReader);
	}


	@Override
	public NeXMLEventReader getEventReader() {
		return (NeXMLEventReader)super.getEventReader();
	}
	
	
	public boolean isAllowLongTokens() {
		return allowLongTokens;
	}


	public void setAllowLongTokens(boolean allowLongTokens) {
		this.allowLongTokens = allowLongTokens;
	}

	
	/**
	 * Returns a map that links the currently used ID to an {@link EventContentType}. This map is used to determine a the linked ID of a set.
	 * 
	 * @return the map linking an ID to an {@link EventContentType}
	 */
	public EnumMap<EventContentType, String> getElementTypeToCurrentIDMap() {
		return elementTypeToCurrentIDMap;
	}


	/**
	 * Returns a stack of {@link EventContentType} that represents the currently encountered nested meta events. 
	 * 
	 * @return the stack of EventContentType representing the encountered meta events
	 */
	public Stack<EventContentType> getMetaType() {
		return metaType;
	}

	
	/**
	 * Returns the {@link LiteralContentSequenceType} of the content nested under the current literal meta element. 
	 * 
	 * @return  the {@link LiteralContentSequenceType} of the content nested under the current literal meta element
	 */
	public LiteralContentSequenceType getCurrentLiteralContentSequenceType() {
		return currentLiteralContentSequenceType;
	}


	public void setCurrentLiteralContentSequenceType(LiteralContentSequenceType currentLiteralContentSequenceType) {
		this.currentLiteralContentSequenceType = currentLiteralContentSequenceType;
	}


	/**
	 * Returns the value of the content attribute of the current literal meta element to be used as an alternative 
	 * string representation by a following {@link LiteralMetadataContentEvent}.
	 * 
	 * @return the value of the content attribute of the current literal meta element
	 */
	public String getAlternativeStringRepresentation() {
		return alternativeStringRepresentation;
	}


	public void setAlternativeStringRepresentation(String alternativeStringRepresentation) {
		this.alternativeStringRepresentation = alternativeStringRepresentation;
	}
	
	
	/**
	 * Returns a {link URIOrStringIdentifier} if an additional {@link ResourceMetadataEvent} is to be created to enclose 
	 * meta data of elements without a {@link JPhyloIOEvent} representation or {@code null} if this the enclosing event 
	 * was already generated.
	 * 
	 * @return a {link URIOrStringIdentifier} describing additional metadata or {@code null}
	 */
	public URIOrStringIdentifier getAdditionalResourceMetaRel() {
		return additionalResourceMetaRel;
	}


	public void setAdditionalResourceMetaRel(URIOrStringIdentifier additionalResourceMetaRel) {
		this.additionalResourceMetaRel = additionalResourceMetaRel;
	}


	/**
	 * Returns {@code true} if the set element that is currently read can be interpreted as a series of {@link JPhyloIOEvent}s 
	 * or {@code false} if the set and all its contents will be ignored.
	 * 
	 * @return {@code true} if the set currently read can be interpreted as a series of {@link JPhyloIOEvent}s
	 */
	public boolean isCurrentSetSupported() {
		return currentSetIsSupported;
	}


	public void setCurrentSetIsSupported(boolean currentSetIsSupported) {
		this.currentSetIsSupported = currentSetIsSupported;
	}


	/**
	 * Returns a map that links a label to a certain OTU ID. This map is used to determine a sequence or node label 
	 * from the OTU linked to it, in case no sequence or node label could be found.
	 * 
	 * @return the map linking a label to an OTU ID
	 */
	public Map<String, String> getOTUIDToLabelMap() {
		return otuIDToLabelMap;
	}

	
	/**
	 * Returns a map that links a {@link NeXMLReaderTokenSetInformation} to a certain token set ID. 
	 * This is used to buffer information about the token set until the start event is created and to be able 
	 * to find the right translation map when reading sequences.
	 * 
	 * @return the map linking a {@link NeXMLReaderTokenSetInformation} to an token set ID
	 */
	public Map<String, NeXMLReaderTokenSetInformation> getTokenSets() {
		return tokenSets;
	}


	/**
	 * Returns a map that links a symbol (i.e. the name of a token definition like 'A' for Adenin) to a single token definition ID.
	 * 
	 * @return the map linking a symbol to a single token definition ID
	 */
	public Map<String, String> getTokenDefinitionIDToSymbolMap() {
		return tokenDefinitionIDToSymbolMap;
	}


	/**
	 * Returns the {@link NeXMLSingleTokenDefinitionInformation} instance of the currently read single token definition, 
	 * e.g to add more constituents.
	 * 
	 * @return the {@link NeXMLSingleTokenDefinitionInformation} instance of the currently read single token definition
	 */
	public NeXMLSingleTokenDefinitionInformation getCurrentSingleTokenDefinition() {
		return currentSingleTokenDefinition;
	}


	public void setCurrentSingleTokenDefinition(NeXMLSingleTokenDefinitionInformation currentSingleTokenDefinition) {
		this.currentSingleTokenDefinition = currentSingleTokenDefinition;
	}
	
	
	/**
	 * Returns a map that links a symbol (i.e. the name of a token definition like 'A' for Adenin) to a single token definition ID.
	 * 
	 * @return the map linking a symbol to a single token definition ID
	 */
	public Map<String, List<String>> getTokenSetIDtoColumnsMap() {
		return tokenSetIDtoColumnsMap;
	}	


	/**
	 * Returns a list of column IDs obtained from NeXML char elements.
	 * 
	 * @return a list of column IDs
	 */
	public List<String> getCharIDs() {
		return charIDs;
	}

	
	/**
	 * Returns a map linking column indices to column IDs obtained from NeXML char elements.
	 * 
	 * @return a map linking column indices to column IDs
	 */
	public Map<String, Integer> getCharIDToIndexMap() {
		return charIDToIndexMap;
	}


	/**
	 * Returns a map linking a token set definition ID to a column ID obtained from a NeXML char elements.
	 * 
	 * @return a map linking a token set definition ID to a column ID
	 */
	public Map<String, String> getCharIDToStatesMap() {
		return charIDToStatesMap;
	}	


	/**
	 * The returned map is used to buffer {@link SingleTokenDefinitionEvent}s and their nested events, in cases where the order of
	 * {@code cell} tags does not match the order of the referenced columns.
	 * 
	 * @return the map instance to be used for buffering
	 */
	public Map<String, BufferedEventInfo<SingleSequenceTokenEvent>> getCurrentCellsBuffer() {
		return currentCellsBuffer;
	}
	
	
	/**
	 * Method used for reading cell tags that clears the map of buffered cell informations ({@link #getCurrentCellsBuffer()}) and resets
	 * the columns ID iterator used by {@link #nextCharID()}.
	 */
	public void clearCurrentRowInformation() {
		currentCharIDIterator = null;
		currentExpectedCharID = null;
		currentCellsBuffer.clear();
	}
	
	
	/**
	 * Returns the next NeXML columns ID (ID of a {@code char} tag) at the current position of the underlying iterator and stores it
	 * in the property {@link #getCurrentExpectedCharID()}.
	 * <p>
	 * If this method is called the first time after creation of this object or after a call of {@link #clearCurrentRowInformation()}
	 * a new iterator will be created before returning an event.
	 * 
	 * @return the ID of the next {@code char} tag or {@code null} if no additional columns are available
	 */
	public String nextCharID() {
		if (currentCharIDIterator == null) {
			currentCharIDIterator = getCharIDs().iterator();
		}
		if (currentCharIDIterator.hasNext()) {
			currentExpectedCharID = currentCharIDIterator.next();
		}
		else {
			currentExpectedCharID = null;
		}
		return currentExpectedCharID;
	}


	/**
	 * Returns the column ID that has been the result of the last call of {@link #nextCharID()}.
	 * 
	 * @return the currently expected column ID or {@code null} if no more IDs are to come
	 */
	public String getCurrentExpectedCharID() {
		if (currentExpectedCharID == null) {
			nextCharID();  // Set ID if iterator was not yet created. If the end of the list was reached, the value will remain null.
		}
		return currentExpectedCharID;
	}


	/**
	 * Determines whether the contents of the current {@code cell} tag are buffered or not. Buffering becomes necessary, if the order
	 * of {@code cell} tags differs from the order of column definitions from {@code char} tags.
	 * 
	 * @return {@code true} if the current cell is buffered or {@code false} otherwise
	 */
	public boolean isCurrentCellBuffered() {
		return currentCellBuffered;
	}


	/**
	 * Allow to specify whether the contents of the current {@code cell} tag are buffered or not.
	 * 
	 * @return Specify {@code true} if the current cell is buffered or {@code false} otherwise.
	 */
	public void setCurrentCellBuffered(boolean currentCellBuffered) {
		this.currentCellBuffered = currentCellBuffered;
	}
}
