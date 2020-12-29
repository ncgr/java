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
package info.bioinfweb.jphyloio.formats.phylip;


import info.bioinfweb.jphyloio.ReadWriteParameterNames;

import java.util.regex.Pattern;



/**
 * Constants to be used by <i>Phylip</i> readers and writers.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface PhylipConstants {
	public static final String PHYLIP_FORMAT_NAME = "Phylip";
	public static final String SEQUENTIAL_PHYLIP_FORMAT_NAME = "Sequential Phylip";
	
	/** 
	 * The default maximum length of sequence names written to the <i<Phylip</i> format.
	 * @see PhylipEventWriter
	 * @see ReadWriteParameterNames#KEY_MAXIMUM_NAME_LENGTH
	 */
	public static final int DEFAULT_NAME_LENGTH = 10;
	
	public static final String PREMATURE_NAME_END_CHARACTER = "\t";
	public static final Pattern RELAXED_PHYLIP_NAME_PATTERN = Pattern.compile(".+\\s+");
}
