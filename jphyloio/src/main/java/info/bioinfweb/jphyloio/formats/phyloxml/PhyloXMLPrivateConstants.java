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


import javax.xml.namespace.QName;



/**
 * This interface contains constants used internally in <i>JPhyloIO</i> by {@link PhyloXMLEventWriter} and <i>PhyloXML</i> data 
 * receivers. They are not supposed to be directly referenced in application code. Public constants for general use are declared in 
 * {@link PhyloXMLConstants}.
 * 
 * @author Sarah Wiechers
 * @author Ben St&ouml;ver
 */
public interface PhyloXMLPrivateConstants {	
	public static final QName IDENTIFIER_PHYLOGENY = new QName("Phylogeny");
	public static final QName IDENTIFIER_EDGE = new QName("Edge");
	public static final QName IDENTIFIER_NODE = new QName("Node");
	
	public static final QName IDENTIFIER_ANY_PREDICATE = new QName("AnyPredicate");
	public static final QName IDENTIFIER_CUSTOM_XML = new QName("CustomXML");
}