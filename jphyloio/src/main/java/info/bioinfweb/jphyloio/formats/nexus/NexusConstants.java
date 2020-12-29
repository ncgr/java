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
package info.bioinfweb.jphyloio.formats.nexus;


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.formats.nexus.blockhandlers.ENewickNetworksBlockHandler;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.trees.ENewickNetworkReader;

import java.util.regex.Pattern;



/**
 * Defines constants necessary to read and write <i>Nexus</i> files.
 * 
 * @author Ben St&ouml;ver
 */
public interface NexusConstants {
	public static final String NEXUS_FORMAT_NAME = "Nexus";
	
	public static final String FIRST_LINE = "#NEXUS";
	public static final String BEGIN_COMMAND = "BEGIN";
	public static final String END_COMMAND = "END";
	public static final String ALTERNATIVE_END_COMMAND = "ENDBLOCK";
	public static final char COMMAND_END = ';';
	public static final char COMMENT_START = '[';
	public static final char COMMENT_END = ']';
	public static final char KEY_VALUE_SEPARATOR = '=';
	public static final char WORD_DELIMITER = '\'';
	public static final char VALUE_DELIMITER = '"';  // The paper really defines another delimiter here.
	public static final char ELEMENT_SEPARATOR = ',';
	
	public static final String BLOCK_NAME_TAXA = "TAXA";
	public static final String BLOCK_NAME_CHARACTERS = "CHARACTERS";
	public static final String BLOCK_NAME_UNALIGNED = "UNALIGNED";
	public static final String BLOCK_NAME_DATA = "DATA";
	public static final String BLOCK_NAME_SETS = "SETS";
	public static final String BLOCK_NAME_TREES = "TREES";
	/** This block name is not part of the initial <i>Nexus</i> standard and used by {@link ENewickNetworksBlockHandler}. */ 
	public static final String BLOCK_NAME_NETWORKS = "NETWORKS";
	
	public static final String COMMAND_NAME_TITLE = "TITLE";
	public static final String COMMAND_NAME_LINK = "LINK";
	public static final String COMMAND_NAME_TAX_LABELS = "TAXLABELS";
	public static final String COMMAND_NAME_DIMENSIONS = "DIMENSIONS";
	public static final String COMMAND_NAME_FORMAT = "FORMAT";
	public static final String COMMAND_NAME_CHAR_LABELS = "CHARLABELS";
	public static final String COMMAND_NAME_CHAR_STATE_LABELS = "CHARSTATELABELS";
	public static final String COMMAND_NAME_MATRIX = "MATRIX";
	public static final String COMMAND_NAME_TRANSLATE = "TRANSLATE";
	public static final String COMMAND_NAME_TREE = "TREE";
	public static final String COMMAND_NAME_CHAR_SET = "CHARSET";
	public static final String COMMAND_NAME_TAXON_SET = "TAXSET";
	public static final String COMMAND_NAME_TREE_SET = "TREESET";
	/** This command name is not part of the initial <i>Nexus</i> standard and used by {@link ENewickNetworkReader}. */ 
	public static final String COMMAND_NAME_NETWORK = "NETWORK";
	
	public static final String DIMENSIONS_SUBCOMMAND_NEW_TAXA = "NEWTAXA";
	public static final String DIMENSIONS_SUBCOMMAND_NTAX = "NTAX";
	public static final String DIMENSIONS_SUBCOMMAND_NCHAR = "NCHAR";
	
	public static final String FORMAT_NAME_STANDARD = "STANDARD";
	public static final String FORMAT_NAME_VECTOR = "VECTOR";

	public static final String FORMAT_SUBCOMMAND_DATA_TYPE = "DATATYPE";
	public static final String FORMAT_SUBCOMMAND_TOKENS = "TOKENS";
	public static final String FORMAT_SUBCOMMAND_NO_TOKENS = "NOTOKENS";
	public static final String FORMAT_SUBCOMMAND_INTERLEAVE = "INTERLEAVE";
	public static final String FORMAT_SUBCOMMAND_TRANSPOSE = "TRANSPOSE";
	public static final String FORMAT_SUBCOMMAND_NO_LABELS = "NOLABELS";
	public static final String FORMAT_SUBCOMMAND_MATCH_CHAR = "MATCHCHAR";
	public static final String FORMAT_SUBCOMMAND_MISSING_CHAR = "MISSING";
	public static final String FORMAT_SUBCOMMAND_GAP_CHAR = "GAP";
	public static final String FORMAT_SUBCOMMAND_SYMBOLS = "SYMBOLS";
	
	public static final String FORMAT_VALUE_STANDARD_DATA_TYPE = "STANDARD";
	public static final String FORMAT_VALUE_NUCLEOTIDE_DATA_TYPE = "NUCLEOTIDE";
	public static final String FORMAT_VALUE_DNA_DATA_TYPE = "DNA";
	public static final String FORMAT_VALUE_RNA_DATA_TYPE = "RNA";
	public static final String FORMAT_VALUE_PROTEIN_DATA_TYPE = "PROTEIN";
	public static final String FORMAT_VALUE_CONTINUOUS_DATA_TYPE = "CONTINUOUS";
	/** This is not part of the official Nexus definition, but is a MrBayes extension. */
	public static final String FORMAT_VALUE_MIXED_DATA_TYPE = "MIXED";

	public static final char CHARACTER_NAME_STATES_SEPARATOR = '/';
	
	public static final char MATRIX_POLYMORPHIC_TOKEN_START = '(';
	public static final char MATRIX_POLYMORPHIC_TOKEN_END = ')';
	public static final char MATRIX_UNCERTAINS_TOKEN_START = '{';
	public static final char MATRIX_UNCERTAINS_TOKEN_END = '}';
	
	public static final char SET_TO_SYMBOL = '-';
	public static final char SET_END_INDEX_SYMBOL = '.';
	public static final char SET_REGULAR_INTERVAL_SYMBOL = '\\';
	public static final String SET_KEY_WORD_ALL = "ALL";
	public static final String SET_KEY_WORD_REMAINING = "REMAINING";
	public static final char SET_VECTOR_NOT_CONTAINED = '0';
	public static final char SET_VECTOR_CONTAINED = '1';
	
	public static final String NEXUS_NAMESPACE_PREFIX = ReadWriteConstants.JPHYLOIO_NAMESPACE_PREFIX + "Formats/Nexus/";
	public static final String NEXUS_PREDICATE_NAMESPACE = NEXUS_NAMESPACE_PREFIX + ReadWriteConstants.PREDICATE_NAMESPACE_FOLDER + "/";
	
	public static final Pattern UNTIL_WHITESPACE_COMMENT_COMMAND_PATTERN = Pattern.compile(
			".*(\\s|\\" + COMMENT_START + "|\\" + COMMAND_END + ")");
	public static final Pattern UNTIL_WHITESPACE_COMMENT_COMMAND_EQUAL_PATTERN = Pattern.compile(
			".*(\\s|\\" + COMMENT_START + "|\\" + COMMAND_END + "|\\=)");
}
