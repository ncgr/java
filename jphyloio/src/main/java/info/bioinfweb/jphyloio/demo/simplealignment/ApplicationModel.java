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
package info.bioinfweb.jphyloio.demo.simplealignment;


import java.util.ArrayList;
import java.util.List;



/**
 * This class acts as an example for an application business model in this demo. It models a multiple sequence alignment
 * which simply consists of a list of objects containing sequence information (a label and a list of sequence tokens).
 * <p>
 * Note that this model can be freely defined by the application and stored no information specific for <i>JPhyloIO</i> 
 * (e.g. sequence IDs).
 * 
 * @author Ben St&ouml;ver
 */
public class ApplicationModel {
	private static class Sequence {
		private String label;
		private List<String> tokens;
		
		public Sequence(String label) {
			super();
			this.label = label;
			tokens = new ArrayList<String>();
		}

		public String getLabel() {
			return label;
		}

		public List<String> getTokens() {
			return tokens;
		}
	}
	
	
	private String label = null;
	private List<Sequence> sequences = new ArrayList<>();
	
	
	/**
	 * Returns the name of this alignment.
	 * 
	 * @return the name of the alignment or {@code null} if no name is specified
	 */
	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	/**
	 * Adds a new empty sequence to this alignment.
	 * 
	 * @param sequenceLabel the label of the new sequence
	 * @return the index the new sequence has in this alignment
	 */
	public int addSequence(String sequenceLabel) {
		sequences.add(new Sequence(sequenceLabel));
		return sequences.size() - 1;
	}
	
	
	public String getSequenceLabel(int index) {
		return sequences.get(index).getLabel();
	}
	
	
	public List<String> getSequenceTokens(int index) {
		return sequences.get(index).getTokens();
	}
	
	
	public int size() {
		return sequences.size();
	}
	
	
	public void clear() {
		label = null;
		sequences.clear();
	}
	
	
	public boolean isEmpty() {
		return (label == null) && sequences.isEmpty();
	}
}
