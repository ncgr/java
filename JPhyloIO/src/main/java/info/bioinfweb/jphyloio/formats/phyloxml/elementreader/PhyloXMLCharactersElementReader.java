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
package info.bioinfweb.jphyloio.formats.phyloxml.elementreader;


import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.AbstractXMLElementReader;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;



/**
 * Element reader that is used to process the character content of <i>PhyloXML</i> tags.
 * 
 * @author Sarah Wiechers
 *
 */
public class PhyloXMLCharactersElementReader extends AbstractXMLElementReader<PhyloXMLReaderStreamDataProvider> {
	private QName datatype;
	
	
	public PhyloXMLCharactersElementReader(QName datatype) {
		super();		
		this.datatype = datatype;
	}


	@Override
	public void readEvent(PhyloXMLReaderStreamDataProvider streamDataProvider, XMLEvent event) throws IOException, XMLStreamException {
		ObjectTranslator<?> translator = streamDataProvider.getParameters().getObjectTranslatorFactory()
				.getDefaultTranslatorWithPossiblyInvalidNamespace(datatype);
		
		if (!datatype.equals(W3CXSConstants.DATA_TYPE_TOKEN) && !datatype.equals(W3CXSConstants.DATA_TYPE_STRING) && (translator != null) && translator.hasStringRepresentation()) {	
			Object objectValue = null;
			String propertyValue = event.asCharacters().getData() + XMLUtils.readCharactersAsString(streamDataProvider.getXMLReader());
			
			if (propertyValue != null) {
				try {
					objectValue = translator.representationToJava(propertyValue, streamDataProvider);
					streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(objectValue, propertyValue));
				}
				catch (InvalidObjectSourceDataException e) {
					throw new JPhyloIOReaderException("The content of this tag could not be parsed to class " + translator.getObjectClass().getSimpleName() + ".", event.getLocation());
				}
			}
		}
		else {
			boolean isContinued = streamDataProvider.getXMLReader().peek().isCharacters();
			streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(event.asCharacters().getData(), isContinued));
		}		
	}
}
