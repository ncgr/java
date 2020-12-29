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
package info.bioinfweb.jphyloio;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;



/**
 * The main JPhyloIO interface to be implemented by all format specific event writers.
 * <p>
 * Instances of this interface access application data through an instance of {@link DocumentDataAdapter} that
 * is passed to a {@code writeDocument()} method. Such an instance links other nested data adapters. Implementations
 * of the adapters will usually be made in application code (based on abstract implementations provided by <i>JPhyloIO</i>)
 * and mediate between instances of this class and the classes implementing the application's data model. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface JPhyloIOEventWriter extends JPhyloIOFormatSpecificObject {
	/**
	 * Writes the data provided by the data adapter to a document in the according format of the implementing class.
	 * 
	 * @param document the adapter providing the data to be written
	 * @param stream the stream to write the data to
	 * @param parameters a map of parameters to exchange information with the writer implementation (Care should be taken, if a
	 *        parameter map is reused for multiple calls, since the writer implementation may add or changes entries in the map.)
	 * @throws InconsistentAdapterDataException if any inconsistency in the specified document adapter is found
	 * @throws Exception implementing classes may choose to throw additional types of exceptions
	 */
	public void writeDocument(DocumentDataAdapter document, OutputStream stream, ReadWriteParameterMap parameters) throws IOException;
	
	/**
	 * Writes the data provided by the data adapter to a document in the according format of the implementing class.
	 * 
	 * @param document the adapter providing the data to be written
	 * @param file the file to write the data to
	 * @param parameters a map of parameters to exchange information with the writer implementation (Care should be taken, if a
	 *        parameter map is reused for multiple calls, since the writer implementation may add or changes entries in the map.)
	 * @throws InconsistentAdapterDataException if any inconsistency in the specified document adapter is found
	 * @throws Exception implementing classes may choose to throw additional types of exceptions
	 */
	public void writeDocument(DocumentDataAdapter document, File file, ReadWriteParameterMap parameters) throws IOException;
	
	/**
	 * Writes the data provided by the data adapter to a document in the according format of the implementing class.
	 * 
	 * @param document the adapter providing the data to be written
	 * @param writer the writer to write the data to
	 * @param parameters a map of parameters to exchange information with the writer implementation (Care should be taken, if a
	 *        parameter map is reused for multiple calls, since the writer implementation may add or changes entries in the map.)
	 * @throws InconsistentAdapterDataException if any inconsistency in the specified document adapter is found
	 * @throws Exception implementing classes may choose to throw additional types of exceptions
	 */
	public void writeDocument(DocumentDataAdapter document, Writer writer, ReadWriteParameterMap parameters) throws IOException;
}
