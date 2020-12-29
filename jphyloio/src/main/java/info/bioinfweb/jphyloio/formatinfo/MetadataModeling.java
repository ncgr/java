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


import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;

import java.util.Collections;
import java.util.Set;



/**
 * Instances of this class describe which metadata can be nested under a certain content type in a certain format and are returned by 
 * {@link JPhyloIOFormatInfo#getMetadataModeling(info.bioinfweb.jphyloio.events.type.EventContentType, boolean)}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 * @see JPhyloIOFormatInfo#getMetadataModeling(info.bioinfweb.jphyloio.events.type.EventContentType, boolean)
 * @see MetadataTopologyType
 * @see LiteralContentSequenceType
 */
public class MetadataModeling {
	public static final MetadataModeling NO_METADATA = new MetadataModeling(MetadataTopologyType.NONE, Collections.<LiteralContentSequenceType>emptySet());
	
	
	private MetadataTopologyType topologyType;
	private Set<LiteralContentSequenceType> contentTypes;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param topologyType the metadata topology type
	 * @param contentTypes the set of supported literal metadata content types
	 * @throws NullPointerException if any parameter is {@code null}
	 */
	public MetadataModeling(MetadataTopologyType topologyType, Set<LiteralContentSequenceType> contentTypes) {
		super();
		if (topologyType == null) {
			throw new NullPointerException("topologyType must not be null.");
		}
		else if (contentTypes == null) {
			throw new NullPointerException("contentTypes must not be null.");
		}
		else {
			this.topologyType = topologyType;
			this.contentTypes = Collections.unmodifiableSet(contentTypes);
		}
	}


	/**
	 * Returns the topology type that may be nested between start and end events of the according content type.
	 * 
	 * @return the topology type (never {@code null})
	 */
	public MetadataTopologyType getTopologyType() {
		return topologyType;
	}


	/**
	 * Returns the set of metadata content types (simple or XML) that may be nested between literal metaevents under start and end 
	 * events of the according content type.
	 * 
	 * @return the set of content types (Maybe an empty set but never {@code null}.)
	 */
	public Set<LiteralContentSequenceType> getContentTypes() {
		return contentTypes;
	}
}
