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


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.formats.phyloxml.receivers.PhyloXMLSpecificPredicatesDataReceiver;
import info.bioinfweb.jphyloio.formats.xml.XMLWriterStreamDataProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;



/**
 * The {@link XMLWriterStreamDataProvider} used by {@link PhyloXMLEventWriter}.
 * 
 * @author Sarah Wiechers
 */
public class PhyloXMLWriterStreamDataProvider extends XMLWriterStreamDataProvider<PhyloXMLEventWriter> implements PhyloXMLConstants, PhyloXMLPrivateConstants {	
	private Map<QName, PhyloXMLPredicateInfo> predicateInfoMap = new HashMap<QName, PhyloXMLPredicateInfo>();
	
	private Map<String, PhyloXMLMetaEventInfo> metaEvents = new HashMap<String, PhyloXMLMetaEventInfo>();
	private Set<String> metaIDs = new HashSet<String>();
	private boolean documentHasMetadata = false;
	private boolean documentHasPhylogeny = false;
	private boolean treeGroupHasMetadata = false;
	
	private Stack<String> customXMLElements = new Stack<String>();
	
	private String phylogenyIDProvider = null;
	private String phylogenyID = null;
	private String currentCladeIDSource = null;
	
	private Set<String> idSources = new HashSet<String>();
	private Map<String, String> nodeEventIDToIDSourceMap = new HashMap<String, String>();
	
	
	public PhyloXMLWriterStreamDataProvider(PhyloXMLEventWriter eventWriter) {
		super(eventWriter);
		
		fillMetaPredicateMap();
	}
	
	
	/**
	 * This map is used by {@link PhyloXMLSpecificPredicatesDataReceiver} to translate meta events with certain predicates to <i>PhyloXML</i> tags.
	 * 
	 * @return a map containing information about the way a predicate shall be translated to a tag
	 */
	public Map<QName, PhyloXMLPredicateInfo> getPredicateInfoMap() {
		return predicateInfoMap;
	}


	public Map<String, PhyloXMLMetaEventInfo> getMetaEvents() {
		return metaEvents;
	}


	public Set<String> getMetaIDs() {
		return metaIDs;
	}


	public boolean isDocumentHasMetadata() {
		return documentHasMetadata;
	}


	public void setDocumentHasMetadata(boolean documentHasCustomXML) {
		this.documentHasMetadata = documentHasCustomXML;
	}


	public boolean isDocumentHasPhylogeny() {
		return documentHasPhylogeny;
	}


	public void setDocumentHasPhylogeny(boolean documentHasPhylogeny) {
		this.documentHasPhylogeny = documentHasPhylogeny;
	}


	public boolean hasTreeGroupMetadata() {
		return treeGroupHasMetadata;
	}


	public void setTreeGroupHasMetadata(boolean treeGroupHasMetadata) {
		this.treeGroupHasMetadata = treeGroupHasMetadata;
	}


	public Stack<String> getCustomXMLElements() {
		return customXMLElements;
	}


	public String getPhylogenyIDProvider() {
		return phylogenyIDProvider;
	}


	public void setPhylogenyIDProvider(String phylogenyIDProvider) {
		this.phylogenyIDProvider = phylogenyIDProvider;
	}


	public String getPhylogenyID() {
		return phylogenyID;
	}


	public void setPhylogenyID(String phylogenyID) {
		this.phylogenyID = phylogenyID;
	}


	public String getCurrentCladeIDSource() {
		return currentCladeIDSource;
	}


	public void setCurrentCladeIDSource(String currentCladeIDSource) {
		this.currentCladeIDSource = currentCladeIDSource;
	}


	public Set<String> getIdSources() {
		return idSources;
	}


	public Map<String, String> getNodeEventIDToIDSourceMap() {
		return nodeEventIDToIDSourceMap;
	}


