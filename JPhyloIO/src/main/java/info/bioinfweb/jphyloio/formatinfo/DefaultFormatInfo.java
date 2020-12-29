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
package info.bioinfweb.jphyloio.formatinfo;


import info.bioinfweb.commons.io.ContentExtensionFileFilter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOContentExtensionFileFilter;
import info.bioinfweb.jphyloio.factory.SingleReaderWriterFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;



/**
 * Default implementation of {@link JPhyloIOFormatInfo}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class DefaultFormatInfo implements JPhyloIOFormatInfo {
	private SingleReaderWriterFactory factory;
	private String formatID;
	private String formatName;

	private Set<EventContentType> supportedReaderContentTypes;
	private Set<EventContentType> supportedWriterContentTypes;
	private Map<EventContentType, MetadataModeling> supportedReaderMetadataModeling;
	private Map<EventContentType, MetadataModeling> supportedWriterMetadataModeling;
	private Set<String> supportedReaderParameters;
	private Set<String> supportedWriterParameters;

	private ReadWriteParameterMap filterParameters;
	private String filterDescription;
	private String[] filterExtensions;


	/**
	 * Creates a new instance of this class.
	 * 
	 * @param factory the single format factory to be used with this format
	 * @param formatID the ID of the format this information object is about
	 * @param formatName the name of the format this information object is about
	 * @param supportedReaderContentTypes the set of content types supported by the associated reader (If {@code null} is specified, 
	 *        an empty set is assumed.)
	 * @param supportedWriterContentTypes the set of content types supported by the associated writer (If {@code null} is specified, 
	 *        an empty set is assumed.)
	 * @param supportedMetadata the set of content types under which any kind of metadata is supported (If {@code null} is 
	 *        specified, an empty set is assumed.)
	 * @param supportedReaderParameters the set of parameters that are supported by the associated reader (If {@code null}  
	 *        is specified, an empty set is assumed.)
	 * @param supportedWriterParameters the set of parameters that are supported by the associated writer (If {@code null}  
	 *        is specified, an empty set is assumed.)
	 * @param filterParameters the reader parameters to be used by the file filter to test the contents of a file as they
	 *        will be passed to {@link SingleReaderWriterFactory#checkFormat(java.io.InputStream, ReadWriteParameterMap)}
	 * @param filterDescription the description of this format to be displayed e.g. in open dialogs
	 * @param filterExtensions the file extension of the associated format (At least one needs to be specified.)
	 * @throws NullPointerException if {@code factory}, {@code formatID}, {@code formatName}, {@code filterDescription} 
	 *         or any element of {@code filterExtensions} is {@code null}
	 * @throws IllegalArgumentException if not at least one extension has been specified
	 */
	public DefaultFormatInfo(SingleReaderWriterFactory factory, String formatID, String formatName, 
			Set<EventContentType> supportedReaderContentTypes, Set<EventContentType> supportedWriterContentTypes, 
			Map<EventContentType, MetadataModeling> supportedReaderMetadataModeling, Map<EventContentType, MetadataModeling> supportedWriterMetadataModeling, 
			Set<String> supportedReaderParameters, Set<String> supportedWriterParameters,
			ReadWriteParameterMap filterParameters,	String filterDescription,	String... filterExtensions) {
		
		super();
		if (factory == null) {
			throw new NullPointerException("factory must not be null.");
		}
		else if (formatID == null) {
			throw new NullPointerException("formatID must not be null.");
		}
		else if (formatName == null) {
			throw new NullPointerException("formatName must not be null.");
		}
		else if (filterDescription == null) {
			throw new NullPointerException("filterDescription must not be null.");
		}
		else {
			for (int i = 0; i < filterExtensions.length; i++) {
				if (filterExtensions[i] == null) {
					throw new NullPointerException("The filter extension with the index " + i + " is null.");
				}
			}
			
			this.factory = factory;
			this.formatID = formatID;
			this.formatName = formatName;

			if (supportedReaderContentTypes == null) {
				this.supportedReaderContentTypes = EnumSet.noneOf(EventContentType.class);
			}
			else {
				this.supportedReaderContentTypes = supportedReaderContentTypes;
			}
			
			if (supportedWriterContentTypes == null) {
				this.supportedWriterContentTypes = EnumSet.noneOf(EventContentType.class);
			}
			else {
				this.supportedWriterContentTypes = supportedWriterContentTypes;
			}
			
			if (supportedReaderMetadataModeling == null) {
				this.supportedReaderMetadataModeling = Collections.emptyMap();
			}
			else {
				this.supportedReaderMetadataModeling = supportedReaderMetadataModeling;
			}
			
			if (supportedWriterMetadataModeling == null) {
				this.supportedWriterMetadataModeling = Collections.emptyMap();
			}
			else {
				this.supportedWriterMetadataModeling = supportedWriterMetadataModeling;
			}
			
			if (supportedReaderParameters == null) {
				this.supportedReaderParameters = Collections.emptySet();
			}
			else {
				this.supportedReaderParameters = Collections.unmodifiableSet(supportedReaderParameters);
			}
			
			if (supportedWriterParameters == null) {
				this.supportedWriterParameters = Collections.emptySet();
			}
			else {
				this.supportedWriterParameters = Collections.unmodifiableSet(supportedWriterParameters);
			}
			
			if (filterParameters == null) {
				this.filterParameters = new ReadWriteParameterMap();
			}
			else {
				this.filterParameters = filterParameters;
			}
			this.filterDescription = filterDescription;
			this.filterExtensions = filterExtensions;
		}
	}


	@Override
	public String getFormatID() {
		return formatID;
	}

	
	@Override
	public String getFormatName() {
		return formatName;
	}

	
	@Override
	public JPhyloIOContentExtensionFileFilter createFileFilter(ContentExtensionFileFilter.TestStrategy testStrategy) {
		return new JPhyloIOContentExtensionFileFilter(factory, filterParameters, filterDescription, true,	testStrategy, false, 
				filterExtensions);
	}
	
	
	@Override
	public boolean isElementModeled(EventContentType contentType, boolean forReading) {
		if (forReading) {
			return supportedReaderContentTypes.contains(contentType);
		}
		else {
			return supportedWriterContentTypes.contains(contentType);
		}
	}

	
	@Override
	public MetadataModeling getMetadataModeling(EventContentType parentContentType, boolean forReading) {
		MetadataModeling result;
		if (forReading) {
			result = supportedReaderMetadataModeling.get(parentContentType);
		}
		else {
			result = supportedWriterMetadataModeling.get(parentContentType);
		}
		
		if (result == null) {
			result = MetadataModeling.NO_METADATA;
		}
		return result;
	}

	
	@Override
	public Set<String> getSupportedParameters(boolean forReading) {
		if (forReading) {
			return supportedReaderParameters;
		}
		else {
			return supportedWriterParameters;
		}
	}
}
