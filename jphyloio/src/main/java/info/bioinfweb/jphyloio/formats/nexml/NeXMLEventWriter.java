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


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.JPhyloIO;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.AnnotatedDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.TreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.UndefinedOTUListDataAdapter;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.nexml.receivers.AbstractNeXMLDataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLAttributeMetadataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLCharacterSetEventReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLCollectCharSetDataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLCollectNamespaceReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLCollectSequenceDataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLCollectSetMetadataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLCollectTokenSetDefinitionDataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLIgnoreCertainMetadataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLMetaDataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLMolecularDataTokenDefinitionReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLOnlyWriteMetaDataReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLPredicateMetaReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLSequenceTokensReceiver;
import info.bioinfweb.jphyloio.formats.nexml.receivers.NeXMLTokenSetEventReceiver;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLEventWriter;
import info.bioinfweb.jphyloio.formats.xml.XMLReadWriteUtils;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



/**
 * Event based writer for the <a href="http://nexml.org/">NeXML format</a>.
 * <p>
 * All data elements modeled by <i>JPhyloIO</i> are supported by this writer, since the data model of <i>JPhyloIO</i> 
 * is heavily influenced by the data model of <i>NeXML</i>. 
 * <p>
 * Comments and meta information can be written under all modeled elements. Meta elements only present to group meta information, 
 * e.g. nested under elements not modeled in <i>JPhyloIO</i>, are ignored by this writer and the nested content is written to the 
 * according position. The ID of a {@link JPhyloIOEvent} will be written to the file unchanged.
 * <p>
 * Namespaces used or declared in custom XML elements are managed according to {@link ReadWriteParameterNames#KEY_CUSTOM_XML_NAMESPACE_HANDLING}. 
 * More information about this can be found in the documentation of {@link XMLReadWriteUtils#manageLiteralContentMetaNamespaces()}.
 * <p>
 * If no OTUs or OTU list are found in the data, but elements that need to reference either of those, the writer will write a 
 * new OTU list or OTU with the ID prefix {@link NeXMLConstants#UNDEFINED_OTU_ID_PREFIX} or 
 * {@link NeXMLConstants#UNDEFINED_OTUS_ID_PREFIX}. If no token set is present in the data, a new token set with the type
 * {@link CharacterStateSetType#DISCRETE} and the ID prefix {@link NeXMLConstants#DEFAULT_TOKEN_DEFINITION_SET_ID_PREFIX} will 
 * be written to the file. If not all alignment characters are defined in the document data, missing definitions will be add.
 * <p>
 * Before writing, the token definitions and sequence data are checked if they fit the specified {@link CharacterStateSetType}.
 * If this is not the case, the {@link CharacterStateSetType#DISCRETE} is assumed. In case of {@code DNA}, {@code RNA} 
 * or {@code NUCLEOTIDE} data it is first checked if the data could also be of another nucleotide data type. All molecular
 * data token definition sets contain all token definitions defined in the IUPAC standards. If single token definitions are missing, 
 * they are add. Token definitions of standard tokens found in the sequence data but not defined previously are also add
 * in case of standard data.
 * <p>
 * Restriction data can not be written, since it is not modeled by <i>JPhyloIO</i>. If the {@link CharacterStateSetType} of the 
 * sequence data is {@code DSICRETE} tokens are translated to integers as required by <i>NeXML</i>.
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3> 
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_WRITER_INSTANCE}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_LOGGER}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_APPLICATION_NAME}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_APPLICATION_VERSION}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_APPLICATION_URL}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_NEXML_TOKEN_DEFINITION_LABEL}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_NEXML_TOKEN_DEFINITION_LABEL_METADATA}</li>
 *   <li>{@link ReadWriteParameterNames#KEY_CUSTOM_XML_NAMESPACE_HANDLING}</li>
 * </ul>
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class NeXMLEventWriter extends AbstractXMLEventWriter<NeXMLWriterStreamDataProvider> implements NeXMLConstants {
	public NeXMLEventWriter() {
		super();
	}


	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.NEXML_FORMAT_ID;
	}
	
	
	@Override
	protected NeXMLWriterStreamDataProvider createStreamDataProvider() {
		return new NeXMLWriterStreamDataProvider(this);
	}
	
	
	@Override
	protected void doWriteDocument() throws IOException, XMLStreamException {
		// Bind default prefixes here to avoid having to change them if an application tries to use them later on
		getStreamDataProvider().setNamespacePrefix(getStreamDataProvider().getNeXMLPrefix(getXMLWriter()), NEXML_NAMESPACE);
		getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getXSIPrefix(getXMLWriter()), XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
		getStreamDataProvider().setNamespacePrefix(XMLReadWriteUtils.getXSDPrefix(getXMLWriter()), XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		checkDocument(getDocument());
		
		getXMLWriter().writeStartElement(TAG_ROOT.getLocalPart());

		getXMLWriter().writeDefaultNamespace(NEXML_NAMESPACE);
		getXMLWriter().writeAttribute(ATTR_VERSION.getLocalPart(), NEXML_VERSION);
		
		StringBuilder generator = new StringBuilder();		
		
		if (getParameters().getString(ReadWriteParameterMap.KEY_APPLICATION_NAME) != null) {
			generator.append(getParameters().getString(ReadWriteParameterMap.KEY_APPLICATION_NAME));
			generator.append(" ");
			
			if (getParameters().getObject(ReadWriteParameterMap.KEY_APPLICATION_VERSION, null) != null) {				
				generator.append(getParameters().getObject(ReadWriteParameterMap.KEY_APPLICATION_VERSION, null).toString());
				generator.append(" ");
			}
			
			generator.append("using ");
		}
		
		generator.append(JPhyloIO.getInstance().getLibraryNameAndVersion());
		
		getXMLWriter().writeAttribute(ATTR_GENERATOR.getLocalPart(), generator.toString());
		
		for (String prefix : getStreamDataProvider().getNamespacePrefixes()) {
			getXMLWriter().writeNamespace(prefix, getXMLWriter().getNamespaceContext().getNamespaceURI(prefix));
		}
		
		getXMLWriter().writeComment(" " + getFileStartInfo(getParameters()) + " ");

		writeOrCheckMetaData(getDocument(), false);

		writeOTUSTags(getDocument());
		writeCharactersTags(getDocument());
		writeTreesTags(getDocument());

		getXMLWriter().writeEndElement();
		
		getStreamDataProvider().setUndefinedOTUID(null);
		getStreamDataProvider().setUndefinedOTUsID(null); 
		getStreamDataProvider().getDocumentIDs().clear();
	}


	private void checkDocument(DocumentDataAdapter document) throws IOException {
		writeOrCheckMetaData(document, true);

		if (document.getOTUListCount(getParameters()) > 0) {
			checkOTUSTags(document);
		}
		else {
			getStreamDataProvider().setHasOTUList(false);
			getStreamDataProvider().setWriteUndefinedOtuList(true);
		}

		if (document.getMatrixIterator(getParameters()).hasNext()) {
			checkCharactersTags(document);
		}

		if (document.getTreeNetworkGroupIterator(getParameters()).hasNext()) {
			checkTreeAndNetworkGroups(document);
		}
	}


	private void writeOrCheckMetaData(AnnotatedDataAdapter adapter, boolean check) throws IOException {
		JPhyloIOEventReceiver receiver;

		if (check) {
			receiver = new NeXMLCollectNamespaceReceiver(getStreamDataProvider(), getParameters());
		}
		else {
			receiver = new NeXMLMetaDataReceiver(getStreamDataProvider(), getParameters());
		}

		adapter.writeMetadata(getParameters(), receiver);
	}


	private void writeOrCheckObjectMetaData(ObjectListDataAdapter<?> adapter, String objectID, boolean check) throws IOException {
		AbstractNeXMLDataReceiver receiver;

		if (check) {
			receiver = new NeXMLCollectNamespaceReceiver(getStreamDataProvider(), getParameters());
		}
		else {
			receiver = new NeXMLMetaDataReceiver(getStreamDataProvider(), getParameters());
		}

		if (objectID != null) {
			adapter.writeContentData(getParameters(), receiver, objectID);
		}
	}
	
	
	private void writeSetTags(EnumMap<EventContentType, String> elementTypeToLinkAttributeMap, ObjectListDataAdapter<LinkedLabeledIDEvent> setAdapter) 
			throws XMLStreamException, IllegalArgumentException, IOException {
		NeXMLOnlyWriteMetaDataReceiver receiver = new NeXMLOnlyWriteMetaDataReceiver(getStreamDataProvider(), getParameters());
		
		Iterator<String> setIDIterator = setAdapter.getIDIterator(getParameters());		
		while (setIDIterator.hasNext()) {
			String setID = setIDIterator.next();
			
			getXMLWriter().writeStartElement(TAG_SET.getLocalPart());
			getStreamDataProvider().writeLabeledIDAttributes(setAdapter.getObjectStartEvent(getParameters(), setID));
				
			for (EventContentType type : elementTypeToLinkAttributeMap.keySet()) {
				StringBuffer setElements = new StringBuffer();
				for (String elementID : getStreamDataProvider().getSetIDToSetElementsMap().get(setID).get(type)) {
					setElements.append(elementID);
					setElements.append(" ");
				}
				
				if (setElements.length() != 0) {
					getXMLWriter().writeAttribute(elementTypeToLinkAttributeMap.get(type), setElements.toString());
				}
			}
			
			setAdapter.writeContentData(getParameters(), receiver, setID);
			
			getXMLWriter().writeEndElement();
		}
	}
	
	
	private void checkSets(EnumMap<EventContentType, String> elementTypeToLinkAttributeMap, EventContentType setType, ObjectListDataAdapter<LinkedLabeledIDEvent> setAdapter) 
				throws IllegalArgumentException, IOException {
		NeXMLCollectSetMetadataReceiver receiver;
		Set<String> encounteredSetIDs = new TreeSet<String>();
		
		Iterator<String> setIDIterator = setAdapter.getIDIterator(getParameters());		
		while (setIDIterator.hasNext()) {
			String setID = setIDIterator.next();
			getStreamDataProvider().addToDocumentIDs(setID);
			encounteredSetIDs.add(setID);
			
			receiver = new NeXMLCollectSetMetadataReceiver(getStreamDataProvider(), getParameters(), setID, false);
			getStreamDataProvider().getSetIDToSetElementsMap().put(setID, new EnumMap<EventContentType, Set<String>>(EventContentType.class));
			
			// Add collection for IDs referencing other sets and all supported event types
			getStreamDataProvider().getSetIDToSetElementsMap().get(setID).put(setType, new TreeSet<String>());
			for (EventContentType type : elementTypeToLinkAttributeMap.keySet()) {
				getStreamDataProvider().getSetIDToSetElementsMap().get(setID).put(type, new TreeSet<String>());
			}
			
			setAdapter.writeContentData(getParameters(), receiver, setID);
			
			while (!getStreamDataProvider().getSetIDToSetElementsMap().get(setID).get(setType).isEmpty()) {
				Set<String> referencedSetIDs = new HashSet<String>();
				referencedSetIDs.addAll(getStreamDataProvider().getSetIDToSetElementsMap().get(setID).get(setType));
				getStreamDataProvider().getSetIDToSetElementsMap().get(setID).get(setType).clear();
				
				receiver = new NeXMLCollectSetMetadataReceiver(getStreamDataProvider(), getParameters(), setID, true);
				
				for (String referencedSetID : referencedSetIDs) {
					if (encounteredSetIDs.add(referencedSetID)) {
						setAdapter.writeContentData(getParameters(), receiver, referencedSetID);
					}
					else {
						throw new JPhyloIOWriterException("A circular reference was encountered when writing sets.");
					}
				}
			}
		
			encounteredSetIDs.clear();
		}
	}
	
	
	private void writeOTUSTags(DocumentDataAdapter document) throws IOException, XMLStreamException {
		Iterator<OTUListDataAdapter> otusIterator = document.getOTUListIterator(getParameters());
	
		while (otusIterator.hasNext()) {
			writeOTUSTag(otusIterator.next());			
		}

		if (getStreamDataProvider().isWriteUndefinedOtuList()) {
			getStreamDataProvider().setUndefinedOTUID(getStreamDataProvider().createNewID(UNDEFINED_OTU_ID_PREFIX));
			getStreamDataProvider().setUndefinedOTUsID(getStreamDataProvider().createNewID(UNDEFINED_OTUS_ID_PREFIX));
			UndefinedOTUListDataAdapter undefinedOTUs = new UndefinedOTUListDataAdapter(getStreamDataProvider().getUndefinedOTUsID(), getStreamDataProvider().getUndefinedOTUID());
			writeOTUSTag(undefinedOTUs);
		}
	}


	private void writeOTUSTag(OTUListDataAdapter otuList) throws IOException, XMLStreamException {
		getXMLWriter().writeStartElement(TAG_OTUS.getLocalPart());

		getStreamDataProvider().writeLabeledIDAttributes(otuList.getStartEvent(getParameters()));

		writeOrCheckMetaData(otuList, false);

		Iterator<String> otuIDIterator = otuList.getIDIterator(getParameters());
		Set<String> otuIDs = new HashSet<String>();
		while (otuIDIterator.hasNext()) {
			String otuID = otuIDIterator.next();
			otuIDs.add(otuID);

			getXMLWriter().writeStartElement(TAG_OTU.getLocalPart());
			getStreamDataProvider().writeLabeledIDAttributes(otuList.getObjectStartEvent(getParameters(), otuID));
			writeOrCheckObjectMetaData(otuList, otuID, false);
			getXMLWriter().writeEndElement();
		}

		if (getStreamDataProvider().isWriteUndefinedOTU() && (getStreamDataProvider().getUndefinedOTUID() == null)) {
			getStreamDataProvider().setUndefinedOTUID(getStreamDataProvider().createNewID(UNDEFINED_OTU_ID_PREFIX));
			
			getXMLWriter().writeStartElement(TAG_OTU.getLocalPart());
			getStreamDataProvider().writeLabeledIDAttributes(new LabeledIDEvent(EventContentType.OTU, getStreamDataProvider().getUndefinedOTUID(), UNDEFINED_OTU_LABEL));
			getXMLWriter().writeEndElement();
		}
		
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.OTU, ATTR_OTU_SET_LINKED_IDS.getLocalPart());
		
		writeSetTags(elementTypeToLinkAttributeMap, otuList.getOTUSets(getParameters()));

		getXMLWriter().writeEndElement();
	}


	private void checkOTUSTags(DocumentDataAdapter document) throws IOException {
		Iterator<OTUListDataAdapter> otusIterator = document.getOTUListIterator(getParameters());
		
		while (otusIterator.hasNext()) {
			checkOTUsTag(otusIterator.next());
		}
	}


	private void checkOTUsTag(OTUListDataAdapter otuList) throws IOException {
		getStreamDataProvider().addToDocumentIDs(otuList.getStartEvent(getParameters()).getID());
		writeOrCheckMetaData(otuList, true);

		Iterator<String> otuIDIterator = otuList.getIDIterator(getParameters());
		while (otuIDIterator.hasNext()) {
			String otuID = otuIDIterator.next();
			getStreamDataProvider().addToDocumentIDs(otuID);
			writeOrCheckObjectMetaData(otuList, otuID, true);			
		}
		
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.OTU, ATTR_OTU_SET_LINKED_IDS.getLocalPart());
		
		checkSets(elementTypeToLinkAttributeMap, EventContentType.OTU_SET, otuList.getOTUSets(getParameters()));
	}
	
	
	private void writeCharactersTags(DocumentDataAdapter document) throws IOException, XMLStreamException {
		Iterator<MatrixDataAdapter> matricesIterator = document.getMatrixIterator(getParameters());
		while (matricesIterator.hasNext()) {
			writeCharactersTag(matricesIterator.next());
		}
	}


	private void writeCharactersTag(MatrixDataAdapter alignment) throws IOException, XMLStreamException {
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getAlignmentInfoByIDMap().get(alignment.getStartEvent(getParameters()).getID());
		
		if (alignmentInfo.isWriteAlignment()) {
			getXMLWriter().writeStartElement(TAG_CHARACTERS.getLocalPart());
			getStreamDataProvider().writeLinkedLabeledIDAttributes(alignment.getStartEvent(getParameters()), TAG_OTUS, true);		
			getStreamDataProvider().setCurrentAlignmentInfo(alignmentInfo);
			
			StringBuffer alignmentType = new StringBuffer();
			alignmentType.append(getStreamDataProvider().getNeXMLPrefix(getXMLWriter()));
			alignmentType.append(XMLUtils.QNAME_SEPARATOR);
	
			if (alignmentInfo.isWriteCellsTags()) {
				switch (alignmentInfo.getAlignmentType()) {
					case NUCLEOTIDE:
					case DNA:
						alignmentType.append(TYPE_DNA_CELLS);
						break;
					case RNA:
						alignmentType.append(TYPE_RNA_CELLS);
						break;
					case AMINO_ACID:
						alignmentType.append(TYPE_PROTEIN_CELLS);
						break;
					case CONTINUOUS:
						alignmentType.append(TYPE_CONTIN_CELLS);
						break;
					case DISCRETE:
					case UNKNOWN:  // Should not occur if previous code worked correctly
						alignmentType.append(TYPE_STANDARD_CELLS);
						break;
					default:
						break;
				}
			}
			else {
				switch (alignmentInfo.getAlignmentType()) {
					case DNA:
						alignmentType.append(TYPE_DNA_SEQS);
						break;
					case RNA:
						alignmentType.append(TYPE_RNA_SEQS);
						break;
					case AMINO_ACID:
						alignmentType.append(TYPE_PROTEIN_SEQS);
						break;
					case CONTINUOUS:
						alignmentType.append(TYPE_CONTIN_SEQ);
						break;
					case DISCRETE:
					case UNKNOWN:  // Should not occur if previous code worked correctly
						alignmentType.append(TYPE_STANDARD_SEQ);
						break;
					default:
						break;
				}
			}
			
			getXMLWriter().writeAttribute(XMLReadWriteUtils.getXSIPrefix(getXMLWriter()),
					ATTR_XSI_TYPE.getNamespaceURI(), ATTR_XSI_TYPE.getLocalPart(), alignmentType.toString());
			
			NeXMLPredicateMetaReceiver predicateDataReceiver = new NeXMLIgnoreCertainMetadataReceiver(getStreamDataProvider(), getParameters(), 
					false, PREDICATE_FORMAT, PREDICATE_MATRIX);  // Do not write format and matrix meta data here
			alignment.writeMetadata(getParameters(), predicateDataReceiver);
	
			writeFormatTag(alignment);
	
			getXMLWriter().writeStartElement(TAG_MATRIX.getLocalPart());  // Tag does not have any attributes
			
			predicateDataReceiver = new NeXMLIgnoreCertainMetadataReceiver(getStreamDataProvider(), getParameters(), 
					true, PREDICATE_MATRIX);
			alignment.writeMetadata(getParameters(), predicateDataReceiver);  // Matrix meta data is written here
	
			Iterator<String> sequenceIDIterator = alignment.getSequenceIDIterator(getParameters());
			while (sequenceIDIterator.hasNext()) {
				writeRowTag(alignment.getSequenceStartEvent(getParameters(), sequenceIDIterator.next()), alignment);
			}
			
			EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
			elementTypeToLinkAttributeMap.put(EventContentType.SEQUENCE, ATTR_SEQUENCE_SET_LINKED_IDS.getLocalPart());
			
			writeSetTags(elementTypeToLinkAttributeMap, alignment.getSequenceSets(getParameters()));
	
			getXMLWriter().writeEndElement();
			getXMLWriter().writeEndElement();
		}
		else {
			getParameters().getLogger().addWarning("Alignment data was found but either the number of sequences or the alignment length were zero. "
					+ "Therefore no characters element was written. It is possible that meta or other data was lost because of this.");
		}
	}


	private void writeFormatTag(MatrixDataAdapter alignment) throws XMLStreamException, IllegalArgumentException, IOException {
		getXMLWriter().writeStartElement(TAG_FORMAT.getLocalPart());
		
		NeXMLPredicateMetaReceiver predicateDataReceiver = new NeXMLIgnoreCertainMetadataReceiver(getStreamDataProvider(), getParameters(), 
				true, PREDICATE_FORMAT);
		alignment.writeMetadata(getParameters(), predicateDataReceiver);  // Format meta data is written here

		writeTokenSetDefinitions(alignment);  // Only written if data is not continuous		
		writeCharacterDefinitionTags(alignment);
		writeCharacterSets(alignment);

		getXMLWriter().writeEndElement();
	}


	private void writeTokenSetDefinitions(MatrixDataAdapter alignment) throws XMLStreamException, IllegalArgumentException, IOException {
		getStreamDataProvider().setIDIndex(0);
		
		ObjectListDataAdapter<TokenSetDefinitionEvent> tokenSetDefinitions = alignment.getTokenSets(getParameters());
		Iterator<String> tokenSetDefinitionIDs = tokenSetDefinitions.getIDIterator(getParameters());		
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getAlignmentInfoByIDMap().get(alignment.getStartEvent(getParameters()).getID());
		
		if (alignmentInfo.hasTokenDefinitionSet()) {
			while (tokenSetDefinitionIDs.hasNext()) {
				String tokenSetID = tokenSetDefinitionIDs.next();
				TokenSetDefinitionEvent startEvent = tokenSetDefinitions.getObjectStartEvent(getParameters(), tokenSetID);
				NeXMLWriterTokenSetInformation info = alignmentInfo.getIDToTokenSetInfoMap().get(tokenSetID);

				switch (alignmentInfo.getAlignmentType()) {
					case CONTINUOUS:  // Can not have a states tag
					case NUCLEOTIDE:  // Should not occur
						break;
					case DNA:
					case RNA:
					case AMINO_ACID:  // Molecular data
						NeXMLMolecularDataTokenDefinitionReceiver molecularDataReceiver = 
								new NeXMLMolecularDataTokenDefinitionReceiver(getStreamDataProvider(), getParameters(), tokenSetID);
						getXMLWriter().writeStartElement(TAG_STATES.getLocalPart());
						getStreamDataProvider().writeLabeledIDAttributes(startEvent);						
						tokenSetDefinitions.writeContentData(getParameters(), molecularDataReceiver, tokenSetID);
						molecularDataReceiver.addRemainingEvents(alignmentInfo.getAlignmentType());
						getXMLWriter().writeEndElement();						
						break;
					default:  // Discrete data
						NeXMLTokenSetEventReceiver receiver = new NeXMLTokenSetEventReceiver(getStreamDataProvider(), getParameters(), tokenSetID);
						getXMLWriter().writeStartElement(TAG_STATES.getLocalPart());
						getStreamDataProvider().writeLabeledIDAttributes(startEvent);
						tokenSetDefinitions.writeContentData(getParameters(), receiver, tokenSetID);
						info.getOccuringTokens().removeAll(info.getTokenTranslationMap().keySet());
						if (!info.getOccuringTokens().isEmpty()) {
							receiver.writeRemainingStandardTokenDefinitions();
						}
						getXMLWriter().writeEndElement();						
				}
			}
		}
		
		if (alignmentInfo.isWriteDefaultTokenSet()) {
			switch (alignmentInfo.getAlignmentType()) {
				case CONTINUOUS:  // Can not have a states tag
				case NUCLEOTIDE:  // Should not occur
					break;
				case DNA:
				case RNA:
				case AMINO_ACID:  // Molecular data
					getXMLWriter().writeStartElement(TAG_STATES.getLocalPart());
					getStreamDataProvider().writeLabeledIDAttributes(new TokenSetDefinitionEvent(alignmentInfo.getAlignmentType(), 
							alignmentInfo.getDefaultTokenSetID(), null));
					new NeXMLMolecularDataTokenDefinitionReceiver(getStreamDataProvider(), getParameters(), 
							alignmentInfo.getDefaultTokenSetID()).addRemainingEvents(alignmentInfo.getAlignmentType());
					getXMLWriter().writeEndElement();
					break;
				default:  // Discrete data
					getXMLWriter().writeStartElement(TAG_STATES.getLocalPart());
					getStreamDataProvider().writeLabeledIDAttributes(new TokenSetDefinitionEvent(alignmentInfo.getAlignmentType(), 
							alignmentInfo.getDefaultTokenSetID(), null));
					if (!alignmentInfo.getIDToTokenSetInfoMap().get(alignmentInfo.getDefaultTokenSetID()).getOccuringTokens().isEmpty()) {
						new NeXMLTokenSetEventReceiver(getStreamDataProvider(), getParameters(), 
								alignmentInfo.getDefaultTokenSetID()).writeRemainingStandardTokenDefinitions();
					}
					getXMLWriter().writeEndElement();
			}
		}
	}
	
	
	private void writeCharacterDefinitionTags(MatrixDataAdapter alignment) throws XMLStreamException, IOException {
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getAlignmentInfoByIDMap().get(alignment.getStartEvent(getParameters()).getID());
		getStreamDataProvider().setIDIndex(0);
		
		// Write character definitions from adapter
		if (alignment.getCharacterDefinitions(getParameters()).getCount(getParameters()) > 0) {
			Iterator<String> charDefinitionIDIterator = alignment.getCharacterDefinitions(getParameters()).getIDIterator(getParameters());
			while (charDefinitionIDIterator.hasNext()) {
				String charID = charDefinitionIDIterator.next();
				CharacterDefinitionEvent charDefEvent = alignment.getCharacterDefinitions(getParameters()).getObjectStartEvent(getParameters(), charID);
				
				alignmentInfo.getColumnIndexToIDMap().put(charDefEvent.getIndex(), charID);
				getStreamDataProvider().addToDocumentIDs(charID);

				getXMLWriter().writeStartElement(TAG_CHAR.getLocalPart());

				getStreamDataProvider().writeLabeledIDAttributes(charDefEvent);
				if (!alignmentInfo.getAlignmentType().equals(CharacterStateSetType.CONTINUOUS)) {
					getXMLWriter().writeAttribute(ATTR_STATES.getLocalPart(), alignmentInfo.getColumnIndexToStatesMap().get(charDefEvent.getIndex()));
				}
				
				NeXMLAttributeMetadataReceiver attributeReceiver = new NeXMLAttributeMetadataReceiver(getStreamDataProvider(), getParameters(), 
						PREDICATE_CHAR_ATTR_TOKENS, PREDICATE_CHAR_ATTR_CODON_POSITION);
				alignment.getCharacterDefinitions(getParameters()).writeContentData(getParameters(), attributeReceiver, charID);
				if (!attributeReceiver.getAttributeToValueMap().isEmpty()) {
					for (QName attribute : attributeReceiver.getAttributeToValueMap().keySet()) {
						if (attribute.equals(PREDICATE_CHAR_ATTR_CODON_POSITION)) {
							getXMLWriter().writeAttribute(ATTR_CODON_POSITION.getLocalPart(), attributeReceiver.getAttributeToValueMap().get(attribute));
						}
						else if (attribute.equals(PREDICATE_CHAR_ATTR_TOKENS)) {
							getXMLWriter().writeAttribute(ATTR_TOKENS.getLocalPart(), attributeReceiver.getAttributeToValueMap().get(attribute));
						}						
					}
				}
				
				NeXMLPredicateMetaReceiver predicateDataReceiver = new NeXMLIgnoreCertainMetadataReceiver(getStreamDataProvider(), getParameters(), 
						false, PREDICATE_CHAR_ATTR_TOKENS, PREDICATE_CHAR_ATTR_CODON_POSITION);
				alignment.getCharacterDefinitions(getParameters()).writeContentData(getParameters(), predicateDataReceiver, charID);  // Metadata created from attributes is not written again
				
				getXMLWriter().writeEndElement();
			}
		}
		
		// Write definitions for remaining characters
		getStreamDataProvider().setIDIndex(0);
		for (long i = 0; i < alignmentInfo.getAlignmentLength(); i++) {
			if (!alignmentInfo.getColumnIndexToIDMap().containsKey(i)) {
				String charID = getStreamDataProvider().createNewID(ReadWriteConstants.DEFAULT_CHARACTER_DEFINITION_ID_PREFIX);
				alignmentInfo.getColumnIndexToIDMap().put(i, charID);
				getStreamDataProvider().addToDocumentIDs(charID);

				getXMLWriter().writeEmptyElement(TAG_CHAR.getLocalPart());

				getXMLWriter().writeAttribute(ATTR_ID.getLocalPart(), charID);
				getXMLWriter().writeAttribute(ATTR_ABOUT.getLocalPart(), "#" + charID);
				
				if (!alignmentInfo.getAlignmentType().equals(CharacterStateSetType.CONTINUOUS)) {
					getXMLWriter().writeAttribute(ATTR_STATES.getLocalPart(), alignmentInfo.getColumnIndexToStatesMap().get(i));
				}
			}
		}
	}


	private void writeCharacterSets(MatrixDataAdapter alignment) throws IllegalArgumentException, XMLStreamException, IOException {
		Iterator<String> characterSetIDs = alignment.getCharacterSets(getParameters()).getIDIterator(getParameters());
		NeXMLCharacterSetEventReceiver receiver = new NeXMLCharacterSetEventReceiver(getStreamDataProvider(), getParameters());
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getAlignmentInfoByIDMap().get(alignment.getStartEvent(getParameters()).getID());

		while (characterSetIDs.hasNext()) {
			String charSetID = characterSetIDs.next();
			
			String linkedMatrixID = alignment.getCharacterSets(getParameters()).getObjectStartEvent(getParameters(), charSetID).getLinkedID();
			if ((linkedMatrixID != null) && !getStreamDataProvider().getDocumentIDs().contains(linkedMatrixID)) {
				throw new InconsistentAdapterDataException("The character set with the ID \"" + charSetID 
						+ "\" linked to the matrix ID \"" + linkedMatrixID + "\" which does not exist in the adapter data.");
			}

			StringBuffer value = new StringBuffer();
			for (long columnIndex : alignmentInfo.getCharSets().get(charSetID)) {
				value.append(alignmentInfo.getColumnIndexToIDMap().get(columnIndex));
				value.append(" ");
			}
			
			getXMLWriter().writeStartElement(TAG_SET.getLocalPart());
			getStreamDataProvider().writeLabeledIDAttributes(alignment.getCharacterSets(getParameters()).getObjectStartEvent(getParameters(), charSetID));
			getXMLWriter().writeAttribute(ATTR_SINGLE_CHAR_LINK.getLocalPart(), value.toString());		

			alignment.getCharacterSets(getParameters()).writeContentData(getParameters(), receiver, charSetID);

			getXMLWriter().writeEndElement();
		}
	}


	private void writeRowTag(LinkedLabeledIDEvent sequenceEvent, MatrixDataAdapter alignment) throws IOException, XMLStreamException {
		boolean longTokens = alignment.containsLongTokens(getParameters());
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getAlignmentInfoByIDMap().get(alignment.getStartEvent(getParameters()).getID());

		if (alignmentInfo.getAlignmentType().equals(CharacterStateSetType.DISCRETE)
				|| alignmentInfo.getAlignmentType().equals(CharacterStateSetType.CONTINUOUS)) {
			longTokens = true;
		}

		NeXMLSequenceTokensReceiver tokenReceiver = new NeXMLSequenceTokensReceiver(getStreamDataProvider(), getParameters(), longTokens);
		NeXMLOnlyWriteMetaDataReceiver metaDataReceiver = new NeXMLOnlyWriteMetaDataReceiver(getStreamDataProvider(), getParameters());

		getXMLWriter().writeStartElement(TAG_ROW.getLocalPart());
		getStreamDataProvider().writeLinkedLabeledIDAttributes(sequenceEvent, TAG_OTU, true);

		alignment.writeSequencePartContentData(getParameters(), metaDataReceiver, sequenceEvent.getID(), 0, alignment.getSequenceLength(getParameters(), sequenceEvent.getID()));

		if (alignmentInfo.isWriteCellsTags()) {
			alignment.writeSequencePartContentData(getParameters(), tokenReceiver, sequenceEvent.getID(), 0, alignment.getSequenceLength(getParameters(), sequenceEvent.getID()));
		}
		else {
			getXMLWriter().writeStartElement(TAG_SEQ.getLocalPart());
			alignment.writeSequencePartContentData(getParameters(), tokenReceiver, sequenceEvent.getID(), 0, alignment.getSequenceLength(getParameters(), sequenceEvent.getID()));
			getXMLWriter().writeEndElement();
		}

		getXMLWriter().writeEndElement();
	}


	private void checkCharactersTags(DocumentDataAdapter document) throws IllegalArgumentException, IOException {
		Iterator<MatrixDataAdapter> matricesIterator = document.getMatrixIterator(getParameters());

		while (matricesIterator.hasNext()) {
			checkMatrix(matricesIterator.next());
		}
	}


	private void checkMatrix(MatrixDataAdapter alignment) throws IllegalArgumentException, IOException {
		NeXMLCollectSequenceDataReceiver receiver = new NeXMLCollectSequenceDataReceiver(getStreamDataProvider(), getParameters(), false);  // Also collects metadata namespaces
		String alignmentID = alignment.getStartEvent(getParameters()).getID();
		
		getStreamDataProvider().setCurrentAlignmentInfo(new NeXMLWriterAlignmentInformation());		
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();		
		getStreamDataProvider().addToDocumentIDs(alignmentID);
		getStreamDataProvider().getAlignmentInfoByIDMap().put(alignmentID, alignmentInfo);

		if (alignment.getStartEvent(getParameters()).getLinkedID() == null) {
			getStreamDataProvider().setWriteUndefinedOtuList(true);
		}
		
		writeOrCheckMetaData(alignment, true);
		
		// Check character definition
		checkCharacterDefinitions(alignment.getCharacterDefinitions(getParameters()));
		
		// Check token sets
		checkTokenSets(alignment);
		
		long alignmentLength = determineMaxSequenceLength(alignment, getParameters());
		alignmentInfo.setAlignmentLength(alignmentLength);
		
		if (!((Long.compare(alignment.getColumnCount(getParameters()), -1) == 0) 
				|| (Long.compare(alignment.getColumnCount(getParameters()), alignmentLength) == 0))) {
			throw new InconsistentAdapterDataException("The number of columns given in the matrix data adapter is not correct.");
		}
		
		String defaultTokenSetID = getStreamDataProvider().createNewID(DEFAULT_TOKEN_DEFINITION_SET_ID_PREFIX);
		alignmentInfo.setDefaultTokenSetID(defaultTokenSetID);
		
		// Determine which token set is valid in which alignment column
		for (long i = 0; i < alignmentLength; i++) {
			if (alignmentInfo.getColumnIndexToStatesMap().get(i) == null) {				
				alignmentInfo.getColumnIndexToStatesMap().put(i, defaultTokenSetID);
				alignmentInfo.setWriteDefaultTokenSet(true);
			}
		}
		
		// Add default token set to map if necessary
		if (alignmentInfo.isWriteDefaultTokenSet()) {
			NeXMLWriterTokenSetInformation tokenSetInfo = new NeXMLWriterTokenSetInformation();
			tokenSetInfo.setNucleotideType(false);
			getStreamDataProvider().addToDocumentIDs(defaultTokenSetID);
			alignmentInfo.getIDToTokenSetInfoMap().put(defaultTokenSetID, tokenSetInfo);
		}
		
		// Check character and sequence sets
		checkCharacterSets(alignment);
		
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.SEQUENCE, ATTR_SEQUENCE_SET_LINKED_IDS.getLocalPart());
		checkSets(elementTypeToLinkAttributeMap, EventContentType.SEQUENCE_SET, alignment.getSequenceSets(getParameters()));
		
		setTokenList(alignmentInfo);
		
		// Check sequences
		Iterator<String> sequenceIDs = alignment.getSequenceIDIterator(getParameters());
		while (sequenceIDs.hasNext()) {
			String sequenceID = sequenceIDs.next();
			getStreamDataProvider().addToDocumentIDs(sequenceID);
			LinkedLabeledIDEvent sequenceStartEvent = alignment.getSequenceStartEvent(getParameters(), sequenceID);
			String linkedOtuID = sequenceStartEvent.getLinkedID();

			if ((linkedOtuID == null) || linkedOtuID.isEmpty()) {
				getStreamDataProvider().setWriteUndefinedOTU(true);
			}
			
			alignment.writeSequencePartContentData(getParameters(), receiver, sequenceID, 0, alignment.getSequenceLength(getParameters(), sequenceStartEvent.getID()));
			receiver.setTokenIndex(0);
		}		
		
		if (alignmentInfo.getTokenSetType().equals(alignmentInfo.getTokenType())) {
			alignmentInfo.setAlignmentType(alignmentInfo.getTokenSetType());
		}
		else {
			alignmentInfo.setAlignmentType(CharacterStateSetType.DISCRETE);
		}
		
		// Check if alignment is empty
		alignmentInfo.setWriteAlignment((alignment.getSequenceCount(getParameters()) > 0) && (alignmentLength > 0));
	}
	
	
	private void setTokenList(NeXMLWriterAlignmentInformation alignmentInfo) {
		switch (alignmentInfo.getTokenSetType()) {
			case AMINO_ACID:
				for (Character aminoAcidToken : SequenceUtils.getAminoAcidOneLetterCodes(true)) {
					alignmentInfo.getDefinedTokens().add(Character.toString(aminoAcidToken));					
				}
				alignmentInfo.getDefinedTokens().remove("J");
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.GAP_CHAR));				
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.MISSING_DATA_CHAR));
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.STOP_CODON_CHAR));
				break;
			case DNA:
				for (Character nucleotideToken : SequenceUtils.getNucleotideCharacters()) {
					alignmentInfo.getDefinedTokens().add(Character.toString(nucleotideToken));
				}
				alignmentInfo.getDefinedTokens().remove("U");				
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.GAP_CHAR));				
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.MISSING_DATA_CHAR));
				break;
			case RNA:
				for (Character nucleotideToken : SequenceUtils.getNucleotideCharacters()) {
					alignmentInfo.getDefinedTokens().add(Character.toString(nucleotideToken));
				}
				alignmentInfo.getDefinedTokens().remove("T");				
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.GAP_CHAR));				
				alignmentInfo.getDefinedTokens().add(Character.toString(SequenceUtils.MISSING_DATA_CHAR));
				break;
			default:
				break;
		}
	}
	
	
	private void checkCharacterDefinitions(ObjectListDataAdapter<CharacterDefinitionEvent> characterDefinitions) throws IOException {
		Iterator<String> charIDs = characterDefinitions.getIDIterator(getParameters());
		
		while (charIDs.hasNext()) {
			writeOrCheckObjectMetaData(characterDefinitions, charIDs.next(), true);
		}
	}


	private void checkTokenSets(MatrixDataAdapter alignment) throws IllegalArgumentException, IOException {
		NeXMLCollectTokenSetDefinitionDataReceiver receiver;
		ObjectListDataAdapter<TokenSetDefinitionEvent> tokenSets = alignment.getTokenSets(getParameters());
		Iterator<String> tokenSetDefinitionIDs = tokenSets.getIDIterator(getParameters());
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();

		if (tokenSets.getCount(getParameters()) > 0) {
			while (tokenSetDefinitionIDs.hasNext()) {
				String tokenSetID = tokenSetDefinitionIDs.next();
				receiver = new NeXMLCollectTokenSetDefinitionDataReceiver(getStreamDataProvider(), getParameters(), tokenSetID);
				
				CharacterStateSetType alignmentType = tokenSets.getObjectStartEvent(getParameters(), tokenSetID).getSetType();
				
				getStreamDataProvider().addToDocumentIDs(tokenSetID);
				getStreamDataProvider().setCurrentTokenSetInfo(new NeXMLWriterTokenSetInformation());

				getStreamDataProvider().getCurrentTokenSetInfo().setNucleotideType(alignmentType.equals(CharacterStateSetType.NUCLEOTIDE));

				CharacterStateSetType previousType = alignmentInfo.getTokenSetType();
				if ((previousType == null) || previousType.equals(CharacterStateSetType.UNKNOWN)) {
					alignmentInfo.setTokenSetType(alignmentType);
				}
				else {
					if (!previousType.equals(alignmentType)) {
						throw new JPhyloIOWriterException("Different data types were encountered but only character data of one type "
								+ "(e.g DNA or amino acid) can be written to a single NeXML characters tag.");
					}
				}
				alignmentInfo.getIDToTokenSetInfoMap().put(tokenSetID, getStreamDataProvider().getCurrentTokenSetInfo());
				tokenSets.writeContentData(getParameters(), receiver, tokenSetID);
			}
		}
		
		if (alignmentInfo.getTokenSetType() == null || alignmentInfo.getTokenSetType().equals(CharacterStateSetType.UNKNOWN)) {
			alignmentInfo.setTokenSetType(CharacterStateSetType.CONTINUOUS);
		}
		
		alignmentInfo.setTokenType(alignmentInfo.getTokenSetType());
	}	


	private void checkCharacterSets(MatrixDataAdapter alignment) throws IllegalArgumentException, IOException {
		ObjectListDataAdapter<LinkedLabeledIDEvent> charSets = alignment.getCharacterSets(getParameters());
		Iterator<String> charSetIDs = charSets.getIDIterator(getParameters());
		NeXMLWriterAlignmentInformation alignmentInfo = getStreamDataProvider().getCurrentAlignmentInfo();

		while (charSetIDs.hasNext()) {
			String charSetID = charSetIDs.next();
			NeXMLCollectCharSetDataReceiver receiver = new NeXMLCollectCharSetDataReceiver(getStreamDataProvider(), getParameters(), charSetID);
			getStreamDataProvider().addToDocumentIDs(charSetID);
			
			alignmentInfo.getCharSets().put(charSetID, new TreeSet<Long>());
			charSets.writeContentData(getParameters(), receiver, charSetID);
		}
	}
	

	private void writeTreesTags(DocumentDataAdapter document) throws XMLStreamException, IOException {
		Iterator<TreeNetworkGroupDataAdapter> treeAndNetworkGroupIterator = document.getTreeNetworkGroupIterator(getParameters());
		while (treeAndNetworkGroupIterator.hasNext()) {
			writeTreesTag(treeAndNetworkGroupIterator.next());
		}
	}


	private void writeTreesTag(TreeNetworkGroupDataAdapter treeOrNetworkGroup) throws XMLStreamException, IOException {
		getXMLWriter().writeStartElement(TAG_TREES.getLocalPart());
		getStreamDataProvider().writeLinkedLabeledIDAttributes(treeOrNetworkGroup.getStartEvent(getParameters()), TAG_OTUS, true);

		writeOrCheckMetaData(treeOrNetworkGroup, false);
		
		Iterator<TreeNetworkDataAdapter> treesAndNetworksIterator = treeOrNetworkGroup.getTreeNetworkIterator(getParameters());
		while (treesAndNetworksIterator.hasNext()) {
			writeTreeOrNetworkTag(treesAndNetworksIterator.next());
		}
		
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.TREE, ATTR_TREE_SET_LINKED_TREE_IDS.getLocalPart());
		elementTypeToLinkAttributeMap.put(EventContentType.NETWORK, ATTR_TREE_SET_LINKED_NETWORK_IDS.getLocalPart());
		
		writeSetTags(elementTypeToLinkAttributeMap, treeOrNetworkGroup.getTreeSets(getParameters()));
		
		getXMLWriter().writeEndElement();
	}


	private void writeTreeOrNetworkTag(TreeNetworkDataAdapter treeOrNetwork) throws XMLStreamException, IOException {
		NeXMLMetaDataReceiver receiver = new NeXMLMetaDataReceiver(getStreamDataProvider(), getParameters());
		StringBuffer treeType = new StringBuffer();
		treeType.append(getStreamDataProvider().getNeXMLPrefix(getXMLWriter()));
		treeType.append(XMLUtils.QNAME_SEPARATOR);

		if (treeOrNetwork.isTree(getParameters())) {
			getXMLWriter().writeStartElement(TAG_TREE.getLocalPart());
			treeType.append(TYPE_FLOAT_TREE);
		}
		else {
			getXMLWriter().writeStartElement(TAG_NETWORK.getLocalPart());
			treeType.append(TYPE_FLOAT_NETWORK);
		}

		getStreamDataProvider().writeLabeledIDAttributes(treeOrNetwork.getStartEvent(getParameters()));
		getXMLWriter().writeAttribute(XMLReadWriteUtils.getXSIPrefix(getXMLWriter()), ATTR_XSI_TYPE.getNamespaceURI(),
				ATTR_XSI_TYPE.getLocalPart(), treeType.toString());  // Trees and networks are always written as float type

		writeOrCheckMetaData(treeOrNetwork, false);
		
		// Write node elements
		Iterator<String> nodeIDIterator = treeOrNetwork.getNodes(getParameters()).getIDIterator(getParameters());
		while (nodeIDIterator.hasNext()) {
			String nodeID = nodeIDIterator.next();
			NodeEvent node = treeOrNetwork.getNodes(getParameters()).getObjectStartEvent(getParameters(), nodeID);
			getXMLWriter().writeStartElement(TAG_NODE.getLocalPart());
			getStreamDataProvider().writeLinkedLabeledIDAttributes(node, TAG_OTU, false);
			if (node.isRootNode()) {
				getXMLWriter().writeAttribute(ATTR_ROOT.getLocalPart(), Boolean.toString(node.isRootNode()));
			}
			treeOrNetwork.getNodes(getParameters()).writeContentData(getParameters(), receiver, nodeID);
			getXMLWriter().writeEndElement();
		}
		
		// Write edge elements
		Iterator<String> edgeIDIterator = treeOrNetwork.getEdges(getParameters()).getIDIterator(getParameters());
		while (edgeIDIterator.hasNext()) {
			String edgeID = edgeIDIterator.next();
			writeEdgeOrRootedgeTag(treeOrNetwork, treeOrNetwork.getEdges(getParameters()).getObjectStartEvent(getParameters(), edgeID));
		}
		
		// Write node edge sets
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.NODE, ATTR_NODE_EDGE_SET_LINKED_NODE_IDS.getLocalPart());
		elementTypeToLinkAttributeMap.put(EventContentType.EDGE, ATTR_NODE_EDGE_SET_LINKED_EDGE_IDS.getLocalPart());
		elementTypeToLinkAttributeMap.put(EventContentType.ROOT_EDGE, ATTR_NODE_EDGE_SET_LINKED_ROOTEDGE_IDS.getLocalPart());
		
		writeSetTags(elementTypeToLinkAttributeMap, treeOrNetwork.getNodeEdgeSets(getParameters()));

		getXMLWriter().writeEndElement();
	}


	private void writeEdgeOrRootedgeTag(TreeNetworkDataAdapter tree, EdgeEvent edge) throws XMLStreamException, IllegalArgumentException, IOException {
		NeXMLMetaDataReceiver receiver = new NeXMLMetaDataReceiver(getStreamDataProvider(), getParameters());

		if (edge.hasSource()) {
			getXMLWriter().writeStartElement(TAG_ROOTEDGE.getLocalPart());  // At most one root can be contained in the adapter
		}
		else {
			getXMLWriter().writeStartElement(TAG_EDGE.getLocalPart());
			getXMLWriter().writeAttribute(ATTR_SOURCE.getLocalPart(), edge.getSourceID());
		}
		getXMLWriter().writeAttribute(ATTR_TARGET.getLocalPart(), edge.getTargetID());
		getStreamDataProvider().writeLabeledIDAttributes(edge);

		if (!Double.isNaN(edge.getLength())) {
			getXMLWriter().writeAttribute(ATTR_LENGTH.getLocalPart(), Double.toString(edge.getLength()));
		}

		tree.getEdges(getParameters()).writeContentData(getParameters(), receiver, edge.getID());
		getXMLWriter().writeEndElement();
	}


	private void checkTreeAndNetworkGroups(DocumentDataAdapter document) throws IOException {
		Iterator<TreeNetworkGroupDataAdapter> treeAndNetworkGroupIterator = document.getTreeNetworkGroupIterator(getParameters());
		
		while (treeAndNetworkGroupIterator.hasNext()) {
			checkTreesAndNetworkGroup(treeAndNetworkGroupIterator.next());
		}
	}


	private void checkTreesAndNetworkGroup(TreeNetworkGroupDataAdapter treesAndNetworks) throws IOException {
		String linkedOTUs = treesAndNetworks.getStartEvent(getParameters()).getLinkedID();
		getStreamDataProvider().addToDocumentIDs(treesAndNetworks.getStartEvent(getParameters()).getID());
		
		writeOrCheckMetaData(treesAndNetworks, true);

		if (linkedOTUs == null) {
			getStreamDataProvider().setWriteUndefinedOtuList(true);
		}

		Iterator<TreeNetworkDataAdapter> treesAndNetworksIterator = treesAndNetworks.getTreeNetworkIterator(getParameters());
		while (treesAndNetworksIterator.hasNext()) {
			checkTreeOrNetwork(treesAndNetworksIterator.next());
		}
		
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.TREE, ATTR_TREE_SET_LINKED_TREE_IDS.getLocalPart());
		elementTypeToLinkAttributeMap.put(EventContentType.NETWORK, ATTR_TREE_SET_LINKED_NETWORK_IDS.getLocalPart());
		
		checkSets(elementTypeToLinkAttributeMap, EventContentType.TREE_NETWORK_SET, treesAndNetworks.getTreeSets(getParameters()));
	}


	private void checkTreeOrNetwork(TreeNetworkDataAdapter treeOrNetwork) throws IOException {
		NeXMLCollectNamespaceReceiver receiver = new NeXMLCollectNamespaceReceiver(getStreamDataProvider(), getParameters());
		Set<String> referencedNodeIDs = new HashSet<String>();

		getStreamDataProvider().addToDocumentIDs(treeOrNetwork.getStartEvent(getParameters()).getID());

		writeOrCheckMetaData(treeOrNetwork, true);
		
		EnumMap<EventContentType, String> elementTypeToLinkAttributeMap = new EnumMap<EventContentType, String>(EventContentType.class);
		elementTypeToLinkAttributeMap.put(EventContentType.NODE, ATTR_NODE_EDGE_SET_LINKED_NODE_IDS.getLocalPart());
		elementTypeToLinkAttributeMap.put(EventContentType.EDGE, ATTR_NODE_EDGE_SET_LINKED_EDGE_IDS.getLocalPart());
		elementTypeToLinkAttributeMap.put(EventContentType.ROOT_EDGE, ATTR_NODE_EDGE_SET_LINKED_ROOTEDGE_IDS.getLocalPart());
		
		checkSets(elementTypeToLinkAttributeMap, EventContentType.NODE_EDGE_SET, treeOrNetwork.getNodeEdgeSets(getParameters()));

		// Check edges
		Iterator<String> edgeIDIterator = treeOrNetwork.getEdges(getParameters()).getIDIterator(getParameters());
		while (edgeIDIterator.hasNext()) {
			String edgeID = edgeIDIterator.next();
			EdgeEvent edge = treeOrNetwork.getEdges(getParameters()).getObjectStartEvent(getParameters(), edgeID);
			
			getStreamDataProvider().addToDocumentIDs(edgeID);
			treeOrNetwork.getEdges(getParameters()).writeContentData(getParameters(), receiver, edgeID);
			referencedNodeIDs.add(edge.getSourceID());
			referencedNodeIDs.add(edge.getTargetID());
		}

		referencedNodeIDs.remove(null);
		
		// Check nodes
		Set<String> nodeIDs = new HashSet<String>();
		Iterator<String> nodeIDIterator = treeOrNetwork.getNodes(getParameters()).getIDIterator(getParameters());
		while(nodeIDIterator.hasNext()) {
			String nodeID = nodeIDIterator.next();
			nodeIDs.add(nodeID);
			getStreamDataProvider().addToDocumentIDs(nodeID);
			
			treeOrNetwork.getNodes(getParameters()).writeContentData(getParameters(), receiver, nodeID);
		}

		if (!(referencedNodeIDs.size() == nodeIDs.size())) {
			StringBuffer message = new StringBuffer("The nodes ");

			for (String nodeID : referencedNodeIDs) {
				if (!nodeIDs.contains(nodeID)) {
					message.append(nodeID);
					message.append(", ");
				}
			}

			message.append("are referenced by edges but not defined in the document.");
			throw new JPhyloIOWriterException(message.toString());
		}
	}
}