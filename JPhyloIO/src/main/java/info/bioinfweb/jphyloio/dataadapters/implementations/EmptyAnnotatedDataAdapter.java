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
package info.bioinfweb.jphyloio.dataadapters.implementations;


import java.io.IOException;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.AnnotatedDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;



/**
 * Default implementation of {@link AnnotatedDataAdapter}, which does not generate any metaevents.
 * Implementations that do not specify any metadata can be inherited from this class.
 * 
 * @author Ben St&ouml;ver
 */
public abstract class EmptyAnnotatedDataAdapter<E extends JPhyloIOEvent> implements AnnotatedDataAdapter {
	/**
	 * This default implementation does nothing and can be overwritten by inherited classes if necessary.
	 */
	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {}
}
