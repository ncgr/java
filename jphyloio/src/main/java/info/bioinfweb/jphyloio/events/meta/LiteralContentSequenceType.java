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
package info.bioinfweb.jphyloio.events.meta;


import javax.xml.stream.XMLEventReader;



/**
 * Enumerates the types of sequences of {@link LiteralMetadataContentEvent}s that can be nested inside an
 * {@link LiteralMetadataEvent}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public enum LiteralContentSequenceType {
	/** Indicates that a single {@link LiteralMetadataContentEvent} representing a simple value will be nested. */
	SIMPLE,
	
	/** 
	 * Indicates that a sequence of {@link LiteralMetadataContentEvent}s will be nested. Each of them represents
	 * one XML element as they are read by an implementation of {@link XMLEventReader}. (The data represented by the
	 * XML sequence as a whole is the literal value.) 
	 */
	XML;
}
