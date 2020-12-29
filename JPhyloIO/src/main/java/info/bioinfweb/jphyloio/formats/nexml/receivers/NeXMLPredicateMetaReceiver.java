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
package info.bioinfweb.jphyloio.formats.nexml.receivers;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventReader;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLEventWriter;
import info.bioinfweb.jphyloio.formats.nexml.NeXMLWriterStreamDataProvider;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;



/**
 * General implementation of a receiver that allows to process metadata events with certain predicates in a special way.
 * 
 * @author Sarah Wiechers
 */
public abstract class NeXMLPredicateMetaReceiver extends NeXMLMetaDataReceiver {
	private List<QName> predicates = new ArrayList<QName>();
	private boolean isUnderPredicate;
	private long metaLevel = 0;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param writer the XML writer of the calling {@link NeXMLEventWriter}
	 * @param parameterMap the parameter map of the calling {@link NeXMLEventWriter}
	 * @param streamDataProvider the stream data provider of the calling {@link NeXMLEventReader}
	 * @param predicates the predicates to be processed in a special way
	 */
	public NeXMLPredicateMetaReceiver(NeXMLWriterStreamDataProvider streamDataProvider, ReadWriteParameterMap parameterMap, QName... predicates) {
		super(streamDataProvider, parameterMap);
		
		for (int i = 0; i < predicates.length; i++) {
			this.predicates.add(predicates[i]);
		}
	}
	

	protected long getMetaLevel() {
		return metaLevel;
	}	


	protected void setMetaLevel(long metaLevel) {
		this.metaLevel = metaLevel;
	}
	
	
	protected void changeMetaLevel(long addend) {
		metaLevel = metaLevel + addend;
	}


	public boolean isUnderPredicate() {
		return isUnderPredicate;
	}


	public void setUnderPredicate(boolean isUnderPredicate) {
		this.isUnderPredicate = isUnderPredicate;
	}


	protected List<QName> getPredicates() {
		return predicates;
	}
}
