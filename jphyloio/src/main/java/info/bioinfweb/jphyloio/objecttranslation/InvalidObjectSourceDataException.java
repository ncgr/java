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
package info.bioinfweb.jphyloio.objecttranslation;



/**
 * Exception that indicates that an an instance of {@link ObjectTranslator} was not able to use the provided source data 
 * (string or XML events) to create an instance of the handled object type (e.g. because an invalid string was provided
 * or an unexpected XML event was encountered). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class InvalidObjectSourceDataException extends Exception {
	private static final long serialVersionUID = 1L;


	public InvalidObjectSourceDataException() {
		super();
	}

	
	public InvalidObjectSourceDataException(String message, Throwable cause) {
		super(message, cause);
	}

	
	public InvalidObjectSourceDataException(String message) {
		super(message);
	}

	
	public InvalidObjectSourceDataException(Throwable cause) {
		super(cause);
	}
}
