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
package info.bioinfweb.jphyloio.formats;



/**
 * Provides unique IDs for all formats that are supported by <i>JPhyloIO</i>.
 * <p>
 * Additional formats may be defined by application developers or third party libraries. Such additional definitions
 * should also follow the reverse domain name pattern as the IDs defined here do (e.g. {@code com.example.myformat}) 
 * and never start with {@value #FORMAT_ID_PREFIX}, which is the reserved prefix for <i>JPhyloIO</i>. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface JPhyloIOFormatIDs {
	/** The prefix of all format IDs defined in <i>JPhyloIO</i>. Do not define custom IDs starting with this prefix. */
	public static final String FORMAT_ID_PREFIX = "info.bioinfweb.jphyloio.";
	
	public static final String NEXML_FORMAT_ID = FORMAT_ID_PREFIX + "nexml";
	public static final String NEXUS_FORMAT_ID = FORMAT_ID_PREFIX + "nexus";
	public static final String NEWICK_FORMAT_ID = FORMAT_ID_PREFIX + "newick";
	public static final String PHYLOXML_FORMAT_ID = FORMAT_ID_PREFIX + "phyloxml";
	public static final String FASTA_FORMAT_ID = FORMAT_ID_PREFIX + "fasta";
	public static final String PHYLIP_FORMAT_ID = FORMAT_ID_PREFIX + "phylip";
	public static final String SEQUENTIAL_PHYLIP_FORMAT_ID = FORMAT_ID_PREFIX + "sequentialphylip";
	public static final String MEGA_FORMAT_ID = FORMAT_ID_PREFIX + "mega";
	public static final String XTG_FORMAT_ID = FORMAT_ID_PREFIX + "xtg";
	public static final String PDE_FORMAT_ID = FORMAT_ID_PREFIX + "pde";
}
