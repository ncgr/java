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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.all;


import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.newick.NewickConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractKeyValueCommandReader;
import info.bioinfweb.jphyloio.formats.text.KeyValueInformation;



/**
 * Reads the {@code LINK} command of a Nexus block and stores the links in the shared information map of the
 * stream data provider under the key {@link NexusReaderStreamDataProvider#INFO_KEY_BLOCK_LINKS}.
 * <p>
 * Note that the {@code LINK} command is not part of the initial Nexus definition, but was used by Mesquite
 * as a custom command to allow references between blocks using the {@code TITLE} command.
 * <p>
 * This reader is valid for all blocks, therefore {@link #getValidBlocks()} returns an empty collection.
 * <p>
 * <b>Example usage:</b>
 * <pre>
 * #NEXUS
 * 
 * BEGIN TAXA;
 *   <b>TITLE</b> 'taxon list 1';
 *   DIMENSIONS NTAX = 3;
 *   TAXLABELS A B C;
 * END;
 * BEGIN TAXA;
 *   <b>TITLE</b> TaxonList2;
 *   DIMENSIONS NTAX = 3;
 *   TAXLABELS D E F;
 * END;
 * 
 * BEGIN TREES;
 *   <b>LINK</b> TAXA = 'taxon list 1';
 *   TREE someTree = (A, (B, C));
 * END;
 * BEGIN TREES;
 *   <b>LINK</b> TAXA = TaxonList2;
 *   TREE someTree = (D, (E, F));
 * END;
 * </pre>
 * The {@code LINK} command may also be used to link other blocks than the {@code TAXA} block, but <i>JPhyloIO</i> will
 * not use this information.
 * 
 * @author Ben St&ouml;ver
 * @see TitleReader
 */
public class LinkReader extends AbstractKeyValueCommandReader implements NexusConstants {
	private static final Set<String> BLOCK_NAMES_WITH_EVENTS = createBlockNamesWithEvents();
	
	
	private static Set<String> createBlockNamesWithEvents() {
		Set<String> result = new TreeSet<String>();
		result.add(BLOCK_NAME_TAXA);  // All constants must be upper case characters.
		result.add(BLOCK_NAME_CHARACTERS);
		result.add(BLOCK_NAME_DATA);
		result.add(BLOCK_NAME_UNALIGNED);
		result.add(BLOCK_NAME_TREES);
		return result;
	}
	
	
	public LinkReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_LINK, new String[0], nexusDocument);
	}
	
	
	@Override
	protected boolean processSubcommand(KeyValueInformation info)	throws IOException {
		String value = info.getValue();
		if (!info.wasValueDelimited()) {
			value = value.replace(NewickConstants.FREE_NAME_BLANK, ' ');
		}
		
		String key = info.getOriginalKey().toUpperCase();
		if (BLOCK_NAMES_WITH_EVENTS.contains(key)) {
			String nexusBlockLabel = value;  // Save undelimited value for possible error message.
			value = getStreamDataProvider().getBlockTitleToIDMap().getID(key, value);
			if (value == null) {
				throw new JPhyloIOReaderException("The linked Nexus " + key + " block with the label \"" + 
						nexusBlockLabel +	"\" was not previously declared unsing a TITLE command.", getStreamDataProvider().getDataReader());
			}
		}
		getStreamDataProvider().getBlockLinks().put(key, value);
		return false;
	}
	
	
	@Override
	protected boolean addStoredEvents() {
		return false;
	}
}
