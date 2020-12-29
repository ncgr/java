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
package info.bioinfweb.jphyloio.formats.xml;


import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.formats.xml.stax.MetaXMLEventWriter;
import info.bioinfweb.jphyloio.formats.xml.stax.MetaXMLStreamWriter;



//TODO point to this information at a central place (e.g. JPhyloIOEventWriter)
/**
 * Interface providing basic functionality for all JPhyloIO writers of XML formats.
 * 
 * Note, that these writers are able to change the prefix a namespace is bound to. Therefore
 * it is important that applications writing prefixes in attributes or character data always use the provided 
 * namespace context object to obtain the prefix a namespace is currently bound to.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public interface JPhyloIOXMLEventWriter extends JPhyloIOEventWriter {
	/**
	 * Returns the currently valid namespace context of the writer.
	 * 
	 * @return the currently valid {@link NamespaceContext} object
	 */
	public NamespaceContext getNamespaceContext();
	
	/**
	 * Creates a new {@link XMLEventWriter} that allows to write events of <i>XML</i> content of literal metadata to this 
	 * <i>JPhyloIO</i> event writer instance through that interface. Instances can be created any time, while this instance is located
	 * inside a literal metadata subsequence with the {@link LiteralContentSequenceType#XML}, but not outside of such a sequence.
	 * <p>
	 * In principle this method may be called multiple times within the same literal metadata event subsequence, which would result in 
	 * having multiple <i>XML</i> event writer instances delegating to the same <i>JPhyloIO</i> reader, although that is not 
	 * recommended.
	 * <p>
	 * In implementations of this interface that are part of <i>JPhyloIO</i>, the returned writer is an instance of 
	 * {@link MetaXMLEventWriter}. You can have a look at its documentation for furthter details in how the returned instance
	 * behaves. (Note that third party implementations might return a different implementation of {@link XMLEventWriter} here.)
	 * 
	 * @param receiver the receiver to write events to (Each {@link XMLEvent} will be translated into a 
	 *        {@link LiteralMetadataContentEvent}.)
	 * @return the new writer instance
	 * @throws IllegalStateException if no writer can be created at the current position (outside of a literal metadata event 
	 *         subsequence with type {@link LiteralContentSequenceType#XML})
	 */
	public XMLEventWriter createMetaXMLEventWriter(JPhyloIOEventReceiver receiver) throws IllegalStateException;	
	
	/**
	 * Creates a new {@link XMLStreamWriter} that allows to write events of <i>XML</i> content of literal metadata to this 
	 * <i>JPhyloIO</i> event writer instance through that interface. Instances can be created any time, while this instance is located
	 * inside a literal metadata subsequence with the {@link LiteralContentSequenceType#XML}, but not outside of such a sequence.
	 * <p>
	 * In theory this method may be called multiple times within the same literal metadata event subsequence, which would result in 
	 * having multiple <i>XML</i> event writer instances delegating to the same <i>JPhyloIO</i> reader, although that is not 
	 * recommended.
	 * <p>
	 * In implementations of this interface that are part of <i>JPhyloIO</i>, the returned writer is an instance of 
	 * {@link MetaXMLStreamWriter}. You can have a look at its documentation for furthter details in how the returned instance
	 * behaves. (Note that third party implementations might return a different implementation of {@link XMLStreamWriter} here.)
	 * 
	 * @param receiver the receiver to write events to (Each writing operation will be translated into a 
	 *        {@link LiteralMetadataContentEvent}.)
	 * @return the new writer instance
	 * @throws IllegalStateException if no writer can be created at the current position (outside of a literal metadata event 
	 *         subsequence with type {@link LiteralContentSequenceType#XML})
	 */
	public XMLStreamWriter createMetaXMLStreamWriter(JPhyloIOEventReceiver receiver) throws IllegalStateException;
}
