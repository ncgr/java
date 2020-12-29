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
package info.bioinfweb.jphyloio.formats.pde;



/**
 * The type of a meta column in the PDE format.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public enum PDEMetaColumnType {
	STRING,
	NUMBER,
	FILE,
	UNKNOWN;
	
	
	public static PDEMetaColumnType parseColumnType(String columnType) {
		if (PDEConstants.META_TYPE_STRING.equals(columnType.toUpperCase())) {
			return STRING;
		}
		else if (PDEConstants.META_TYPE_NUMBER.equals(columnType.toUpperCase())) {
			return NUMBER;
		}
		else if (PDEConstants.META_TYPE_FILE.equals(columnType.toUpperCase())) {
			return FILE;
		}
		else {
			return UNKNOWN;
		}
	}
	

	@Override
	public String toString() {
		switch (this) {
			case STRING:
				return PDEConstants.META_TYPE_STRING;
			case NUMBER:
				return PDEConstants.META_TYPE_NUMBER;
			case FILE:
				return PDEConstants.META_TYPE_FILE;
			default:
				return super.toString();
		}
	}
}
