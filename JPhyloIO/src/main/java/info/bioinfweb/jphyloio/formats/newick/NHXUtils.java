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
package info.bioinfweb.jphyloio.formats.newick;


import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLConstants;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;



/**
 * Tool method for working with NHX annotation on Newick strings. It currently offers methods to convert between NHX keys and
 * according PhyloXML predicates.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class NHXUtils implements ReadWriteConstants, NewickConstants, PhyloXMLConstants {
	private static final int NHX_KEY_COUNT = 6;


	private static NHXUtils firstInstance = null;
	
	private Map<String, QName> predicateByKeyMap;
	private Map<QName, String> keyByPredicateMap;
	
	
	private NHXUtils() {
		super();
		createMaps();
	}
	
	
	private void createMaps() {
		predicateByKeyMap = new HashMap<String, QName>(NHX_KEY_COUNT);
		predicateByKeyMap.put(NHX_KEY_GENE_NAME, PREDICATE_SEQUENCE_NAME);
		predicateByKeyMap.put(NHX_KEY_SEQUENCE_ACCESSION, PREDICATE_SEQUENCE_ACCESSION_VALUE);
		predicateByKeyMap.put(NHX_KEY_CONFIDENCE, PREDICATE_CONFIDENCE_VALUE);
		predicateByKeyMap.put(NHX_KEY_SCIENTIFIC_NAME, PREDICATE_TAXONOMY_SCIENTIFIC_NAME);
		predicateByKeyMap.put(NHX_KEY_TAXONOMY_ID, PREDICATE_TAXONOMY_ID_VALUE);
		// There is no predicate directly matching NHX_KEY_EVENT.
		
		keyByPredicateMap = new HashMap<QName, String>(NHX_KEY_COUNT);
		for (String key : predicateByKeyMap.keySet()) {
			keyByPredicateMap.put(predicateByKeyMap.get(key), key);
		}
	}
	
	
	/**
	 * Returns the shared instance of this class
	 * 
	 * @return the singleton instance
	 */
	public static NHXUtils getInstance() {
		if (firstInstance == null) {
			firstInstance = new NHXUtils();
		}
		return firstInstance;
	}
	
	
	/**
	 * Returns the PhyloXML predicate associated with the specified NHX key.
	 * <p>
	 * Note that the NHX key {@value NewickConstants#NHX_KEY_EVENT} is not converted, since there is no directly equivalent PhyloXML 
	 * predicate.
	 * 
	 * @param key the NHX key
	 * @return the PhyloXML predicate or {@link ReadWriteConstants#PREDICATE_HAS_LITERAL_METADATA} if no according predicate was found
	 */
	public QName predicateByKey(String key) {
		QName result = predicateByKeyMap.get(key);
		if (result == null) {
			result = PREDICATE_HAS_LITERAL_METADATA;
		}
		return result;
	}
	
	
	/**
	 * Returns the NHX key associated with the specified PhyloXML predicate.
	 * 
	 * @param predicate the PhyloXML predicate
	 * @return the according NHX key or {@code null}, if no key could be found
	 */
	public String keyByPredicate(QName predicate) {
		return keyByPredicateMap.get(predicate);
	}
}
