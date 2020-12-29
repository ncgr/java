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
package info.bioinfweb.jphyloio.formats.nexus;


import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandEventReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.all.BlockTitleToIDMap;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees.NexusTranslationTable;
import info.bioinfweb.jphyloio.formats.text.KeyValueInformation;
import info.bioinfweb.jphyloio.formats.text.TextReaderStreamDataProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;



/**
 * Stores data that shall be shared among different implementations of {@link NexusCommandEventReader}
 * reading from the same document.
 * 
 * @author Ben St&ouml;ver
 */
public class NexusReaderStreamDataProvider extends TextReaderStreamDataProvider<NexusEventReader> implements NexusConstants {
	public static final String INFO_KEY_BLOCK_START_EVENT_FIRED = "info.bioinfweb.jphyloio.nexus.blockStartEventFired";
	
	/** Used to store the ID of the according <i>JPhyloIO</i> event to the current Nexus block (if one exists). */ 
	public static final String INFO_KEY_CURRENT_BLOCK_ID = "info.bioinfweb.jphyloio.nexus.currentBlockID";
	
	/** Used to store the title of the current block if specified by a TITLE command. */
	public static final String INFO_KEY_BLOCK_TITLE = "info.bioinfweb.jphyloio.nexus.blockTitle";
	
	public static final String INFO_KEY_BLOCK_LINKS = "info.bioinfweb.jphyloio.nexus.blockLinks";
	public static final String INFO_KEY_BLOCK_ID_MAP = "info.bioinfweb.jphyloio.nexus.taxa.blockTitleToIDMap";
	public static final String INFO_KEY_ELEMENT_LIST = "info.bioinfweb.jphyloio.nexus.elementlist";
	public static final String INFO_KEY_NEXUS_NAME_TO_ID_MAP = "info.bioinfweb.jphyloio.nexus.nameToIDMap";
	public static final String INFO_CHARACTER_NAME_TO_INDEX_MAP = "info.bioinfweb.jphyloio.nexus.characters.characterNameToIndexMap";
	public static final String INFO_KEY_TREES_TRANSLATION = "info.bioinfweb.jphyloio.nexus.trees.translate";
	
	/** Used to Determine the lengths of character set intervals that shall reach until the end of a matrix. */
	public static final String INFO_KEY_MATRIX_WIDTHS_MAP = "info.bioinfweb.jphyloio.nexus.matrixWidths";
	
	
	private ParameterMap sharedInformationMap = new ParameterMap();
	
	
	public NexusReaderStreamDataProvider(NexusEventReader nexusReader) {
		super(nexusReader);
	}


	@Override
	public NexusEventReader getEventReader() {
		return (NexusEventReader)super.getEventReader();
	}


