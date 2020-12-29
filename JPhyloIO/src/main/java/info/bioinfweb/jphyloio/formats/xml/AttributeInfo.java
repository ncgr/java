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
package info.bioinfweb.jphyloio.formats.xml;


import javax.xml.namespace.QName;



/**
 * This class models an XML attribute. It is used by XML readers.
 * 
 * @author Sarah Wiechers
 *
 */
public class AttributeInfo {
	private QName attributeName;
	private QName predicate;
	private QName datatype;
	
	
	public AttributeInfo(QName attributeName, QName predicate, QName datatype) {
		super();
		this.attributeName = attributeName;
		this.predicate = predicate;
		this.datatype = datatype;
	}


	public QName getAttributeName() {
		return attributeName;
	}


	public QName getPredicate() {
		return predicate;
	}


	public QName getDatatype() {
		return datatype;
	}
}
