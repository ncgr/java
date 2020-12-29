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


import info.bioinfweb.commons.io.ClosingNotAllowedException;
import info.bioinfweb.commons.io.LimitedInputStream;
import info.bioinfweb.commons.io.LimitedReader;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.fasta.FASTAFactory;
import info.bioinfweb.jphyloio.formats.mega.MEGAFactory;
import info.bioinfweb.jphyloio.formats.newick.NewickFactory;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLFactory;
import info.bioinfweb.jphyloio.formats.nexus.NexusFactory;
import info.bioinfweb.jphyloio.formats.pde.PDEFactory;
import info.bioinfweb.jphyloio.formats.phylip.PhylipFactory;
import info.bioinfweb.jphyloio.formats.phylip.SequentialPhylipFactory;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLFactory;
import info.bioinfweb.jphyloio.formats.xtg.XTGFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.apache.commons.collections4.map.ListOrderedMap;



/**
 * Factory to create instances of <i>JPhyloIO</i> event readers and writers as well as {@link JPhyloIOFormatInfo} 
 * instances.
 * <p>
 * It provides methods to create instances for a specific format, which is identified by its format ID, such as 
 * {@link #getFormatInfo(String)}, {@link #getWriter(String)} and different versions of {@code getReader()}.
 * Format IDs are defined in {@link JPhyloIOFormatIDs}.
 * <p>
 * Additionally this class allows to guess a format from the data using the available {@code guessFormat()} or 
 * {@code guessFormat()} methods. This is useful if an application user shall be able to load a file in any supported 
 * format without explicitly specifying the format.
 * <p>
 * To be able to guess the format, a certain amount of the data to be read needs to be buffered. The buffer size may 
 * be specified using {@link #setReadAheadLimit(int)}. The default value 
 * ({@link #DEFAULT_READ_AHEAD_LIMIT}{@code = }{@value #DEFAULT_READ_AHEAD_LIMIT} bytes) should be sufficient in most 
 * cases. An increase maybe necessary if unusual files (e.g. XML files with very long header information - such as 
 * {@code ENTITY} declarations - in front of the root tag) are expected.
 * <p>
 * The following examples shows how to obtain a reader for a file with an unknown format:
 * <pre>
 * JPhyloIOEventReader reader = factory.guessReader(new File("path/to/file"), new ReadWriteParameterMap());
 * if (reader != null) {
 *   // read file
 * }
 * else {
 *   System.out.println("The format of the specified file is not supported.");
 * }
 * </pre>
 * Internally this instance uses a map from the format IDs to instances of {@link SingleReaderWriterFactory} to
 * create reader and writer instances or guess formats. Third party readers and writers can be handled by this 
 * factory, if according single format factory instances are specified using 
 * {@link #addFactory(SingleReaderWriterFactory)}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see JPhyloIOEventReader
 * @see JPhyloIOEventWriter
 * @see JPhyloIOFormatIDs
 * @see JPhyloIOFormatInfo
 */
public class JPhyloIOReaderWriterFactory implements JPhyloIOFormatIDs {
	//TODO Do any other methods need to be synchronized?
	
	/** 
	 * The number of bytes that are buffered by methods of this factory to determine the format from an input stream.
	 * 
	 *  @see #setReadAheadLimit(int)
	 */
	public static final int DEFAULT_READ_AHEAD_LIMIT = 8 * 1024;  // XML files may contain long comments before the root tag.
	
	
	private final ReadWriteLock readAheadLimitLock = new ReentrantReadWriteLock();	
	
