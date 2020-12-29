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

import javax.xml.bind.DatatypeConverter;



/**
 * An object translator between {@code byte[]} and <a href="https://www.w3.org/TR/xmlschema11-2/#hexBinary">xsd:hexBinary</a>. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class HexBinaryTranslator extends IllegalArgumentExceptionSimpleValueTranslator<byte[]> {
	@Override
	public Class<byte[]> getObjectClass() {
		return byte[].class;
	}
	

	@Override
	protected byte[] parseValue(String representation, ReaderStreamDataProvider<?> streamDataProvider) throws IllegalArgumentException {
		return DatatypeConverter.parseHexBinary(representation);
	}


	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider)	throws UnsupportedOperationException, ClassCastException {
		return DatatypeConverter.printHexBinary((byte[])object);
	}
}
