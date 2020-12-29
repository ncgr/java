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
package info.bioinfweb.jphyloio.events.meta;


import javax.xml.namespace.QName;



public class URIOrStringIdentifier {
	private String stringRepresentation;
	private QName uri;
	
	
	public URIOrStringIdentifier(String stringRepresentation, QName uri) {
		super();
		if ((stringRepresentation == null) && (uri == null)) {
			throw new IllegalArgumentException("At least one of \"uri\" or \"stringRepresentation\" must be different from null.");
		}
		else {
			this.stringRepresentation = stringRepresentation;		
			this.uri = uri;
		}
	}


	public String getStringRepresentation() {
		return stringRepresentation;
	}


	public QName getURI() {
		return uri;
	}


	@Override
	public String toString() {
		return "(" + getURI() + ", " + getStringRepresentation() + ")";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((stringRepresentation == null) ? 0 : stringRepresentation.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIOrStringIdentifier other = (URIOrStringIdentifier) obj;
		if (stringRepresentation == null) {
			if (other.stringRepresentation != null)
				return false;
		}
		else if (!stringRepresentation.equals(other.stringRepresentation))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		}
		else if (!uri.equals(other.uri))
			return false;
		return true;
	}
	
	
	/**
	 * Tests whether this and the other object have an equal string representation or an equal URI 
	 * or both.
	 * <p>
	 * Note that both objects having {@code null} for their string representations or URIs
	 * is not considered as equal by this method. (This is a different behavior as in 
	 * {@link #equals(Object)}.)
	 * 
	 * @param other the instance to be compared
	 * @return {@code true} if the condition above is fulfilled or {@code false} otherwise 
	 */
	public boolean equalsStringOrURI(URIOrStringIdentifier other) {
		return (other != null) &&
				(((stringRepresentation != null) && stringRepresentation.equals(other.stringRepresentation)) ||
						((uri != null) && uri.equals(other.uri)));
	}
}
