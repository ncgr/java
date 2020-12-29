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

import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;



/**
 * Abstract base class for implementations of {@link ObjectTranslator} that implements {@link #equals(Object)} and
 * {@link #hashCode()} based of the return values of {@link #getObjectClass()} and {@link #hasStringRepresentation()}.
 * <p>
 * Inherited classes that add additional properties must overwrite these methods. 
 * 
 * @author Ben St&ouml;ver
 *
 * @param <O> the type of Java object this translator instance is able to handle
 */
public abstract class AbstractObjectTranslator<O> implements ObjectTranslator<O> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (hasStringRepresentation() ? 1231 : 1237);
		result = prime * result
				+ ((getObjectClass() == null) ? 0 : getObjectClass().hashCode());
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractObjectTranslator<?> other = (AbstractObjectTranslator<?>) obj;
		if (hasStringRepresentation() != other.hasStringRepresentation())
			return false;
		if (getObjectClass() == null) {
			if (other.getObjectClass() != null)
				return false;
		} else if (!getObjectClass().equals(other.getObjectClass()))
			return false;
		return true;
	}
}
