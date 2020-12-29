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
package info.bioinfweb.jphyloio;


import info.bioinfweb.commons.bio.SequenceUtils;
import info.bioinfweb.commons.collections.ParameterMap;
import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;
import info.bioinfweb.jphyloio.formats.nexml.TokenDefinitionLabelHandling;
import info.bioinfweb.jphyloio.formats.nexml.TokenTranslationStrategy;
import info.bioinfweb.jphyloio.formats.phyloxml.PhyloXMLMetadataTreatment;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslatorFactory;
import info.bioinfweb.jphyloio.utils.LabelEditingReporter;

import java.util.Map;



/**
 * Parameter map that allows to specify (optional) parameters to implementations of {@link JPhyloIOEventReader} or
 * {@link JPhyloIOEventWriter}.
 * 
 * @author Ben St&ouml;ver
 * @see ReadWriteParameterNames
 * @see JPhyloIOFormatInfo#getSupportedParameters(boolean)
 */
public class ReadWriteParameterMap extends ParameterMap implements ReadWriteParameterNames {
	/**
	 * Returns the writer instance registered under {@link #KEY_WRITER_INSTANCE}.
	 * 
	 * @return the current writer instance or {@code null} if none is present in the map (e.g. because it is used in a reader).
	 */
	public JPhyloIOEventWriter getWriterInstance() {
		return (JPhyloIOEventWriter)get(KEY_WRITER_INSTANCE);
	}
	
	
	public ApplicationLogger getLogger() {
		return getApplicationLogger(KEY_LOGGER);
	}
	
	
	public int getMaxTokensToRead() {
		return getInteger(KEY_MAXIMUM_TOKENS_TO_READ, ReadWriteConstants.DEFAULT_MAX_TOKENS_TO_READ);
	}
	
	
	public int getMaxCommentLength() {
		return getInteger(KEY_MAXIMUM_COMMENT_LENGTH, ReadWriteConstants.DEFAULT_MAX_COMMENT_LENGTH);
	}
	
	
	public String getMatchToken() {
		if (getBoolean(KEY_REPLACE_MATCH_TOKENS, true)) {
			return getString(KEY_MATCH_TOKEN, Character.toString(SequenceUtils.MATCH_CHAR));
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Returns the label editing reporter of this map stored under {@link #KEY_LABEL_EDITING_REPORTER}. If no object for 
	 * this key is present in this instance, a new one is created, added to this instance and returned. The same is done, 
	 * if an object which is not an instance of {@link LabelEditingReporter} is found for this key. 
	 * 
	 * @return the map instance
	 */
	public LabelEditingReporter getLabelEditingReporter() {
		Object result = get(KEY_LABEL_EDITING_REPORTER);
		if (!(result instanceof LabelEditingReporter)) {  // Also checks for null.
			result = new LabelEditingReporter();
			put(KEY_LABEL_EDITING_REPORTER, result);
		}
		return (LabelEditingReporter)result;
	}
	
	
	public TokenTranslationStrategy getTranslateTokens() {
		return getObject(ReadWriteParameterMap.KEY_NEXML_TOKEN_TRANSLATION_STRATEGY, 
				TokenTranslationStrategy.SYMBOL_TO_LABEL, TokenTranslationStrategy.class);
	}
	
	
	public TokenDefinitionLabelHandling getLabelHandling() {
		return getObject(ReadWriteParameterMap.KEY_NEXML_TOKEN_DEFINITION_LABEL_METADATA, 
				TokenDefinitionLabelHandling.NEITHER, TokenDefinitionLabelHandling.class);
	}
	
	
	public PhyloXMLMetadataTreatment getPhyloXMLMetadataTreatment() {
		return getObject(ReadWriteParameterMap.KEY_PHYLOXML_METADATA_TREATMENT, 
				PhyloXMLMetadataTreatment.SEQUENTIAL, PhyloXMLMetadataTreatment.class);
	}
	
	
	public Map<String, String> getPhyloXMLEventIDTranslationMap() {
		return getObject(ReadWriteParameterMap.KEY_PHYLOXML_EVENT_ID_TRANSLATION_MAP, null, Map.class);
	}


	/**
	 * Returns the object translator factory stored under {@link #KEY_OBJECT_TRANSLATOR_FACTORY}. If no object for 
	 * this key is present in this instance, a new one is created, added to this instance and returned. The same is done, 
	 * if an object which is not an instance of {@link LabelEditingReporter} is found for this key. The created default
	 * instance will contain all default translators provided with <i>JPhyloIO</i>.
	 * 
	 * @return the factory instance
	 */
	public ObjectTranslatorFactory getObjectTranslatorFactory() {
		Object result = get(KEY_OBJECT_TRANSLATOR_FACTORY);
		if (!(result instanceof ObjectTranslatorFactory)) {  // Also checks for null.
			result = new ObjectTranslatorFactory();
			((ObjectTranslatorFactory)result).addXSDTranslators(true);
			((ObjectTranslatorFactory)result).addJPhyloIOTranslators(true);
			put(KEY_OBJECT_TRANSLATOR_FACTORY, result);
		}
		return (ObjectTranslatorFactory)result;
	}
}
