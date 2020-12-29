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
package info.bioinfweb.jphyloio.factory;


import java.io.InputStream;
import java.io.Reader;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;



/**
 * Implementations of this interface are used internally by {@link JPhyloIOReaderWriterFactory} to create reader
 * and writer instances of a specific format. There must be one implementation if this interface for each format
 * provided to {@link JPhyloIOReaderWriterFactory}.
 * <p>
 * Application or third party library developers who wish to add readers or writers for additional formats to
 * {@link JPhyloIOReaderWriterFactory}, should provide an according implementation of this interface which may
 * than be regisgered using {@link JPhyloIOReaderWriterFactory#addFactory(SingleReaderWriterFactory)}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface SingleReaderWriterFactory {
	/**
	 * Implementations should read the start of the stream here and determine whether the content could be valid
	 * according to the target format.
	 * 
	 * @param stream the stream to read the first bytes from
	 * @param parameters the parameters map that would also be used to create a reader
	 * @return {@code true} if the tested stream may be in the target format or {@code false} if the stream is not
	 *         in valid target format
	 */
	public boolean checkFormat(InputStream stream, ReadWriteParameterMap parameters) throws Exception;
	
	/**
	 * Implementations should read the start of the characters returned by the reader here and determine whether 
	 * the content could be valid according to the target format.
	 * 
	 * @param reader the reader to read the first bytes from
	 * @param parameters the parameters map that would also be used to create a reader
	 * @return {@code true} if the tested reader may return content in target format or {@code false} if the content is not
	 *         in valid target format
	 */
	public boolean checkFormat(Reader reader, ReadWriteParameterMap parameters) throws Exception;
	
  /**
   * Creates a new reader instance for the target format of this factory.
   * 
   * @param stream the stream to provide the content for the reader
	 * @param parameters the parameter map for the new reader instance 
   * @return the new reader instance
   */
  public JPhyloIOEventReader getReader(InputStream stream, ReadWriteParameterMap parameters) throws Exception;
  
  /**
   * Creates a new reader instance for the target format of this factory.
   * 
   * @param stream the stream to provide the content for the reader
	 * @param parameters the parameter map for the new reader instance 
   * @return the new reader instance or {@code null} if this factory is not able to create readers
   * @see #hasReader()
   */
  public JPhyloIOEventReader getReader(Reader reader, ReadWriteParameterMap parameters) throws Exception;
  
  /**
   * Creates a new writer instance for the target format of this factory.
   * 
   * @return the new writer instance or {@code null} if this factory is not able to create writers
   * @see #hasWriter()
   */
  public JPhyloIOEventWriter getWriter();
  
  /**
   * Returns an information objects describing the target format and providing according file filters.
   * 
   * @return the format information object
   */
  public JPhyloIOFormatInfo getFormatInfo();
  
  /**
   * Determines whether this factory is able to create reader instances for its target format.
   * 
   * @return {@code true} if creating readers is supported or {@code false} otherwise 
   */
  public boolean hasReader();
  
  /**
   * Determines whether this factory is able to create writer instances for its target format.
   * 
   * @return {@code true} if creating writers is supported or {@code false} otherwise 
   */
  public boolean hasWriter();
}