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


import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Event that indicates a defined character (alignment column).
 * <p>
 * Events of this type are optional and most readers will create them only if a character label or
 * associated metadata is present or if an ID has explicitly been defined in the document that is read.
 * <p>
 * Note that only the property {@link #getIndex()} defines the index of the modeled alignment column. There are not necessarily
 * events fired for each column, which is why the index cannot be determined by counting the encountered events of this type.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class CharacterDefinitionEvent extends LabeledIDEvent {
	private long index;  //TODO Should a BigInteger be used here or in an optional additional property? (The latter could be done later without API change.)

	
	public CharacterDefinitionEvent(String id, String label, long index) {
		super(EventContentType.CHARACTER_DEFINITION, id, label);
		this.index = index;
	}


	public long getIndex() {
		return index;
	}
}
