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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;



/**
 * Abstract implementation of {@link NexusBlockHandler} that stores a set of block names
 * in an unmodifiable collection.
 * <p>
 * Application developers implementing custom block handlers will usually want to inherit these from this class.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class AbstractNexusBlockHandler implements NexusBlockHandler {
	private Collection<String> blockNames;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param blockNames the block names the implementing class is associated with
	 */
	public AbstractNexusBlockHandler(String[] blockNames) {
		super();
		ArrayList<String> list = new ArrayList<String>(blockNames.length);
		for (int i = 0; i < blockNames.length; i++) {
			list.add(blockNames[i].toUpperCase());
		}
		this.blockNames = Collections.unmodifiableCollection(list);
	}


	@Override
	public Collection<String> getBlockNames() {
		return blockNames;
	}
}
