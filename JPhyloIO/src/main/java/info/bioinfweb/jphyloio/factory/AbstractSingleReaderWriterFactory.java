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
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;

import java.io.InputStream;
import java.io.InputStreamReader;



public abstract class AbstractSingleReaderWriterFactory implements SingleReaderWriterFactory {
	private JPhyloIOFormatInfo formatInfo;
	
	
	@Override
	public boolean checkFormat(InputStream stream, ReadWriteParameterMap parameters) throws Exception {
		return checkFormat(new InputStreamReader(stream), parameters);
	}

	
	protected abstract JPhyloIOFormatInfo createFormatInfo();
	

	@Override
	public JPhyloIOFormatInfo getFormatInfo() {
		if (formatInfo == null) {
			formatInfo = createFormatInfo();
		}
		return formatInfo;
	}
}
