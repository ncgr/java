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
package info.bioinfweb.jphyloio.formats.mega;


import info.bioinfweb.jphyloio.ReadWriteConstants;



public interface MEGAConstants {
	public static final String MEGA_FORMAT_NAME = "MEGA";
	
	public static final String FIRST_LINE = "#MEGA";
	public static final char COMMAND_END = ';';
	public static final char COMMAND_START = '!';
	public static final char SEUQUENCE_START = '#';
	public static final char COMMENT_START = '[';
	public static final char COMMENT_END = ']';
	public static final char DEFAULT_LABEL_CHAR = '_';

	public static final String COMMAND_NAME_TITLE = "TITLE";
	public static final String COMMAND_NAME_DESCRIPTION = "DESCRIPTION"; 
	public static final String COMMAND_NAME_FORMAT = "FORMAT";
	public static final String COMMAND_NAME_LABEL = "LABEL";
	public static final String COMMAND_NAME_GENE = "GENE";
	public static final String COMMAND_NAME_DOMAIN = "DOMAIN";

	public static final String FORMAT_SUBCOMMAND_NTAXA = "NTAXA";
	public static final String FORMAT_SUBCOMMAND_NSITES = "NSITES";
	public static final String FORMAT_SUBCOMMAND_DATA_TYPE = "DATATYPE";
	public static final String FORMAT_SUBCOMMAND_MISSING = "MISSING";
	public static final String FORMAT_SUBCOMMAND_INDEL = "INDEL";
	public static final String FORMAT_SUBCOMMAND_IDENTICAL = "IDENTICAL";
	public static final String FORMAT_SUBCOMMAND_DATA_FORMAT = "DATAFORMAT";

	public static final String FORMAT_VALUE_NUCLEOTIDE_DATA_TYPE = "NUCLEOTIDE";
	public static final String FORMAT_VALUE_DNA_DATA_TYPE = "DNA";
	public static final String FORMAT_VALUE_RNA_DATA_TYPE = "RNA";
	public static final String FORMAT_VALUE_PROTEIN_DATA_TYPE = "PROTEIN";
	public static final String FORMAT_VALUE_INTERLEAVED_DATA_FORMAT = "INTERLEAVED";
	
	public static final String LABEL_CHAR_SET_ID = "MEGALabelCharacterSet";

	public static final String MEGA_NAMESPACE_PREFIX = ReadWriteConstants.JPHYLOIO_NAMESPACE_PREFIX + "Formats/MEGA/";
	public static final String MEGA_PREDICATE_NAMESPACE = MEGA_NAMESPACE_PREFIX + ReadWriteConstants.PREDICATE_NAMESPACE_FOLDER + "/";
}
