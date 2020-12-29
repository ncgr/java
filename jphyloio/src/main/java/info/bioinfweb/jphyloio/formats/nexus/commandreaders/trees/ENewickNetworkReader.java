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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees;


import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.blockhandlers.ENewickNetworksBlockHandler;



/**
 * <i>Nexus</i> command reader for the custom {@code NETWORK} command. Some software uses {@code NETWORK} blocks
 * containing such commands, which define phylogenetic networks in the 
 * <a href="http://dx.doi.org/10.1186/1471-2105-9-532">eNewick</i> format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 * @see ENewickNetworksBlockHandler
 */
public class ENewickNetworkReader extends TreeReader {
	public ENewickNetworkReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_NETWORK, new String[]{BLOCK_NAME_NETWORKS}, nexusDocument);
	}

	
	@Override
	protected boolean getExpectENewick() {
		return true;
	}
	

	@Override
	protected EventContentType getElementContentType() {
		return EventContentType.NETWORK;
	}
}
