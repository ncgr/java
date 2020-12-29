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


import java.awt.Color;

import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;



/**
 * Converts between instances of {@link Color} and their hexadecimal representation (e.g. {@code #F0F0F0}).
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class HexadecimalColorTranslator extends IllegalArgumentExceptionSimpleValueTranslator<Color> {
	@Override
	public Class<Color> getObjectClass() {
		return Color.class;
	}

	
	@Override
	protected Color parseValue(String representation, ReaderStreamDataProvider<?> streamDataProvider)	throws IllegalArgumentException {
		return Color.decode(representation);
	}


	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider)
			throws UnsupportedOperationException, ClassCastException {

		Color c = (Color)object;
		return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
	}
}
