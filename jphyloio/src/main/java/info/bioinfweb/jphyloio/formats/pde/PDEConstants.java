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
package info.bioinfweb.jphyloio.formats.pde;


import info.bioinfweb.jphyloio.ReadWriteConstants;

import javax.xml.namespace.QName;



/**
 * Defines constants necessary to read <i>PDE</i> files.
 * 
 * @author Sarah Wiechers
 */
public interface PDEConstants {
	public static final String PDE_FORMAT_NAME = "PDE (PhyDE)";

	public static final QName TAG_ROOT = new QName("phyde");
	public static final QName TAG_DESCRIPTION = new QName("description");
	public static final QName TAG_ALIGNMENT = new QName("alignment");	
	public static final QName TAG_HEADER = new QName("header");
	public static final QName TAG_META_TYPE_DEFINITIONS = new QName("entries");	
	public static final QName TAG_SEQUENCE_INFORMATION = new QName("seq");
	public static final QName TAG_SEQUENCE_META_INFORMATION = new QName("e");	
	public static final QName TAG_MATRIX = new QName("matrix");
	public static final QName TAG_BLOCK = new QName("block");
	
	public static final QName TAG_CHARSETS = new QName("CharSets");
	public static final QName TAG_CHARSET = new QName("charset");
	
	public static final QName ATTR_DATATYPE = new QName("datatype");
	public static final QName ATTR_ALIGNMENT_LENGTH = new QName("width");
	public static final QName ATTR_SEQUENCE_COUNT = new QName("height");
	
	public static final QName ATTR_SEQUENCE_INDEX = new QName("idx");
	public static final QName ATTR_ID = new QName("id");
	public static final QName ATTR_VERSION = new QName("version");	
	public static final QName ATTR_CHARSET_LABEL = new QName("name");
	public static final QName ATTR_VISIBILITY = new QName("vis");
	public static final QName ATTR_COLOR = new QName("col");
	
	
	public static final String META_TYPE_STRING = "STRING";
	public static final String META_TYPE_NUMBER = "NUMBER";
	public static final String META_TYPE_FILE = "FILE";
	
	public static final String DNA_TYPE = "dna";
	public static final String AMINO_TYPE = "amino";
	
	public static final String SEQUENCE_END = "/FF";
	public static final String UNKNOWN_CHARS = "/FE";
	
	public static final int META_ID_SEQUENCE_LABEL = 1;
	public static final int META_ID_LINKED_FILE = 2;
	public static final int META_ID_ACCESS_NUMBER = 3;
	public static final int META_ID_COMMENT = 4;
	public static final int FIRST_CUSTOM_META_ID = 32;
	
	
	public static final String PDE_NAMESPACE_PREFIX = ReadWriteConstants.JPHYLOIO_NAMESPACE_PREFIX + "Formats/PDE/";	
	public static final String PDE_PREDICATE_NAMESPACE = PDE_NAMESPACE_PREFIX + ReadWriteConstants.PREDICATE_NAMESPACE_FOLDER + "/";
	
	public static final QName PREDICATE_LINKED_FILE = new QName(PDE_PREDICATE_NAMESPACE, "LinkedFile");
	public static final QName PREDICATE_ACCESS_NUMBER = new QName(PDE_PREDICATE_NAMESPACE, "SequenceAccessNumber");
	public static final QName PREDICATE_COMMENT = new QName(PDE_PREDICATE_NAMESPACE, "SequenceComment");
	public static final QName PREDICATE_DESCRIPTION = new QName(PDE_PREDICATE_NAMESPACE, "Description");
	public static final QName PREDICATE_CHARSET_VISIBILITY = new QName(PDE_PREDICATE_NAMESPACE, "CharSetVisibility");
	public static final QName PREDICATE_CHARSET_COLOR = new QName(PDE_PREDICATE_NAMESPACE, "CharSetColor");
}
