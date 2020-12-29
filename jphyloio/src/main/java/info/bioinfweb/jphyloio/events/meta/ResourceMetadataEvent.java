/*
 * JPhyloIO - Event based parsing and stream writing of multiple sequence alignment and tree formats.
 * Copyright (C) 2015-2016  Ben Stöver, Sarah Wiechers
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
import info.bioinfweb.jphyloio.events.type.EventTopologyType;

import java.net.URI;

import javax.xml.namespace.QName;



/**
 * Indicates that metadata linking a <i>RDF</i> resource has been encountered at the current position of the document. The resource 
 * can either be named and referenced by {@link #getHRef()} or (if {@link #getHRef()} returns {@code null}) be an anonymous 
 * <i>RDF</i> which is formed by the set of upcoming nested metaevents.
 * <p>
 * This event has the topology type {@link EventTopologyType#START} and other resource and literal metadata event subsequences may
 * be nested before the according end event. The content type is {@link EventContentType#RESOURCE_META}.
 *
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see LiteralMetadataEvent
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class ResourceMetadataEvent extends LabeledIDEvent {
	private URIOrStringIdentifier rel;
	private URI hRef;
	private String about;


	/**
	 * Creates a new instance of this class.
	 *
	 * @param id the unique ID associated with the represented data element (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label a label associated with the represented data element (Maybe {@code null}.)
	 * @param rel the <i>RDF</i> rel URI of this element
	 * @param hRef the <i>RDF</i> hRef URI of this element (Maybe {@code null}.)
	 * @param about the content of a specific about attribute to be written on the according <i>XML</i> representation of this element
	 *        (Maybe {@code null}.)
	 * @throws NullPointerException if {@code id} or {@code rel} are {@code null}
	 * @throws IllegalArgumentException if the specified ID is not a valid
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>
	 */
	public ResourceMetadataEvent(String id, String label, URIOrStringIdentifier rel, URI hRef, String about) {
		super(EventContentType.RESOURCE_META, id, label);
		if (rel == null) {
			throw new NullPointerException("\"rel\" must not be null.");
		}
		else {
			this.rel = rel;
			this.hRef = hRef;
			this.about = about;
		}
	}


	/**
	 * Creates a new instance of this class.
	 *
	 * @param id the unique ID associated with the represented data element (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label a label associated with the represented data element (Maybe {@code null}.)
	 * @param rel the <i>RDF</i> rel URI of this element (The string representation will be set to the local part of the {@link QName}.)
	 * @param hRef the <i>RDF</i> hRef URI of this element (Maybe {@code null}.)
	 * @param about the content of a specific about attribute to be written on the according <i>XML</i> representation of this element
	 *        (Maybe {@code null}.)
	 * @throws NullPointerException if {@code id} or {@code rel} are {@code null}
	 * @throws IllegalArgumentException if the specified ID is not a valid
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>
	 */
	public ResourceMetadataEvent(String id, String label, QName rel, URI hRef, String about) {
		this(id, label, new URIOrStringIdentifier(null, rel), hRef, about);
	}
	
	
	/**
	 * Creates a new instance of this class with no {@code hRef} and no {@code about} values.
	 *
	 * @param id the unique ID associated with the represented data element (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label a label associated with the represented data element (Maybe {@code null}.)
	 * @param rel the <i>RDF</i> rel URI of this element (The string representation will be set to the local part of the {@link QName}.)
	 * @throws NullPointerException if {@code id} or {@code rel} are {@code null}
	 * @throws IllegalArgumentException if the specified ID is not a valid
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>
	 */
	public ResourceMetadataEvent(String id, String label, QName rel) {
		this(id, label, new URIOrStringIdentifier(null, rel), null, null);
	}
	
	
	/**
	 * Creates a new instance of this class with no {@code hRef} and no {@code about} values.
	 *
	 * @param id the unique ID associated with the represented data element (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label a label associated with the represented data element (Maybe {@code null}.)
	 * @param rel the <i>RDF</i> rel URI of this element
	 * @throws NullPointerException if {@code id} or {@code rel} are {@code null}
	 * @throws IllegalArgumentException if the specified ID is not a valid
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>
	 */
	public ResourceMetadataEvent(String id, String label, URIOrStringIdentifier rel) {
		this(id, label, rel, null, null);
	}
	
	
	public URIOrStringIdentifier getRel() {
		return rel;
	}


	public URI getHRef() {
		return hRef;
	}


	public String getAbout() {
		return about;
	}
}
