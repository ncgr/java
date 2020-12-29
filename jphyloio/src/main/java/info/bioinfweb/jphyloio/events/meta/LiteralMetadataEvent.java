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


import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Indicates that literal metadata is modeled at the current position of the document (event stream). The actual literal value 
 * will be represented by one or more nested {@link LiteralMetadataContentEvent}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see LiteralMetadataContentEvent
 * @see ResourceMetadataEvent
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class LiteralMetadataEvent extends LabeledIDEvent {
	private URIOrStringIdentifier predicate;
	private String alternativeStringValue = null;
	private URIOrStringIdentifier originalType;
	private LiteralContentSequenceType sequenceType;
	
	
	public LiteralMetadataEvent(String id, String label, URIOrStringIdentifier predicate, LiteralContentSequenceType sequenceType) {
		this(id, label, predicate, null, null, sequenceType);
	}
	
	
	public LiteralMetadataEvent(String id, String label, URIOrStringIdentifier predicate, URIOrStringIdentifier originalType, LiteralContentSequenceType sequenceType) {
		this(id, label, predicate, null, originalType, sequenceType);
	}
	
	
	public LiteralMetadataEvent(String id, String label, URIOrStringIdentifier predicate, String alternativeStringValue, URIOrStringIdentifier originalType, 
				LiteralContentSequenceType sequenceType) {
		
		super(EventContentType.LITERAL_META, id, label);
		if (predicate == null) {
			throw new NullPointerException("The predicate must not be null.");
		}
		else if (sequenceType == null) {
			throw new NullPointerException("The sequence type must not be null.");
		}
		else {
			this.predicate = predicate;
			this.alternativeStringValue = alternativeStringValue;
			this.originalType = originalType;			
			this.sequenceType = sequenceType;
		}
	}


	public URIOrStringIdentifier getPredicate() {
		return predicate;
	}
	
	
	/**
	 * Determines whether an alternative string representation is available for the value modeled by the following 
	 * sequence of {@link LiteralMetadataContentEvent}s.
	 * 
	 * @return {@code true} if an alternative representation is available or {@code false} otherwise
	 */
	public boolean hasAlternativeStringValue() {
		return getAlternativeStringValue() != null;
	}
	
	
	/**
	 * Returns the alternative string representation of the literal value modeled by the following 
	 * sequence of {@link LiteralMetadataContentEvent}s.
	 * <p>
	 * Some formats may provide alternative representations of a content, e.g. a human and a machine readable one.
	 * 
	 * @return the alternative representation or {@code null} if there is none
	 */
	public String getAlternativeStringValue() {
		return alternativeStringValue;
	}
	
	
	public URIOrStringIdentifier getOriginalType() {
		return originalType;
	}


	/**
	 * Determines which type of sequence of {@link LiteralMetadataContentEvent}s will be nested within this
	 * event. 
	 * 
	 * @return the content sequence type
	 */
	public LiteralContentSequenceType getSequenceType() {
		return sequenceType;
	}
}
