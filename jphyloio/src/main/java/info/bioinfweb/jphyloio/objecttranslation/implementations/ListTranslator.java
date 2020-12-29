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


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.formats.newick.NewickConstants;
import info.bioinfweb.jphyloio.formats.newick.NewickUtils;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslatorFactory;



/**
 * Reads and writes string representations of simple values as used in <i>Newick</i> or <i>Nexus</i>. Elements can
 * either be strings or numeric values.
 * <p>
 * <b>Example:</b>
 * <pre>{18.2, 'AB C', 22}</pre>
 * For a possible format independent use, the data type {@link ReadWriteConstants#DATA_TYPE_SIMPLE_VALUE_LIST}
 * allows to use this translator also in <i>NeXML</i>, although it primarily meant for <i>Newick</i> and <i>Nexus</i>.
 * 
 * @author Ben St&ouml;ver
 * @see ReadWriteConstants#DATA_TYPE_SIMPLE_VALUE_LIST
 * @see ObjectTranslatorFactory#addJPhyloIOTranslators(boolean)
 */
public class ListTranslator extends SimpleValueTranslator<List<Object>> implements NewickConstants {
	@SuppressWarnings("unchecked")
	@Override
	public Class<List<Object>> getObjectClass() {
		return (Class<List<Object>>)(Object)List.class;
	}
		
	
	/**
	 * Reads a string representation of a simple value list into an implementation of {@link List}. The single elements are either
	 * instances of {@link String} or any implementation of {@link Number}. {@link Double} will be used by default, for values
	 * having a higher precision, {@link BigDecimal} will be used instead. 
	 * 
	 * @param representation the string representation of the list
	 * @return a list instance
	 * @throws InvalidObjectSourceDataException
	 */
	public static List<Object> parseList(String representation) throws InvalidObjectSourceDataException {
		representation = representation.trim();
		if (representation.startsWith("" + FIELD_START_SYMBOL) && representation.endsWith("" + FIELD_END_SYMBOL)) {
			List<Object> result = new ArrayList<Object>();
			NewickUtils.ReadElement element = NewickUtils.readNextElement(representation, 1, representation.length() - 1);
			while (element != null) {
				if (element.getNumericValue() != null) {
					result.add(element.getNumericValue());
				}
				else {
					result.add(element.getText());
				}
				element = NewickUtils.readNextElement(representation, element.getEndPos(), representation.length() - 1);
			}
			return result;
		}
		else {
			throw new InvalidObjectSourceDataException("List representations must be encolsed between '" + FIELD_START_SYMBOL + "' and '"
					+ FIELD_END_SYMBOL + "'.");
		}
	}
	
	
	/**
	 * Reads a string representation of a simple value list into an implementation of {@link List}. The single elements are either
	 * instances of {@link String} or any implementation of {@link Number}. {@link Double} will be used by default, for values
	 * having a higher precision, {@link BigDecimal} will be used instead.
	 * 
	 * @see info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator#representationToJava(java.lang.String, info.bioinfweb.jphyloio.ReaderStreamDataProvider)
	 */
	@Override
	public List<Object> representationToJava(String representation,	ReaderStreamDataProvider<?> streamDataProvider)
			throws InvalidObjectSourceDataException, UnsupportedOperationException {

		return parseList(representation);
	}


	public static String listToString(Iterable<?> list)	throws ClassCastException {
		StringBuilder result = new StringBuilder();
		result.append(FIELD_START_SYMBOL);
		
		Iterator<?> iterator = list.iterator();
		while (iterator.hasNext()) {
			Object element = iterator.next();
			if (element instanceof Number) {  // Write numeric value
				result.append(element.toString());
			}
			else {  // Write string
				result.append(NAME_DELIMITER);
				result.append(element.toString().replaceAll("\\" + NAME_DELIMITER, "" + NAME_DELIMITER + NAME_DELIMITER));  // Mask name delimiters contained in the string.
				result.append(NAME_DELIMITER);
			}
			
			if (iterator.hasNext()) {
				result.append(ELEMENT_SEPERATOR);
				result.append(' ');
			}
		}
		
		result.append(FIELD_END_SYMBOL);
		return result.toString();
	}
	
	
	/**
	 * Creates the string representation of a simple value list from any object that implements {@link Iterable}.
	 * 
	 * @param object the {@link Iterable} to be converted
	 * @return the string representation of the list 
	 * @throws ClassCastException if {@code object} does not implement {@link Iterable}
	 */
	@Override
	public String javaToRepresentation(Object object, WriterStreamDataProvider<?> streamDataProvider)	throws ClassCastException {
		return listToString((List<?>)object);
	}
}
