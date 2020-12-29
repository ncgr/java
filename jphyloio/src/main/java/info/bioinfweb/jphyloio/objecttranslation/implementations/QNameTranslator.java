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
package info.bioinfweb.jphyloio.objecttranslation.implementations;


import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.JPhyloIOXMLEventReader;
import info.bioinfweb.jphyloio.formats.xml.JPhyloIOXMLEventWriter;
import info.bioinfweb.jphyloio.formats.xml.XMLReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.xml.XMLWriterStreamDataProvider;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;



/**
 * An object translator from and to {@link QName}. If an instance of {@link XMLReaderStreamDataProvider} is specified when calling a
 * parser method, the namespace related to a possible prefix will be determined. Otherwise {@link XMLConstants#NULL_NS_URI} will be
 * set by default.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class QNameTranslator extends IllegalArgumentExceptionSimpleValueTranslator<QName> {
	@Override
	public Class<QName> getObjectClass() {
		return QName.class;
	}

	
	@Override
	protected QName parseValue(String representation, ReaderStreamDataProvider<?> streamDataProvider)	throws IllegalArgumentException {
		if (streamDataProvider instanceof XMLReaderStreamDataProvider<?>) {
			return DatatypeConverter.parseQName(representation, ((JPhyloIOXMLEventReader)streamDataProvider.getEventReader()).getNamespaceContext());  //TODO Is it allowed in parseQName() if namespaceContext is null?
		}
		else {
			int splitPos = representation.indexOf(XMLUtils.QNAME_SEPARATOR);
			if (splitPos == -1) {
				return new QName(representation);
			}
			else {
				return new QName(XMLConstants.NULL_NS_URI, representation.substring(splitPos + 1), representation.substring(0, splitPos));
			}
			//TODO Should any additional validation (e.g. of NCNames) be done in here?
			//TODO Should parsing QNames including "{namespaceURI} also be supported?
		}
	}


	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider) throws UnsupportedOperationException, ClassCastException {  //TODO Provide WriterStreamDataProvider here
		QName qName = (QName)object;
		
		if (streamDataProvider instanceof XMLWriterStreamDataProvider<?>) {			
			return DatatypeConverter.printQName(qName, ((JPhyloIOXMLEventWriter)streamDataProvider.getEventWriter()).getNamespaceContext());
		}
		else {
			if ("".equals(qName.getPrefix())) {  // Constructing instances with null is not possible.
				return qName.getLocalPart();
			}
			else {
				return qName.getPrefix() + XMLUtils.QNAME_SEPARATOR + qName.getLocalPart();
			}
		}		
		//TODO Should output of QNames including "{namespaceURI} also or in another translator be supported?
	}
}
