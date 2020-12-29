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


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLConstants;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;



public class UndefinedOTUListDataAdapter implements OTUListDataAdapter, NeXMLConstants {
	private LabeledIDEvent startEvent;
	private LabeledIDEvent elementEvent;


	public UndefinedOTUListDataAdapter(String undefinedOTUsID, String undefinedOTUID) {
		super();
		
		startEvent = new LabeledIDEvent(EventContentType.OTU_LIST, undefinedOTUsID, UNDEFINED_OTUS_LABEL);
		elementEvent = new LabeledIDEvent(EventContentType.OTU, undefinedOTUID, UNDEFINED_OTU_LABEL);
	}


	@Override
	public LabeledIDEvent getObjectStartEvent(ReadWriteParameterMap parameters, String id) throws IllegalArgumentException {
		if (id.equals(elementEvent.getID())) {
			return elementEvent;
		}
		else {
			throw new IllegalArgumentException("No OTU with the ID \"" + id + "\" is offered by this adapter.");
		}
	}


	@Override
	public long getCount(ReadWriteParameterMap parameters) {
		return 1;
	}


	@Override
	public Iterator<String> getIDIterator(ReadWriteParameterMap parameters) {
		return Arrays.asList(new String[]{elementEvent.getID()}).iterator();
	}


	public String getUndefinedOtuID() {
		return elementEvent.getID();
	}


	@Override
	public void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id) throws IOException, IllegalArgumentException {}


	@Override
	public void writeMetadata(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver) throws IOException {}


	@Override
	public LabeledIDEvent getStartEvent(ReadWriteParameterMap parameters) {
		return startEvent;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public ObjectListDataAdapter<LinkedLabeledIDEvent> getOTUSets(ReadWriteParameterMap parameters) {
		return EmptyObjectListDataAdapter.SHARED_EMPTY_OBJECT_LIST_ADAPTER;
	}
}
