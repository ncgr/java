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
package info.bioinfweb.jphyloio.formats.nexus.blockhandlers;


import java.util.Collection;

import info.bioinfweb.jphyloio.formats.nexus.NexusEventReader;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandEventReader;



/**
 * A <i>Nexus</i> block handler implements methods that shall be executed by {@link NexusEventReader}
 * each time an according block {@code BEGIN} or {@code END} command is read.
 * <p>
 * Application developers implementing support for custom <i>Nexus</i> blocks can implement this interface
 * for their block is necessary. In most cases it will be useful to inherit such implementations from
 * {@link AbstractNexusBlockHandler}.
 * 
 * @author Ben St&ouml;ver
 * @see AbstractNexusBlockHandler
 * @see NexusEventReader
 */
public interface NexusBlockHandler {
	/**
	 * Returns a collection of the block names this handler supports.
	 * 
	 * @return an umodifyable collection of block names
	 */
	public Collection<String> getBlockNames();
	
	/**
	 * This method will be called by {@link NexusEventReader} each time a {@code BEGIN} command for
	 * a block listed in {@link #getBlockNames()} is read. 
	 * 
	 * @param streamDataProvider the stream data provider of the calling {@link NexusEventReader}
	 * @see NexusEventReader#getStreamDataProvider()
	 */
	public void handleBegin(NexusReaderStreamDataProvider streamDataProvider);

	/**
	 * This method will be called by {@link NexusEventReader} each time an {@code END} command for
	 * a block listed in {@link #getBlockNames()} is read. 
	 * 
	 * @param streamDataProvider the stream data provider of the calling {@link NexusEventReader}
	 * @see NexusEventReader#getStreamDataProvider()
	 */
	public void handleEnd(NexusReaderStreamDataProvider streamDataProvider);
	
	/**
	 * This method is called before a command found in a block handled by this handler is processed,
	 * by calling the according command reader. 
	 * 
	 * @param streamDataProvider the stream data provider of the calling {@link NexusEventReader}
	 * @param commandName the name of the Nexus command, that will be processed next
	 * @param commandReader the reader that will be used to process this command or {@code null} if
	 *        no according reader was found
	 * @see NexusEventReader#getStreamDataProvider()
	 */
	public void beforeCommand(NexusReaderStreamDataProvider streamDataProvider, String commandName, NexusCommandEventReader commandReader);
}
