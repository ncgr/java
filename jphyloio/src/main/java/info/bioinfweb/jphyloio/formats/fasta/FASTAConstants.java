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
package info.bioinfweb.jphyloio.formats.fasta;



/**
 * Contains format specific constants used by {@link FASTAEventReader} and {@link FASTAEventWriter}.
 * 
 * @author Ben St&ouml;ver
 */
public interface FASTAConstants {
	public static final String FASTA_FORMAT_NAME = "FASTA";
	
	public static final int DEFAULT_LINE_LENGTH = 80;	
	
	public static final char NAME_START_CHAR = '>';
	public static final char COMMENT_START_CHAR = ';';
}
