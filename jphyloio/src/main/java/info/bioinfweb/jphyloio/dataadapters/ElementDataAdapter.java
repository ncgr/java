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
package info.bioinfweb.jphyloio.dataadapters;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;



/**
 * Data adapters that model a data element identified by a certain start event should implement this interface.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <E> the type of start element that identifies the modeled data element
 */
public interface ElementDataAdapter<E extends JPhyloIOEvent> extends AnnotatedDataAdapter {
	/**
	 * Returns the start event of this data element. The returned event can be used to determine the label and ID 
	 * of the modeled data element or an optionally linked data element.
	 * 
	 * @param parameters the parameter map of the calling writer that provides context information for the data request
	 * @return the start event of this data element
	 */
	public E getStartEvent(ReadWriteParameterMap parameters);
}
