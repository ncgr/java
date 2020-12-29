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
package info.bioinfweb.jphyloio.formats.xtg;


import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.NodeEdgeInfo;
import info.bioinfweb.jphyloio.formats.xml.AttributeInfo;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLStartElementReader;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * This element reader is used to process the contents of <i>XTG</i> tags that also contain information about 
 * the intended formatting (e.g. textual or as a decimal value).
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public class XTGFormattedTextElementReader extends XMLStartElementReader implements XTGConstants {
	public XTGFormattedTextElementReader(QName literalPredicate, QName resourcePredicate, URIOrStringIdentifier datatype,
			boolean isEdgeMeta, AttributeInfo... attributeInformation) {
		
		super(literalPredicate, resourcePredicate, datatype, isEdgeMeta, attributeInformation);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readEvent(XMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
		StartElement element = event.asStartElement();
		boolean isDecimal = XMLUtils.readBooleanAttr(element, ATTR_TEXT_IS_DECIMAL, false);
		String value = XMLUtils.readStringAttr(element, ATTR_TEXT, null);
		URIOrStringIdentifier datatype;
		
		if (isEdgeMeta()) {
			streamDataProvider.setCurrentEventCollection(((NodeEdgeInfo)streamDataProvider.getSourceNode().peek()).getNestedEdgeEvents());
		}
		
		if (isDecimal) {
			datatype = new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_DOUBLE);
		}
		else {
			datatype = new URIOrStringIdentifier(null, W3CXSConstants.DATA_TYPE_STRING);
		}

		streamDataProvider.getCurrentEventCollection().add(
				new ResourceMetadataEvent(ReadWriteConstants.DEFAULT_META_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null, 
						new URIOrStringIdentifier(null, getResourcePredicate()), null, null));
		
		if (value != null) {  //TODO Wird isDecimal auch irgendwo ausgegeben?
			streamDataProvider.getCurrentEventCollection().add(
					new LiteralMetadataEvent(ReadWriteConstants.DEFAULT_META_ID_PREFIX + streamDataProvider.getIDManager().createNewID(), null, 
					new URIOrStringIdentifier(null, PREDICATE_TEXT), datatype, LiteralContentSequenceType.SIMPLE));
			
			if (isDecimal) {
				try {
					streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(Double.parseDouble(value), value));				
				}
				catch (NumberFormatException e) {
					throw new JPhyloIOReaderException("The value \"" + value + "\" was marked as decimal, but could not be parsed.", event.getLocation());
				}
			}
			else {			
				streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(value, value));			
			}
			
			streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.LITERAL_META));
		}
		
		readAttributes(streamDataProvider, element, "", getAttributeInformationMap());
	}
}
