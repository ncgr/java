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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders;


import info.bioinfweb.jphyloio.formats.nexus.NexusEventReader;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.all.LinkReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.all.TitleReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.CharLabelsReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.CharStateLabelsReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.DimensionsReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.FormatReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters.MatrixReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.sets.CharSetReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.sets.TaxSetReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.sets.TreeSetReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.taxa.TaxLabelsReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees.TranslateReader;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees.TreeReader;

import java.util.Map;
import java.util.TreeMap;



/**
 * A factory class that creates instance of {@link NexusCommandEventReader}. Each <i>Nexus</i> command can have a 
 * specific reader, which can be created by an instance of this class. The readers that can be created by a factory 
 * instance depend on the registered reader classes (see below). 
 * <p>
 * An instance of this class needs to be passed to the constructor of {@link NexusEventReader}. The readers added to
 * this factory determine the <i>Nexus</i> commands that will be supported by that reader instance. 
 * <p>
 * Any class of command reader can be added to this factory to be associated with a certain <i>Nexus</i> command in a 
 * certain <i>Nexus</i> block. The method {@link #addJPhyloIOReaders()} can be used to add all command readers that 
 * are defined in <i>JPhyloIO</i> to an instance. If additional custom readers are defined on application level, 
 * these can be added using {@link #addReaderClass(Class)}.
 * 
 * @author Ben St&ouml;ver
 */
public class NexusCommandReaderFactory {
	public static final String ALL_BLOCKS_IDENTIFIER = "ALL BLOCKS";  // A space will not be included in any other Nexus block name.
	public static final char BLOCK_COMMAND_CONNECTOR = '.';

	
	private Map<String, Class<? extends NexusCommandEventReader>> readers = 
			new TreeMap<String, Class<? extends NexusCommandEventReader>>();

	
	/**
	 * Returns a new instance of this class, already containing all command readers available in
	 * the core module of <i>JPhyloIO</i>. ({@link #addJPhyloIOReaders()} is called internally.)
	 * 
	 * @return the new instance
	 */
	public static NexusCommandReaderFactory newJPhyloIOInstance() {
		NexusCommandReaderFactory result = new NexusCommandReaderFactory();
		result.addJPhyloIOReaders();
		return result;
	}
	
	
	/**
	 * Creates a new empty instance of this class.
	 * 
	 * @see #addJPhyloIOReaders()
	 * @see #newJPhyloIOInstance()
	 */
	public NexusCommandReaderFactory() {
		super();
	}


	/**
	 * Adds all <i>Nexus</i> command readers available in <i>JPhyloIO</i> to this instance.
	 */
	public void addJPhyloIOReaders() {
		addReaderClass(TitleReader.class);
		addReaderClass(LinkReader.class);
		addReaderClass(TaxLabelsReader.class);
		addReaderClass(DimensionsReader.class);
		addReaderClass(CharLabelsReader.class);
		addReaderClass(CharStateLabelsReader.class);
		addReaderClass(FormatReader.class);
		addReaderClass(MatrixReader.class);
		addReaderClass(TranslateReader.class);
		addReaderClass(TreeReader.class);
		addReaderClass(TaxSetReader.class);
		addReaderClass(CharSetReader.class);
		addReaderClass(TreeSetReader.class);
		// add new classes here
	}
	
	
	private NexusCommandEventReader createReaderInstance(Class<? extends NexusCommandEventReader> readerClass, 
			NexusReaderStreamDataProvider streamDataProvider) throws IllegalArgumentException {
		
		try {
			return readerClass.getConstructor(NexusReaderStreamDataProvider.class).newInstance(streamDataProvider);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Unable to create instance with single " + NexusReaderStreamDataProvider.class.getName() + 
					" argument. The specified reader class does not seem to offer the appropriate constructor.", e);
		}
	}
	
	
	/**
	 * Adds a new type of reader to this factory instance.
	 * 
	 * @param readerClass the class of the Nexus command reader type to be added
	 * @throws IllegalArgumentException if the specified class does not offer an constructor as described in 
	 *         {@link NexusCommandEventReader}
	 */
	public void addReaderClass(Class<? extends NexusCommandEventReader> readerClass) throws IllegalArgumentException {
		NexusCommandEventReader reader = createReaderInstance(readerClass, null);
		if (reader.getValidBlocks().isEmpty()) {  // Valid in all blocks.
			readers.put(ALL_BLOCKS_IDENTIFIER + BLOCK_COMMAND_CONNECTOR + reader.getCommandName().toUpperCase(), readerClass);
		}
		else {
			for (String blockName : reader.getValidBlocks()) {
				readers.put(blockName.toUpperCase() + BLOCK_COMMAND_CONNECTOR + reader.getCommandName().toUpperCase(), readerClass);
			}
		}
	}
	
	
	/**
	 * Creates a new reader instance that is able to parse the specified command in the specified block, if
	 * an according class was registered in this factory instance.
	 * 
	 * @param blockName the name of the block the command to parse is contained in
	 * @param commandName the name of the command to be parsed
	 * @param streamDataProvider the stream and data provider to be used by the returned reader
	 * @return the reader or {@code null} if no according reader was found in this factory
	 */
	public NexusCommandEventReader createReader(String blockName, String commandName, NexusReaderStreamDataProvider streamDataProvider) {
		Class<? extends NexusCommandEventReader> readerClass = readers.get(blockName.toUpperCase() + BLOCK_COMMAND_CONNECTOR + 
				commandName.toUpperCase());
		
		if (readerClass == null) {  // Try to get a reader valid in all blocks.
			readerClass = readers.get(ALL_BLOCKS_IDENTIFIER + BLOCK_COMMAND_CONNECTOR +	commandName.toUpperCase());
		}
		
		if (readerClass == null) {
			return null;
		}
		else {
			try {
				return createReaderInstance(readerClass, streamDataProvider);
			}
			catch (IllegalArgumentException e) {
				throw new InternalError("The according reader class does not offer an contructor appropriate contructor.");
			}
		}
	}
}
