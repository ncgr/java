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
package info.bioinfweb.jphyloio.formats.nexml;


import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.nexml.receivers.AbstractNeXMLDataReceiver;
import info.bioinfweb.jphyloio.formats.xml.XMLWriterStreamDataProvider;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;



/**
 * Stores data that shall be shared among different implementations of {@link AbstractNeXMLDataReceiver}
 * and the writer instance that uses them.
 * 
 * @author Sarah Wiechers
 */
public class NeXMLWriterStreamDataProvider extends XMLWriterStreamDataProvider<NeXMLEventWriter> implements NeXMLConstants {	
	private Set<String> documentIDs = new HashSet<String>();
	private int idIndex = 0;
	
	private LiteralContentSequenceType currentLiteralMetaSequenceType;
	private URIOrStringIdentifier currentLiteralMetaDatatype;
	
	private Map<String, EnumMap<EventContentType, Set<String>>> setIDToSetElementsMap = new HashMap<String, EnumMap<EventContentType,Set<String>>>();
	
	private boolean hasOTUList = true;
	private boolean writeUndefinedOTU = false;
	private boolean writeUndefinedOtuList = false;
	private String undefinedOTUID;
	private String undefinedOTUsID;
	
	private NeXMLWriterAlignmentInformation currentAlignmentInfo;
	private Map<String, NeXMLWriterAlignmentInformation> alignmentInfoByIDMap = new HashMap<String, NeXMLWriterAlignmentInformation>();
	
	private NeXMLWriterTokenSetInformation currentTokenSetInfo;
	
	private String singleToken = null;


	public NeXMLWriterStreamDataProvider(NeXMLEventWriter eventWriter) {
		super(eventWriter);
	}


	public Set<String> getDocumentIDs() {
		return documentIDs;
	}
	
	
	public int getIDIndex() {
		return idIndex;
	}


	public void setIDIndex(int idIndex) {
		this.idIndex = idIndex;
	}


	public void addToDocumentIDs(String id) throws JPhyloIOWriterException {
		if (!getDocumentIDs().add(id)) {
			throw new InconsistentAdapterDataException("The encountered ID " + id + " already exists in the document. IDs have to be unique.");
		}
	}


	public boolean hasOTUList() {
		return hasOTUList;
	}


	public void setHasOTUList(boolean hasOTUList) {
		this.hasOTUList = hasOTUList;
	}


	public LiteralContentSequenceType getCurrentLiteralMetaSequenceType() {
		return currentLiteralMetaSequenceType;
	}


	public void setCurrentLiteralMetaSequenceType(LiteralContentSequenceType currentLiteralMetaType) {
		this.currentLiteralMetaSequenceType = currentLiteralMetaType;
	}


	public URIOrStringIdentifier getCurrentLiteralMetaDatatype() {
		return currentLiteralMetaDatatype;
	}


	public void setCurrentLiteralMetaDatatype(URIOrStringIdentifier currentLiteralMetaDatatype) {
		this.currentLiteralMetaDatatype = currentLiteralMetaDatatype;
	}
	

	public Map<String, EnumMap<EventContentType, Set<String>>> getSetIDToSetElementsMap() {
		return setIDToSetElementsMap;
	}


	public NeXMLWriterAlignmentInformation getCurrentAlignmentInfo() {
		return currentAlignmentInfo;
	}


	public void setCurrentAlignmentInfo(NeXMLWriterAlignmentInformation currentAlignmentInfo) {
		this.currentAlignmentInfo = currentAlignmentInfo;
	}


	public Map<String, NeXMLWriterAlignmentInformation> getAlignmentInfoByIDMap() {
		return alignmentInfoByIDMap;
	}


	public NeXMLWriterTokenSetInformation getCurrentTokenSetInfo() {
		return currentTokenSetInfo;
	}


	public void setCurrentTokenSetInfo(NeXMLWriterTokenSetInformation currentTokenSetInfo) {
		this.currentTokenSetInfo = currentTokenSetInfo;
	}


	public String getSingleToken() {
		return singleToken;
	}


	public void setSingleToken(String singleToken) {
		this.singleToken = singleToken;
	}


	public boolean isWriteUndefinedOTU() {
		return writeUndefinedOTU;
	}


	public void setWriteUndefinedOTU(boolean writeUndefinedOTU) {
		this.writeUndefinedOTU = writeUndefinedOTU;
	}


	public boolean isWriteUndefinedOtuList() {
		return writeUndefinedOtuList;
	}


	public void setWriteUndefinedOtuList(boolean writeUndefinedOtuList) {
		this.writeUndefinedOtuList = writeUndefinedOtuList;
	}
	
	
	public String getUndefinedOTUID() {
		return undefinedOTUID;
	}


	public void setUndefinedOTUID(String undefinedOTUID) {
		this.undefinedOTUID = undefinedOTUID;
	}


	public String getUndefinedOTUsID() {
		return undefinedOTUsID;
	}


	public void setUndefinedOTUsID(String undefinedOTUsID) {
		this.undefinedOTUsID = undefinedOTUsID;
	}


	public String createNewID(String prefix) {
		String id;
		
		do {
			id = prefix + getIDIndex();
			setIDIndex(getIDIndex() + 1);
		} while (getDocumentIDs().contains(id));
		
		return id;
	}
	
	
	public void writeLabeledIDAttributes(LabeledIDEvent event) throws XMLStreamException, JPhyloIOWriterException {
		writeLabeledIDAttributes(event, event.getID());
	}


	public void writeLabeledIDAttributes(LabeledIDEvent event, String about) throws XMLStreamException, JPhyloIOWriterException {
		getWriter().writeAttribute(ATTR_ID.getLocalPart(), event.getID());
		
		if (about != null) {
			getWriter().writeAttribute(ATTR_ABOUT.getLocalPart(), "#" + about);
		}
		
		if (event.hasLabel()) {
			getWriter().writeAttribute(ATTR_LABEL.getLocalPart(), event.getLabel());
		}
	}
	
	
	public void writeLinkedLabeledIDAttributes(LinkedLabeledIDEvent event, QName linkAttribute, boolean forceOTULink) throws XMLStreamException, JPhyloIOWriterException {		
		writeLabeledIDAttributes(event);
		if (event.hasLink()) {
			if (hasOTUList()) {
				if (!getDocumentIDs().contains(event.getLinkedID())) {
					throw new InconsistentAdapterDataException("An element links to a non-existent OTU list or OTU.");
				}
				getWriter().writeAttribute(linkAttribute.getLocalPart(), event.getLinkedID());
			}
			else {
				throw new InconsistentAdapterDataException("An element links to an OTU list or OTU though no OTU list exists in the document.");
			}
		}
		else if (forceOTULink) {
			if (linkAttribute.equals(TAG_OTUS)) {
				getWriter().writeAttribute(linkAttribute.getLocalPart(), getUndefinedOTUsID());			
			}
			else if (linkAttribute.equals(TAG_OTU)) {
				getWriter().writeAttribute(linkAttribute.getLocalPart(), getUndefinedOTUID());
			}
		}
	}
	
	
	public String getNeXMLPrefix(XMLStreamWriter writer) throws XMLStreamException {
		String prefix = writer.getPrefix(NeXMLConstants.NEXML_NAMESPACE);
		if (prefix == null || prefix.isEmpty()) {
			prefix = NeXMLConstants.NEXML_DEFAULT_NAMESPACE_PREFIX;
		}
		return prefix;
	}
}