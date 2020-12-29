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
package info.bioinfweb.jphyloio.formats.newick;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.AbstractSingleReaderWriterFactory;
import info.bioinfweb.jphyloio.factory.SingleReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.DefaultFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.MetadataModeling;
import info.bioinfweb.jphyloio.formatinfo.MetadataTopologyType;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;



/**
 * Reader and writer factory for the Newick format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class NewickFactory extends AbstractSingleReaderWriterFactory implements SingleReaderWriterFactory, 
		JPhyloIOFormatIDs, NewickConstants {
	
	private static final int NUMBER_OF_EVENTS_TO_TEST = 5;
	
	
	/**
	 * Since there is no real characteristic starting string for any Newick file, it is just checked whether at least five 
	 * events (if available) can be created from the input without an exception. Since this method may fail in some cases, 
	 * factories should test this format after all the others have been tested unsuccessfully.
	 * 
	 * @see info.bioinfweb.jphyloio.factory.SingleReaderWriterFactory#checkFormat(java.io.Reader, info.bioinfweb.jphyloio.ReadWriteParameterMap)
	 */
	@Override
	public boolean checkFormat(Reader reader, ReadWriteParameterMap parameters) {
		try {
			NewickEventReader newickReader = new NewickEventReader(reader, parameters);
			for (int i = 0; i < NUMBER_OF_EVENTS_TO_TEST; i++) {
				if (newickReader.hasNextEvent()) {
					newickReader.next();
				}
				else {
					return true;
				}
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}


	@Override
	public JPhyloIOEventReader getReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException {
		return new NewickEventReader(stream, parameters);
	}


	@Override
	public JPhyloIOEventReader getReader(Reader reader, ReadWriteParameterMap parameters) throws IOException {
		return new NewickEventReader(reader, parameters);
	}


	@Override
	public JPhyloIOEventWriter getWriter() {
		return new NewickEventWriter();
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
		Set<EventContentType> supportedContentTypes = EnumSet.of(EventContentType.DOCUMENT, EventContentType.LITERAL_META, 
				EventContentType.LITERAL_META_CONTENT, EventContentType.COMMENT, EventContentType.TREE_NETWORK_GROUP, 
				EventContentType.TREE, EventContentType.NODE, EventContentType.EDGE, EventContentType.ROOT_EDGE);
		
		Map<EventContentType, MetadataModeling> supportedMetadataModeling = new EnumMap<EventContentType, MetadataModeling>(EventContentType.class);
		supportedMetadataModeling.put(EventContentType.NODE, new MetadataModeling(MetadataTopologyType.LITERAL_ONLY, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.ROOT_EDGE, new MetadataModeling(MetadataTopologyType.LITERAL_ONLY, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.EDGE, new MetadataModeling(MetadataTopologyType.LITERAL_ONLY, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		
		Set<String> supportedWriterParameters = new TreeSet<String>();
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_WRITER_INSTANCE);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_LOGGER);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_MAXIMUM_NAME_LENGTH);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_LABEL_EDITING_REPORTER);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_OBJECT_TRANSLATOR_FACTORY);
		
		return new DefaultFormatInfo(this, NEWICK_FORMAT_ID, NEWICK_FORMAT_NAME, 
				supportedContentTypes, supportedContentTypes,	
				supportedMetadataModeling, supportedMetadataModeling, 
				Collections.<String>emptySet(), supportedWriterParameters,
				new ReadWriteParameterMap(), "Newick tree format", "nwk", "newick", "tre", "tree", "con");
	}
}
