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


import java.util.ArrayList;
import java.util.List;



/**
 * This class is used by {@link TreeTopologyExtractor} to model a node in a phylogenetic tree or network.
 * 
 * @see TreeTopologyExtractor
 * @author Sarah Wiechers
 *
 */
public class TopoplogicalNodeInfo {
	private String parentNodeID;
	private List<String> childNodeIDs = new ArrayList<String>();
	private String afferentBranchID;
	
	
	public TopoplogicalNodeInfo() {
		super();
	}


	public String getParentNodeID() {
		return parentNodeID;
	}
	
	
	public void setParentNodeID(String parentNodeID) {
		this.parentNodeID = parentNodeID;
	}
	
	
	public String getAfferentBranchID() {
		return afferentBranchID;
	}
	
	
	public void setAfferentBranchID(String afferentBranchID) {
		this.afferentBranchID = afferentBranchID;
	}
	
	
	public List<String> getChildNodeIDs() {
		return childNodeIDs;
	}	
}
