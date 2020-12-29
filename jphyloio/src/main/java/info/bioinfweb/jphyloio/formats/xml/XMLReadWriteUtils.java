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


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLConstants;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLConstants;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;



/**
 * Provides commonly used tool methods and constants used for XML reader and writer classes.
 * 
 * @author Sarah Wiechers
 * @since 0.0.0
 */
public class XMLReadWriteUtils {	
	public static final String XSI_DEFAULT_PRE = "xsi";
	public static final String XSD_DEFAULT_PRE = "xsd";	
	public static final String RDF_DEFAULT_PRE = "rdf";
	
	public static final String SCHEMA_LOCATION = "schemaLocation";
	
	public static final String NAMESPACE_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns";
	
//	public static final QName ATTRIBUTE_RDF_PROPERTY = new QName(NAMESPACE_RDF, "property");
//	public static final QName ATTRIBUTE_RDF_DATATYPE = new QName(NAMESPACE_RDF, "datatype");
	//TODO Is an "about"-attribute needed if these attributes are used? Does PhyloXML with about attributes still validate?
	
  public static final String DEFAULT_NAMESPACE_PREFIX = "p";
	
	
	public static String getXSIPrefix(XMLStreamWriter writer) throws XMLStreamException {
		String prefix = writer.getPrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		if (prefix == null || prefix.isEmpty()) {
			prefix = XSI_DEFAULT_PRE;
		}
		return prefix;
	}
	
	
	public static String getXSDPrefix(XMLStreamWriter writer) throws XMLStreamException {
		String prefix = writer.getPrefix(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		if (prefix == null || prefix.isEmpty()) {
			prefix = XSD_DEFAULT_PRE;
		}
		return prefix;
	}
	
	
//	public static String getRDFPrefix(XMLStreamWriter writer) throws XMLStreamException {
//		String prefix = writer.getPrefix(NAMESPACE_RDF);
//		if (prefix == null || prefix.isEmpty()) {
//			prefix = RDF_DEFAULT_PRE;
//		}
//		return prefix;
//	}
	
	
	/**
	 * This method returns a predefined default prefix for a number of namespaces or, if the given namespace does not have a predefined prefix 
	 * and the given prefix was {@code null}, {@link DEFAULT_NAMESPACE_PREFIX}.
	 * 
	 * @param writer the currently used {@link XMLStreamWriter} 
	 * @param givenPrefix the prefix that shall be bound to a namespace
	 * @param namespaceURI the namespace a prefix shall be bound to
	 * @return either the given prefix or a default prefix
	 * @throws XMLStreamException
	 */
	public static String getDefaultNamespacePrefix(XMLStreamWriter writer, String givenPrefix, String namespaceURI) throws XMLStreamException {
		String result = givenPrefix;
		
		if (namespaceURI.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI)) {
			result = XSI_DEFAULT_PRE;
		}
		else if (namespaceURI.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
			result = XSD_DEFAULT_PRE;
		}
		else if (namespaceURI.equals(NAMESPACE_RDF)) {
			result = RDF_DEFAULT_PRE;
		}
		else if (namespaceURI.equals(NeXMLConstants.NEXML_NAMESPACE)) {
			result = NeXMLConstants.NEXML_DEFAULT_NAMESPACE_PREFIX;
		}
		else if (namespaceURI.equals(PhyloXMLConstants.PHYLOXML_NAMESPACE)) {
			result = PhyloXMLConstants.PHYLOXML_DEFAULT_PRE;
		}
		else if (namespaceURI.equals(ReadWriteConstants.JPHYLOIO_PREDICATE_NAMESPACE)) {
			result = ReadWriteConstants.JPHYLOIO_PREDICATE_PREFIX;
		}
		else if (namespaceURI.equals(ReadWriteConstants.JPHYLOIO_ATTRIBUTES_NAMESPACE)) {
			result = ReadWriteConstants.JPHYLOIO_ATTRIBUTES_PREFIX;
		}
		else if ((result == null) || result.isEmpty()) {
			result = DEFAULT_NAMESPACE_PREFIX;
		}

		return result;
	}
	
	
	public static void writeCustomXML(XMLStreamWriter writer, ReadWriteParameterMap parameters, XMLEvent event) throws XMLStreamException {
		switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				StartElement element = event.asStartElement();
				boolean manageCustomXMLNamespaces = parameters.getBoolean(ReadWriteParameterNames.KEY_CUSTOM_XML_NAMESPACE_HANDLING, false);
				
				String prefix = obtainCustomXMLPrefix(writer, element.getName().getPrefix(), element.getName().getNamespaceURI(), manageCustomXMLNamespaces);
				if ((prefix == null) || prefix.isEmpty()) {
					if ((element.getName().getNamespaceURI() == null) || element.getName().getNamespaceURI().isEmpty()) {
						writer.writeStartElement(element.getName().getLocalPart());
					}
					else {
						writer.writeStartElement(element.getName().getNamespaceURI(), element.getName().getLocalPart());
					}
				}					
				else {
					writer.writeStartElement(prefix, element.getName().getLocalPart(), element.getName().getNamespaceURI());
				}
				
				// Write attributes
				@SuppressWarnings("unchecked")
				Iterator<Attribute> attributes = element.getAttributes();
				while (attributes.hasNext()) {	
					writeAttribute(writer, attributes.next());
				}
				
				// Write namespace declarations if they define a new default namespace or the according parameter is set to false
				@SuppressWarnings("unchecked")
				Iterator<Namespace> namespaces = element.getNamespaces();
				while (namespaces.hasNext()) {
					Namespace namespace = namespaces.next();
					
					if (namespace.getPrefix().equals("")) {						
						writer.writeDefaultNamespace(namespace.getNamespaceURI());
					}
					else if (!manageCustomXMLNamespaces) {
						writer.writeNamespace(obtainCustomXMLPrefix(writer, namespace.getPrefix(), namespace.getNamespaceURI(), manageCustomXMLNamespaces),
								namespace.getNamespaceURI());
					}
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				writer.writeEndElement();
				break;
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.SPACE:
				writer.writeCharacters(event.asCharacters().getData());
				break;
			case XMLStreamConstants.CDATA:
				writer.writeCData(event.asCharacters().getData()); //TODO multiple events with continued content should be buffered and written to a single CDATA element
				break;
			case XMLStreamConstants.ATTRIBUTE:
				writeAttribute(writer, ((Attribute)event));
				break;
			case XMLStreamConstants.NAMESPACE:
				Namespace contentNamespace = ((Namespace)event);
				
				if (contentNamespace.getPrefix().equals("") || !parameters.getBoolean(ReadWriteParameterNames.KEY_CUSTOM_XML_NAMESPACE_HANDLING, false)) {
					writer.writeNamespace(contentNamespace.getPrefix(), contentNamespace.getNamespaceURI());
				}
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				ProcessingInstruction contentProcessingInstruction = ((ProcessingInstruction)event);
				if (contentProcessingInstruction.getData() == null) {
					writer.writeProcessingInstruction(contentProcessingInstruction.getTarget());
				}
				else {
					writer.writeProcessingInstruction(contentProcessingInstruction.getTarget(), contentProcessingInstruction.getData());
				}
				break;
			case XMLStreamConstants.COMMENT:
				writer.writeComment(((Comment)event).getText());
				break;
			case XMLStreamConstants.DTD:
				StringBuffer message = new StringBuffer();
				message.append("A document type declaration (DTD) with the content \"");
				
				if (((DTD)event).getDocumentTypeDeclaration().length() > 128) {
					message.append(((DTD)event).getDocumentTypeDeclaration().substring(0, 128));
					message.append(" [...]");
				}
				else {
					message.append(((DTD)event).getDocumentTypeDeclaration());
				}
				
				message.append("\" was found but can not be written at this position of the document.");
				parameters.getLogger().addWarning(message.toString());
				break;
			case XMLStreamConstants.NOTATION_DECLARATION:
				parameters.getLogger().addWarning("A notation declaration with the name \"" + ((NotationDeclaration)event).getName() + "\" was found but"
						+ "can not be written at this position of the document.");
				break;
			case XMLStreamConstants.ENTITY_DECLARATION:
				parameters.getLogger().addWarning("An entity declaration with the name \"" + ((EntityDeclaration)event).getName() + "\" was found but"
						+ "can not be written at this position of the document.");
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				writer.writeEntityRef(((EntityReference)event).getName());
				break;
			default: // START_DOCUMENT and END_DOCUMENT can be ignored
				break;
		}
	}
	
	
	private static String obtainCustomXMLPrefix(XMLStreamWriter writer, String prefix, String namespaceURI, boolean manageCustomXMLNamespaces) throws XMLStreamException {
		if (manageCustomXMLNamespaces) {
			return writer.getPrefix(namespaceURI);  // Writer obtains the correct prefix from its namespace context if custom XML namespaces are managed
		}
		else {
			return prefix;
		}
	}
	
	
	private static void writeAttribute(XMLStreamWriter writer, Attribute attribute) throws XMLStreamException {
		String prefix = writer.getPrefix(attribute.getName().getNamespaceURI());
		if ((prefix == null) || prefix.isEmpty()) {
			if ((attribute.getName().getNamespaceURI() == null) || attribute.getName().getNamespaceURI().isEmpty()) {
				writer.writeAttribute(attribute.getName().getLocalPart(), attribute.getValue());
			}
			else {
				writer.writeAttribute(attribute.getName().getNamespaceURI(), attribute.getName().getLocalPart(), attribute.getValue());
			}
		}
		else {
			writer.writeAttribute(writer.getPrefix(attribute.getName().getNamespaceURI()), attribute.getName().getNamespaceURI(), 
					attribute.getName().getLocalPart(), attribute.getValue());
		}
	}
	
	
	/**
	 * This method manages namespaces used or declared in any custom XML events depending on the parameter 
	 * {@link ReadWriteParameterNames#KEY_CUSTOM_XML_NAMESPACE_HANDLING}. In case this parameter is set to {@code false} 
	 * or not set at all application developers need to make sure that all prefixes used in the custom XML are properly 
	 * declared within the custom XML. In case it is set to {@code true} it is possible that the prefix a namespace is 
	 * bound to is altered. In both cases default namespace declarations are written to the according custom XML elements, 
	 * therefore these are never managed here.
	 * <p>
	 * Namespaces used in object values of type {@link QName} are always managed.
	 * 
	 * @param streamDataProvider the StreamDataProvider used by the current writer
	 * @param event the LiteralMetadataContentEvent containing some content
	 * 
	 * @throws XMLStreamException if the underlying writer encounters an exception while writing
	 */
	public static void manageLiteralContentMetaNamespaces(XMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameters,
			LiteralMetadataContentEvent event) throws XMLStreamException {
		
		QName resourceIdentifier;
		
		if (event.hasXMLEventValue()) {
			
			if (parameters.getBoolean(ReadWriteParameterNames.KEY_CUSTOM_XML_NAMESPACE_HANDLING, false)) {
				
				switch (event.getXMLEvent().getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						StartElement element = event.getXMLEvent().asStartElement();
						resourceIdentifier = element.getName();					

						streamDataProvider.setNamespacePrefix(getDefaultNamespacePrefix(streamDataProvider.getWriter(),	
								resourceIdentifier.getPrefix(), resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
						
						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributesIterator = element.getAttributes();
						while (attributesIterator.hasNext()) {
							Attribute attribute = attributesIterator.next();
							resourceIdentifier = attribute.getName();
							
							streamDataProvider.setNamespacePrefix(getDefaultNamespacePrefix(streamDataProvider.getWriter(), resourceIdentifier.getPrefix(), 
									resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
						}
						
						@SuppressWarnings("unchecked")
						Iterator<Namespace> namespaceIterator = element.getNamespaces();
						while (namespaceIterator.hasNext()) {
							Namespace namespace = namespaceIterator.next();
							
							// Default namespace declarations are always written, so they do not need to be managed here
							if (!namespace.getPrefix().equals("")) {
								streamDataProvider.setNamespacePrefix(getDefaultNamespacePrefix(streamDataProvider.getWriter(), namespace.getPrefix(), 
										namespace.getNamespaceURI()), namespace.getNamespaceURI());
							}							
						}
						break;
					case XMLStreamConstants.ATTRIBUTE:
						Attribute attribute = (Attribute)event.getXMLEvent();
						resourceIdentifier = attribute.getName();					

						streamDataProvider.setNamespacePrefix(getDefaultNamespacePrefix(streamDataProvider.getWriter(),	
								resourceIdentifier.getPrefix(), resourceIdentifier.getNamespaceURI()), resourceIdentifier.getNamespaceURI());
						break;
					case XMLStreamConstants.NAMESPACE:
						Namespace namespace = (Namespace)event.getXMLEvent();

						if (!namespace.getPrefix().equals("")) {
							streamDataProvider.setNamespacePrefix(getDefaultNamespacePrefix(streamDataProvider.getWriter(),	namespace.getPrefix(), 
									namespace.getNamespaceURI()), namespace.getNamespaceURI());
						}
						break;
					default:
						break;
				}				
			}
		}
		else if (event.hasObjectValue() && (event.getObjectValue() instanceof QName)) {
			QName objectValue = (QName)event.getObjectValue();
			streamDataProvider.setNamespacePrefix(getDefaultNamespacePrefix(streamDataProvider.getWriter(), objectValue.getPrefix(), 
					objectValue.getNamespaceURI()), objectValue.getNamespaceURI());			
		}
	}
}
