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
package info.bioinfweb.jphyloio.formats.phyloxml.receivers;


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLWriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.phyloxml.PropertyOwner;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



/**
 * Receiver, that  writes only custom XML, e.g nested under {@code document}.
 * If an event has XML content, this is written to the file, if it does not contain characters that are not nested 
 * under any tags or elements using the <i>PhyloXML</i> namespace, because this is not valid in <i>PhyloXML</i>.
 * 
 * @author Sarah Wiechers
 *
 */
public class PhyloXMLOnlyCustomXMLDataReceiver extends PhyloXMLMetaDataReceiver {
	private boolean writeCustomXML;
	
	
	public PhyloXMLOnlyCustomXMLDataReceiver(PhyloXMLWriterStreamDataProvider streamDataProvider,
			ReadWriteParameterMap parameterMap, PropertyOwner propertyOwner) {
		
		super(streamDataProvider, parameterMap, propertyOwner);
	}
	
	
	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		if (writeCustomXML) {
			if (!hasSimpleContent() && event.hasXMLEventValue()) {  // Write custom XML
				writeCustomXMLTag(event.getXMLEvent());
			}
			else {  // Write XML object representation
				QName datatype = null;
				if (getOriginalType() != null) {
					datatype = getOriginalType().getURI();
				}
				
				ObjectTranslator<?> translator = getParameterMap().getObjectTranslatorFactory().getDefaultTranslatorWithPossiblyInvalidNamespace(datatype);
//				String value = processLiteralContent(event, translator, datatype); 
//				
//				if (value != null) {
//					getStreamDataProvider().getWriter().writeCharacters(value);  // Could be written nested under special meta tag
//				}
				if ((translator != null) && !translator.hasStringRepresentation()) {  // Make sure no single character events are written
					translator.writeXMLRepresentation(getStreamDataProvider().getWriter(), event.getObjectValue(), getStreamDataProvider());
				}
				
				getStreamDataProvider().setLiteralContentIsContinued(event.isContinuedInNextEvent());				
			}
		}
	}


	@Override
	protected void handleResourceMetaStart(ResourceMetadataEvent event) throws IOException, XMLStreamException {
		writeCustomXML = false;
		
		if (getStreamDataProvider().getMetaIDs().contains(event.getID())) {
			if ((event.getRel().getURI() != null) && event.getRel().getURI().equals(ReadWriteConstants.PREDICATE_HAS_CUSTOM_XML)) {			
				switch (getParameterMap().getPhyloXMLMetadataTreatment()) {
					case NONE:
						break;
					case LEAVES_ONLY:
					case SEQUENTIAL:
					case TOP_LEVEL_WITH_CHILDREN:
					case TOP_LEVEL_WITHOUT_CHILDREN:
						writeCustomXML = true;
						break;
				}				
			}
		}
		
		if (writeCustomXML) {
			getStreamDataProvider().getMetaIDs().remove(event.getID());
		}
	}
	
	
	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		super.handleMetaEndEvent(event);
		
		if (event.getType().getContentType().equals(EventContentType.RESOURCE_META)) {
			writeCustomXML = false;
		}
	}
}
