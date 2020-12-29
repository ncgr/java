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

import javax.xml.namespace.QName;



/**
 * Defines constants necessary to read and write <i>PhyloXML</i> files.
 * <p>
 * Of interest for application developers are the predicates defined here that allow to read and write <i>PhyloXML</i>-specific
 * tags for predefined annotations.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @see PhyloXMLEventReader
 * @see PhyloXMLEventWriter
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public interface PhyloXMLConstants {
	public static final String PHYLOXML_FORMAT_NAME = "PhyloXML";
	public static final String PHYLOXML_DEFAULT_PRE = "phy";
	public static final String PHYLOXML_NAMESPACE = "http://www.phyloxml.org";
	public static final String JPHYLOIO_PHYLOXML_NAMESPACE = ReadWriteConstants.JPHYLOIO_NAMESPACE_PREFIX + "PhyloXML/";
	public static final String PHYLOXML_SCHEMA_LOCATION_URI = PHYLOXML_NAMESPACE + " " + "http://www.phyloxml.org/1.10/phyloxml.xsd";
	
	public static final QName TAG_ROOT = new QName(PHYLOXML_NAMESPACE, "phyloxml");
	
	public static final QName TAG_PHYLOGENY = new QName(PHYLOXML_NAMESPACE, "phylogeny");
	public static final QName TAG_NAME = new QName(PHYLOXML_NAMESPACE, "name");
	public static final QName TAG_ID = new QName(PHYLOXML_NAMESPACE, "id");
	public static final QName TAG_DESCRIPTION = new QName(PHYLOXML_NAMESPACE, "description");
	public static final QName TAG_DATE = new QName(PHYLOXML_NAMESPACE, "date");
	public static final QName TAG_CONFIDENCE = new QName(PHYLOXML_NAMESPACE, "confidence");
	public static final QName TAG_CLADE_RELATION = new QName(PHYLOXML_NAMESPACE, "clade_relation");
	public static final QName TAG_SEQUENCE_RELATION = new QName(PHYLOXML_NAMESPACE, "sequence_relation");
	public static final QName TAG_PROPERTY = new QName(PHYLOXML_NAMESPACE, "property");	
	
	public static final QName TAG_CLADE = new QName(PHYLOXML_NAMESPACE, "clade");
	public static final QName TAG_BRANCH_LENGTH = new QName(PHYLOXML_NAMESPACE, "branch_length");
	public static final QName TAG_BRANCH_WIDTH = new QName(PHYLOXML_NAMESPACE, "width");
	public static final QName TAG_BRANCH_COLOR = new QName(PHYLOXML_NAMESPACE, "color");
	public static final QName TAG_RED = new QName(PHYLOXML_NAMESPACE, "red");
	public static final QName TAG_GREEN = new QName(PHYLOXML_NAMESPACE, "green");
	public static final QName TAG_BLUE = new QName(PHYLOXML_NAMESPACE, "blue");
	public static final QName TAG_ALPHA = new QName(PHYLOXML_NAMESPACE, "alpha");
	public static final QName TAG_NODE_ID = new QName(PHYLOXML_NAMESPACE, "node_id");
	
	public static final QName TAG_TAXONOMY = new QName(PHYLOXML_NAMESPACE, "taxonomy");
	public static final QName TAG_CODE = new QName(PHYLOXML_NAMESPACE, "code");
	public static final QName TAG_SCI_NAME = new QName(PHYLOXML_NAMESPACE, "scientific_name");
	public static final QName TAG_AUTHORITY = new QName(PHYLOXML_NAMESPACE, "authority");
	public static final QName TAG_COMMON_NAME = new QName(PHYLOXML_NAMESPACE, "common_name");
	public static final QName TAG_SYNONYM = new QName(PHYLOXML_NAMESPACE, "synonym");
	public static final QName TAG_RANK = new QName(PHYLOXML_NAMESPACE, "rank");
	public static final QName TAG_URI = new QName(PHYLOXML_NAMESPACE, "uri");
	
	public static final QName TAG_SEQUENCE = new QName(PHYLOXML_NAMESPACE, "sequence");
	public static final QName TAG_SYMBOL = new QName(PHYLOXML_NAMESPACE, "symbol");
	public static final QName TAG_ACCESSION = new QName(PHYLOXML_NAMESPACE, "accession");
	public static final QName TAG_LOCATION = new QName(PHYLOXML_NAMESPACE, "location");
	public static final QName TAG_MOL_SEQ = new QName(PHYLOXML_NAMESPACE, "mol_seq");
	public static final QName TAG_ANNOTATION = new QName(PHYLOXML_NAMESPACE, "annotation");
	public static final QName TAG_DOMAIN_ARCHITECTURE = new QName(PHYLOXML_NAMESPACE, "domain_architecture");
	public static final QName TAG_DOMAIN = new QName(PHYLOXML_NAMESPACE, "domain");
	public static final QName TAG_GENE_NAME = new QName(PHYLOXML_NAMESPACE, "gene_name");
	public static final QName TAG_CROSS_REFERENCES = new QName(PHYLOXML_NAMESPACE, "cross_references");
	
	public static final QName TAG_EVENTS = new QName(PHYLOXML_NAMESPACE, "events");
	public static final QName TAG_TYPE = new QName(PHYLOXML_NAMESPACE, "type");
	public static final QName TAG_DUPLICATIONS = new QName(PHYLOXML_NAMESPACE, "duplications");
	public static final QName TAG_SPECIATIONS = new QName(PHYLOXML_NAMESPACE, "speciations");
	public static final QName TAG_LOSSES = new QName(PHYLOXML_NAMESPACE, "losses");	
	
	public static final QName TAG_BINARY_CHARACTERS = new QName(PHYLOXML_NAMESPACE, "binary_characters");
	public static final QName TAG_GAINED = new QName(PHYLOXML_NAMESPACE, "gained");
	public static final QName TAG_LOST = new QName(PHYLOXML_NAMESPACE, "lost");
	public static final QName TAG_PRESENT = new QName(PHYLOXML_NAMESPACE, "present");
	public static final QName TAG_ABSENT = new QName(PHYLOXML_NAMESPACE, "absent");
	public static final QName TAG_BC = new QName(PHYLOXML_NAMESPACE, "bc");
	
	public static final QName TAG_DISTRIBUTION = new QName(PHYLOXML_NAMESPACE, "distribution");
	public static final QName TAG_DESC = new QName(PHYLOXML_NAMESPACE, "desc");
	public static final QName TAG_POINT = new QName(PHYLOXML_NAMESPACE, "point");
	public static final QName TAG_LAT = new QName(PHYLOXML_NAMESPACE, "lat");
	public static final QName TAG_LONG = new QName(PHYLOXML_NAMESPACE, "long");
	public static final QName TAG_ALT = new QName(PHYLOXML_NAMESPACE, "alt");	
	public static final QName TAG_POLYGON = new QName(PHYLOXML_NAMESPACE, "polygon");	
	
	public static final QName TAG_REFERENCE = new QName(PHYLOXML_NAMESPACE, "reference");
	public static final QName TAG_VALUE = new QName(PHYLOXML_NAMESPACE, "value");
	public static final QName TAG_MINIMUM = new QName(PHYLOXML_NAMESPACE, "minimum");
	public static final QName TAG_MAXIMUM = new QName(PHYLOXML_NAMESPACE, "maximum");	
		
	public static final QName ATTR_ROOTED = new QName("rooted");
	public static final QName ATTR_REROOTABLE = new QName("rerootable");
	public static final QName ATTR_BRANCH_LENGTH_UNIT = new QName("branch_length_unit");
	public static final QName ATTR_TYPE = new QName("type");
	
	public static final QName ATTR_BRANCH_LENGTH = new QName("branch_length");
	public static final QName ATTR_ID_SOURCE = new QName("id_source");
	public static final QName ATTR_COLLAPSE = new QName("collapse");
	
	public static final QName ATTR_ID_PROVIDER = new QName("provider");
	
	public static final QName ATTR_ID_REF_0 = new QName("id_ref_0");
	public static final QName ATTR_ID_REF_1 = new QName("id_ref_1");
	public static final QName ATTR_DISTANCE = new QName("distance");
	
	public static final QName ATTR_REF = new QName("ref");
	public static final QName ATTR_UNIT = new QName("unit");
	public static final QName ATTR_DATATYPE = new QName("datatype");
	public static final QName ATTR_APPLIES_TO = new QName("applies_to");
	public static final QName ATTR_ID_REF = new QName("id_ref");
	
	public static final QName ATTR_DESC = new QName("desc");
	public static final QName ATTR_SOURCE = new QName("source");
	public static final QName ATTR_IS_ALIGNED = new QName("is_aligned");
	public static final QName ATTR_EVIDENCE = new QName("evidence");
	public static final QName ATTR_LENGTH = new QName("length");
	public static final QName ATTR_GEO_DATUM = new QName("geodetic_datum");
	public static final QName ATTR_ALT_UNIT = new QName("alt_unit");
	public static final QName ATTR_DOI = new QName("doi");
	
	public static final QName ATTR_GAINED_COUNT = new QName("gained_count");
	public static final QName ATTR_LOST_COUNT = new QName("lost_count");
	public static final QName ATTR_PRESENT_COUNT = new QName("present_count");
	public static final QName ATTR_ABSENT_COUNT = new QName("absent_count");
	
	public static final QName ATTR_FROM = new QName("from");
	public static final QName ATTR_TO = new QName("to");
	public static final QName ATTR_CONFIDENCE = new QName("confidence");
	public static final QName ATTR_ID = new QName("id");
	public static final QName ATTR_COMMENT = new QName("comment");
	public static final QName ATTR_STANDARD_DEVIATION = new QName("stddev");
	
	
	public static final String APPLIES_TO_PHYLOGENY = "phylogeny";
	public static final String APPLIES_TO_CLADE = "clade";
	public static final String APPLIES_TO_NODE = "node";
	public static final String APPLIES_TO_ANNOTATION = "annotation";
	public static final String APPLIES_TO_PARENT_BRANCH = "parent_branch";
	public static final String APPLIES_TO_OTHER = "other";
	
	public static final String TYPE_NETWORK_EDGE = "network_edge";	
	
	
	public static final String PHYLOXML_PREDICATE_NAMESPACE = JPHYLOIO_PHYLOXML_NAMESPACE + ReadWriteConstants.PREDICATE_NAMESPACE_FOLDER + "/";
		
	public static final QName PREDICATE_PHYLOGENY_ID = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID");
	public static final QName PREDICATE_PHYLOGENY_ID_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_PHYLOGENY_ID_ATTR_PROVIDER = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Provider");
	public static final QName PREDICATE_PHYLOGENY_DESCRIPTION = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Description");
	public static final QName PREDICATE_PHYLOGENY_DATE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Date");
	public static final QName PREDICATE_PHYLOGENY_ATTR_REROOTABLE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Rerootable");
	public static final QName PREDICATE_PHYLOGENY_ATTR_BRANCH_LENGTH_UNIT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "BranchLengthUnit");
	public static final QName PREDICATE_PHYLOGENY_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Phylogeny" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	
	public static final QName PREDICATE_CLADE_REL = new QName(PHYLOXML_PREDICATE_NAMESPACE, "CladeRelation");
	public static final QName PREDICATE_CLADE_REL_ATTR_IDREF0 = new QName(PHYLOXML_PREDICATE_NAMESPACE, "CladeRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef0");
	public static final QName PREDICATE_CLADE_REL_ATTR_IDREF1 = new QName(PHYLOXML_PREDICATE_NAMESPACE, "CladeRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef1");
	public static final QName PREDICATE_CLADE_REL_ATTR_DISTANCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "CladeRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Distance");
	public static final QName PREDICATE_CLADE_REL_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "CladeRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	
	public static final QName PREDICATE_SEQ_REL = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation");
	public static final QName PREDICATE_SEQ_REL_ATTR_IDREF0 = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef0");
	public static final QName PREDICATE_SEQ_REL_ATTR_IDREF1 = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef1");
	public static final QName PREDICATE_SEQ_REL_ATTR_DISTANCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Distance");
	public static final QName PREDICATE_SEQ_REL_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_SEQ_REL_CONFIDENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence");
	public static final QName PREDICATE_SEQ_REL_CONFIDENCE_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_SEQ_REL_CONFIDENCE_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "SequenceRelation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	
	public static final QName PREDICATE_ATTR_ID_SOURCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "IDSource");
	public static final QName PREDICATE_ATTR_COLLAPSE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Collapse");
	public static final QName PREDICATE_WIDTH = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BranchWidth");
	public static final QName PREDICATE_COLOR = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BranchColor");
	public static final QName PREDICATE_COLOR_RED = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BranchColor" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Red");
	public static final QName PREDICATE_COLOR_GREEN = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BranchColor" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Green");
	public static final QName PREDICATE_COLOR_BLUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BranchColor" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Blue");
	public static final QName PREDICATE_COLOR_ALPHA = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BranchColor" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Alpha"); //TODO Adjust color translator to existence of new predicate
	
	public static final QName PREDICATE_CONFIDENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Confidence");
	public static final QName PREDICATE_CONFIDENCE_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_CONFIDENCE_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_CONFIDENCE_ATTR_STDDEV = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "StandardDeviation");
	
	public static final QName PREDICATE_NODE_ID = new QName(PHYLOXML_PREDICATE_NAMESPACE, "NodeID");
	public static final QName PREDICATE_NODE_ID_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "NodeID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_NODE_ID_ATTR_PROVIDER = new QName(PHYLOXML_PREDICATE_NAMESPACE, "NodeID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Provider");	
	
	public static final QName PREDICATE_TAXONOMY = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy");
	public static final QName PREDICATE_TAXONOMY_ID = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID");
	public static final QName PREDICATE_TAXONOMY_ID_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_TAXONOMY_ID_ATTR_PROVIDER = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Provider");
	public static final QName PREDICATE_TAXONOMY_CODE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Code");
	public static final QName PREDICATE_TAXONOMY_SCIENTIFIC_NAME = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ScientificName");
	public static final QName PREDICATE_TAXONOMY_AUTHORITY = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Authority");
	public static final QName PREDICATE_TAXONOMY_COMMON_NAME = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CommonName");
	public static final QName PREDICATE_TAXONOMY_SYNONYM = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Synonym");
	public static final QName PREDICATE_TAXONOMY_RANK = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Rank");
	public static final QName PREDICATE_TAXONOMY_URI = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI");
	public static final QName PREDICATE_TAXONOMY_URI_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_TAXONOMY_URI_ATTR_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_TAXONOMY_URI_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");	
	
	public static final QName PREDICATE_SEQUENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence");
	public static final QName PREDICATE_SEQUENCE_SYMBOL = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Symbol");
	public static final QName PREDICATE_SEQUENCE_ACCESSION = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession");
	public static final QName PREDICATE_SEQUENCE_ACCESSION_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_SEQUENCE_ACCESSION_ATTR_COMMENT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Comment");
	public static final QName PREDICATE_SEQUENCE_ACCESSION_ATTR_SOURCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Source");
	public static final QName PREDICATE_SEQUENCE_NAME = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Name");
	public static final QName PREDICATE_SEQUENCE_LOCATION = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Location");
	public static final QName PREDICATE_SEQUENCE_MOL_SEQ = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MolSeq");
	public static final QName PREDICATE_SEQUENCE_MOL_SEQ_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MolSeq" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_SEQUENCE_MOL_SEQ_ATTR_IS_ALIGNED = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "MolSeq" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IsAligned");
	public static final QName PREDICATE_SEQUENCE_URI = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI");
	public static final QName PREDICATE_SEQUENCE_URI_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_SEQUENCE_URI_ATTR_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_SEQUENCE_URI_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_SEQUENCE_GENE_NAME = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "GeneName");
	public static final QName PREDICATE_SEQUENCE_CROSS_REFERENCES = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CrossReferences");
	public static final QName PREDICATE_SEQUENCE_CROSS_REFERENCES_ACCESSION = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CrossReferences" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession");
	public static final QName PREDICATE_SEQUENCE_CROSS_REFERENCES_ACCESSION_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CrossReferences" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_SEQUENCE_CROSS_REFERENCES_ACCESSION_ATTR_COMMENT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CrossReferences" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Comment");
	public static final QName PREDICATE_SEQUENCE_CROSS_REFERENCES_ACCESSION_ATTR_SOURCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "CrossReferences" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Accession" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Source");
	public static final QName PREDICATE_SEQUENCE_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_SEQUENCE_ATTR_ID_REF = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef");
	
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Domain");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Domain" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_FROM = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Domain" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "From");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_TO = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Domain" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "To");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_CONFIDENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Domain" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_DOMAIN_ATTR_ID = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Domain" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "ID");
	public static final QName PREDICATE_DOMAIN_ARCHITECTURE_ATTR_LENGTH = new QName(PHYLOXML_PREDICATE_NAMESPACE, "DomainArchitecture" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Length");
	
	public static final QName PREDICATE_ANNOTATION = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation");
	public static final QName PREDICATE_ANNOTATION_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_ANNOTATION_CONFIDENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence");
	public static final QName PREDICATE_ANNOTATION_CONFIDENCE_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_ANNOTATION_CONFIDENCE_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_ANNOTATION_PROPERTY = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Property");
	public static final QName PREDICATE_ANNOTATION_PROPERTY_ATTR_UNIT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Unit");
	public static final QName PREDICATE_ANNOTATION_PROPERTY_ATTR_DATATYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Datatype");
	public static final QName PREDICATE_ANNOTATION_PROPERTY_ATTR_APPLIES_TO = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "AppliesTo");
	public static final QName PREDICATE_ANNOTATION_PROPERTY_ATTR_ID_REF = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef");	
	public static final QName PREDICATE_ANNOTATION_URI = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI");
	public static final QName PREDICATE_ANNOTATION_URI_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_ANNOTATION_URI_ATTR_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_ANNOTATION_URI_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "URI" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_ANNOTATION_ATTR_REF = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Ref");
	public static final QName PREDICATE_ANNOTATION_ATTR_SOURCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Source");
	public static final QName PREDICATE_ANNOTATION_ATTR_EVIDENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Evidence");
	public static final QName PREDICATE_ANNOTATION_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Annotation" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	
	public static final QName PREDICATE_EVENTS = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events");
	public static final QName PREDICATE_EVENTS_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_EVENTS_DUPLICATIONS = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Duplications");
	public static final QName PREDICATE_EVENTS_SPECIATIONS = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Speciations");
	public static final QName PREDICATE_EVENTS_LOSSES = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Losses");
	public static final QName PREDICATE_EVENTS_CONFIDENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence");
	public static final QName PREDICATE_EVENTS_CONFIDENCE_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_EVENTS_CONFIDENCE_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Events" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Confidence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	
	public static final QName PREDICATE_BINARY_CHARACTERS = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters");
	public static final QName PREDICATE_BINARY_CHARACTERS_GAINED = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Gained");
	public static final QName PREDICATE_BINARY_CHARACTERS_GAINED_BC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Gained" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Bc");
	public static final QName PREDICATE_BINARY_CHARACTERS_LOST = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Lost");
	public static final QName PREDICATE_BINARY_CHARACTERS_LOST_BC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Lost" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Bc");
	public static final QName PREDICATE_BINARY_CHARACTERS_PRESENT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Present");
	public static final QName PREDICATE_BINARY_CHARACTERS_PRESENT_BC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Present" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Bc");
	public static final QName PREDICATE_BINARY_CHARACTERS_ABSENT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Absent");
	public static final QName PREDICATE_BINARY_CHARACTERS_ABSENT_BC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Absent" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Bc");
	public static final QName PREDICATE_BINARY_CHARACTERS_ATTR_TYPE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Type");
	public static final QName PREDICATE_BINARY_CHARACTERS_ATTR_GAINED_COUNT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "GainedCount");
	public static final QName PREDICATE_BINARY_CHARACTERS_ATTR_LOST_COUNT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "LostCount");
	public static final QName PREDICATE_BINARY_CHARACTERS_ATTR_PRESENT_COUNT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "PresentCount");
	public static final QName PREDICATE_BINARY_CHARACTERS_ATTR_ABSENT_COUNT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "BinaryCharacters" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "AbsentCount");
	
	public static final QName PREDICATE_DISTRIBUTION = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution");
	public static final QName PREDICATE_DISTRIBUTION_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_DISTRIBUTION_POINT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point");
	public static final QName PREDICATE_DISTRIBUTION_POINT_LAT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Lat");
	public static final QName PREDICATE_DISTRIBUTION_POINT_LONG = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Long");
	public static final QName PREDICATE_DISTRIBUTION_POINT_ALT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Alt");
	public static final QName PREDICATE_DISTRIBUTION_POINT_ATTR_GEODETIC_DATUM = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "GeodeticDatum");
	public static final QName PREDICATE_DISTRIBUTION_POINT_ATTR_ALT_UNIT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "AltUnit");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON_POINT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON_POINT_LAT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Lat");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON_POINT_LONG = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Long");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON_POINT_ALT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Alt");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON_POINT_ATTR_GEODETIC_DATUM = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "GeodeticDatum");
	public static final QName PREDICATE_DISTRIBUTION_POLYGON_POINT_ATTR_ALT_UNIT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Distribution" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Polygon" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Point" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "AltUnit");
	
	public static final QName PREDICATE_DATE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Date");
	public static final QName PREDICATE_DATE_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Date" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_DATE_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Date" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_DATE_MINIMUM = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Date" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Minimum");
	public static final QName PREDICATE_DATE_MAXIMUM = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Date" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Maximum");
	public static final QName PREDICATE_DATE_ATTR_UNIT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Date" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Unit");
	
	public static final QName PREDICATE_REFERENCE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Reference");	
	public static final QName PREDICATE_REFERENCE_DESC = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Reference" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Desc");
	public static final QName PREDICATE_REFERENCE_VALUE = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Reference" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Value");
	public static final QName PREDICATE_REFERENCE_ATTR_DOI = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Reference" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "DOI");
	
	public static final QName PREDICATE_PROPERTY = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Property");
	public static final QName PREDICATE_PROPERTY_ATTR_UNIT = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Unit");
	public static final QName PREDICATE_PROPERTY_ATTR_APPLIES_TO = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "AppliesTo");
	public static final QName PREDICATE_PROPERTY_ATTR_ID_REF = new QName(PHYLOXML_PREDICATE_NAMESPACE, "Property" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "IDRef");

	public static final String PHYLOXML_DATA_TYPE_NAMESPACE = JPHYLOIO_PHYLOXML_NAMESPACE + ReadWriteConstants.DATA_TYPE_NAMESPACE_FOLDER + "/";
	
	public static final QName DATA_TYPE_BRANCH_COLOR = new QName(PHYLOXML_DATA_TYPE_NAMESPACE, "Clade" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Color");	
	public static final QName DATA_TYPE_EVENTTYPE = new QName(PHYLOXML_DATA_TYPE_NAMESPACE, "Event" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "EventType");
	public static final QName DATA_TYPE_RANK = new QName(PHYLOXML_DATA_TYPE_NAMESPACE, "Taxonomy" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "Rank");
	public static final QName DATA_TYPE_SEQUENCE_SYMBOL = new QName(PHYLOXML_DATA_TYPE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "SequenceSymbol");
	public static final QName DATA_TYPE_TAXONOMY_CODE = new QName(PHYLOXML_DATA_TYPE_NAMESPACE, "Sequence" + ReadWriteConstants.PREDICATE_PART_SEPERATOR + "TaxonomyCode");
}
