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
package info.bioinfweb.jphyloio.formats.newick;


import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;

import java.math.BigDecimal;



public class NewickUtils implements NewickConstants {
	public static class ReadElement {
		private String text;
		private Number numericValue;
		private int endPos;
		
		public ReadElement(String text, Number numericValue, int endPos) {
			super();
			this.text = text;
			this.numericValue = numericValue;
			this.endPos = endPos;
		}

		public String getText() {
			return text;
		}

		public Number getNumericValue() {
			return numericValue;
		}

		public int getEndPos() {
			return endPos;
		}
	}
	
	
	public static Number parseNumericValue(String text) {
		text = text.trim();
		try {  //TODO Try Integer and Long before?
			Double result = Double.parseDouble(text);
			if (result.isInfinite()) {
				return new BigDecimal(text);
			}
			else {
				return result;
			}
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	
	private static int consumeElementSeparator(int pos, int end, final String text) throws InvalidObjectSourceDataException {
		while ((pos < end) && (text.charAt(pos) != ELEMENT_SEPERATOR)) {
			if (!Character.isWhitespace(text.charAt(pos))) {
				throw new InvalidObjectSourceDataException("Literal value starting with '" + text.charAt(pos) + 
						"' found although an element separator ('" + ELEMENT_SEPERATOR +	"') was expected.");  //TODO Should InvalidObjectSourceDataExceptions also store the position, so that the calling reader can calculate the file position? (Would be a problem, if line breaks are present.)
			}
			pos++;
		}
		return pos + 1;  // Skip ','
	}
	
	
	public static ReadElement readNextElement(final String text, int start, int end) throws InvalidObjectSourceDataException {
		// This method is similar to NexusEventReader.readNexusWord() but operates on a string.
		
		int pos = start;
		while ((pos < end) && Character.isWhitespace(text.charAt(pos))) {  // Skip leading whitespace.
			pos++;
		}
		
		if (pos < end) {
			StringBuilder buffer = new StringBuilder();
			if ((text.charAt(pos) == NAME_DELIMITER) || (text.charAt(pos) == ALTERNATIVE_NAME_DELIMITER)) {  // Read delimited string
				char nameDelimiter = text.charAt(pos);
				pos++;
				
				while ((pos < end) && (text.charAt(pos) != nameDelimiter)) {  //TODO Handle double delimiters
					buffer.append(text.charAt(pos));
					pos++;
					if ((pos + 2 < end) && (text.charAt(pos) == nameDelimiter) && (text.charAt(pos + 1) == nameDelimiter)) {  // Handle masked delimiters.
						buffer.append(nameDelimiter);
						pos += 2;
					}
				}
				if (text.charAt(pos) == nameDelimiter) {
					pos++;
				}
				
				if (pos <= end) {
					return new ReadElement(buffer.toString(), null, consumeElementSeparator(pos, end, text));  // Delimited strings are never considered numeric.
				}
				else {
					throw new InvalidObjectSourceDataException("Unterminated string constant.");
				}
			}
			else {  // Read undelimited string
				while ((pos < end) && (text.charAt(pos) != ELEMENT_SEPERATOR)) {
					buffer.append(text.charAt(pos));
					pos++;
				}
				
				String bufferText = buffer.toString();
				if (bufferText.length() > 0) {
					return new ReadElement(bufferText, parseNumericValue(bufferText), consumeElementSeparator(pos, end, text));
				}
			}
		}
		return null;
	}	
}
