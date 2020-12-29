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


import info.bioinfweb.jphyloio.ReadWriteParameterNames;



/**
 * Instances of this enum can be used as parameter values for {@link ReadWriteParameterNames#KEY_PHYLOXML_METADATA_TREATMENT}.
 * It enumerates ways how metadata from hierarchical <i>RDF</i>-like structures is be written to a <i>PhyloXML</i> document. 
 * This is necessary, since it is not possible to represent nested annotations in <i>PhyloXML</i>.
 * 
 * @author Sarah Wiechers
 * @see ReadWriteParameterNames#KEY_PHYLOXML_METADATA_TREATMENT
 * @see PhyloXMLEventWriter
 * @since 0.0.0
 */
public enum PhyloXMLMetadataTreatment {
	/**
	 * The contents of all hierarchically structured metadata events are written to the file in a sequential order. 
	 * Topological information gets lost in this strategy.
	 */
	SEQUENTIAL,
	
	//TOP_LEVEL_ONLY,  //TODO This parameter is currently not supported by PhyloXMLWriter, see comment below.
	
	/**
	 * Only the contents of metadata events on the top level of a hierarchical structure are written, if they 
	 * have further events nested under them. The contents of the nested events are not written to the file.
	 */
	TOP_LEVEL_WITH_CHILDREN,  //TODO Why is there no TOP_LEVEL_ONLY strategy or is this meant here?
	
	/**
	 * Only the contents of metadata events on the top level of a hierarchical structure are written, if they 
	 * do not have further events nested under them.
	 */
	TOP_LEVEL_WITHOUT_CHILDREN,
	
	/**
	 * Only the contents of metadata events without any nested events are written.
	 */
	LEAVES_ONLY,
	
	/**
	 * No content of metadata events is written.
	 */
	NONE;
}