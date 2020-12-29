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


import java.io.FileInputStream;

import info.bioinfweb.commons.io.ContentExtensionFileFilter;
import info.bioinfweb.jphyloio.JPhyloIOFormatSpecificObject;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;



public class JPhyloIOContentExtensionFileFilter extends ContentExtensionFileFilter implements JPhyloIOFormatSpecificObject {
	private SingleReaderWriterFactory factory;
	private ReadWriteParameterMap defaultParameters;
	
	
	public JPhyloIOContentExtensionFileFilter(SingleReaderWriterFactory factory, ReadWriteParameterMap defaultParamaters, 
			String description,	boolean addExtensionListToDescription, TestStrategy testStrategy, 
			boolean acceptFilesWithExceptions,	String... extensions) {
		
		super(description, addExtensionListToDescription, testStrategy, acceptFilesWithExceptions, extensions);
		
		if (factory == null) {
			throw new IllegalArgumentException("factory must not be null.");
		}
		else if (defaultParamaters == null) {
			throw new IllegalArgumentException("defaultParamaters must not be null.");
		}
		else {
			this.factory = factory;
			this.defaultParameters = defaultParamaters;
		}
	}

	
	@Override
	protected boolean acceptContent(FileInputStream stream) throws Exception {
		return factory.checkFormat(stream, defaultParameters);
	}


	@Override
	public String getFormatID() {
		return factory.getFormatInfo().getFormatID();
	}
}
