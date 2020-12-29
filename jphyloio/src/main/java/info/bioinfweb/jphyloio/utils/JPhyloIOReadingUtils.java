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
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.events.type.EventType;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;



/**
 * Provides tool methods to be used by application developers when implementing a reader class that processes
 * events from an implementations of {@link JPhyloIOEventReader} and stores relevant content in the application
 * business model. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class JPhyloIOReadingUtils {
	private static final EventType LITERAL_META_END = new EventType(EventContentType.LITERAL_META, EventTopologyType.END);
	
	
	/**
   * Reads all events from the reader until one more end element than start elements is found.
   * 
   * @param reader the <i>JPhyloIO</i> event reader providing the event stream
   * @return {@code true} if any other event was encountered before the next end event
   * @throws IOException if an I/O error occurs while reading from {@code reader}
   */
  public static void reachElementEnd(JPhyloIOEventReader reader) throws IOException {
  	JPhyloIOEvent event = reader.next();
		 
		while (!event.getType().getTopologyType().equals(EventTopologyType.END)) {			
		  if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
	 	    reachElementEnd(reader);
		  }
	    event = reader.next();
		}  	
  }
  
  
  /**
   * Reads a subsequence from an <i>JPhyloIO</i> event stream modeling contents of a literal metadata element into a string 
   * builder. The string representation of the modeled object is returned, even if it is not an instance of 
   * {@link CharSequence}. Encountered comment events are skipped and ignored.
   * <p>
   * This tool method can be called after a start event with the content type {@link EventContentType#LITERAL_META} was read 
   * from {@code reader} and will consume all following events including the respective end event with the type 
   * {@link EventContentType#LITERAL_META}. Note that this method may only be used if the sequence type of the literal 
   * metadata start event is {@link LiteralContentSequenceType#SIMPLE}.
   * <p>
   * Large strings maybe split among multiple content events by <i>JPhyloIO</i> readers. (See the documentation of
   * {@link LiteralMetadataContentEvent} for details.) A sequence of such events is concatenated into a single value returned 
   * by this method. Note that sequential reading of large strings that do not need to be in memory as a whole to be processed 
   * by the application is more efficient, if the application handles the content events directly. This method should be used 
   * with care if such cases are likely.
   * 
   * @param reader the <i>JPhyloIO</i> event reader providing the event stream
   * @return an instance of {@link StringBuilder} containing the string content or {@code null} if no content events were
   *         encountered before the literal metadata end event
   * @throws IOException if an I/O error occurs while reading from {@code reader} or if another content event was encountered
   *         although sequence was declared to be terminated by the last event (The latter case would indicate an invalid event
   *         sequence produced by {@code reader}, which is not to expect from a well-tested reader. If you encounter such 
   *         problems with a built-in reader from <i>JPhyloIO</i>, inform the developers.)
   * @throws NoSuchElementException if the event stream ends before a literal metadata end event is encountered (This should
   *         not happen, if {@code reader} produces an event stream according to the grammar defined in the documentation of
   *         {@link JPhyloIOEventReader}.)
   * @see LiteralMetadataContentEvent
   * @see ObjectTranslator
   * @see #readLiteralMetadataContentAsString(JPhyloIOEventReader)
   * @see #readLiteralMetadataContentAsObject(JPhyloIOEventReader, Class)
   */
  public static StringBuilder readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader reader) throws IOException {
  	JPhyloIOEvent event = reader.next();
  	if (!event.getType().equals(LITERAL_META_END)) {
  		StringBuilder result = new StringBuilder();
  		boolean isUnfinished = true;
    	do {
    		if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
    			LiteralMetadataContentEvent contentEvent = event.asLiteralMetadataContentEvent();
    			if (isUnfinished) {
      			if (contentEvent.getStringValue() == null) {
      				result.append(contentEvent.getObjectValue().toString());
      			}
      			else {
      				result.append(contentEvent.getStringValue());  // Object values are null, if a string is split among mutliple events.
      			}
    				isUnfinished = contentEvent.isContinuedInNextEvent();
    			}
    			else {
    				throw new IOException("Another literal metadata content event was encountered, although the sequence was declared "
    						+ "to be terminated by the last event.");  //TODO Use other exception type.
    			}
    		}
    		else if (!event.getType().getTopologyType().equals(EventTopologyType.SOLE)) {
    			reachElementEnd(reader);  // Skip over possibly nested events. (The current grammar does not allow such events here, so this implementation treats possible future extensions.)
    		}
    		
    		event = reader.next();  // May throw a NoSuchElementException, if the sequence ends before a literal metadata end event is encountered (which would be invalid).
    	} while (!event.getType().equals(LITERAL_META_END));
    	
    	return result;
  	}
  	else {  // Empty literal metadata content sequence encountered.
  		return null;
  	}
  }
  
  
  /**
   * This convenience method calls {@link #readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader)} internally and
   * converts its content to a {@link String} if the builder is not {@code null}.
   * 
   * @param reader the <i>JPhyloIO</i> event reader providing the event stream
   * @return the string content or {@code null} if no content events were
   *         encountered before the literal metadata end event
   * @throws IOException if an I/O error occurs while reading from {@code reader} or if another content event was encountered
   *         although sequence was declared to be terminated by the last event (See the documentation of 
   *         {@link #readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader)} for details.)
   * @throws NoSuchElementException if the event stream ends before a literal metadata end event is encountered (See the 
   *         documentation of {@link #readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader)} for details.)
   * @see #readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader)
   */
  public static String readLiteralMetadataContentAsString(JPhyloIOEventReader reader) throws IOException {
  	StringBuilder result = readLiteralMetadataContentAsStringBuilder(reader);
  	if (result == null) {
  		return null;
  	}
  	else {
  		return result.toString();
  	}
  }
  
  
  /**
   * Reads a subsequence from an <i>JPhyloIO</i> event stream modeling contents of a literal metadata element into an object of
   * the specified class. This is only possible if a single metadata content event containing an object value implementing
   * the specified type is nested. Encountered comment events are skipped and ignored.
   * <p>
   * This tool method can be called after a start event with the content type {@link EventContentType#LITERAL_META} was read 
   * from {@code reader} and will consume all following events including the respective end event with the type 
   * {@link EventContentType#LITERAL_META}. Note that this method may only be used if the sequence type of the literal 
   * metadata start event is {@link LiteralContentSequenceType#SIMPLE} and the specified type of the object value is expected.
   * <p>
   * Note that this method is not able to read strings that are separated among multiple content events. 
   * {@link #readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader)} or 
   * {@link #readLiteralMetadataContentAsString(JPhyloIOEventReader)} should be used instead to read strings.
   * <p>
   * It is beneficial in many cases to use a value for {@code objectClass} that is as general as possible. If e.g. a double 
   * value is expected, the expression 
   * <pre>double d = JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Number.class).doubleValue();</pre> 
   * should be used instead of 
   * <pre>double d = JPhyloIOReadingUtils.readLiteralMetadataContentAsObject(reader, Double.class);</pre> 
   * This allows to read metadata declaring different numeric types into the {@code double}. Otherwise processing of metadata 
   * of e.g. the type {@link Float} or {@link Integer} would cause a {@link ClassCastException}. The same applies for all
   * classes that have commons supertypes that would be sufficient for reading.
   * <p>
   * Note that <i>JPhyloIO</i> readers will only produce content events with Java objects for which an appropriate 
   * {@link ObjectTranslator} instance is available. Custom object translators can be provided using the parameter
   * {@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}.
   * 
   * @param reader the <i>JPhyloIO</i> event reader providing the event stream
   * @param objectClass the type of object value to be read
   * @return the object value or {@code null} if no content events were
   *         encountered before the literal metadata end event
   * @throws IOException if an I/O error occurs while reading from {@code reader} or if more then one content event was 
   *         encountered or if the encountered content event did not have an object value
   * @throws ClassCastException if the encountered object values does not implement {@code objectClass}
   * @see #readLiteralMetadataContentAsStringBuilder(JPhyloIOEventReader)
   * @see ObjectTranslator
   */
  @SuppressWarnings("unchecked")
	public static <O> O readLiteralMetadataContentAsObject(JPhyloIOEventReader reader, Class<O> objectClass) 
			throws IOException, ClassCastException {
  	
  	O result = null;
  	JPhyloIOEvent event = reader.next();
  	if (!event.getType().equals(LITERAL_META_END)) {
    	do {
    		if (event.getType().getContentType().equals(EventContentType.LITERAL_META_CONTENT)) {
    			if (result == null) {
      			LiteralMetadataContentEvent contentEvent = event.asLiteralMetadataContentEvent();
      			if (contentEvent.hasObjectValue()) {
      				if (objectClass.isInstance(contentEvent.getObjectValue())) {
      					result = (O)contentEvent.getObjectValue();
      				}
      				else {
      					throw new ClassCastException("The encountered object value does not implement the specified type " + 
      							objectClass.getCanonicalName() + ".");
      				}
      			}
      			else {
      				throw new IOException("The encountered literal metadata content event did not carry an object value.");  //TODO Use other exception type.
      			}
    			}
    			else {
    				throw new IOException("More than one literal metadata content event was encountered, although a single object value was expected.");  //TODO Use other exception type.
    			}
    		}
    		else if (!event.getType().getTopologyType().equals(EventTopologyType.SOLE)) {
    			reachElementEnd(reader);  // Skip over possibly nested events. (The current grammar does not allow such events here, so this implementation treats possible future extensions.)
    		}
    		
    		event = reader.next();  // May throw a NoSuchElementException, if the sequence ends before a literal metadata end event is encountered (which would be invalid).
    	} while (!event.getType().equals(LITERAL_META_END));
  	}
  	return result;
  }
  
  
  /**
   * Reads all (remaining) events from the specified reader and adds events that implement the specified class and have
   * a type that is contained in the specified set to the returned list.
   * <p>
   * Possible use cases could e.g. be to collect all alignment start events from a document to display a list of alignments
   * contained in a file. 
   * 
   * @param reader the reader to read the events from
   * @param types a set of types to be collected
   * @param instanceClass the type that needs to be implemented by the collected event instances
   * @return a list of collected events
   * @throws IOException if an I/O error occurs while reading events from {@code reader}
   */
  @SuppressWarnings("unchecked")
	public static <E extends JPhyloIOEvent> List<E> collectEvents(JPhyloIOEventReader reader, Set<EventType> types, 
  		Class<E> instanceClass) throws IOException {
  	
  	List<E> result = new ArrayList<E>();
  	while (reader.hasNextEvent()) {
  		JPhyloIOEvent event = reader.next();
  		if (instanceClass.isInstance(event) && (types.contains(event.getType()))) {
  			result.add((E)event);
  		}
  	}
  	return result;
  }
  
  
  /**
   * Reads all (remaining) events from the specified reader and adds events that have a type that is contained in the #
   * specified set to the returned list.
   * 
   * @param reader the reader to read the events from
   * @param types a set of types to be collected
   * @return a list of collected events
   * @throws IOException if an I/O error occurs while reading events from {@code reader}
   */
	public static List<JPhyloIOEvent> collectEvents(JPhyloIOEventReader reader, Set<EventType> types) throws IOException {
		return collectEvents(reader, types, JPhyloIOEvent.class);
  }
}
