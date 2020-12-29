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
package info.bioinfweb.jphyloio.formats.phyloxml;


import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;

import java.util.HashMap;
import java.util.Map;



/**
 * The {@link XMLReaderStreamDataProvider} used by {@link PhyloXMLEventReader}.
 * 
 * @author Sarah Wiechers
 */
public class PhyloXMLReaderStreamDataProvider extends XMLReaderStreamDataProvider<PhyloXMLEventReader>  {
	private String treeLabel;	
	private boolean createTreeGroupStart;
	private boolean createPhylogenyStart;
	private boolean createTreeGroupEnd;	
	private String lastNodeID;
	private boolean propertyHasResource;
	private boolean propertyIsURI;
	private boolean resetEventCollection;
	private boolean isFirstContentEvent;
	private Map<String, String> idSourceToEventIDMap = new HashMap<String, String>();
	

	public PhyloXMLReaderStreamDataProvider(PhyloXMLEventReader eventReader) {
		super(eventReader);
	}


	public String getTreeLabel() {
		return treeLabel;
	}


	public void setTreeLabel(String treeLabel) {
		this.treeLabel = treeLabel;
	}


	public boolean isCreateTreeGroupStart() {
		return createTreeGroupStart;
	}


	public void setCreateTreeGroupStart(boolean createTreeGroupStart) {
		this.createTreeGroupStart = createTreeGroupStart;
	}


	public boolean isCreatePhylogenyStart() {
		return createPhylogenyStart;
	}


	public void setCreatePhylogenyStart(boolean createPhylogenyStart) {
		this.createPhylogenyStart = createPhylogenyStart;
	}


	public boolean isCreateTreeGroupEnd() {
		return createTreeGroupEnd;
	}


	public void setCreateTreeGroupEnd(boolean createTreeGroupEnd) {
		this.createTreeGroupEnd = createTreeGroupEnd;
	}


	public String getLastNodeID() {
		return lastNodeID;
	}


	public void setLastNodeID(String lastNodeID) {
		this.lastNodeID = lastNodeID;
	}
	

	public boolean isFirstContentEvent() {
		return isFirstContentEvent;
	}


	public void setFirstContentEvent(boolean isFirstContentEvent) {
		this.isFirstContentEvent = isFirstContentEvent;
	}


	public Map<String, String> getIdSourceToEventIDMap() {
		return idSourceToEventIDMap;
	}


	public boolean isResetEventCollection() {
		return resetEventCollection;
	}


	public void setResetEventCollection(boolean resetEventCollection) {
		this.resetEventCollection = resetEventCollection;
	}


	public boolean isPropertyHasResource() {
		return propertyHasResource;
	}


	public void setPropertyHasResource(boolean propertyHasResource) {
		this.propertyHasResource = propertyHasResource;
	}


	public boolean isPropertyIsURI() {
		return propertyIsURI;
	}


	public void setPropertyIsURI(boolean propertyIsURI) {
		this.propertyIsURI = propertyIsURI;
	}
}
