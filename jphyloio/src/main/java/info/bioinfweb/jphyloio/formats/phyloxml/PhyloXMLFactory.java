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
package info.bioinfweb.jphyloio.formats.phyloxml;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

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



/**
 * Reader and writer factory for the <a href="http://phyloxml.org/">PhyloXML</a> format.
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers
 * @since 0.0.0
 */
public class PhyloXMLFactory extends AbstractXMLFactory implements PhyloXMLConstants, JPhyloIOFormatIDs {
	public PhyloXMLFactory() {
		super(TAG_ROOT);
	}


	@Override
	public JPhyloIOEventReader getReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		return new PhyloXMLEventReader(stream, parameters);
	}

	
	@Override
	public JPhyloIOEventReader getReader(Reader reader, ReadWriteParameterMap parameters) throws IOException, XMLStreamException {
		return new PhyloXMLEventReader(reader, parameters);
	}

	
	@Override
	public JPhyloIOEventWriter getWriter() {
		return new PhyloXMLEventWriter();
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
		Set<EventContentType> supportedContentTypes = EnumSet.of(EventContentType.DOCUMENT, EventContentType.RESOURCE_META, 
				EventContentType.LITERAL_META, EventContentType.LITERAL_META_CONTENT, EventContentType.COMMENT, 
				EventContentType.TREE_NETWORK_GROUP, EventContentType.TREE, EventContentType.NETWORK, EventContentType.NODE, 
				EventContentType.EDGE, EventContentType.ROOT_EDGE);
		
		Map<EventContentType, MetadataModeling> supportedMetadataModeling = new EnumMap<EventContentType, MetadataModeling>(EventContentType.class);
		supportedMetadataModeling.put(EventContentType.DOCUMENT, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.XML)));
		supportedMetadataModeling.put(EventContentType.RESOURCE_META, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE, LiteralContentSequenceType.XML)));
		supportedMetadataModeling.put(EventContentType.NETWORK, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE, LiteralContentSequenceType.XML)));
		supportedMetadataModeling.put(EventContentType.TREE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE, LiteralContentSequenceType.XML)));
		supportedMetadataModeling.put(EventContentType.NODE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE, LiteralContentSequenceType.XML)));
		supportedMetadataModeling.put(EventContentType.EDGE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE, LiteralContentSequenceType.XML)));
		supportedMetadataModeling.put(EventContentType.ROOT_EDGE, new MetadataModeling(MetadataTopologyType.FULL_TREE, 
				EnumSet.of(LiteralContentSequenceType.SIMPLE, LiteralContentSequenceType.XML)));
		
		Set<String> supportedReaderParameters = new HashSet<String>();
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_LOGGER);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_OBJECT_TRANSLATOR_FACTORY);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_ALLOW_DEFAULT_NAMESPACE);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_PHYLOXML_CONSIDER_PHYLOGENY_AS_TREE);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_PHYLOXML_EVENT_ID_TRANSLATION_MAP);		
		
		Set<String> supportedWriterParameters = new HashSet<String>();
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_WRITER_INSTANCE);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_LOGGER);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_OBJECT_TRANSLATOR_FACTORY);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_APPLICATION_NAME);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_APPLICATION_VERSION);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_APPLICATION_URL);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_PHYLOXML_METADATA_TREATMENT);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_CUSTOM_XML_NAMESPACE_HANDLING);
		
		return new DefaultFormatInfo(this, PHYLOXML_FORMAT_ID, PHYLOXML_FORMAT_NAME, 
				supportedContentTypes, supportedContentTypes,	supportedMetadataModeling, supportedMetadataModeling,
				supportedReaderParameters, supportedWriterParameters,
				new ReadWriteParameterMap(), "PhyloXML", "phyloxml", "phylo.xml", "pxml", "xml");
	}
}
