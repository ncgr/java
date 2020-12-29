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
package info.bioinfweb.jphyloio.formats.pde;



/**
 * Models the definition of a metadata column in the PDE format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class PDEMetaColumnDefintion {
	private long index = -1;
	private String name = null;
	private PDEMetaColumnType type = PDEMetaColumnType.UNKNOWN;
	
	
	public PDEMetaColumnDefintion(long index, String name, PDEMetaColumnType type) {
		super();
		this.index = index;
		this.name = name;
		this.type = type;
	}


	public long getIndex() {
		return index;
	}


	public String getName() {
		return name;
	}


	public PDEMetaColumnType getType() {
		return type;
	}
}
