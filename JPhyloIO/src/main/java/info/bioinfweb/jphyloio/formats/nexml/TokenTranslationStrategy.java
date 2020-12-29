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
package info.bioinfweb.jphyloio.formats.nexml;



/**
 * Instances of this enum can be used as parameter values for {@link ReadWriteParameterNames#KEY_NEXML_TOKEN_TRANSLATION_STRATEGY}.
 * It enumerates ways how tokens stored in a NeXML characters block of type {@code standard} should be parsed.
 * 
 * @author Sarah Wiechers
 * @see ReadWriteParameterNames#KEY_NEXML_TOKEN_TRANSLATION_STRATEGY
 * @see NeXMLEventReader
 * @since 0.0.0
 */
public enum TokenTranslationStrategy {
	/** 
	 * Sequences consisting of symbols are parsed without change.
	 */
	NEVER,
	
	/** 
	 * Symbols are translated to according IDs.
	 */
	SYMBOL_TO_ID,
	
	/** 
	 * Symbols are translated to according labels, if no labels are present IDs are used.
	 */
	SYMBOL_TO_LABEL;
}
