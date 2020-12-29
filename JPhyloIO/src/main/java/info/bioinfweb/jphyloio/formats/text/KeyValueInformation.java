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
package info.bioinfweb.jphyloio.formats.text;



/**
 * Used internally by {@link AbstractTextEventReader} and inherited classes to store contents of commands
 * containing key value information.
 * 
 * @author Ben St&ouml;ver
 */
public class KeyValueInformation {
	private String originalKey;
	private String value;
	private char delimiter;
	
	
	public KeyValueInformation(String originalKey, String value, char delimiter) {
		super();
		this.originalKey = originalKey;
		this.value = value;
		this.delimiter = delimiter;
	}
	
	
	/**
	 * Returns the key as it was written from the source stream (file).
	 * 
	 * @return the original key
	 */
	public String getOriginalKey() {
		return originalKey;
	}
	

	/**
	 * Returns the value as it was written from the source stream (file).
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	
	/**
	 * Returns the delimiter that was used to delimit the value of this instance.
	 * 
	 * @return the delimiter or {@code ' '} if the value was not delimited
	 */
	public char getDelimiter() {
		return delimiter;
	}
	
	
	/**
	 * Determines whether the read value was delimited in the source stream.
	 * 
	 * @return {@code true}, if the value was delimited or {@code false} otherwise.
	 */
	public boolean wasValueDelimited() {
		return getDelimiter() != ' ';
	}
}
