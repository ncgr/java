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
package info.bioinfweb.jphyloio.events.meta;


import info.bioinfweb.jphyloio.events.ContinuedEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslatorFactory;

import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;



/**
 * This event represents a value of a literal metadata and is therefore always nested inside start and end events of the type
 * {@link EventContentType#LITERAL_META}.
 * <p>
 * Values can be represented in the following ways:
 * <ul>
 *   <li>An instance can represent a single object value directly.</li>
 *   <li>A sequence of events can represent a longer string that is distributed among several events.</li>
 *   <li>A sequence of events can represent a more complex XML representation of the literal value. In such cases one event 
 *       instance will represent each {@link XMLEvent} created from the encountered XML.</li>
 * </ul>
 * When readers create instances of this event, they try to create object values from their string representations using a
 * translator returned by {@link ObjectTranslatorFactory}. If no according translator is available, the object value will be
 * {@code null}.
 * <p>
 * If the declared data type is mapped to an instance of {@link String} (e.g. {@code xsd:string} or {@code xsd:token}) the object
 * value and its string representation are the same {@link String} instance. Large strings may be separated among several events
 * for performance reasons, while {@link #isContinuedInNextEvent()} will be {@code true} in all but the last event of such a 
 * sequence. If a string is separated among multiple events, the object value in all of these events will be {@code null} and 
 * the single parts can be obtained using {@link #getStringValue()}. (That is because only the whole string is considered as the 
 * object and not its parts.)
 * <p>
 * <i>JPhyloIO</i> event objects are generally immutable. Anyway complex object values may have editable properties themselves. 
 * In application code, it should be absolutely avoided to edit such properties while the according event object is still in use, 
 * especially if the event contains a string representation of that object (which cannot be edited accordingly).
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see LiteralMetadataEvent
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class LiteralMetadataContentEvent extends ContinuedEvent {
	private String stringValue;
	private Object objectValue;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param stringValue the string value of the meta information.
	 * @param continuedInNextEvent Specify {@code true} here if this event does not contain the final part of 
	 *        its value and more events are ahead or {@code false otherwise}.
	 */
	public LiteralMetadataContentEvent(String stringValue, boolean continuedInNextEvent) {
		this(stringValue, null, continuedInNextEvent);
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param objectValue the object value of the meta information.
	 * @param stringValue the string value of the meta information.
	 */
	public LiteralMetadataContentEvent(Object objectValue, String stringValue) {
		this(stringValue, objectValue, false);
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param originalType the original type of meta information or null. 
	 * @param stringValue the string value of the meta information.
	 * @param objectValue the object value of the meta information.
	 * @param alternativeStringValue an alternative string representation of the value (Some formats may provide alternative
	 *        representations of a value, e.g. a human and a machine readable one.)
	 * @param continuedInNextEvent Specify {@code true} here if this event does not contain the final part of 
	 *        its value and more events are ahead or {@code false otherwise}.
	 * @throws NullPointerException if {@code stringValue} is {@code null} and {@code alternativeStringValue} is not
	 */
	private LiteralMetadataContentEvent(String stringValue, Object objectValue, boolean continuedInNextEvent) {		
		super(EventContentType.LITERAL_META_CONTENT, continuedInNextEvent);
		
		if ((stringValue == null) && (objectValue == null)) {
			throw new NullPointerException("Either stringValue or objectValue must be specified. If a literal meta event has no content, the content event should be omitted.");
		}
		else {
			this.stringValue = stringValue;
			if ((objectValue != null) && isContinuedInNextEvent()) {
				throw new IllegalArgumentException("If a value is separated among several events no object value may be specified.");
			}
			else {
				this.objectValue = objectValue;
			}
		}
	}
	

	/**
	 * Creates a new instance of this class wrapping an {@link XMLEvent}.
	 * 
	 * @param xmlEvent the XML event object to be wrapped
	 * @param continuedInNextEvent Specify {@code true} here if this event does not contain the final part of 
	 *        its value and more events are ahead or {@code false otherwise}. (Note that {@code true} this is only allowed
	 *        for character XML events which contain only a part of the represented string.)
	 * @throws IllegalArgumentException if {@code continuedInNextEvent} in set to {@code true}, but the specified XML event
	 *         was not a characters event 
	 */
	public LiteralMetadataContentEvent(XMLEvent xmlEvent, boolean continuedInNextEvent) {
		super(EventContentType.LITERAL_META_CONTENT, continuedInNextEvent);
		
		if (!xmlEvent.isCharacters() && continuedInNextEvent) {
			throw new IllegalArgumentException("Only character XML events may be continued in the next event. "
					+ "The specified event had the type " + xmlEvent.getEventType() + ".");
		}
		else {
			if (xmlEvent.isCharacters()) {
				this.stringValue = xmlEvent.asCharacters().getData();
			}
			this.objectValue = xmlEvent;
		}
	}
	

	/**
	 * Returns the string value of the meta information.
	 * <p>
	 * If larger strings are separated among multiple content events, this property returns the part of the string modeled by this
	 * event.
	 * <p>
	 * It may return {@code null} if no string representation was provided in the constructor. Note that no instance of this
	 * class will return {@code null} for both the object value and the string representation.
	 * <p>
	 * If this instance represents carries an {@link XMLEvent} as its object value, this method will return the characters for
	 * <i>XML</i> event instances implementing {@link Characters} and {@code null} for other implementations.
	 * 
	 * @return the string representation of the meta information or {@code null} if this metadata event carries no string value
	 * @see #getObjectValue()
	 */
	public String getStringValue() {
		return stringValue;
	}


	/**
	 * Returns the <i>Java</i> object representing the metadata element modeled by this event. An alternative string representation 
	 * of this object may be provided that can be obtained using {@link #getStringValue()}.
	 * <p>
	 * This method can return {@code null} if this content event represents are larger string that is separated among multiple 
	 * events. In such cases {@link #getStringValue()} returns the part of the string that is modeled by this event. This method
	 * may also return {@code null} if {@code null} was specified as the object value in the constructor.
	 * 
	 * @return the metadata object modeled by this event or {@code null} 
	 */
	public Object getObjectValue() {
		return objectValue;
	}
	
	
	/**
	 * Determines whether this instance carries a string representation of the metadata element it models.
	 * 
	 * @return {@code true} of a string representation is available or {@code false} if {@link #getStringValue()} will
	 *         return {@code null}
	 */
	public boolean hasStringValue() {
		return (getStringValue() != null);
	}
	
	
	/**
	 * Determines whether this instance carries a <i>Java</i> object of the metadata element it models.
	 * 
	 * @return {@code true} of an object is available or {@code false} if {@link #getObjectValue()} will return {@code null}
	 */
	public boolean hasObjectValue() {
		return (getObjectValue() != null);
	}
	
	
	/**
	 * Determines whether this instance carries an {@link XMLEvent} as its object value. This happens if this instance is
	 * used to represent a part the content of an <i>XML</i> representation of a literal metadata element.
	 * 
	 * @return {@code true} if an <i>XML</i> stream event can be returned by {@link #getXMLEvent()} or {@code false} otherwise
	 */
	public boolean hasXMLEventValue() {
		return getObjectValue() instanceof XMLEvent;
	}
	
	
	/**
	 * Returns the {@link XMLEvent} modeled by this instance. This is only possible if this instance is used to represent a part 
	 * the content of an <i>XML</i> representation of a literal metadata element.
	 * <p>
	 * This convenience method calls {@link #getObjectValue()} internally and tries to cast it to {@link XMLEvent}.
	 * 
	 * @return the {@link XMLEvent} or {@code null} if this instance carries no object value
	 * @throws ClassCastException if the object value of this instance does not implement {@link XMLEvent}
	 * @see #hasXMLEventValue()
	 * @see #getObjectValue()
	 */
	public XMLEvent getXMLEvent() throws ClassCastException {
		return (XMLEvent)getObjectValue();
	}


	/**
	 * Returns the stored string representation of the modeled metadata element if available. If not, the {@link Object#toString()} 
	 * method of the object value is returned.
	 * 
	 * @return the string representation of the metadata element modeled by this event
	 */
	@Override
	public String toString() {
		if (hasStringValue()) {
			return getStringValue();
		}
		else if (hasObjectValue()) {
			return getObjectValue().toString();
		}
		else {
			return super.toString();
		}
	}
}