	private ListOrderedMap<String, SingleReaderWriterFactory> formatMap = new ListOrderedMap<String, SingleReaderWriterFactory>();
	private Set<String> formatIDsSet;
	private int readAheadLimit = DEFAULT_READ_AHEAD_LIMIT;
	
	
	/**
	 * Returns a new instance of this class, which supports all build-in readers and writers of <i>JPhyloIO</i> with a 
	 * buffer size as defined by {@link #DEFAULT_READ_AHEAD_LIMIT}.
	 */
	public JPhyloIOReaderWriterFactory() {
		super();
		fillMap();
		formatIDsSet = Collections.unmodifiableSet(formatMap.keySet());
	}
	
	
	/**
	 * Allows to add additional or replace existing single format factories to be used by this instance. Using this method
	 * will only be necessary, if third party readers or writers shall be created by this instance.  
	 * 
	 * @param factory the single format factory object to be add to the internal map of this factory instance
	 * @return the previous value associated with the format ID of the specified factory, or {@code null} if there was no 
	 *         mapping for that format ID
	 * @since 0.1.0
	 */
	public SingleReaderWriterFactory addFactory(SingleReaderWriterFactory factory) {
		return formatMap.put(factory.getFormatInfo().getFormatID(), factory);
	}
	
	
	/**
	 * Removes all single format factories from the internal map of this instance. After calling this method, no format
	 * will be supported anymore by this instance until {@link #addFactory(SingleReaderWriterFactory)} is called. If
	 * all build-in readers and writers of <i>JPhyloIO</i> shall be supported, using this method will be unnecessary.
	 * 
	 * @since 0.1.0
	 */
	public void removeAllFactories() {
		formatMap.clear();
	}
	
	
	private void fillMap() {
		addFactory(new NeXMLFactory());
		addFactory(new NexusFactory());
		addFactory(new PhyloXMLFactory());
		addFactory(new FASTAFactory());
		addFactory(new PhylipFactory());
		addFactory(new MEGAFactory());
		addFactory(new XTGFactory());
		addFactory(new PDEFactory());
		addFactory(new NewickFactory());  // Should be tested in guess*() methods in the end, since the test is insecure.
		addFactory(new SequentialPhylipFactory());  // Does not have to be tested by guess*() methods at all, since PhylipFactory would have returned true before.
		//TODO Can Newick and sequencential Phylip factories be tested at the end otherwise? (The order here is also used in the format IDs map, e.g. to create file filters.)
	}
	
	
	/**
	 * Returns the maximal number of bytes this factory will read to determine the format of an input in the 
	 * {@code #guess*()} methods.
	 * <p>
	 * This method is thread save a secured with an {@link ReadWriteLock} together with {@link #setReadAheadLimit(int)}.
	 * 
	 * @return the current read ahead limit
	 */
	public int getReadAheadLimit() {
		readAheadLimitLock.readLock().lock();
		try {
			return readAheadLimit;
		}
		finally {
			readAheadLimitLock.readLock().unlock();
		}
	}


	/**
	 * Allows to specify the maximal number of bytes this factory will read to determine the format of an input
	 * in the {@code #guess*()} methods.
	 * <p>
	 * This method is thread save a secured with an {@link ReadWriteLock} together with {@link #getReadAheadLimit()}.
	 * 
	 * @param readAheadLimit the new read ahead limit
	 */
	public void setReadAheadLimit(int readAheadLimit) {
		readAheadLimitLock.writeLock().lock();
		try {
			this.readAheadLimit = readAheadLimit;
		}
		finally {
			readAheadLimitLock.writeLock().unlock();
		}
	}
	//TODO Isn't setting an integer an atomic operation and synchronizing is unnecessary?
	
	
	/**
	 * Returns the set of IDs of formats that are currently supported by this factory.
	 * 
	 * @return an unmodifiable set of format IDs
	 * @since 0.1.0
	 */
	public Set<String> getFormatIDsSet() {
		return formatIDsSet;
	}


