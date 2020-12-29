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
package info.bioinfweb.jphyloio.formats.nexus;


import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;

import java.io.IOException;
import java.util.Map;



public class NexusWriterStreamDataProvider extends TextWriterStreamDataProvider<NexusEventWriter> {


	public NexusWriterStreamDataProvider(NexusEventWriter eventWriter) {
		super(eventWriter);
	}


	public ReadWriteParameterMap getParameters() {
		return getEventWriter().getParameters();
	}


	public ApplicationLogger getLogger() {
		return getEventWriter().getLogger();
	}


	public Map<String, NexusMatrixWriteResult> getMatrixIDToBlockTypeMap() {
		return getEventWriter().getMatrixIDToBlockTypeMap();
	}
	
	
	public void writeBlockStart(String name) throws IOException {
		getEventWriter().writeBlockStart(name);
	}

	
	public void writeLinkCommand(String linkedID, String linkedBlockName, EventContentType linkedContentType) throws IOException {
		getEventWriter().writeLinkCommand(linkedID, linkedBlockName, linkedContentType);
	}
	
	
	public void writeLineStart(String text) throws IOException {
		getEventWriter().writeLineStart(getWriter(), text);
	}
	
	
	public void writeCommandEnd() throws IOException {
		getEventWriter().writeCommandEnd();
	}
	
	
	public void writeBlockEnd() throws IOException {
		getEventWriter().writeBlockEnd();
	}
}
