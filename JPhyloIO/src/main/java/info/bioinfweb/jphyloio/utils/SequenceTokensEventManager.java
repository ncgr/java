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


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.SequenceTokensEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Class used by implementations of {@link JPhyloIOEventReader} to keep track of the current alignment
 * position and replacing match tokens by the according tokens from the first sequence. 
 * 
 * @author Ben St&ouml;ver
 */
public class SequenceTokensEventManager {
	private JPhyloIOEventReader owner;
	private String matchToken;
	private List<String> firstSequence;
	private List<String> unmodifiableFirstSequence;
	private long currentPosition = 0;  //TODO Does -1 need to be specified here? If so, this would be inconsistent with the initial block start.
	private long currentBlockStartPosition = 0;
	private long currentBlockLength = 0;
	private String firstSequenceName = null;
	private String currentSequenceName = null;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the PhyloIO event reader using the returned instance
	 * @param matchToken the match token to be replaced in sequences or {@code null} if no replacement shall be performed
	 * @throws NullPointerException if {@code owner} is set to {@code null}
	 */
	public SequenceTokensEventManager(JPhyloIOEventReader owner, String matchToken) {
		super();
		if (owner == null) {
			throw new NullPointerException("owner cannot be null.");
		}
		else {
			this.owner = owner;
			this.matchToken = matchToken;
			firstSequence = new ArrayList<String>();
			unmodifiableFirstSequence = Collections.unmodifiableList(firstSequence);
		}
	}


	/**
	 * Returns the reader instance using this object.
	 * 
	 * @return the reader class (never {@code null})
	 */
	public JPhyloIOEventReader getOwner() {
		return owner;
	}
	
	
	public String getMatchToken() {
		return matchToken;
	}


	public void setMatchToken(String matchToken) {
		this.matchToken = matchToken;
	}


	public long getCurrentPosition() {
		return currentPosition;
	}


	public long getCurrentBlockStartPosition() {
		return currentBlockStartPosition;
	}


	public long getCurrentBlockLength() {
		return currentBlockLength;
	}


	public String getFirstSequenceName() {
		return firstSequenceName;
	}


	public String getCurrentSequenceName() {
		return currentSequenceName;
	}
	
	
	public List<String> getFirstSequence() {
		return unmodifiableFirstSequence;
	}


	private String replaceMatchToken(String token) {
		if (token.equals(matchToken)) {
			if (currentPosition > Integer.MAX_VALUE) {
				throw new IndexOutOfBoundsException("Sequences with more than " + Integer.MAX_VALUE + 
						" characters are not supported if replacing match tokens is switched on."); 
			}
			else if (currentPosition >= firstSequence.size()) {
				throw new IndexOutOfBoundsException("The match token in column " + currentPosition + 
						" cannot be replaced because the first sequence only has " + firstSequence.size() + " characters."); 
			}
			else {
				return firstSequence.get((int)currentPosition);
			}
		}
		else {
			return token;
		}
	}
	
	
	/**
	 * Creates a sequence character event object from the provided data and manages the replacement of match tokens
	 * by tokens of the first sequence.
	 * <p>
	 * This method stores the tokens of the first sequence and the current sequence position. Therefore implementing 
	 * readers supporting match token replacement must always use this method to create sequence character event 
	 * objects and never do this directly, otherwise the replacement by this method will not work.
	 * <p>
	 * Note that the algorithm for position monitoring will only work for interleaved formats where each sequence part
	 * has the same length in each block or for non-interleaved formats (where each sequence only occurs once).
	 * 
	 * @param sequenceID the event ID or another unique name of the sequence to append the tokens to
	 * @param tokens the newly read tokens
	 * @return the event object
	 * @throws NullPointerException if either {@code sequenceID} or {@code tokens} is {@code null}
	 */
	public SequenceTokensEvent createEvent(String sequenceID, List<String> tokens) {
		if ((sequenceID == null) || (tokens == null)) {
			throw new NullPointerException("Sequence names must not be null.");
		}
		else {
			boolean sequenceNameChange = !sequenceID.equals(currentSequenceName);
			if (sequenceNameChange) {
				currentSequenceName = sequenceID;
				currentPosition = currentBlockStartPosition;  // Will be 0, until the second interleaved block is reached. (Therefore this implementation also works for non-interleaved data, where the sequences have an unequal length.) 
			}
			if (firstSequenceName == null) {
				firstSequenceName = sequenceID;
			}
			
			if (firstSequenceName.equals(sequenceID)) {
				firstSequence.addAll(tokens);
				if (sequenceNameChange) {
					currentBlockStartPosition += currentBlockLength;  // Add length of previous block that is now finished.
					currentBlockLength = tokens.size();  // Save length of current block to add it to the start after it was processed.
					currentPosition = currentBlockStartPosition;
				}
				else {
					currentBlockLength += tokens.size();  // Add additional length, if one block is split into separate events.
				}
				currentPosition += tokens.size();
			}
			else if (matchToken != null) {
				for (int i = 0; i < tokens.size(); i++) {
					tokens.set(i, replaceMatchToken(tokens.get(i)));
					currentPosition++;
				}
			}
			else {
				currentPosition += tokens.size();
			}
			return new SequenceTokensEvent(tokens);
		}
	}
}