	/**
	 * Tries to determine the format of the contents of the specified file by examining at its beginning (e.g. the root 
	 * tag in XML formats). The format is determined by subsequent calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(Reader, ReadWriteParameterMap)} until a matching factory is found.
	 * <p>
	 * The parameter map is only necessary for formats that are so variable that parameter values are needed to determine 
	 * how a valid input would look like. That is currently not the case for any format supported in <i>JPhyloIO</i>, but
	 * may be necessary for third parts format-specific factories used in this instance. (Refer to the documentation of 
	 * third party format-specific factories for details.) In most cases the convenience method {@link #guessFormat(File)}
	 * will be sufficient.
	 * <p>
	 * Note that in contrast to {@link #guessReader(File, ReadWriteParameterMap)}, this method does not support
	 * GZIPed inputs.
	 * 
	 * @param reader the reader providing the contents
	 * @param parameters the parameter map containing parameters for the <i>JPhyloIO</i> event reader that would 
	 *        be used with {@code reader} 
	 * @return the ID of the determined format or {@code null} if no supported format seems to be matching the contents
	 * @throws Exception if the underlying format specific factory throws an exception when testing the stream (e.g. 
	 *         because of an IO error)
	 * @see JPhyloIOFormatIDs
	 * @see #guessReader(File, ReadWriteParameterMap)
	 */
	public String guessFormat(File file, ReadWriteParameterMap parameters) throws Exception {
		String result;
		FileInputStream stream = new FileInputStream(file);
		try {
			result = guessFormat(new FileReader(file), parameters);
		}
		finally {
			stream.close();
		}
		return result;
	}
	
	
	/**
	 * Tries to determine the format of the contents of the specified file by examining at its beginning (e.g. the root 
	 * tag in XML formats). The format is determined by subsequent calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(Reader, ReadWriteParameterMap)} until a matching factory is found.
	 * <p>
	 * It uses an empty parameter map that is passed to the internal calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(Reader, ReadWriteParameterMap)}. Use 
	 * {@link #guessFormat(File, ReadWriteParameterMap)} if parameters are necessary to determine the format correctly.
	 * <p>
	 * Note that in contrast to {@link #guessReader(File, ReadWriteParameterMap)}, this method does not support
	 * GZIPed inputs.
	 * 
	 * @param reader the reader providing the contents
	 * @return the ID of the determined format or {@code null} if no supported format seems to be matching the contents
	 * @throws Exception if the underlying format specific factory throws an exception when testing the stream (e.g. 
	 *         because of an IO error)
	 * @see JPhyloIOFormatIDs
	 * @see #guessReader(File, ReadWriteParameterMap)
	 */
	public String guessFormat(File file) throws Exception {
		return guessFormat(file, new ReadWriteParameterMap());
	}
	
	
	/**
	 * Tries to determine the format of the contents of the specified reader by examining at its beginning (e.g. the root 
	 * tag in XML formats). The format is determined by subsequent calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(Reader, ReadWriteParameterMap)} until a matching factory is found.
	 * <p>
	 * The parameter map is only necessary for formats that are so variable that parameter values are needed to determine 
	 * how a valid input would look like. That is currently not the case for any format supported in <i>JPhyloIO</i>, but
	 * may be necessary for third parts format-specific factories used in this instance. (Refer to the documentation of 
	 * third party format-specific factories for details.) In most cases the convenience method {@link #guessFormat(Reader)}
	 * will be sufficient.
	 * <p>
	 * Note that in contrast to {@link #guessReader(InputStream, ReadWriteParameterMap)}, this method does not support
	 * GZIPed inputs.
	 * 
	 * @param reader the reader providing the contents
	 * @param parameters the parameter map containing parameters for the <i>JPhyloIO</i> event reader that would 
	 *        be used with {@code reader} 
	 * @return the ID of the determined format or {@code null} if no supported format seems to be matching the contents
	 * @throws Exception if the underlying format specific factory throws an exception when testing the stream (e.g. 
	 *         because of an IO error)
	 * @see JPhyloIOFormatIDs
	 * @see #guessReader(InputStream, ReadWriteParameterMap)
	 */
	public String guessFormat(Reader reader, ReadWriteParameterMap parameters) throws Exception {
		LimitedReader limitedReader = new LimitedReader(new BufferedReader(reader, getReadAheadLimit()), getReadAheadLimit());
		limitedReader.mark(getReadAheadLimit());
		limitedReader.setAllowClose(false);  // Disallow closing, to avoid that XMLEventReaders close this reader, if they encounter an incomplete tag, because of the read limit. (Some implementations of XMLEventReader close the underlying stream, if they encounter an eof while trying to produce the next event.)
		for (SingleReaderWriterFactory factory : formatMap.values()) {
			boolean formatFound;
			try {
				formatFound = factory.checkFormat(limitedReader, parameters);
			}
			catch (ClosingNotAllowedException e) {
				formatFound = false;  // The limit was reached, before an XML format could be determined or an invalid XML file was encountered.
				//TODO Should formats that could not be determined anyway be treated as candidates or should any feedback be given on this?
			}
			limitedReader.reset();
			if (formatFound) {
				return factory.getFormatInfo().getFormatID();
			}
		}
		return null;
	}
	
	
	/**
	 * Tries to determine the format of the contents of the specified reader by examining at its beginning (e.g. the root 
	 * tag in XML formats). The format is determined by subsequent calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(Reader, ReadWriteParameterMap)} until a matching factory is found.
	 * <p>
	 * It uses an empty parameter map that is passed to the internal calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(Reader, ReadWriteParameterMap)}. Use 
	 * {@link #guessFormat(Reader, ReadWriteParameterMap)} if parameters are necessary to determine the format correctly.
	 * <p>
	 * Note that in contrast to {@link #guessReader(InputStream, ReadWriteParameterMap)}, this method does not support
	 * GZIPed inputs.
	 * 
	 * @param reader the reader providing the contents
	 * @return the ID of the determined format or {@code null} if no supported format seems to be matching the contents
	 * @throws Exception if the underlying format specific factory throws an exception when testing the stream (e.g. 
	 *         because of an IO error)
	 * @see JPhyloIOFormatIDs
	 * @see #guessReader(InputStream, ReadWriteParameterMap)
	 */
	public String guessFormat(Reader reader) throws Exception {
		return guessFormat(reader, new ReadWriteParameterMap());
	}
	
	
	/**
	 * Tries to determine the format of the contents of the specified input stream by examining at its beginning (e.g. 
	 * the root tag in XML formats). The format is determined by subsequent calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(InputStream, ReadWriteParameterMap)} until a matching factory is found.
	 * <p>
	 * The parameter map is only necessary for formats that are so variable that parameter values are needed to determine 
	 * how a valid input would look like. That is currently not the case for any format supported in <i>JPhyloIO</i>, but
	 * may be necessary for third parts format-specific factories used in this instance. (Refer to the documentation of 
	 * third party format-specific factories for details.) In most cases the convenience method 
	 * {@link #guessFormat(InputStream)} will be sufficient.
	 * <p>
	 * Note that in contrast to {@link #guessReader(InputStream, ReadWriteParameterMap)}, this method does not support
	 * GZIPed inputs.
	 * 
	 * @param reader the reader providing the contents
	 * @param parameters the parameter map containing parameters for the <i>JPhyloIO</i> event reader that would 
	 *        be used with {@code reader} 
	 * @return the ID of the determined format or {@code null} if no supported format seems to be matching the contents
	 * @throws Exception if the underlying format specific factory throws an exception when testing the stream (e.g. 
	 *         because of an IO error)
	 * @see JPhyloIOFormatIDs
	 * @see #guessReader(InputStream, ReadWriteParameterMap)
	 */
	public String guessFormat(InputStream stream, ReadWriteParameterMap parameters) throws Exception {
		return guessFormatFromLimitedStream(new LimitedInputStream(new BufferedInputStream(stream, getReadAheadLimit()), 
				getReadAheadLimit()), parameters);
	}
	
	
	private String guessFormatFromLimitedStream(InputStream limitedStream, ReadWriteParameterMap parameters) throws Exception {
		limitedStream.mark(getReadAheadLimit());  // Will also mark the decorated stream.
		for (SingleReaderWriterFactory factory : formatMap.values()) {
			boolean formatFound = factory.checkFormat(limitedStream, parameters);
			limitedStream.reset();  // Will also reset the decorated stream.
			if (formatFound) {
				return factory.getFormatInfo().getFormatID();
			}
		}
		return null;
	}

	
	/**
	 * Tries to determine the format of the contents of the specified reader by examining at its beginning (e.g. the root 
	 * tag in XML formats). The format is determined by subsequent calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(InputStream, ReadWriteParameterMap)} until a matching factory is found.
	 * <p>
	 * It uses an empty parameter map that is passed to the internal calls of 
	 * {@link SingleReaderWriterFactory#checkFormat(InputStream, ReadWriteParameterMap)}. Use 
	 * {@link #guessFormat(InputStream, ReadWriteParameterMap)} if parameters are necessary to determine the format 
	 * correctly.
	 * <p>
	 * Note that in contrast to {@link #guessReader(InputStream, ReadWriteParameterMap)}, this method does not support
	 * GZIPed inputs.
	 * 
	 * @param reader the reader providing the contents
	 * @return the ID of the determined format or {@code null} if no supported format seems to be matching the contents
	 * @throws Exception if the underlying format specific factory throws an exception when testing the stream (e.g. 
	 *         because of an IO error)
	 * @see JPhyloIOFormatIDs
	 * @see #guessReader(InputStream, ReadWriteParameterMap)
	 */
	public String guessFormat(InputStream stream) throws Exception {
		return guessFormat(stream, new ReadWriteParameterMap());
	}
	
	
	/**
	 * Returns an information object for the specified format.
	 * 
	 * @param formatID the unique format ID specifying the format
	 * @return the information object
	 */
	public JPhyloIOFormatInfo getFormatInfo(String formatID) {
		SingleReaderWriterFactory factory = formatMap.get(formatID);
		if (factory == null) {
			return null;
		}
		else {
			return factory.getFormatInfo();
		}
	}
	
	
	/**
	 * Tries to determine the format of the contents of the specified input stream as described in the documentation of
	 * {@link #guessFormat(InputStream)}, resets the input stream and than creates a reader for the according format
	 * from that stream. Additionally this method checks, if the input data is GZIPed and then returns a reader instance
	 * that unpacks the stream data while reading.
	 * <p>
	 * In other words, this method is able to return a functional reader for input streams in all formats supported by 
	 * this factory, as well as GZIPed streams providing data in any of these formats.
	 * <p>
	 * Note that bytes of {@code stream} will be consumed by this method to determine the format, even if no according 
	 * reader is found. If an event reader is returned, no events of this reader will have been consumed.
	 * <p>
	 * Note that there is no version of this method accepting a {@link Reader} instead of an {@link InputStream},
	 * because uncompressing data from a reader is not directly possible.
	 * 
	 * @param stream the stream to read the data from
	 * @param parameters the parameter map optionally containing parameters for the returned reader
	 * @return the new reader instance or {@code null} if no reader fitting the format of the stream could be found
	 * @throws Exception if an exception occurs while determining the format from the stream or creating the returned
	 *         reader instance (Depending on the type of reader that is returned, this will mostly be 
	 *         {@link IOException}s.)
	 * @see #guessFormat(InputStream)
	 * @see #getReader(String, InputStream, ReadWriteParameterMap)
	 */
	public JPhyloIOEventReader guessReader(InputStream stream, ReadWriteParameterMap parameters) throws Exception {
		// Buffer stream for testing:
		InputStream bufferedStream = new BufferedInputStream(stream, getReadAheadLimit());
		InputStream limitedStream = new LimitedInputStream(bufferedStream, getReadAheadLimit());
		limitedStream.mark(getReadAheadLimit());
		
	  // Try if the input is GZIPed:
		boolean isZipped = true;
		try {
			limitedStream = new BufferedInputStream(new GZIPInputStream(limitedStream), getReadAheadLimit());  // Test if stream is zipped with limitted stream. Will throw an exception otherwise. Uderlying stream still limits the read length.
			limitedStream.mark(getReadAheadLimit());
		}
		catch (ZipException e) {
			isZipped = false;
			limitedStream.reset();  // Reset bytes that have been read by GZIPInputStream. (If this code is called, limitedStream was not set in the try block.)
		}
		
		// Return reader:
		String format = guessFormatFromLimitedStream(limitedStream, parameters);
		if (format == null) {
			return null;
		}
		else {
			if (isZipped) {
				bufferedStream.reset();  // guessFormatFromLimitedStream() resets another buffered reader in this case.
				bufferedStream = new GZIPInputStream(bufferedStream);
			}
			return getReader(format, bufferedStream, parameters);
		}
		//TODO Does the any of the created streams in here need to be closed, if the underlying stream is closed later in application code? (Usually the top-most stream would be closed, which is not known by the application.)
	}
	
	
	/**
	 * Tries to determine the format of the contents of the specified file as described in the documentation of
	 * {@link #guessFormat(File)} and than creates a reader for the according format from that stream. Additionally this 
	 * method checks, if the input data is GZIPed and then returns a reader instance that unpacks the stream data while 
	 * reading.
	 * <p>
	 * In other words, this method is able to return a functional reader for files in all formats supported by 
	 * this factory, as well as GZIPed files in any of these formats.
	 * <p>
	 * Note that there is no version of this method accepting a {@link Reader} instead of an {@link InputStream},
	 * because uncompressing data from a reader is not directly possible.
	 * 
	 * @param stream the stream to read the data from
	 * @param parameters the parameter map optionally containing parameters for the returned reader
	 * @return the new reader instance or {@code null} if no reader fitting the format of the stream could be found
	 * @throws Exception if an exception occurs while determining the format from the stream or creating the returned
	 *         reader instance (Depending on the type of reader that is returned, this will mostly be 
	 *         {@link IOException}s.)
	 * @see #guessFormat(File)
	 * @see #getReader(String, File, ReadWriteParameterMap)
	 */
	public JPhyloIOEventReader guessReader(File file, ReadWriteParameterMap parameters) throws Exception {
		JPhyloIOEventReader result = null;
		FileInputStream stream = new FileInputStream(file);
		try {
			result = guessReader(stream, parameters);
		}
		finally {
			if (result == null) {  // Otherwise stream must be closed, by calling close() of the returned reader.
				stream.close();
			}
		}
		return result;
	}
	
	
	public JPhyloIOEventReader getReader(String formatID, InputStream stream, ReadWriteParameterMap parameters) throws Exception {
		SingleReaderWriterFactory factory = formatMap.get(formatID);
		if (factory == null) {
			return null;
		}
		else {
			return factory.getReader(stream, parameters);
		}
	}
	
	
	public JPhyloIOEventReader getReader(String formatID, File file, ReadWriteParameterMap parameters) throws Exception {
		JPhyloIOEventReader result = null;
		FileReader reader = new FileReader(file);
		try {
			result = getReader(formatID, reader, parameters);
		}
		finally {
			if (result == null) {  // Otherwise stream must be closed, by calling close() of the returned reader.
				reader.close();
			}
		}
		return result;
	}
	
	
	public JPhyloIOEventReader getReader(String formatID, Reader reader, ReadWriteParameterMap parameters) throws Exception {
		SingleReaderWriterFactory factory = formatMap.get(formatID);
		if (factory == null) {
			return null;
		}
		else {
			return factory.getReader(reader, parameters);
		}
	}
	
	
	public JPhyloIOEventWriter getWriter(String formatID) {
		SingleReaderWriterFactory factory = formatMap.get(formatID);
		if (factory == null) {
			return null;
		}
		else {
			return factory.getWriter();
		}
	}
}
