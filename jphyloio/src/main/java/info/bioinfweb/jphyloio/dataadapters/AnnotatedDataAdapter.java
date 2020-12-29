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


import java.io.IOException;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;



/**
 * Superinterface for all data adapters providing metadata to the object they model.
 * 
 * @author Ben St&ouml;ver
 */
public interface AnnotatedDataAdapter {
	/**
	 * Writes events describing the metadata associated with the object represented by this instance.
	 * 
	 * @param parameters the parameter map of the calling writer that provides context information for the data request
	 * @param receiver the writer accepting the events
	 */
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException;
}
