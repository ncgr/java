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


import java.util.Collection;
import java.util.Collections;

import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.commons.bio.CharacterSymbolType;
import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Event that indicates the definition of a single new valid token that could be contained in a sequence 
 * of the current alignment. 
 * <p>
 * Not all formats support or require a token definition, therefore sequences might contain tokens that 
 * were not previously defined by {@link SingleTokenDefinitionEvent}.   
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SingleTokenDefinitionEvent extends LabeledIDEvent {
	private String tokenName;
	private CharacterSymbolMeaning meaning;
	private CharacterSymbolType tokenType;
	private Collection<String> constituents = null;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param id the unique ID associated with the represented single token definition (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label an optional label of the modeled single token definition (Maybe {@code null}, if no label is present. This is 
	 *        not the token name.)
	 * @param tokenName the string representation of the new token
	 * @param meaning the meaning of the new token
	 * @param constituents if this token is an ambiguity or uncertain token, a list of constituents can be specified here
	 *        (Maybe {@code null}.)
	 * @throws NullPointerException if {@code null} is specified for any of the arguments
	 */
	public SingleTokenDefinitionEvent(String id, String label, String tokenName, CharacterSymbolMeaning meaning, 
			CharacterSymbolType tokenType, Collection<String> constituents) {
		
		super(EventContentType.SINGLE_TOKEN_DEFINITION, id, label);
		
		if (tokenName == null) {
			throw new NullPointerException("The token name must not be null.");
		}
		else if (meaning == null) {
			throw new NullPointerException("The token meaning must not be null.");
		}
		else {
			this.tokenName = tokenName;
			this.meaning = meaning;
			this.tokenType = tokenType;
			
			if (constituents != null) {
				this.constituents = Collections.unmodifiableCollection(constituents);
			}
		}
	}


	/**
	 * Creates a new instance of this class without a constituents collection.
	 * 
	 * @param tokenName the string representation of the new token
	 * @param meaning the meaning of the new token
	 * @throws NullPointerException if {@code null} is specified for any of the arguments
	 */
	public SingleTokenDefinitionEvent(String id, String label, String tokenName, CharacterSymbolMeaning meaning, CharacterSymbolType tokenType) {
		this(id, label, tokenName, meaning, tokenType, null);
	}
	
	
	/**
	 * Returns the string representation of the new token.
	 * 
	 * @return the string representation of one or more characters in length 
	 */
	public String getTokenName() {
		return tokenName;
	}


	/**
	 * Returns the meaning of the new token.
	 * 
	 * @return the meaning
	 */
	public CharacterSymbolMeaning getMeaning() {
		return meaning;
	}

	
	/**
	 * Returns the type of the new token (e.g. UNCERTAIN if the token definition is an ambiguity code).
	 * 
	 * @return the tokenType
	 */
	public CharacterSymbolType getTokenType() {
		return tokenType;
	}


	/**
	 * Returns a collection of tokens that are constituents of this token (e.g. in case of ambiguity codes) or null, if no constituents are specified.
	 * 
	 * @return a collection of constituents or null
	 */
	public Collection<String> getConstituents() {
		return constituents;
	}


	/**
	 * Returns true if a collection of constituents is specified for this token or false if it is not.
	 * 
	 * @return true if this token has constituents that are specified or false if not
	 */
	public boolean hasConstituents() {
		if (constituents != null) {
			return true;
		}
		else {
			return false;
		}
	}
}
