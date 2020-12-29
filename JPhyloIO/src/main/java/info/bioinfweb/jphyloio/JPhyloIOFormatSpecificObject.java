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


import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;



/**
 * Interface to be implemented by all format-specific classes (e.g. event readers and writers) that provide 
 * information about their target format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public interface JPhyloIOFormatSpecificObject {
	/**
	 * Returns a string ID uniquely identifying the target format of this instance. Additional information on the
	 * format can be retrieved by passing the returned ID to {@link JPhyloIOReaderWriterFactory#getFormatInfo(String)}.
	 * <p>
	 * Third party developers that create readers or writers for additional formats must make sure to use a globally unique
	 * format ID. It is strongly recommended to use owned reverse domain names for this (e.g. 
	 * <code>org.example.additionalformat</code>).
	 * 
	 * @return the ID of the target format of this instance
	 * @see JPhyloIOReaderWriterFactory#getFormatInfo(String)
	 */
	public String getFormatID();
}
