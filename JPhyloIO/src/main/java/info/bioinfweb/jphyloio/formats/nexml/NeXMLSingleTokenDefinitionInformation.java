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


import java.util.Collection;



/**
 * Class that is used by {@link NeXMLEventReader} to store information about a single token definition.
 * 
 * @author Sarah Wiechers
 *
 */
public class NeXMLSingleTokenDefinitionInformation {
	private String id;
	private String label;
	private String symbol;
	private Collection<String> constituents;
	
	
	public NeXMLSingleTokenDefinitionInformation(String id, String label, String symbol, Collection<String> constituents) {
		super();
		this.id = id;
		this.label = label;
		this.symbol = symbol;
		this.constituents = constituents;
	}

	
	protected String getID() {
		return id;
	}
	

	protected String getLabel() {
		return label;
	}
	

	protected String getSymbol() {
		return symbol;
	}


	protected Collection<String> getConstituents() {
		return constituents;
	}
}
