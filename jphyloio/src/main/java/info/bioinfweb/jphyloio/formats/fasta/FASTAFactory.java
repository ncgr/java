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
package info.bioinfweb.jphyloio.formats.fasta;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.AbstractStartStringSingleFactory;
import info.bioinfweb.jphyloio.factory.SingleReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.DefaultFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.jphyloio.formatinfo.MetadataModeling;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;



/**
 * Reader and writer factory for the FASTA format.
 * <p>
 * The {@code checkFormat()} methods test, if the content starts with {@link FASTAConstants#NAME_START_CHAR}. They will
 * therefore return {@code false} for empty files, which might anyway be FASTA files containing no sequences.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class FASTAFactory extends AbstractStartStringSingleFactory implements SingleReaderWriterFactory, JPhyloIOFormatIDs, 
		FASTAConstants {
	
	public FASTAFactory() {
		super(Character.toString(NAME_START_CHAR));  // This is the only unique characteristic of a FASTA file.
	}


	@Override
	public JPhyloIOEventReader getReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException {
		return new FASTAEventReader(stream, parameters);
	}


	@Override
	public JPhyloIOEventReader getReader(Reader reader, ReadWriteParameterMap parameters) throws IOException {
		return new FASTAEventReader(reader, parameters);
	}


	@Override
	public JPhyloIOEventWriter getWriter() {
		return new FASTAEventWriter();
	}

	
	@Override
	public boolean hasReader() {
		return true;
	}

	
	@Override
	public boolean hasWriter() {
		return true;
	}


	/**
	 * Returns the FASTA format info including a file filter that specifies the file extensions listed
	 * <a href="https://en.wikipedia.org/wiki/FASTA_format#File_extension">here</a>.
	 * 
	 * @see info.bioinfweb.jphyloio.factory.SingleReaderWriterFactory#getFormatInfo()
	 */
	@Override
	protected JPhyloIOFormatInfo createFormatInfo() {
		Set<EventContentType> supportedReaderContentTypes = EnumSet.of(EventContentType.DOCUMENT, EventContentType.COMMENT, 
				EventContentType.ALIGNMENT,	EventContentType.SEQUENCE, EventContentType.SEQUENCE_TOKENS); 

		Set<EventContentType> supportedWriterContentTypes = EnumSet.copyOf(supportedReaderContentTypes);
		supportedWriterContentTypes.add(EventContentType.SINGLE_SEQUENCE_TOKEN);
		
		Set<String> supportedReaderParameters = new HashSet<String>();
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_MATCH_TOKEN);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_REPLACE_MATCH_TOKENS);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_MAXIMUM_TOKENS_TO_READ);
		supportedReaderParameters.add(ReadWriteParameterNames.KEY_MAXIMUM_COMMENT_LENGTH);

		Set<String> supportedWriterParameters = new HashSet<String>();
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_WRITER_INSTANCE);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_SEQUENCE_EXTENSION_TOKEN);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_LOGGER);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_MAXIMUM_NAME_LENGTH);
		supportedWriterParameters.add(ReadWriteParameterNames.KEY_LABEL_EDITING_REPORTER);

		return new DefaultFormatInfo(this, FASTA_FORMAT_ID, FASTA_FORMAT_NAME,
				supportedReaderContentTypes, supportedWriterContentTypes, 
				Collections.<EventContentType, MetadataModeling>emptyMap(), Collections.<EventContentType, MetadataModeling>emptyMap(),
				supportedReaderParameters, supportedWriterParameters,
				new ReadWriteParameterMap(), "FASTA format", "fasta", "fas", "fa", "fas", "fna", "ffn", "faa", "frn");
	}
}
