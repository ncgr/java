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
package info.bioinfweb.jphyloio.objecttranslation.implementations;


import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;



/**
 * Abstract base class for converting simple types that can be described by strings and do not need a more complex
 * XML representation.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 *
 * @param <O> the type of Java object this translator instance is able to handle
 */
public abstract class SimpleValueTranslator<O> extends AbstractObjectTranslator<O> {
	public static final int MAX_STRING_REPRESENTATION_LENGTH = 16 * 1024;
	
	
	@Override
	public boolean hasStringRepresentation() {
		return true;
	}
	

	/**
	 * Converts the object by invoking its {@link Object#toString()} method.
	 * 
	 * @param object the object to be converted
	 * @return the string representation of the object 
	 * @see info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator#javaToRepresentation(java.lang.Object, WriterStreamDataProvider)
	 */
	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider) throws UnsupportedOperationException, ClassCastException {
		return object.toString();
	}
	

	/**
	 * Writes the string representation of the specified object to an XML stream. (Calls {@link #javaToRepresentation(Object, WriterStreamDataProvider)} 
	 * internally.)
	 * 
	 * @param writer the XML stream writer
	 * @param object the object to be converted
	 */
	@Override
	public void writeXMLRepresentation(XMLStreamWriter writer, Object object, WriterStreamDataProvider<?> streamDataProvider) throws IOException, XMLStreamException {
		writer.writeCharacters(javaToRepresentation(object, streamDataProvider));
	}

	
	/**
	 * Reads an object from the character data available at the current position of the XML reader. Character data is consumed until
	 * an event different from {@link XMLStreamConstants#CHARACTERS} is consumed. If no event of this type is available, parsing the 
	 * empty string is tried. (Calls {@link #representationToJava(String, ReaderStreamDataProvider)} internally.)
	 * 
	 * @param reader the XML event reader to read the data from
	 * @return the parsed object
	 * 
	 * @throws JPhyloIOReaderException if the textual data found at the current position is longer than 
	 *         {@link #MAX_STRING_REPRESENTATION_LENGTH}
	 */
	@Override
	public O readXMLRepresentation(XMLEventReader reader, ReaderStreamDataProvider<?> streamDataProvider) throws IOException,	XMLStreamException, InvalidObjectSourceDataException {
		StringBuilder text = new StringBuilder();
		while (reader.peek().isCharacters()) {
			if (text.length() > MAX_STRING_REPRESENTATION_LENGTH) {  // Avoid loading very large amounts of (invalid) data.
				throw new JPhyloIOReaderException("The text to parse a simple value from is longer than " + MAX_STRING_REPRESENTATION_LENGTH + 
						" characters. Reading is aborted.", reader.peek().getLocation());
			}
			text.append(reader.nextEvent().asCharacters().getData());
		}
		return representationToJava(text.toString(), streamDataProvider);
	}
}
