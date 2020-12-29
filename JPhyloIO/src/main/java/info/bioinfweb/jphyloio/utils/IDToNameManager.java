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
package info.bioinfweb.jphyloio.utils;


import java.util.HashMap;
import java.util.Map;

import info.bioinfweb.commons.LongIDManager;



/**
 * Allows to assign IDs to labels, without having multiple IDs for the same label.
 * This class can only be used for unique labels.
 * 
 * @author Ben St&ouml;ver
 */
public class IDToNameManager {
	private String prefix;
	private LongIDManager longIDManager = new LongIDManager();
	private Map<String, String> sequenceLabelToIDMap = new HashMap<String, String>();
	
	
	public IDToNameManager(String prefix) {
		super();
		this.prefix = prefix;
	}


	/**
	 * Makes sure that only one ID as assigned to each sequence Nexus name.
	 * 
	 * @param sequenceLabel
	 * @return
	 */
	public String getID(String sequenceLabel) {
		String result = sequenceLabelToIDMap.get(sequenceLabel);
		if (result == null) {
			result = prefix + longIDManager.createNewID();
			sequenceLabelToIDMap.put(sequenceLabel, result);
		}
		return result;
	}
}
