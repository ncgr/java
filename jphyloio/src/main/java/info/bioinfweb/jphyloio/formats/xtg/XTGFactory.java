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
package info.bioinfweb.jphyloio.formats.xtg;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.formatinfo.DefaultFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.MetadataModeling;
import info.bioinfweb.jphyloio.formatinfo.MetadataTopologyType;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.xml.AbstractXMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;



/**
 * Reader and writer factory for the <a href="http://bioinfweb.info/xmlns/xtg">XTG</a> format.
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers
 * @since 0.0.0
 */
public class XTGFactory extends AbstractXMLFactory implements XTGConstants, JPhyloIOFormatIDs {
	public XTGFactory() {
		super(TAG_ROOT);
	}


	@Override
	public JPhyloIOEventReader getReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		return new XTGEventReader(stream, parameters);
	}

	
	@Override
	public JPhyloIOEventReader getReader(Reader reader, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		return new XTGEventReader(reader, parameters);
	}

	
	@Override
	public JPhyloIOEventWriter getWriter() {
		return null;
	}

	
	@Override
	public boolean hasReader() {
		return true;
	}

	
	@Override
	public boolean hasWriter() {
		return false;
	}

	
	@Override
	protected JPhyloIOFormatInfo createFormatInfo() {
		Map<EventContentType, MetadataModeling> supportedMetadataModeling = new EnumMap<EventContentType, MetadataModeling>(EventContentType.class);
		supportedMetadataModeling.put(EventContentType.DOCUMENT, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.RESOURCE_META, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.TREE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.NODE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.EDGE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		supportedMetadataModeling.put(EventContentType.ROOT_EDGE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE)));
		
		Set<String> supportedReaderParameters = new HashSet<String>();
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_ALLOW_DEFAULT_NAMESPACE);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_LOGGER);
		
		return new DefaultFormatInfo(this, XTG_FORMAT_ID, XTG_FORMAT_NAME, 
				EnumSet.of(EventContentType.DOCUMENT, EventContentType.RESOURCE_META, EventContentType.LITERAL_META, 
						EventContentType.LITERAL_META_CONTENT, EventContentType.COMMENT, EventContentType.TREE_NETWORK_GROUP, 
						EventContentType.TREE, EventContentType.NODE, EventContentType.EDGE, EventContentType.ROOT_EDGE),
						
				null, supportedMetadataModeling, null,
				supportedReaderParameters, null,
				new ReadWriteParameterMap(), "XTG format of TreeGraph 2", "xtg", "xml");
	}
}
