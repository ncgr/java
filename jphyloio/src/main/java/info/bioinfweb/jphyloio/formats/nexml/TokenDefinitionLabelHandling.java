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
package info.bioinfweb.jphyloio.formats.nexml;


import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;



/**
 * Instances of this enum can be used as parameter values for 
 * {@link ReadWriteParameterNames#KEY_NEXML_TOKEN_DEFINITION_LABEL_METADATA}. It enumerates ways how the 
 * properties {@link SingleTokenDefinitionEvent#getTokenName()} and 
 * {@link SingleTokenDefinitionEvent#getLabel()} are written to <i>NeXML</i>.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 * @see ReadWriteParameterNames#KEY_NEXML_TOKEN_DEFINITION_LABEL_METADATA
 * @see NeXMLEventWriter
 * @since 0.0.0
 */
public enum TokenDefinitionLabelHandling {
	/** 
	 * Neither the label nor the token name stored in a {@link SingleTokenDefinitionEvent} are written as metadata.
	 */
	NEITHER,
	
	/** 
	 * Both the label and the token name stored in a {@link SingleTokenDefinitionEvent} are written as metadata.
	 */
	BOTH,
	
	/** 
	 * The label or the token name stored in a {@link SingleTokenDefinitionEvent} are written as metadata, 
	 * if they were not written to the file as the value of an attribute of a state element.
	 */
	DISCARDED;
}
