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


import info.bioinfweb.jphyloio.JPhyloIOEventWriter;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;



/**
 * Exception thrown by implementations of {@link JPhyloIOEventWriter}, if an error during writing occurs. The exception
 * can be used directly to indicate an error or to wrap another exception (e.g. an {@link XMLStreamException}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see InconsistentAdapterDataException
 */
public class JPhyloIOWriterException extends IOException {
	public JPhyloIOWriterException() {
		super();
	}

	
	public JPhyloIOWriterException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public JPhyloIOWriterException(String message) {
		super(message);
	}

	
	public JPhyloIOWriterException(Throwable cause) {
		super(cause);
	}	
}
