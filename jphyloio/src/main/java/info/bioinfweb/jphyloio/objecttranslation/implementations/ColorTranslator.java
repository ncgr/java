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
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslatorFactory;

import java.awt.Color;



/**
 * An object translator that converts between a {@link Color} instance and its hexadecimal representation.
 * <p>
 * This implementation can be used with any predicate but needs to be registered in 
 * {@link ObjectTranslatorFactory}. Since there is no default color XDS type, it is not used by default.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class ColorTranslator extends IllegalArgumentExceptionSimpleValueTranslator<Color> {
	@Override
	public Class<Color> getObjectClass() {
		return Color.class;
	}

	
	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider)
			throws UnsupportedOperationException, ClassCastException {
		
		Color color = (Color)object;
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}


	@Override
	protected Color parseValue(String representation, ReaderStreamDataProvider<?> streamDataProvider)
			throws NumberFormatException {

		return Color.decode(representation);
	}	
}
