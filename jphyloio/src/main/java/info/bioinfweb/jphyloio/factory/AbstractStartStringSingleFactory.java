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


import info.bioinfweb.jphyloio.ReadWriteParameterMap;

import java.io.IOException;
import java.io.Reader;



/**
 * Abstract single format factory that implements the {@code checkFormat()} methods by testing of the read content
 * start with a certain string, determined by {@link #getExpectedStart()}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AbstractStartStringSingleFactory extends AbstractSingleReaderWriterFactory 
		implements SingleReaderWriterFactory {
	
	private String expectedStart;
	

	/**
	 * Creates a new instance of this class.
	 * 
	 * @param expectedStart the start of the content to be expected if it is valid for the target format
	 */
	public AbstractStartStringSingleFactory(String expectedStart) {
		super();
		this.expectedStart = expectedStart;
	}


	protected String getExpectedStart() {
		return expectedStart;
	}


	@Override
	public boolean checkFormat(Reader reader, ReadWriteParameterMap parameters) throws IOException {
		for (int i = 0; i < getExpectedStart().length(); i++) {
			int c = reader.read();
			if ((c == -1) || (Character.toUpperCase((char)c) != getExpectedStart().charAt(i))) {
				return false;
			}
		}
		return true;
	}	
}
