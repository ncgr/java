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


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.AbstractStartStringSingleFactory;
import info.bioinfweb.jphyloio.formatinfo.DefaultFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.MetadataModeling;
import info.bioinfweb.jphyloio.formatinfo.MetadataTopologyType;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;



/**
 * Reader and writer factory for the Nexus format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class NexusFactory extends AbstractStartStringSingleFactory implements NexusConstants, JPhyloIOFormatIDs {
	public NexusFactory() {
		super(FIRST_LINE);
	}


	@Override
	public JPhyloIOEventReader getReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException {
		return new NexusEventReader(stream, parameters);
	}

	
	@Override
	public JPhyloIOEventReader getReader(Reader reader,	ReadWriteParameterMap parameters) throws IOException {
		return new NexusEventReader(reader, parameters);
	}
	

	@Override
	public JPhyloIOEventWriter getWriter() {
		return new NexusEventWriter();
	}

	
	@Override
	public boolean hasReader() {
		return true;
	}
	

	@Override
	public boolean hasWriter() {
		return true;
	}

	
	@Override
	protected JPhyloIOFormatInfo createFormatInfo() {
		Set<EventContentType> supportedReaderContentTypes = EnumSet.of(EventContentType.DOCUMENT, EventContentType.LITERAL_META, 
				EventContentType.LITERAL_META_CONTENT, EventContentType.UNKNOWN_COMMAND, EventContentType.COMMENT, 
				EventContentType.OTU_LIST, EventContentType.OTU, EventContentType.OTU_SET, EventContentType.ALIGNMENT, 
				EventContentType.CHARACTER_DEFINITION, EventContentType.SEQUENCE, EventContentType.SEQUENCE_TOKENS, 
				EventContentType.TREE_NETWORK_GROUP, EventContentType.TREE, EventContentType.NODE, EventContentType.EDGE, 
				EventContentType.ROOT_EDGE, EventContentType.TOKEN_SET_DEFINITION, EventContentType.SINGLE_TOKEN_DEFINITION, 
				EventContentType.CHARACTER_SET, EventContentType.CHARACTER_SET_INTERVAL, EventContentType.SET_ELEMENT, 
				EventContentType.OTU_SET, EventContentType.TREE_NETWORK_SET);
		
		Set<EventContentType> supportedWriterContentTypes = EnumSet.copyOf(supportedReaderContentTypes);
		supportedWriterContentTypes.add(EventContentType.SINGLE_SEQUENCE_TOKEN);
		
		Map<EventContentType, MetadataModeling> readerMetadataModeling = new EnumMap<EventContentType, MetadataModeling>(EventContentType.class);
		readerMetadataModeling.put(EventContentType.NODE, new MetadataModeling(MetadataTopologyType.LITERAL_ONLY, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		readerMetadataModeling.put(EventContentType.EDGE, new MetadataModeling(MetadataTopologyType.LITERAL_ONLY, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));

		Map<EventContentType, MetadataModeling> writerMetadataModeling = new EnumMap<EventContentType, MetadataModeling>(readerMetadataModeling);
		writerMetadataModeling.put(EventContentType.ALIGNMENT, new MetadataModeling(MetadataTopologyType.LITERAL_ONLY, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));

		Set<String> readerParameters = new TreeSet<String>();
		readerParameters.add(ReadWriteParameterNames.KEY_NEXUS_BLOCK_HANDLER_MAP);
		readerParameters.add(ReadWriteParameterNames.KEY_NEXUS_COMMAND_READER_FACTORY);
		readerParameters.add(ReadWriteParameterNames.KEY_CREATE_UNKNOWN_COMMAND_EVENTS);
		readerParameters.add(ReadWriteParameterNames.KEY_MAXIMUM_TOKENS_TO_READ);
		readerParameters.add(ReadWriteParameterNames.KEY_MAXIMUM_COMMENT_LENGTH);
		readerParameters.add(ReadWriteParameterNames.KEY_REPLACE_MATCH_TOKENS);

		Set<String> writerParameters = new TreeSet<String>();
		writerParameters.add(ReadWriteParameterNames.KEY_WRITER_INSTANCE);
		writerParameters.add(ReadWriteParameterNames.KEY_LOGGER);
		writerParameters.add(ReadWriteParameterNames.KEY_APPLICATION_NAME);
		writerParameters.add(ReadWriteParameterNames.KEY_APPLICATION_VERSION);
		writerParameters.add(ReadWriteParameterNames.KEY_APPLICATION_URL);
		writerParameters.add(ReadWriteParameterNames.KEY_LINE_SEPARATOR);
		writerParameters.add(ReadWriteParameterNames.KEY_SEQUENCE_EXTENSION_TOKEN);

		return new DefaultFormatInfo(this, NEXUS_FORMAT_ID, NEXUS_FORMAT_NAME, 
				supportedReaderContentTypes, supportedWriterContentTypes, 
				readerMetadataModeling, writerMetadataModeling,
				readerParameters, writerParameters,
				new ReadWriteParameterMap(), "Nexus format", "nex", "nexus", "tre", "tree", "con");  //TODO Should the tree extension better be removed?
	}
}
