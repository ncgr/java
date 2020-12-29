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


import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;



/**
 * Allows to access data from the application business model that make up an OTU list. 
 * 
 * @author Ben St&ouml;ver
 * @see DocumentDataAdapter
 * @see JPhyloIOEventWriter
 */
public interface OTUListDataAdapter extends ObjectListDataAdapter<LabeledIDEvent>, ElementDataAdapter<LabeledIDEvent> {
	/**
	 * Returns a list of OTU sets defined for the OTU list modeled by this instance.
	 * 
	 * @param parameters the parameter map of the calling writer that provides context information for the data request
	 * @return a (possibly empty) list of OTU sets
	 */
	public ObjectListDataAdapter<LinkedLabeledIDEvent> getOTUSets(ReadWriteParameterMap parameters);
}
