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
package info.bioinfweb.jphyloio.objecttranslation;


import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.objecttranslation.implementations.Base64BinaryTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.BigDecimalTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.BigIntegerTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.BooleanTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.ByteTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.DateTimeTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.DateTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.DoubleTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.FloatTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.HexBinaryTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.IntegerTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.ListTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.LongTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.QNameTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.ShortTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.StringTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.TimeTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.URITranslator;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;



/**
 * Factory to create instances of {@link ObjectTranslator} to be used with readers and writers of <i>JPhyloIO</i> to read 
 * and write literal metadata objects. The factory to be used can be specified using the parameter 
 * {@link ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY}.
 * <p>
 * After creation this factory is empty. New translators can be added to this factory using 
 * {@link #addTranslator(ObjectTranslator, boolean)}. A default set of translators for XSD types can be added by calling
 * {@link #addXSDTranslators(boolean)}. Support for custom <i>JPhyloIO</i> data types can be added using 
 * {@link #addJPhyloIOTranslators(boolean)}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 * @see ObjectTranslator
 * @see ReadWriteParameterNames#KEY_OBJECT_TRANSLATOR_FACTORY
 */
public class ObjectTranslatorFactory implements W3CXSConstants, ReadWriteConstants {
	private Map<TranslatorMapKey, ObjectTranslator<?>> translatorMap = new HashMap<TranslatorMapKey, ObjectTranslator<?>>();
	
	
	/**
	 * Registers a new translator in this factory for a single data type.
	 * 
	 * @param translator the translator to be registered
	 * @param asDefault Determines whether this translator shall become the default translator for its data type. (It will
	 *        always become the default, if no other translator is currently registered for this data type.)
	 * @param dataType the data type to be associated with this translator
	 */
	private void addTranslator(ObjectTranslator<?> translator, boolean asDefault, QName dataType) {
		translatorMap.put(new TranslatorMapKey(dataType, translator.getObjectClass()), translator);
		if (asDefault || (getDefaultTranslator(dataType) == null)) {
			translatorMap.put(new TranslatorMapKey(dataType, null), translator);
		}
	}

	
	/**
	 * Registers a new translator in this factory for one or more data types.
	 * 
	 * @param translator the translator to be registered
	 * @param asDefault Determines whether this translator shall become the default translator for its data type. (It will
	 *        always become the default, if no other translator is currently registered for this data type.)
	 * @param dataType the data type to be associated with this translator
	 * @param additionalDataTypes additional data types for which the specified translator is also valid
	 */
	public void addTranslator(ObjectTranslator<?> translator, boolean asDefault, QName dataType, QName... additionalDataTypes) {
		addTranslator(translator, asDefault, dataType);
		for (int i = 0; i < additionalDataTypes.length; i++) {
			addTranslator(translator, asDefault, additionalDataTypes[i]);
		}
	}

	
	/**
	 * Adds all translators for XSD types available in <i>JPhyloIO</i>.
	 * 
	 * @param asDefault Determines whether the added translators shall become the default translators for their data type, 
	 *        if another default instance is already registered. (If {@code true} is specified, previous defaults will be
	 *        overwritten. If {@code false} is specified, previous defaults will be maintained. In all cases previous entries
	 *        will remain in the factory, if they have a different object type or will be completely overwritten if they have 
	 *        the same.)  
	 */
	public void addXSDTranslators(boolean asDefault) {
		addTranslator(new StringTranslator(), asDefault, DATA_TYPE_STRING, DATA_TYPE_TOKEN);
		addTranslator(new BooleanTranslator(), asDefault, DATA_TYPE_BOOLEAN);

		addTranslator(new QNameTranslator(), asDefault, DATA_TYPE_QNAME);
		addTranslator(new URITranslator(), asDefault, DATA_TYPE_ANY_URI);
		
		addTranslator(new ByteTranslator(), asDefault, DATA_TYPE_BYTE);
		addTranslator(new ShortTranslator(), asDefault, DATA_TYPE_SHORT, DATA_TYPE_UNSIGNED_BYTE);
		addTranslator(new IntegerTranslator(), asDefault, DATA_TYPE_INT, DATA_TYPE_UNSIGNED_SHORT);
		addTranslator(new LongTranslator(), asDefault, DATA_TYPE_LONG, DATA_TYPE_UNSIGNED_INT);
		addTranslator(new BigIntegerTranslator(), asDefault, DATA_TYPE_INTEGER, DATA_TYPE_NON_POSITIVE_INTEGER, DATA_TYPE_NEGATIVE_INTEGER, 
				DATA_TYPE_NON_NEGATIVE_INTEGER, DATA_TYPE_UNSIGNED_LONG, DATA_TYPE_POSITIVE_INTEGER);
		addTranslator(new FloatTranslator(), asDefault, DATA_TYPE_FLOAT);
		addTranslator(new DoubleTranslator(), asDefault, DATA_TYPE_DOUBLE);
		addTranslator(new BigDecimalTranslator(), asDefault, DATA_TYPE_DECIMAL);

		addTranslator(new DateTimeTranslator(), asDefault, DATA_TYPE_DATE_TIME);
		//TODO Add translator for https://www.w3.org/TR/2012/REC-xmlschema11-2-20120405/datatypes.html#dateTimeStamp
		//     Parsing can be done using DatatypeConverter.parseDateTime() but printing must be different. Maybe XMLGregorianCalender can be used?
		addTranslator(new DateTranslator(), asDefault, DATA_TYPE_DATE);
		addTranslator(new TimeTranslator(), asDefault, DATA_TYPE_TIME);

		addTranslator(new Base64BinaryTranslator(), asDefault, DATA_TYPE_BASE_64_BINARY);
		addTranslator(new HexBinaryTranslator(), asDefault, DATA_TYPE_HEX_BINARY);
		
		//TODO Add support for additional XSD types.
	}
	
	
	/**
	 * Adds all translators for custom types defined in <i>JPhyloIO</i>. Currently {@link ListTranslator} is registered
	 * under {@link ReadWriteConstants#DATA_TYPE_SIMPLE_VALUE_LIST}.
	 * 
	 * @param asDefault Determines whether the added translators shall become the default translators for their data type, 
	 *        if another default instance is already registered. (If {@code true} is specified, previous defaults will be
	 *        overwritten. If {@code false} is specified, previous defaults will be maintained. In all cases previous entries
	 *        will remain in the factory, if they have a different object type and will be completely overwritten if they have 
	 *        the same.)
	 * @since 0.3.0  
	 */
	public void addJPhyloIOTranslators(boolean asDefault) {
		addTranslator(new ListTranslator(), asDefault, DATA_TYPE_SIMPLE_VALUE_LIST);
	}
	
	
	@SuppressWarnings("unchecked")
	public <O> ObjectTranslator<O> getTranslator(QName dataType, Class<O> objectClass) {
		return (ObjectTranslator<O>)translatorMap.get(new TranslatorMapKey(dataType, objectClass));  // QName comparison works this way, since the prefix is not checked by QName.equals().
	}

	
	public ObjectTranslator<?> getDefaultTranslator(QName dataType) {
		return getTranslator(dataType, null);
	}

	
	public ObjectTranslator<?> getDefaultTranslatorWithPossiblyInvalidNamespace(QName dataType) {
		ObjectTranslator<?> translator = getTranslator(dataType, null);
		
		if ((translator == null) && (dataType != null) && (dataType.getNamespaceURI() != null) && !dataType.getNamespaceURI().isEmpty() 
				&& dataType.getNamespaceURI().endsWith("#")) {
			translator = getDefaultTranslator(new QName(dataType.getNamespaceURI().substring(0, dataType.getNamespaceURI().length() - 1), 
					dataType.getLocalPart()));
		}

		return translator;
	}
}
