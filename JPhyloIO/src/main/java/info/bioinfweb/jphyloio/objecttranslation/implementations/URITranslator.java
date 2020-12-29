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
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;

import java.net.URI;
import java.net.URISyntaxException;



/**
 * An object translator from and to {@link URI}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class URITranslator extends SimpleValueTranslator<URI> {
	@Override
	public Class<URI> getObjectClass() {
		return URI.class;
	}
	

	@Override
	public URI representationToJava(String representation, ReaderStreamDataProvider<?> streamDataProvider) throws InvalidObjectSourceDataException, UnsupportedOperationException {
		try {
			return new URI(representation);
		} 
		catch (URISyntaxException e) {
			throw new InvalidObjectSourceDataException(e);
		}
	}
}
