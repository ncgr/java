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
package info.bioinfweb.jphyloio.events.type;



/**
 * Allows to determine between events that start and a end a data model structure (e.g. a tree)
 * and events that do not describe a structure that can have nested data.
 * <p>
 * Most structures in JPhyloIO allow to have nested data and therefore trigger a start and later an
 * end event. Some exceptions that are not allowed to have nested data (not even metadata) will have
 * the type {@link #SOLE}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public enum EventTopologyType {
	/** 
	 * Indicates that this event marks the start of a sequence of subevents of this event 
	 * (e.g. the start of a tree definition) following by an end event of the same content type.
	 */
	START,
	
	/** 
	 * Indicates that this event marks the end of a structure (e.g. a tree definition or an 
	 * alignment). 
	 */
	END,
	
	/**
	 * Indicates that this event is not separated into a start and an end event and no nested
	 * data will follow.
	 */
	SOLE;
}