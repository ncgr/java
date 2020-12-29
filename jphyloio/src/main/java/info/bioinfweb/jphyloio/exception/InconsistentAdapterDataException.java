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
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;



/**
 * Exception which is thrown by an instance of {@link JPhyloIOEventWriter} if the data in the specified {@link DocumentDataAdapter} 
 * or one of its nested adapter classes is inconsistent. (An example could be a sequence event referencing an OTU which is not 
 * defined in the document adapter.)
 * <p>
 * In contrast to {@link JPhyloIOWriterException} this exception is not checked, since implementations of {@link DocumentDataAdapter}
 * and its nested classes are usually expected to produce consistent event streams.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see JPhyloIOWriterException
 */
public class InconsistentAdapterDataException extends RuntimeException {
	public InconsistentAdapterDataException() {
		super();
	}

	
	public InconsistentAdapterDataException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public InconsistentAdapterDataException(String message) {
		super(message);
	}

	
	public InconsistentAdapterDataException(Throwable cause) {
		super(cause);
	}
}
