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
package info.bioinfweb.jphyloio.objecttranslation.implementations;


import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;



/**
 * Abstract implementation for object translators that need XML tags to represent their object and do not support 
 * simple string representations.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <O> the type of Java object this translator instance is able to handle
 */
public abstract class AbstractXMLObjectTranslator<O> extends AbstractObjectTranslator<O> {
	@Override
	public boolean hasStringRepresentation() {
		return false;
	}

	
	@Override
	public String javaToRepresentation(Object object,	WriterStreamDataProvider<?> streamDataProvider)
			throws UnsupportedOperationException, ClassCastException {

		throw new UnsupportedOperationException("This translator does not support simple string representations.");
	}

	
	@Override
	public O representationToJava(String representation, ReaderStreamDataProvider<?> streamDataProvider)
			throws InvalidObjectSourceDataException, UnsupportedOperationException {

		throw new UnsupportedOperationException("This translator does not support simple string representations.");
	}
}
