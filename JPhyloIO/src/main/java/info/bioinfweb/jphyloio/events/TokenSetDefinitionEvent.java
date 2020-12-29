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
package info.bioinfweb.jphyloio.events;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Event that indicates that a (predefined) definition of a character state set was found in the parsed file.
 * <p>
 * This event may or may not occur in combination with one or more {@link SingleTokenDefinitionEvent}, which 
 * define single tokens (e.g. nucleotides or the gap character) used in this set. If no or only a few of the tokens 
 * implied by the return value of {@link #getSetType()} are defined by nested events, the remaining one are still 
 * assumed to be part of the tokens set.
 * <p>
 * JPhyloIO enumerates some standard token sets in {@link TokenSetType}. Some formats might define additional
 * sets which would be represented as {@link TokenSetType#UNKNOWN}. Application developers would have to rely on 
 * {@link #getLabel()} in such cases, to determine the meaning.
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers
 */
public class TokenSetDefinitionEvent extends LabeledIDEvent {
	private CharacterStateSetType setType;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param type the meaning of the token set as defined by {@link TokenSetType}
	 * @param id the document-wide unique ID associated with the represented token set (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label a name describing this token set
	 * @throws NullPointerException if {@code null} is specified for {@code type} 
	 */
	public TokenSetDefinitionEvent(CharacterStateSetType type, String id, String label) {
		super(EventContentType.TOKEN_SET_DEFINITION, id, label);

		if (type == null) {
			throw new NullPointerException("The set type must not be null.");
		}
		else {
			this.setType = type;
		}
	}

	
	/**
	 * Returns the meaning of the the new character state set.
	 * 
	 * @return the meaning of the token set as defined by {@link TokenSetType}
	 */
	public CharacterStateSetType getSetType() {
		return setType;
	}
}
