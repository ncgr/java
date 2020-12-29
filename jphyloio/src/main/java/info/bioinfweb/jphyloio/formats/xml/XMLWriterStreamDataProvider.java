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


import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.receivers.AbstractXMLDataReceiver;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



/**
 * Stores data that shall be shared among different implementations of {@link AbstractXMLDataReceiver} 
 * and the according writer that uses them.
 * 
 * @author Sarah Wiechers
 */
public class XMLWriterStreamDataProvider<R extends AbstractXMLEventWriter<? extends XMLWriterStreamDataProvider<R>>> extends WriterStreamDataProvider<R> {
	private Set<String> namespacePrefixes = new HashSet<String>();	
	private StringBuffer commentContent = new StringBuffer();
	private boolean literalContentIsContinued = false;
	
	
	public XMLWriterStreamDataProvider(R eventWriter) {
		super(eventWriter);
	}
	

	public XMLStreamWriter getWriter() {
		return getEventWriter().getXMLWriter();
	}
	
	
	public Set<String> getNamespacePrefixes() {
		return namespacePrefixes;
	}


	public StringBuffer getCommentContent() {
		return commentContent;
	}	


	public boolean isLiteralContentContinued() {
		return literalContentIsContinued;
	}


	public void setLiteralContentIsContinued(boolean literalContentIsContinued) {
		this.literalContentIsContinued = literalContentIsContinued;
	}
	
	
	public void setNamespacePrefix(String prefix, String namespace) throws XMLStreamException {
		if (!((namespace == null) || namespace.isEmpty())) {
			if (getWriter().getPrefix(namespace) == null) {  // URI is not yet bound to a prefix
				int index = 1;
				String nameSpacePrefix = prefix;
				if (!getNamespacePrefixes().add(nameSpacePrefix)) {
					do {
						nameSpacePrefix = prefix + index;  // NeXML documents that contain CURIEs with prefixes containing numbers might not validate
						index++;
					} while (!getNamespacePrefixes().add(nameSpacePrefix));
				}
				
				getWriter().setPrefix(nameSpacePrefix, namespace);
			}
		}
	}
}
