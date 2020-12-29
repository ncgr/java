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
package info.bioinfweb.jphyloio.formats.xml.elementreaders;


import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;



/**
 * XML element reader that can be used to process the content of elements that do not allow nested characters.
 * <p>
 * If character content is encountered by this element reader, a {@link JPhyloIOReaderException} is thrown.
 * 
 * @author Sarah Wiechers
 *
 */
@SuppressWarnings("rawtypes")
public class XMLNoCharactersAllowedElementReader extends AbstractXMLElementReader {
	public XMLNoCharactersAllowedElementReader() {
		super();
	}
	

	@Override
	public void readEvent(XMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException,
			XMLStreamException {
		if (!event.asCharacters().getData().matches("\\s+")) {
			throw new JPhyloIOReaderException("No character data is allowed under the element \"" + streamDataProvider.getElementName()
					+ "\", but the string " + event.asCharacters().getData() + " was found.", event.getLocation());
		}
	}
}
