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
package info.bioinfweb.jphyloio.exception;


import info.bioinfweb.commons.io.StreamLocationProvider;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;

import javax.xml.stream.Location;



/**
 * This exception is thrown by implementations of {@link JPhyloIOEventReader} if a feature of their format is encountered
 * that is not supported by this reader. Not all readers necessarily support all features of their format (e.g. because 
 * they are rarely used or because they cannot be supported in event based reading without buffering a significant amount 
 * of data).
 * <p>
 * Refer to the documentation of the single readers to find out, if some features of their source format are not supported.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class UnsupportedFormatFeatureException extends JPhyloIOReaderException {
	public UnsupportedFormatFeatureException(String message, Location location,	Throwable cause) {
		super(message, location, cause);
	}

	
	public UnsupportedFormatFeatureException(String message, Location location) {
		super(message, location);
	}

	
	public UnsupportedFormatFeatureException(String message, long characterOffset, long lineNumber, long columnNumber, Throwable cause) {
		super(message, characterOffset, lineNumber, columnNumber, cause);
	}


	public UnsupportedFormatFeatureException(String message, long characterOffset, long lineNumber, long columnNumber) {
		super(message, characterOffset, lineNumber, columnNumber);
	}

	
	public UnsupportedFormatFeatureException(String message, StreamLocationProvider location, Throwable cause) {
		super(message, location, cause);
	}

	
	public UnsupportedFormatFeatureException(String message, StreamLocationProvider location) {
		super(message, location);
	}

	
	public UnsupportedFormatFeatureException(String message, Throwable cause) {
		super(message, cause);
	}
}
