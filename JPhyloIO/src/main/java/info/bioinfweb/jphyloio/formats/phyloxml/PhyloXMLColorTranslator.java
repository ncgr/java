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
package info.bioinfweb.jphyloio.formats.phyloxml;


import java.awt.Color;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;

import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;
import info.bioinfweb.jphyloio.objecttranslation.implementations.AbstractObjectTranslator;



/**
 * Object translator between {@link Color} and the PhyloXML complex type 
 * <a href="http://www.phyloxml.org/documentation/version_1.10/phyloxml.xsd.html#h-1691165380">BranchColor</a>.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class PhyloXMLColorTranslator extends AbstractObjectTranslator<Color> implements PhyloXMLConstants {
	@Override
	public Class<Color> getObjectClass() {
		return Color.class;
	}

	
	@Override
	public boolean hasStringRepresentation() {
		return false;
	}

	
	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider) throws UnsupportedOperationException, ClassCastException {
		throw new UnsupportedOperationException("PhyloXML color definitions can only be represented as structured XML.");
	}

	
	@Override
	public void writeXMLRepresentation(XMLStreamWriter writer, Object object, WriterStreamDataProvider<?> streamDataProvider) throws IOException, XMLStreamException {
		Color color = (Color)object;
		
		writer.writeStartElement(TAG_RED.getLocalPart());
		writer.writeCharacters(Integer.toString(color.getRed()));
		writer.writeEndElement();
		
		writer.writeStartElement(TAG_GREEN.getLocalPart());
		writer.writeCharacters(Integer.toString(color.getGreen()));
		writer.writeEndElement();
		
		writer.writeStartElement(TAG_BLUE.getLocalPart());
		writer.writeCharacters(Integer.toString(color.getBlue()));
		writer.writeEndElement();		
	}

	
	@Override
	public Color representationToJava(String representation, ReaderStreamDataProvider<?> streamDataProvider) throws InvalidObjectSourceDataException, UnsupportedOperationException {
		throw new UnsupportedOperationException("PhyloXML color definitions can only be read from structured XML.");
	}

	
	@Override
	public Color readXMLRepresentation(XMLEventReader reader, ReaderStreamDataProvider<?> streamDataProvider) throws IOException, XMLStreamException,
			InvalidObjectSourceDataException {
		
		Color color = null;
		String red = null;
		String green = null;
		String blue = null;
		
		Set<QName> encounteredTags = new HashSet<QName>();
		XMLEvent event = reader.peek();
		
		while (!(event.isEndElement() && !encounteredTags.contains(event.asEndElement().getName()))) {
			XMLEvent nextEvent = reader.nextEvent();
			
			if (nextEvent.isStartElement()) {
				QName elementName = nextEvent.asStartElement().getName();
				encounteredTags.add(elementName);
				
				if (elementName.equals(TAG_RED)) {
					red = reader.getElementText();
				}
				else if (elementName.equals(TAG_GREEN)) {
					green = reader.getElementText();
				}
				else if (elementName.equals(TAG_BLUE)) {
					blue = reader.getElementText();
				}
				else {
					XMLUtils.reachElementEnd(reader);				
				}
			}
			
			event = reader.peek();
		}
		
		if ((red != null) && (green != null) && (blue != null)) {
			try {
				color = new Color(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
			}
			catch (NumberFormatException e) {
				throw new InvalidObjectSourceDataException("The encountered XML could not be parsed to a color object.");
			}
		}
		
		return color;
	}
}
