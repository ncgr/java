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
package info.bioinfweb.jphyloio.formats.xml.stax;


import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;

import javax.xml.stream.XMLStreamWriter;



/**
 * Implements shared functionality for the <i>StAX</i> adapter writers of <i>JPhyloIO</i>.
 * 
 * @author Ben St&ouml;ver
 */
public class AbstractMetaXMLWriter {
	private JPhyloIOEventReceiver receiver;
	private XMLStreamWriter underlyingXMLWriter;
	
	
	public AbstractMetaXMLWriter(JPhyloIOEventReceiver receiver, XMLStreamWriter underlyingXMLWriter) {
		super();
		this.receiver = receiver;
		this.underlyingXMLWriter = underlyingXMLWriter;
	}


	protected JPhyloIOEventReceiver getReceiver() {
		return receiver;
	}


	protected XMLStreamWriter getUnderlyingXMLWriter() {
		return underlyingXMLWriter;
	}
}
