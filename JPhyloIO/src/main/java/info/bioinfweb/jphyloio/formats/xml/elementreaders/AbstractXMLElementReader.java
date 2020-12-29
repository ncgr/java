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


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLEventReader;
import info.bioinfweb.jphyloio.formats.xml.AttributeInfo;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xtg.XTGConstants;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;

import java.awt.Color;
import java.util.LinkedHashMap;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;



/**
 * Abstract implementation of an element reader that is used to process parts of an <i>XML</i> document.
 * <p>
 * Methods implemented here are used commonly by element readers used to process <i>XML</i> formats.
 * 
 * @author Sarah Wiechers
 *
 */
public abstract class AbstractXMLElementReader<P extends XMLReaderStreamDataProvider<? extends AbstractXMLEventReader<P>>>
		implements XMLElementReader<P> {
	
	
	protected void readAttributes(P streamDataProvider, StartElement element, String idPrefix, AttributeInfo... attributeInformation) 
			throws JPhyloIOReaderException {
		
		LinkedHashMap<QName, AttributeInfo> attributeInformationMap = new LinkedHashMap<QName, AttributeInfo>();
		for (int i  = 0; i  < attributeInformation.length; i++) {
			attributeInformationMap.put(attributeInformation[i].getAttributeName(), attributeInformation[i]);
		}
		
		readAttributes(streamDataProvider, element, idPrefix, attributeInformationMap);
	}
	
	
	protected void readAttributes(P streamDataProvider, StartElement element, String idPrefix, LinkedHashMap<QName, AttributeInfo> attributeInformationMap) 
			throws JPhyloIOReaderException {
		
		if ((attributeInformationMap != null) && !attributeInformationMap.isEmpty()) {
			String metaIDPrefix = idPrefix + ReadWriteConstants.DEFAULT_META_ID_PREFIX;
			
			for (QName attribute : attributeInformationMap.keySet()) {
				if (element.getAttributeByName(attribute) != null) {
					String attributeValue = element.getAttributeByName(attribute).getValue();
					QName datatype = attributeInformationMap.get(attribute).getDatatype(); 
					Object objectValue = null;					

					if (datatype != null) {
						ObjectTranslator<?> translator = streamDataProvider.getParameters().getObjectTranslatorFactory()
								.getDefaultTranslatorWithPossiblyInvalidNamespace(datatype);
						
						if (translator != null) {
							try {
								objectValue = translator.representationToJava(attributeValue, streamDataProvider);
							}
							catch (InvalidObjectSourceDataException e) {
								throw new JPhyloIOReaderException("The content of the XML attribute \"" + attribute + "\" (\"" + attributeValue + 
										"\") could not be parsed as class " + translator.getObjectClass().getSimpleName() + ".", 
										element.getLocation());
							}
						}
						else if (datatype.equals(XTGConstants.DATA_TYPE_COLOR)) {  //TODO Handle this after checking for a translator or do not handle at all. (TG translators could be used instead.)
							try {
								objectValue = Color.decode(attributeValue);
							}
							catch (IllegalArgumentException f) {}
						}
					}
					
					streamDataProvider.getCurrentEventCollection().add(
							new LiteralMetadataEvent(metaIDPrefix + streamDataProvider.getIDManager().createNewID(), null, 
							new URIOrStringIdentifier(null, attributeInformationMap.get(attribute).getPredicate()), 
							new URIOrStringIdentifier(null, attributeInformationMap.get(attribute).getDatatype()), LiteralContentSequenceType.SIMPLE));
					
					if (attributeValue != null) {
						streamDataProvider.getCurrentEventCollection().add(new LiteralMetadataContentEvent(objectValue, attributeValue));
					}
							
					streamDataProvider.getCurrentEventCollection().add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.LITERAL_META));					
				}
			}
		}
	}
}