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
package info.bioinfweb.jphyloio.formats.newick;


import info.bioinfweb.jphyloio.ReadWriteConstants;

import javax.xml.namespace.QName;



public interface NewickConstants {
	public static final String NEWICK_FORMAT_NAME = "Newick";	
	
	public static final char SUBTREE_START = '(';
	public static final char SUBTREE_END = ')';
	public static final char NAME_DELIMITER = '\'';
	public static final char ALTERNATIVE_NAME_DELIMITER = '"';
	public static final char LENGTH_SEPERATOR = ':'; 
	public static final char ELEMENT_SEPERATOR = ','; 
	public static final char TERMINAL_SYMBOL = ';';
	public static final char COMMENT_START = '[';
	public static final char COMMENT_END = ']';
	public static final char FREE_NAME_BLANK = '_';
	
	public static final String ROOTED_HOT_COMMENT = "&r";
	public static final String UNROOTED_HOT_COMMENT = "&u";
	
	
	// Hot comment constants:
	
	public static final char HOT_COMMENT_START_SYMBOL = '&';
	public static final char ALLOCATION_SEPARATOR_SYMBOL = ',';
	public static final char ALLOCATION_SYMBOL = '=';
	public static final char FIELD_START_SYMBOL = '{';
  public static final char FIELD_END_SYMBOL = '}';
	public static final char FIELD_VALUE_SEPARATOR_SYMBOL = ',';
	
	public static final char INDEX_START_SYMBOL = '[';
	public static final char INDEX_END_SYMBOL = ']';

	public static final char NHX_VALUE_SEPARATOR_SYMBOL = ':';
	public static final String NHX_START = "&&NHX" + NHX_VALUE_SEPARATOR_SYMBOL;
	public static final String NHX_KEY_PREFIX = "NHX:";
	
	public static final String NHX_KEY_GENE_NAME = "GN";
	public static final String NHX_KEY_SEQUENCE_ACCESSION = "AC";
	public static final String NHX_KEY_CONFIDENCE = "B";
	public static final String NHX_KEY_EVENT = "D";
	public static final String NHX_KEY_SCIENTIFIC_NAME = "S";
	public static final String NHX_KEY_TAXONOMY_ID = "T";
	
//	public static final String UNNAMED_EDGE_DATA_NAME = "unnamedEdgeHotComment";  //TODO Specify URL or similar ID here?
//	public static final String UNNAMED_NODE_DATA_NAME = "unnamedNodeHotComment";  //TODO Specify URL or similar ID here?
	
	public static final String E_NEWICK_NETWORK_DATA_SEPARATOR = "#";
	public static final String E_NEWICK_EDGE_TYPE_RECOMBINATION = "R";
	public static final String E_NEWICK_EDGE_TYPE_HYBRIDIZATION = "H";
	public static final String E_NEWICK_EDGE_TYPE_LATERAL_GENE_TRANSFER = "LRT";

	public static final String E_NEWICK_NAMESPACE_PREFIX = ReadWriteConstants.JPHYLOIO_FORMATS_NAMESPACE_PREFIX + "ENewick/";
	public static final QName PREDICATE_E_NEWICK_EDGE_TYPE = new QName(E_NEWICK_NAMESPACE_PREFIX, "EdgeType");

	
	// Data type constants:
	
	public static final String NEWICK_NAMESPACE_PREFIX = ReadWriteConstants.JPHYLOIO_FORMATS_NAMESPACE_PREFIX + "Newick/";
	public static final String NEWICK_DATA_TYPE_NAMESPACE = NEWICK_NAMESPACE_PREFIX + ReadWriteConstants.DATA_TYPE_NAMESPACE_FOLDER + "/";
	public static final QName DATA_TYPE_NEWICK_ARRAY = new QName(NEWICK_DATA_TYPE_NAMESPACE, "Array");
}