	/**
	 * Calls {@link #consumeWhiteSpaceAndComments(char, char)} with according parameters and
	 * ensures visibility for {@link NexusReaderStreamDataProvider}.
	 * 
	 * @throws IOException if an I/O error occurs during the read operation
	 */
	public void consumeWhiteSpaceAndComments() throws IOException {
		getEventReader().consumeWhiteSpaceAndComments();
	}
	
	
	public String readNexusWord() throws IOException {
		return getEventReader().readNexusWord();
	}
	
	
	/**
	 * Tries to read a positive integer from the current position of the underlying stream-
	 * 
	 * @param startOrEndIndex the value to be returned, if {@code '.'} is encountered (It is used as a placeholder 
	 *        for the highest possible index in some commands.)
	 * @return the read positive integer or {@code startOrEndIndex} or -2 if neither a positive integer nor {@code '.'} was found
	 * @throws IOException
	 */
	public long readPositiveInteger(long startOrEndIndex) throws IOException {
		return getEventReader().readPositiveInteger(startOrEndIndex);
	}
	
	
	public void readComment() throws IOException {
		getEventReader().readComment();
	}
	
	
	public KeyValueInformation readKeyValueMetaInformation() throws IOException {
		return getEventReader().readKeyValueMetaInformation();
	}
	
	
	/**
	 * This map can be used to store objects to be shared between different instances of 
	 * {@link NexusCommandEventReader}.
	 * 
	 * @return a map providing access to shared data objects
	 */
	public ParameterMap getSharedInformationMap() {
		return sharedInformationMap;
	}
	
	
	/**
	 * Returns the map that contains links to other blocks that are explicitly linked to this block.
	 * <p>
	 * The returned map is used internally in <i>JPhyloIO</i> to manage links of the current block that are defined with a 
	 * {@code LINK} command. It should never be used to determine a currently linked block, but {@link #getCurrentLinkedBlockID(String)}
	 * should be used instead. 
	 * 
	 * @return the map containing the blocks explicitly linked to the current block
	 */
	public Map<String, String> getBlockLinks() {
		@SuppressWarnings("unchecked")
		Map<String, String> result = (Map<String, String>)getSharedInformationMap().get(INFO_KEY_BLOCK_LINKS);  // Casting null is possible.
		if (result == null) {
			result = new TreeMap<String, String>();
			getSharedInformationMap().put(INFO_KEY_BLOCK_LINKS, result);
		}
		return result;
	}
	
	
	/**
	 * Returns the ID if a {@code CHARACTERS}, {@code DATA} or {@code UNALIGNED} block that is linked to the current block.
	 * If none of these blocks is explicitly linked, returning a default linked block will be tried in this order. If no default
	 * linked block is found, {@code null} will be returned. 
	 * 
	 * @return a linked block containing sequences or {@code null}
	 */
	public String getMatrixLink() {
		Map<String, String> map = getBlockLinks();
		String result = map.get(BLOCK_NAME_CHARACTERS);
		if (result == null) {
			result = map.get(BLOCK_NAME_DATA);
			if (result == null) {
				result = map.get(BLOCK_NAME_UNALIGNED);
				
				if (result == null) {
					result = getBlockTitleToIDMap().getDefaultBlockID(BLOCK_NAME_CHARACTERS);
					if (result == null) {
						result = getBlockTitleToIDMap().getDefaultBlockID(BLOCK_NAME_DATA);
						if (result == null) {
							result = getBlockTitleToIDMap().getDefaultBlockID(BLOCK_NAME_UNALIGNED);
						}
					}
				}
			}
		}  //TODO Should an exception be thrown if more than one link is defined in a SETS block?
		return result;
	}
	
	
	public void clearBlockInformation() {
		getSharedInformationMap().remove(INFO_KEY_BLOCK_TITLE);
		getSharedInformationMap().remove(INFO_KEY_BLOCK_LINKS);
		getSharedInformationMap().remove(INFO_KEY_CURRENT_BLOCK_ID);
		getSharedInformationMap().remove(INFO_KEY_BLOCK_START_EVENT_FIRED);
	}
	
	
	/**
	 * Gets a map object from the shared information map. If no such object if found, a new {@link HashMap} instance is stored under
	 * the specified key and returned. 
	 * 
	 * @param key the key in the shared information map
	 * @return the map object contained in the shared information map
	 */
	@SuppressWarnings("rawtypes")
	public Map<?, ?> getMap(String key) {
		Map result = (Map)getSharedInformationMap().get(key);  // Casting null is possible.
		if (result == null) {
			result = new HashMap();
			getSharedInformationMap().put(key, result);
		}
		return result;
	}
	
	
	/**
	 * Returns a map object that can translate Nexus block titles to the IDs of the according <i>JPhyloIO</i> start events.
	 * The returned object can also determine default block links.
	 * 
	 * @return the translation map
	 */
	public BlockTitleToIDMap getBlockTitleToIDMap() {
		BlockTitleToIDMap result = getSharedInformationMap().getObject(INFO_KEY_BLOCK_ID_MAP, null, BlockTitleToIDMap.class);
		if (result == null) {
			result = new BlockTitleToIDMap();
			getSharedInformationMap().put(INFO_KEY_BLOCK_ID_MAP, result);  // If an object of another type is stored under this key, getObject() would also return null and it will be overwritten here. 
		}
		return result;
	}
	
	
	/**
	 * Returns the <i>JPhyloIO</i> ID of the start event describing the Nexus block of the specified type that is linked to the current 
	 * block. It will first look for an explicitly linked block in {@link #getBlockLinks()}. If no link is found, the default link
	 * from {@link BlockTitleToIDMap#getDefaultBlockID(String)} will be returned.
	 * 
	 * @param blockTypeName the name of the Nexus block type that could be linked
	 * @return the start event ID or {@code null} if no according block could be found
	 */
	public String getCurrentLinkedBlockID(String blockTypeName) {
		String result = getBlockLinks().get(blockTypeName);
		if (result == null) {
			result = getBlockTitleToIDMap().getDefaultBlockID(blockTypeName);
		}
		return result;
	}
	
	
	public List<String> getElementList(EventContentType type, String listID) {
		if (listID == null) {
			throw new NullPointerException("The specified listID must not be null.");
		}
		else {
			String key = INFO_KEY_ELEMENT_LIST + "." + type.toString() + "." + listID;
			@SuppressWarnings("unchecked")
			List<String> result = (List<String>)getSharedInformationMap().get(key);  // Casting null is possible.
			if (result == null) {
				result = new ArrayList<String>();
				getSharedInformationMap().put(key, result);
			}
			return result;
		}
	}
	
	
	/**
	 * Returns a map object that translates between a Nexus name and the <i>JPhyloIO</i> ID of the according event.
	 * 
	 * @param type the event type of events of the requested ID
	 * @param listID the <i>JPhyloIO</i> ID of the according OTU list start event
	 * 
	 * @return the map
	 * @throws NullPointerException if {@code listID is null}
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getNexusNameToIDMap(EventContentType type, String listID) {
		if (listID == null) {
			throw new NullPointerException("The specified listID must not be null.");
		}
		else {
			return (Map<String, String>)getMap(INFO_KEY_NEXUS_NAME_TO_ID_MAP + "." + type.toString() + "." + listID);
		}
	}
	
	
	/**
	 * Returns the current Nexus translation table object. If none is present, a new empty instance will be created and stored in
	 * the shared information map.
	 * 
	 * @return the current table
	 */
	public NexusTranslationTable getTreesTranslationTable() {
		NexusTranslationTable result = (NexusTranslationTable)getSharedInformationMap().get(INFO_KEY_TREES_TRANSLATION);  // Casting null is possible.
		if (result == null) {
			result = new NexusTranslationTable();
			getSharedInformationMap().put(INFO_KEY_TREES_TRANSLATION, result);
		}
		return result;
	}
	
	
	/**
	 * Returns a map object that stores the widths of all sequence matrices encountered until now. It is used by readers of set commands
	 * to determine the last column of a matrix, which may be referred by a set definition.
	 * 
	 * @return the map 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Long> getMatrixWidthsMap() {
		return (Map<String, Long>)getMap(INFO_KEY_MATRIX_WIDTHS_MAP);
	}
}
