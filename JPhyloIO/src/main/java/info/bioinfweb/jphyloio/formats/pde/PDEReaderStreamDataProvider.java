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


import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * The XML stream data provider used by {@link PDEEventReader}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class PDEReaderStreamDataProvider extends XMLReaderStreamDataProvider<PDEEventReader>{
	private int alignmentLength;
	private int sequenceCount;
	private int currentSequenceIndex;
	private int currentSequenceLength;
	
	private String otuListID;
	
	private String currentAlignmentID;
	private boolean createAlignmentStart;
	private boolean createAlignmentEnd;
	private String currentSequenceID;
	private Map<Integer, String> sequenceIndexToOTUID = new HashMap<Integer, String>();
	
	private List<Map<Integer, String>> sequenceInformations = new ArrayList<Map<Integer,String>>();	
	private Map<Integer, PDEMetaColumnDefintion> metaColumns = new HashMap<Integer, PDEMetaColumnDefintion>();
	
	
	public PDEReaderStreamDataProvider(PDEEventReader eventReader) {
		super(eventReader);
	}


	public int getAlignmentLength() {
		return alignmentLength;
	}


	public void setAlignmentLength(int alignmentLength) {
		this.alignmentLength = alignmentLength;
	}


	protected int getSequenceCount() {
		return sequenceCount;
	}


	protected void setSequenceCount(int sequenceCount) {
		this.sequenceCount = sequenceCount;
	}


	public String getOtuListID() {
		return otuListID;
	}


	public void setOtuListID(String otuListID) {
		this.otuListID = otuListID;
	}


	/**
	 * Returns the ID of the characters element (representing an alignment) that is currently read.
	 * 
	 * @return the current alignment ID
	 */
	public String getCurrentAlignmentID() {
		return currentAlignmentID;
	}


	public void setCurrentAlignmentID(String currentAlignmentID) {
		this.currentAlignmentID = currentAlignmentID;
	}
	
	
	protected boolean isCreateAlignmentStart() {
		return createAlignmentStart;
	}


	protected void setCreateAlignmentStart(boolean createAlignmentStart) {
		this.createAlignmentStart = createAlignmentStart;
	}
	

	protected boolean isCreateAlignmentEnd() {
		return createAlignmentEnd;
	}


	protected void setCreateAlignmentEnd(boolean createAlignmentEnd) {
		this.createAlignmentEnd = createAlignmentEnd;
	}


	protected String getCurrentSequenceID() {
		return currentSequenceID;
	}


	protected void setCurrentSequenceID(String currentSequenceID) {
		this.currentSequenceID = currentSequenceID;
	}


	public Map<Integer, String> getSequenceIndexToOTUID() {
		return sequenceIndexToOTUID;
	}


	public int getCurrentSequenceIndex() {
		return currentSequenceIndex;
	}


	public void setCurrentSequenceIndex(int currentSequenceIndex) {
		this.currentSequenceIndex = currentSequenceIndex;
	}


	protected int getCurrentSequenceLength() {
		return currentSequenceLength;
	}


	protected void setCurrentSequenceLength(int currentSequenceLength) {
		this.currentSequenceLength = currentSequenceLength;
	}


	public List<Map<Integer, String>> getSequenceInformations() {
		return sequenceInformations;
	}


	public Map<Integer, PDEMetaColumnDefintion> getMetaColumns() {
		return metaColumns;
	}
}
