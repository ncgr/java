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
package info.bioinfweb.jphyloio.objecttranslation;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



/**
 * Classes implementing this interface are able to convert between Java Objects and their text or XML representation.
 * <p>
 * Instances of implementations can be obtained using {@link ObjectTranslatorFactory}, which also allows to register
 * custom implementations (e.g. provided in application code). Instances of {@link JPhyloIOEventReader} and 
 * {@link JPhyloIOEventWriter} make use of object translators to read and write literal metadata objects. The factory
 * to be used can be specified using the parameter {@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}.  
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 *
 * @param <O> the type of Java object this translator instance is able to handle
 * @see ObjectTranslatorFactory
 * @see ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY
 */
public interface ObjectTranslator<O> {
	//TODO How are object values represented in events, if they are read from a whole sequence of XML-events?
	//     Is it an alternative to either output the XMLEvents as objects values or directly parse them to output one single object value
	//     or should parsing of XML not at all be supported? => Probably the first alternative.
	
	/**
	 * Returns the Java class of object instances created by this translator.
	 * <p>
	 * Note that {@link #javaToRepresentation(Object, WriterStreamDataProvider)} and {@link #writeXMLRepresentation(XMLStreamWriter, Object, WriterStreamDataProvider)} may also 
	 * accept instances of other classes. This is independent from the return value here.
	 * 
	 * @return the class of the handled objects
	 */
	public Class<O> getObjectClass();
	//TODO Should subclasses always be allowed here?
	//TODO Should it be possible to support more than one class?
	
	/**
	 * Determines whether the objects handled by this instance have a simple string representation or need a more complex
	 * XML representation.
	 * <p>
	 * Instances that return {@code false here} will throw an {@link UnsupportedOperationException}, if 
	 * {@link #javaToRepresentation(Object, WriterStreamDataProvider)} is called.
	 * 
	 * @return {@code true} if handled objects have a simple string representation or {@code false} if XML is necessary to
	 *         represent the handled objects
	 */
	public boolean hasStringRepresentation();
	
	/**
	 * Converts the specified Java object to its string representation.
	 * 
	 * @param object the object to be translated
	 * @param streamDataProvider TODO
	 * @return the string representation of the object
	 * @throws UnsupportedOperationException if objects handled by this instance can only be represented as XML
	 * @throws ClassCastException if the specified object is not an instance of the supported class or does not implement the supported
	 *         interface
	 * @see #hasStringRepresentation()
	 */
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider) throws UnsupportedOperationException, ClassCastException;
	
	/**
	 * Writes the XML representation of the specified object into the specified XML writer.
	 * 
	 * @param writer the writer to be used to write the XML representation
	 * @param object the object to be converted
	 * @param streamDataProvider TODO
	 * @throws IOException if an I/O error occurs while trying to write to the specified writer
	 * @throws XMLStreamException if an XML stream exception occurs while trying to write to the specified writer
	 */
	public void writeXMLRepresentation(XMLStreamWriter writer, Object object, WriterStreamDataProvider<?> streamDataProvider) throws IOException, XMLStreamException, ClassCastException;
	
	/**
	 * Converts the specified string representation to a new instance of the according Java object.
	 * <p>
	 * If {@link #getClass()} returns an interface for this instance, the concrete class of the returned object may 
	 * depend on the representation. 
	 * 
	 * @param representation the string representation of the object to be created
	 * @param streamDataProvider the stream data provider of the calling reader (Maybe {@code null}. Some translators will use it to gain 
	 *        additional status information required for translating, e.g. prefix to namespace mapping for creating QNames.)
	 * @return the new object
	 * @throws UnsupportedOperationException if objects handled by this instance can only be represented as XML
	 * @throws InvalidObjectSourceDataException if the specified string representation cannot be parsed to a supported object
	 */
	public O representationToJava(String representation, ReaderStreamDataProvider<?> streamDataProvider) throws InvalidObjectSourceDataException, UnsupportedOperationException;

	/**
	 * Tries to create a new instance of the handled object type from the data provided by the specified XML reader.
	 * <p>
	 * This method will start reading from the current position of the reader and read only as far as necessary to collect all
	 * data for the new object. Therefore the reader should be positioned before the start tag, that represents the object and
	 * will read until the according end tag was consumed. If the supported objects have a simple string representation, the 
	 * reader should be positioned in front of the according characters.
	 * <p>
	 * If this 
	 * 
	 * @param reader the XML reader providing the data to create a new object
	 * @param streamDataProvider TODO
	 * @return the new object
	 * @throws IOException if an I/O error occurs while trying to read from the specified reader
	 * @throws XMLStreamException if an XML stream exception occurs while trying to read from the specified reader
	 * @throws InvalidObjectSourceDataException if an unexpected XML event was encountered or an XML event has unexpected contents
	 */
	public O readXMLRepresentation(XMLEventReader reader, ReaderStreamDataProvider<?> streamDataProvider) throws IOException, XMLStreamException, InvalidObjectSourceDataException;  //TODO Is inversion of control necessary here instead (e.g. to consume the XML events elsewhere too)?
}