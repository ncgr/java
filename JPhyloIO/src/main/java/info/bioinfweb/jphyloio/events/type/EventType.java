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
package info.bioinfweb.jphyloio.events.type;



/**
 * Event type object that combines the content and topology type of an event.
 * 
 * @author Ben St&ouml;ver
 */
public class EventType implements Comparable<EventType> {
	private EventContentType contentType;
	private EventTopologyType topologyType;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param contentType the content type
	 * @param topologyType the topology type
	 * @throws NullPointerException if {@code contentType} or {@code topologyType} are {@code null}
	 */
	public EventType(EventContentType contentType, EventTopologyType topologyType) {
		super();
		if (contentType == null) {
			throw new NullPointerException("The content type must not be null.");
		}
		else {
			this.contentType = contentType;
		}
		if (topologyType == null) {
			throw new NullPointerException("The topology type must not be null.");
		}
		else {
			this.topologyType = topologyType;
		}
	}
	
	
	public EventContentType getContentType() {
		return contentType;
	}
	
	
	public EventTopologyType getTopologyType() {
		return topologyType;
	}


	@Override
	public int compareTo(EventType other) {
		int result = getContentType().compareTo(other.getContentType());
		if (result == 0) {
			result = getTopologyType().compareTo(other.getTopologyType());
		}
		return result;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result
				+ ((topologyType == null) ? 0 : topologyType.hashCode());
		return result;
	}


	public boolean equals(EventContentType contentType, EventTopologyType topologyType) {
		return getContentType().equals(contentType) && getTopologyType().equals(topologyType);
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventType other = (EventType) obj;
		if (contentType != other.contentType)
			return false;
		if (topologyType != other.topologyType)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return getContentType().toString() + "." + getTopologyType().toString();
	}
}
