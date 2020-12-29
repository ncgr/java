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
package info.bioinfweb.jphyloio.utils;


import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * Data storage class that allows instances of {@link JPhyloIOEventWriter} to give feedback to the application
 * on how labels of different elements (e.g. OTUs, sequences, nodes) were changed in order to fulfill the 
 * requirements of the target format. In some cases it may also happen that the specific element was not at all
 * written to the target format, because it is not supported there.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class LabelEditingReporter {
	private static class ContentTypeEntry {
		public Map<String, LabelMapping> labelMappings = new HashMap<String, LabelMapping>();
		public Set<String> usedLabels = new HashSet<String>();
	}
	
	
	private static class LabelMapping {
		public String label;
		public boolean edited;
		
		public LabelMapping(String label, boolean edited) {
			super();
			this.label = label;
			this.edited = edited;
		}
		
		public boolean wasNotWritten() {
			return label == null;
		}
	}
	
	
	/**
	 * Enumerates the possible cases of how the label associated with a data element was treated by an
	 * instance of {@link JPhyloIOEventWriter}.
	 * 
	 * @author Ben St&ouml;ver
	 * @since 0.0.0
	 */
	public static enum LabelStatus {
		/** The label was used unchanged in the target document. */
		UNCHANGED,
		
		/** The label was edited to match the requirements of the target document. */
		EDITED,
		
		/** The specified element was not written, because the target format does not support such elements. */
		NOT_WRITTEN,
		
		/** There is no entry for the specified element (probably it was not contained in the data to be written). */
		NOT_FOUND;
	}
	
	
	private Map<EventContentType, ContentTypeEntry> translations = 
			new EnumMap<EventContentType, ContentTypeEntry>(EventContentType.class);
	
	
	private ContentTypeEntry getContentTypeEntry(EventContentType contentType) {
		if (contentType == null) {
			throw new NullPointerException("The content type must not be null.");
		}
		else {
			ContentTypeEntry result = translations.get(contentType);
			if (result == null) {
				result = new ContentTypeEntry();
				translations.put(contentType, result);
			}
			return result;
		}
	}
	
	
	private LabelMapping getMapping(EventContentType contentType, String id) {
		return getContentTypeEntry(contentType).labelMappings.get(id);
	}
	
	
	/**
	 * Adds a new mapping from an element ID to the label used for it in the target document to this instance.
	 * 
	 * @param contentType the content type of the element to be mapped
	 * @param id the ID of the element to be mapped
	 * @param label the label that was used in the target document for the specified element
	 * @param edited Specify {@code true} here, if the specified label is not the label originally associated
	 *        with the specified element (denoted {@code id}).
	 * @throws NullPointerException if {@code contentType} or {@code id} are {@code null}
	 */
	public void addEdit(EventContentType contentType, String id, String label, boolean edited) {
		if (id == null) {
			throw new NullPointerException("The specified ID must not be null.");
		}
		else {
			ContentTypeEntry entry = getContentTypeEntry(contentType);
			entry.labelMappings.put(id, new LabelMapping(label, edited));
			entry.usedLabels.add(label);
		}
	}
	
	
	/**
	 * Adds a new mapping from an element ID to the label used for it in the target document to this instance.
	 * 
	 * @param event the event object describing the element associated with the edited label
	 * @param label the label that was used in the target document for the specified element
	 * @throws NullPointerException if {@code event} is {@code null}
	 */
	public void addEdit(LabeledIDEvent event, String label) {
		addEdit(event.getType().getContentType(), event.getID(), label, (label == null) || !label.equals(event.getLabel()));
	}
	
	
	/**
	 * Removes all mappings from this instance.
	 */
	public void clear() {
		translations.clear();
	}
	
	
	/**
	 * Returns the (edited) label associated with the specified data element. 
	 * 
	 * @param contentType the content type of the labeled data element
	 * @param id the ID of the labeled data element
	 * @return the label used in the document or {@code null} if the specified data element was either not
	 *         written to the target document or no according mapping could not be found in this instance
	 */
	public String getEditedLabel(EventContentType contentType, String id) {
		LabelMapping mapping = getMapping(contentType, id);
		if (mapping == null) {
			return null;
		}
		else {
			return mapping.label;
		}
	}
	
	
	/**
	 * Returns the (edited) label associated with the specified data element. 
	 * 
	 * @param event the event representing the labeled data element
	 * @return the label used in the document or {@code null} if the specified data element was either not
	 *         written to the target document or no according mapping could not be found in this instance
	 */
	public String getEditedLabel(LabeledIDEvent event) {
		return getEditedLabel(event.getType().getContentType(), event.getID());
	}
	
	
	/**
	 * Returns the status of the specified data element and its label.
	 * 
	 * @param contentType the content type of the labeled data element
	 * @param id the ID of the labeled data element
	 * @return the label status associated with the specified data element
	 */
	public LabelStatus getLabelStatus(EventContentType contentType, String id) {
		LabelMapping mapping = getContentTypeEntry(contentType).labelMappings.get(id);
		if (mapping == null) {
			return LabelStatus.NOT_FOUND;
		}
		else if (mapping.wasNotWritten()) {
			return LabelStatus.NOT_WRITTEN;
		}
		else if (mapping.edited) {
			return LabelStatus.EDITED;
		}
		else {
			return LabelStatus.UNCHANGED;
		}
	}
	
	
	/**
	 * Returns the status of the specified data element and its label.
	 * 
	 * @param event the event representing the labeled data element
	 * @return the label status associated with the specified data element
	 */
	public LabelStatus getLabelStatus(LabeledIDEvent event) {
		return getLabelStatus(event.getType().getContentType(), event.getID());
	}
	
	
	/**
	 * Checks whether the specified label was already used for the specified content type.
	 * This method is intended for internal use by implementations of {@link JPhyloIOEventWriter}
	 * and will usually not be of much use for application developers.
	 * 
	 * @param contentType the content type for which the label could have been used
	 * @param label the label to search for
	 * @return {@code true} if the specified label was already used until now or {@code false} otherwise
	 */
	public boolean isLabelUsed(EventContentType contentType, String label) {
		return getContentTypeEntry(contentType).usedLabels.contains(label);
	}
	
	
	/**
	 * Determines whether any label of data elements of the specified content type was edited. This method
	 * is useful, e.g. if applications want to check whether they shall inform the user about label editing
	 * after an instance of {@link JPhyloIOEventWriter} has been used to write a document.
	 * 
	 * @param contentType the content type of the labeled data element
	 * @return {@code true}
	 */
	public boolean anyLabelEdited(EventContentType contentType) {
		Map<String, LabelMapping> labelMappings = getContentTypeEntry(contentType).labelMappings; 
		for (String id : labelMappings.keySet()) {
			if (labelMappings.get(id).edited) {
				return true;
			}
		}
		return false;
	}
}
