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


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;



/**
 * Interface providing basic functionality for all <i>JPhyloIO</i> readers of <i>XML</i> formats.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public interface JPhyloIOXMLEventReader extends JPhyloIOEventReader {
	/**
	 * Returns the namespace context that is valid at the current position of the document. The returned value therefore may change with 
	 * each call of {@link #next()}.
	 * <p>
	 * Applications can use this namespace context e.g. to handle custom XML in literal meta events. In such cases, the according context
	 * object should be requested (and saved if necessary) before the next call of {@link #next()}, because prefix to namespace mapping
	 * may already change by reading the data for the next event.
	 * 
	 * @return the current namespace context object or {@code null} if the reader did not encounter the first XML tag of the document yet
	 */
	public NamespaceContext getNamespaceContext();
	//TODO Add hint for possible problems with events created from buffered data? (See also #123.)	
	
	/**
	 * Creates a new {@link XMLEventReader} that allows to read events of XML content of literal metadata from this 
	 * <i>JPhyloIO</i> event reader instance through that interface. Instances can be created any time, while this instance is located
	 * inside a literal metadata subsequence with the {@link LiteralContentSequenceType#XML}, but not outside of such a sequence.
	 * <p>
	 * In principle this method may be called multiple times within the same literal metadata event subsequence, which would result in having
	 * multiple XML event reader instances delegating to the same <i>JPhyloIO</i> reader, although that is not recommended for performance
	 * reasons.
	 * 
	 * @return the new reader instance
	 * @throws IllegalStateException if no reader can be created at the current position (outside of a literal metadata event 
	 *         subsequence with type {@link LiteralContentSequenceType#XML})
	 */
	public XMLEventReader createMetaXMLEventReader() throws IllegalStateException;	
	
	/**
	 * Creates a {@link XMLStreamReader} that allows to read events of XML content of literal metadata from this 
	 * <i>JPhyloIO</i> event reader instance through that interface. Instances can be created any time, while this instance is located
	 * inside a literal metadata subsequence with the {@link LiteralContentSequenceType#XML}, but not outside of such a sequence.
	 * <p>
	 * In principle this method may be called multiple times within the same literal metadata event subsequence, which would result in having
	 * multiple XML event reader instances delegating to the same <i>JPhyloIO</i> reader, although that is not recommended for performance
	 * reasons.
	 * 
	 * @return the new reader instance
	 * @throws IllegalStateException if no reader can be created at the current position (outside of a literal metadata event 
	 *         subsequence with type {@link LiteralContentSequenceType#XML})
	 */
	public XMLStreamReader createMetaXMLStreamReader() throws IllegalStateException;
}
