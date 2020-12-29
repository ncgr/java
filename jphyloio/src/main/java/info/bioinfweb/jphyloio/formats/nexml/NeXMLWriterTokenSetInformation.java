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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * Class that is used by {@link NeXMLEventWriter} to store information about a token set. Since different 
 * information is needed while writing a document, NeXML writers use this class instead of {@link NeXMLReaderTokenSetInformation}.
 * 
 * @author Sarah Wiechers
 *
 */
public class NeXMLWriterTokenSetInformation {
	private boolean isNucleotideType = false;
	private Set<String> singleTokenDefinitions = new HashSet<String>();
	private Map<String, String> tokenTranslationMap = new HashMap<String, String>();
	private Set<String> occuringTokens = new HashSet<String>();


	public boolean isNucleotideType() {
		return isNucleotideType;
	}

	
	public void setNucleotideType(boolean isNucleotideType) {
		this.isNucleotideType = isNucleotideType;
	}


	public Set<String> getSingleTokenDefinitions() {
		return singleTokenDefinitions;
	}


	public Map<String, String> getTokenTranslationMap() {
		return tokenTranslationMap;
	}


	public Set<String> getOccuringTokens() {
		return occuringTokens;
	}
}