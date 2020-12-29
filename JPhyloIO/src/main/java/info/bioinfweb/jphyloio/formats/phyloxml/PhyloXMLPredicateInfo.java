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
package info.bioinfweb.jphyloio.formats.phyloxml;


import javax.xml.namespace.QName;



/**
 * Class that is used as a value in {@link PhyloXMLWriterStreamDataProvider#getPredicateInfoMap()}. It stores information 
 * about the way a predicate shall be translated to a <i>PhyloXML</i> specific tag.
 * 
 * @author Sarah Wiechers
 *
 */
public class PhyloXMLPredicateInfo {
	private PhyloXMLPredicateTreatment treatment;
	private QName translation;
	private QName[] allowedChildren;
	//TODO add properties to store how often an element can occur (e.g. minimum 0, maximum 1; minimum 3, maximum arbitrary; ...)
	
	
	public PhyloXMLPredicateInfo(PhyloXMLPredicateTreatment treatment, QName translation, QName... allowedChildren) {
		super();
		this.treatment = treatment;
		this.translation = translation;
		this.allowedChildren = allowedChildren;
	}


	public PhyloXMLPredicateTreatment getTreatment() {
		return treatment;
	}


	public QName getTranslation() {
		return translation;
	}


	public QName[] getAllowedChildren() {
		return allowedChildren;
	}
}
