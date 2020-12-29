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


import java.util.Set;

import info.bioinfweb.commons.io.ContentExtensionFileFilter;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.JPhyloIOFormatSpecificObject;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOContentExtensionFileFilter;



/**
 * Classes implementing this interface provide information about a phylogenetic file format that is supported by <i>JPhyloIO</i>.
 * and allow to create according file filters and provide information on the content and parameter set of associated 
 * implementations of {@link JPhyloIOEventReader} and {@link JPhyloIOEventWriter}.
 * 
 * @author Ben St&ouml;ver
 * @author Sarah Wiechers
 * @since 0.0.0
 */
public interface JPhyloIOFormatInfo extends JPhyloIOFormatSpecificObject {
	/**
	 * Returns the human readable name of the modeled format. 
	 * 
	 * @return the name of the format
	 */
	public String getFormatName();
	
	/**
	 * Creates a new file filter instance that is associated with the modeled format. It will check valid file extensions and 
	 * possibly the contents of the file, if the extension is not unique.
	 * 
	 * @param testStrategy the strategy to be used to accept a file or not
	 * 
	 * @return a new file filter instance
	 */
	public JPhyloIOContentExtensionFileFilter createFileFilter(ContentExtensionFileFilter.TestStrategy testStrategy);
	
	/**
	 * Determines whether the represented format allows to model the specified data element (content type). Readers will not 
	 * produce events of an unsupported type, while writers will accept but ignore events of this type.
	 * <p>
	 * Note that the values returned here indicate the capabilities of the provided readers and writers and not the 
	 * capabilities of the respective format. It may happen that elements of a format are not supported by its reader and 
	 * writer. In such cases {@code false} would be returned here for the according content type.
	 * <p>
	 * Note that this method will return {@code true} for content types that may occur at multiple positions in the event stream 
	 * (e.g. {@link EventContentType#LITERAL_META} or {@link EventContentType#SET_ELEMENT}), if they are supported at least
	 * at one position. Use {@link #isMetadataModeled(EventContentType)} to check at which position metadata elements are
	 * supported.
	 * 
	 * @param contentType the content type to be tested
	 * @param forReading Specify {@code true} here, if support for {@code contentType} in reading this format or 
	 *        {@code false} for writing it should be returned.  
	 * @return {@code true} if the specified content type is supported, {@code false} otherwise
	 * @see #isMetadataModeled(EventContentType)
	 * @since 0.1.0
	 */
	public boolean isElementModeled(EventContentType contentType, boolean forReading);
	
	/**
	 * Determines which kind of metadata the represented format allows to model nested under the specified data element 
	 * Readers will not produce metaevents under an unsupported type, while writers will accept but ignore metaevents under 
	 * this type.
	 * <p>
	 * Note that the values returned here indicate the capabilities of the provided readers and writers and not the 
	 * capabilities of the respective format.
	 * 
	 * @param contentType the content type of the parent element of possibly supported metadata
	 * @param forReading Specify {@code true} here, if support for metadata nested under {@code contentType} in reading this 
	 *        format or {@code false} for writing it should be returned.  
	 * @return an object describing which metadata is supported, never {@code null}
	 * @see #isElementModeled(EventContentType)
	 * @since 0.1.0
	 */
	public MetadataModeling getMetadataModeling(EventContentType parentContentType, boolean forReading);
	
	/**
	 * Returns a set of the parameters supported by the associated reader or writer. Parameter names used by readers
	 * and writers included in <i>JPhyloIO</i> are defined as string constants in {@link ReadWriteParameterNames}. Readers
	 * and writers from third party developers may introduce additional parameter names defined elsewhere.
	 * 
	 * @param forReading Specify {@code true} here, if support for {@code parameterName} in reading this format or 
	 *        {@code false} for writing it should be returned.  
	 * @return an unmodifiable set of supported parameters
	 * @since 0.1.0
	 */
	public Set<String> getSupportedParameters(boolean forReading);
}