	private void fillMetaPredicateMap() {
		// Phylogeny
		predicateInfoMap.put(IDENTIFIER_PHYLOGENY, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, null, PREDICATE_PHYLOGENY_ATTR_REROOTABLE, 
				PREDICATE_PHYLOGENY_ATTR_BRANCH_LENGTH_UNIT, PREDICATE_PHYLOGENY_ATTR_TYPE, PREDICATE_PHYLOGENY_DESCRIPTION, PREDICATE_PHYLOGENY_DATE, 
				PREDICATE_CONFIDENCE, PREDICATE_PROPERTY));
		predicateInfoMap.put(PREDICATE_PHYLOGENY_ATTR_REROOTABLE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_REROOTABLE));
		predicateInfoMap.put(PREDICATE_PHYLOGENY_ATTR_BRANCH_LENGTH_UNIT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_BRANCH_LENGTH_UNIT));
		predicateInfoMap.put(PREDICATE_PHYLOGENY_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));			
		predicateInfoMap.put(PREDICATE_PHYLOGENY_DESCRIPTION, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DESCRIPTION));
		predicateInfoMap.put(PREDICATE_PHYLOGENY_DATE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DATE));		
		predicateInfoMap.put(PREDICATE_CONFIDENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_CONFIDENCE, PREDICATE_CONFIDENCE_ATTR_TYPE, 
				PREDICATE_CONFIDENCE_VALUE));
		predicateInfoMap.put(PREDICATE_CONFIDENCE_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_CONFIDENCE_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
//		predicateInfoMap.put(PREDICATE_CLADE_REL, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_CLADE_RELATION, PREDICATE_CLADE_REL_ATTR_IDREF0, 
//				PREDICATE_CLADE_REL_ATTR_IDREF1, PREDICATE_CLADE_REL_ATTR_DISTANCE, PREDICATE_CLADE_REL_ATTR_TYPE));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_IDREF0, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_REF_0));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_IDREF1, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_REF_1));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_DISTANCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_DISTANCE));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
//		
//		predicateInfoMap.put(PREDICATE_SEQ_REL, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_SEQUENCE_RELATION, PREDICATE_SEQ_REL_ATTR_IDREF0, 
//				PREDICATE_SEQ_REL_ATTR_IDREF1, PREDICATE_SEQ_REL_ATTR_DISTANCE, PREDICATE_SEQ_REL_ATTR_TYPE, PREDICATE_SEQ_REL_CONFIDENCE));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_IDREF0, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_REF_0));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_IDREF1, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_REF_1));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_DISTANCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_DISTANCE));
//		predicateInfoMap.put(PREDICATE_CLADE_REL_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
//		
//		predicateInfoMap.put(PREDICATE_SEQ_REL_CONFIDENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_CONFIDENCE, PREDICATE_SEQ_REL_CONFIDENCE_ATTR_TYPE, 
//				PREDICATE_SEQ_REL_CONFIDENCE_VALUE));
//		predicateInfoMap.put(PREDICATE_SEQ_REL_CONFIDENCE_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
//		predicateInfoMap.put(PREDICATE_SEQ_REL_CONFIDENCE_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));	
		
		// Clade (edge specific)
		predicateInfoMap.put(IDENTIFIER_EDGE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, null, PREDICATE_CONFIDENCE, PREDICATE_WIDTH, 
				PREDICATE_COLOR));		
		
		predicateInfoMap.put(PREDICATE_WIDTH, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_BRANCH_WIDTH));
		predicateInfoMap.put(PREDICATE_COLOR, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_BRANCH_COLOR));
		
		// Clade (node specific)
		predicateInfoMap.put(IDENTIFIER_NODE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, null,
				PREDICATE_NODE_ID, PREDICATE_TAXONOMY, PREDICATE_SEQUENCE, PREDICATE_EVENTS, PREDICATE_BINARY_CHARACTERS, PREDICATE_DISTRIBUTION, 
				PREDICATE_DATE, PREDICATE_REFERENCE, PREDICATE_PROPERTY, IDENTIFIER_ANY_PREDICATE));
		
		predicateInfoMap.put(PREDICATE_ATTR_ID_SOURCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_SOURCE));
		predicateInfoMap.put(PREDICATE_NODE_ID, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_NODE_ID, PREDICATE_NODE_ID_ATTR_PROVIDER, 
				PREDICATE_NODE_ID_VALUE));
		predicateInfoMap.put(PREDICATE_NODE_ID_ATTR_PROVIDER, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_PROVIDER));
		predicateInfoMap.put(PREDICATE_NODE_ID_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		// Taxonomy
		predicateInfoMap.put(PREDICATE_TAXONOMY, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_TAXONOMY, PREDICATE_ATTR_ID_SOURCE, 
				PREDICATE_TAXONOMY_ID, PREDICATE_TAXONOMY_CODE, PREDICATE_TAXONOMY_SCIENTIFIC_NAME, PREDICATE_TAXONOMY_AUTHORITY, PREDICATE_TAXONOMY_COMMON_NAME,
				PREDICATE_TAXONOMY_SYNONYM, PREDICATE_TAXONOMY_RANK, PREDICATE_TAXONOMY_URI, ReadWriteConstants.PREDICATE_HAS_CUSTOM_XML));
		
		predicateInfoMap.put(PREDICATE_TAXONOMY_ID, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_ID, PREDICATE_TAXONOMY_ID_ATTR_PROVIDER, 
				PREDICATE_TAXONOMY_ID_VALUE));
		predicateInfoMap.put(PREDICATE_TAXONOMY_ID_ATTR_PROVIDER, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_PROVIDER));
		predicateInfoMap.put(PREDICATE_TAXONOMY_ID_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		predicateInfoMap.put(PREDICATE_TAXONOMY_CODE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_CODE));
		predicateInfoMap.put(PREDICATE_TAXONOMY_SCIENTIFIC_NAME, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_SCI_NAME));
		predicateInfoMap.put(PREDICATE_TAXONOMY_AUTHORITY, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_AUTHORITY));
		predicateInfoMap.put(PREDICATE_TAXONOMY_COMMON_NAME, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_COMMON_NAME));
		predicateInfoMap.put(PREDICATE_TAXONOMY_SYNONYM, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_SYNONYM));
		predicateInfoMap.put(PREDICATE_TAXONOMY_RANK, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_RANK));
		
		predicateInfoMap.put(PREDICATE_TAXONOMY_URI, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_URI, PREDICATE_TAXONOMY_URI_ATTR_DESC,
				PREDICATE_TAXONOMY_URI_ATTR_TYPE, PREDICATE_TAXONOMY_URI_VALUE));
		predicateInfoMap.put(PREDICATE_TAXONOMY_URI_ATTR_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_DESC));
		predicateInfoMap.put(PREDICATE_TAXONOMY_URI_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_TAXONOMY_URI_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		predicateInfoMap.put(ReadWriteConstants.PREDICATE_HAS_CUSTOM_XML, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.CUSTOM_XML, null, 
				PhyloXMLPrivateConstants.IDENTIFIER_CUSTOM_XML));
		predicateInfoMap.put(PhyloXMLPrivateConstants.IDENTIFIER_CUSTOM_XML, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.CUSTOM_XML, null));
		
		// Sequence
		predicateInfoMap.put(PREDICATE_SEQUENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_SEQUENCE, PREDICATE_SEQUENCE_ATTR_TYPE, 
				PREDICATE_ATTR_ID_SOURCE, PREDICATE_SEQUENCE_ATTR_ID_REF, PREDICATE_SEQUENCE_SYMBOL, PREDICATE_SEQUENCE_ACCESSION, PREDICATE_SEQUENCE_NAME,
				PREDICATE_SEQUENCE_LOCATION, PREDICATE_SEQUENCE_MOL_SEQ, PREDICATE_SEQUENCE_URI, PREDICATE_ANNOTATION, PREDICATE_DOMAIN_ARCHITECTURE,
				ReadWriteConstants.PREDICATE_HAS_CUSTOM_XML));		
		predicateInfoMap.put(PREDICATE_SEQUENCE_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_SEQUENCE_ATTR_ID_REF, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_REF));		
		predicateInfoMap.put(PREDICATE_SEQUENCE_SYMBOL, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_SYMBOL));		
		predicateInfoMap.put(PREDICATE_SEQUENCE_ACCESSION, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_ACCESSION, 
				PREDICATE_SEQUENCE_ACCESSION_ATTR_SOURCE, PREDICATE_SEQUENCE_ACCESSION_VALUE));
		predicateInfoMap.put(PREDICATE_SEQUENCE_ACCESSION_ATTR_SOURCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_SOURCE));
		predicateInfoMap.put(PREDICATE_SEQUENCE_ACCESSION_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));		
		predicateInfoMap.put(PREDICATE_SEQUENCE_NAME, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_NAME));
		predicateInfoMap.put(PREDICATE_SEQUENCE_LOCATION, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LOCATION));		
		predicateInfoMap.put(PREDICATE_SEQUENCE_MOL_SEQ, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_MOL_SEQ, 
				PREDICATE_SEQUENCE_MOL_SEQ_ATTR_IS_ALIGNED, PREDICATE_SEQUENCE_MOL_SEQ_VALUE));
		predicateInfoMap.put(PREDICATE_SEQUENCE_MOL_SEQ_ATTR_IS_ALIGNED, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_IS_ALIGNED));
		predicateInfoMap.put(PREDICATE_SEQUENCE_MOL_SEQ_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));		
		predicateInfoMap.put(PREDICATE_SEQUENCE_URI, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_URI, PREDICATE_SEQUENCE_URI_ATTR_DESC,
				PREDICATE_SEQUENCE_URI_ATTR_TYPE, PREDICATE_SEQUENCE_URI_VALUE));
		predicateInfoMap.put(PREDICATE_SEQUENCE_URI_ATTR_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_DESC));
		predicateInfoMap.put(PREDICATE_SEQUENCE_URI_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_SEQUENCE_URI_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		//Annotation
		predicateInfoMap.put(PREDICATE_ANNOTATION, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_ANNOTATION, PREDICATE_ANNOTATION_ATTR_EVIDENCE,
				PREDICATE_ANNOTATION_ATTR_REF, PREDICATE_ANNOTATION_ATTR_SOURCE, PREDICATE_ANNOTATION_ATTR_TYPE, PREDICATE_ANNOTATION_DESC, 
				PREDICATE_ANNOTATION_CONFIDENCE, PREDICATE_PROPERTY, IDENTIFIER_ANY_PREDICATE, PREDICATE_ANNOTATION_URI));
		predicateInfoMap.put(PREDICATE_ANNOTATION_ATTR_EVIDENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_EVIDENCE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_ATTR_REF, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_REF));
		predicateInfoMap.put(PREDICATE_ANNOTATION_ATTR_SOURCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_SOURCE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DESC));		
		predicateInfoMap.put(PREDICATE_ANNOTATION_CONFIDENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_CONFIDENCE, 
				PREDICATE_ANNOTATION_CONFIDENCE_ATTR_TYPE, PREDICATE_ANNOTATION_CONFIDENCE_VALUE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_CONFIDENCE_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_CONFIDENCE_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));		
		predicateInfoMap.put(PREDICATE_ANNOTATION_URI, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_URI, PREDICATE_ANNOTATION_URI_ATTR_DESC,
				PREDICATE_ANNOTATION_URI_ATTR_TYPE, PREDICATE_ANNOTATION_URI_VALUE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_URI_ATTR_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_DESC));
		predicateInfoMap.put(PREDICATE_ANNOTATION_URI_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_ANNOTATION_URI_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		// Domain architecture
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_DOMAIN_ARCHITECTURE, 
				PREDICATE_DOMAIN_ARCHITECTURE_ATTR_LENGTH, PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN));
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_ATTR_LENGTH, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_LENGTH));		
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_DOMAIN, 
				PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_FROM, PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_TO, PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_CONFIDENCE, 
				PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_ID, PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_VALUE));
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_FROM, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_FROM));
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_TO, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TO));
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_CONFIDENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_CONFIDENCE));
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_ID, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID));
		predicateInfoMap.put(PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		// Events
		predicateInfoMap.put(PREDICATE_EVENTS, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_EVENTS, 
				PREDICATE_EVENTS_TYPE, PREDICATE_EVENTS_DUPLICATIONS, PREDICATE_EVENTS_SPECIATIONS, PREDICATE_EVENTS_LOSSES, PREDICATE_EVENTS_CONFIDENCE));		
		predicateInfoMap.put(PREDICATE_EVENTS_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_TYPE));		
		predicateInfoMap.put(PREDICATE_EVENTS_DUPLICATIONS, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DUPLICATIONS));
		predicateInfoMap.put(PREDICATE_EVENTS_SPECIATIONS, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_SPECIATIONS));
		predicateInfoMap.put(PREDICATE_EVENTS_LOSSES, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LOSSES));
		predicateInfoMap.put(PREDICATE_EVENTS_CONFIDENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_CONFIDENCE, 
				PREDICATE_EVENTS_CONFIDENCE_ATTR_TYPE, PREDICATE_EVENTS_CONFIDENCE_VALUE));
		predicateInfoMap.put(PREDICATE_EVENTS_CONFIDENCE_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_EVENTS_CONFIDENCE_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.VALUE, null));
		
		// BinaryCharacters
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_BINARY_CHARACTERS, 
				PREDICATE_BINARY_CHARACTERS_ATTR_TYPE, PREDICATE_BINARY_CHARACTERS_ATTR_GAINED_COUNT, PREDICATE_BINARY_CHARACTERS_ATTR_LOST_COUNT, 
				PREDICATE_BINARY_CHARACTERS_ATTR_PRESENT_COUNT, PREDICATE_BINARY_CHARACTERS_ATTR_ABSENT_COUNT, PREDICATE_BINARY_CHARACTERS_GAINED, 
				PREDICATE_BINARY_CHARACTERS_LOST, PREDICATE_BINARY_CHARACTERS_PRESENT, PREDICATE_BINARY_CHARACTERS_ABSENT));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_ATTR_TYPE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_TYPE));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_ATTR_GAINED_COUNT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_GAINED_COUNT));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_ATTR_LOST_COUNT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_LOST_COUNT));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_ATTR_PRESENT_COUNT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_PRESENT_COUNT));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_ATTR_ABSENT_COUNT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ABSENT_COUNT));		
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_GAINED, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_GAINED));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_LOST, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LOST));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_PRESENT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_PRESENT));
		predicateInfoMap.put(PREDICATE_BINARY_CHARACTERS_ABSENT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_ABSENT));
		
		// Distribution
		predicateInfoMap.put(PREDICATE_DISTRIBUTION, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_DISTRIBUTION, PREDICATE_DISTRIBUTION_DESC, 
				PREDICATE_DISTRIBUTION_POINT, PREDICATE_DISTRIBUTION_POLYGON));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DESC));
		
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POINT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_POINT, 
				PREDICATE_DISTRIBUTION_POINT_ATTR_GEODETIC_DATUM, PREDICATE_DISTRIBUTION_POINT_ATTR_ALT_UNIT, PREDICATE_DISTRIBUTION_POINT_LAT, 
				PREDICATE_DISTRIBUTION_POINT_LONG, PREDICATE_DISTRIBUTION_POINT_ALT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POINT_ATTR_GEODETIC_DATUM, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_GEO_DATUM));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POINT_ATTR_ALT_UNIT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ALT_UNIT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POINT_LAT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LAT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POINT_LONG, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LONG));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POINT_ALT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_ALT));
		
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_POLYGON, 
				PREDICATE_DISTRIBUTION_POLYGON_POINT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON_POINT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_POINT, 
				PREDICATE_DISTRIBUTION_POLYGON_POINT_ATTR_GEODETIC_DATUM, PREDICATE_DISTRIBUTION_POLYGON_POINT_ATTR_ALT_UNIT, PREDICATE_DISTRIBUTION_POLYGON_POINT_LAT, 
				PREDICATE_DISTRIBUTION_POLYGON_POINT_LONG, PREDICATE_DISTRIBUTION_POLYGON_POINT_ALT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON_POINT_ATTR_GEODETIC_DATUM, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_GEO_DATUM));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON_POINT_ATTR_ALT_UNIT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ALT_UNIT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON_POINT_LAT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LAT));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON_POINT_LONG, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_LONG));
		predicateInfoMap.put(PREDICATE_DISTRIBUTION_POLYGON_POINT_ALT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_ALT));
		
		// Date
		predicateInfoMap.put(PREDICATE_DATE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_DATE, PREDICATE_DATE_ATTR_UNIT, PREDICATE_DATE_DESC, 
				PREDICATE_DATE_VALUE, PREDICATE_DATE_MINIMUM, PREDICATE_DATE_MAXIMUM));
		predicateInfoMap.put(PREDICATE_DATE_ATTR_UNIT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_UNIT));
		predicateInfoMap.put(PREDICATE_DATE_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DESC));
		predicateInfoMap.put(PREDICATE_DATE_VALUE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_VALUE));
		predicateInfoMap.put(PREDICATE_DATE_MINIMUM, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_MINIMUM));
		predicateInfoMap.put(PREDICATE_DATE_MAXIMUM, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_MAXIMUM));
		
		// Reference
		predicateInfoMap.put(PREDICATE_REFERENCE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_REFERENCE, PREDICATE_REFERENCE_ATTR_DOI,
				PREDICATE_REFERENCE_DESC));
		predicateInfoMap.put(PREDICATE_REFERENCE_ATTR_DOI, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_DOI));
		predicateInfoMap.put(PREDICATE_REFERENCE_DESC, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_DESC));
		
		// Property
		predicateInfoMap.put(PREDICATE_PROPERTY, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG, TAG_PROPERTY, 
				PREDICATE_PROPERTY_ATTR_UNIT, PREDICATE_PROPERTY_ATTR_APPLIES_TO, PREDICATE_PROPERTY_ATTR_ID_REF, IDENTIFIER_ANY_PREDICATE));
		predicateInfoMap.put(PREDICATE_PROPERTY_ATTR_UNIT, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_UNIT));
		predicateInfoMap.put(PREDICATE_PROPERTY_ATTR_APPLIES_TO, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_APPLIES_TO));
		predicateInfoMap.put(PREDICATE_PROPERTY_ATTR_ID_REF, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.ATTRIBUTE, ATTR_ID_REF));
		
		predicateInfoMap.put(IDENTIFIER_ANY_PREDICATE, new PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment.TAG_AND_VALUE, TAG_PROPERTY));
	}
}