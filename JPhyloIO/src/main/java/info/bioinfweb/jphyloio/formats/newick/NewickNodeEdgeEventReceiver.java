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


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.receivers.BasicEventReceiver;
import info.bioinfweb.jphyloio.events.CommentEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.IllegalEventException;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;
import info.bioinfweb.jphyloio.exception.JPhyloIOWriterException;
import info.bioinfweb.jphyloio.formats.text.TextWriterStreamDataProvider;
import info.bioinfweb.jphyloio.objecttranslation.ObjectTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.ListTranslator;
import info.bioinfweb.jphyloio.objecttranslation.implementations.StringTranslator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;



@SuppressWarnings("rawtypes")  // Needs to be a raw type in order to work in Newick and Nexus.  //TODO Is there a better solution?
public class NewickNodeEdgeEventReceiver<E extends JPhyloIOEvent> extends BasicEventReceiver implements NewickConstants {
	public static final char STRING_DELEMITER_REPLACEMENT = '\'';
	
	
	private static class Metadata {
		public String key;
		public String value = null;
		public QName originalType = null;
		
		public Metadata(String key, URIOrStringIdentifier originalType) {
			super();
			this.key = key;
			if (originalType != null) {
				this.originalType = originalType.getURI();
			}
		}
	}
	
	
	private List<Metadata> metadataList = new ArrayList<Metadata>();
	private List<CommentEvent> commentEvents = new ArrayList<CommentEvent>();
	private boolean ignoredXMLMetadata = false;
	private StringBuilder currentLiteralValue = new StringBuilder();
	private ListTranslator listTranslator = new ListTranslator();
	private StringTranslator stringTranslator = new StringTranslator();

	
	@SuppressWarnings("unchecked")
	public NewickNodeEdgeEventReceiver(TextWriterStreamDataProvider<?> streamDataProvider, ReadWriteParameterMap parameterMap) {
		super(streamDataProvider, parameterMap);
	}


	@Override
	public TextWriterStreamDataProvider getStreamDataProvider() {
		return (TextWriterStreamDataProvider)super.getStreamDataProvider();
	}


	public boolean isIgnoredXMLMetadata() {
		return ignoredXMLMetadata;
	}


	private void clearCurrentLiteralValue() {
		currentLiteralValue.delete(0, currentLiteralValue.length());
	}
	
	
	public void clear() {
		commentEvents.clear();
		ignoredXMLMetadata = false;
		clearCurrentLiteralValue();
	}
	
	
	@Override
	protected void handleLiteralMetaStart(LiteralMetadataEvent event) throws IOException, XMLStreamException {
		if (event.getSequenceType().equals(LiteralContentSequenceType.SIMPLE)) {
			String key = event.getPredicate().getStringRepresentation();  //TODO Does the string representation need to be checked e.g. for spaces?
			if (key == null) {
				if (event.getPredicate().getURI() == null) {
					throw new JPhyloIOWriterException("A literal metadata event without predicate or alternative string representation was encountered.");  // Should not happen, since this was already checked in the constructor of URIOrStringIdentifier.
				}
				else {
					key = event.getPredicate().getURI().getLocalPart();  // uri cannot be null, if stringRepresentation was null. 
				}
			}
			metadataList.add(new Metadata(key, event.getOriginalType()));
			//TODO Add values later. Possibly throw exception e.g. in handleMetaEndEvent, if no value was specified or allow empty annotations.
		}
		else {  // Will also be executed for OTHER.
			ignoredXMLMetadata = true;
		}
	}


	@Override
	protected void handleLiteralContentMeta(LiteralMetadataContentEvent event) throws IOException, XMLStreamException {
		if (metadataList.isEmpty()) {
			throw new InternalError("No metadata entry was add for the parent literal meta event.");  // Should not happen.
		}
		else {
			//TODO What happens to XML content events? -> Write test case!
			
			if (event.isContinuedInNextEvent() || (currentLiteralValue.length() > 0)) {
				if (!event.hasStringValue() || event.hasObjectValue()) {
					throw new IllegalEventException("A literal metadata content event with null as its string representation and/or a non null "
							+ "object value was encountered, although it specifies to be continued or followes upon a continued string event.", this, 
							getParentEvent(), event);
				}
				else {
					currentLiteralValue.append(event.getStringValue());
				}
			}

			if (!event.isContinuedInNextEvent()) {
				String value;
				Metadata metadata = metadataList.get(metadataList.size() - 1);
				if (!event.hasStringValue()) {
					ObjectTranslator<?> translator = null;
					if (metadata.originalType != null) {
						translator = getParameterMap().getObjectTranslatorFactory().getDefaultTranslator(metadata.originalType);
					}
					if (translator == null) {
						if (event.getObjectValue() instanceof Collection) {
							translator = listTranslator;
						}
						else {
							translator = stringTranslator;
						}
					}
					value = translator.javaToRepresentation(event.getObjectValue(), getStreamDataProvider());
				}
				else {
					if (currentLiteralValue.length() > 0) {
						value = currentLiteralValue.toString();
					}
					else {
						value = event.getStringValue();
					}
					if (!(event.getObjectValue() instanceof Number)) {
						value = NAME_DELIMITER + value.replaceAll("\\" + NAME_DELIMITER, "" + NAME_DELIMITER + NAME_DELIMITER) + NAME_DELIMITER;
					}
				}
				
				if (metadata.value == null) {
					metadata.value = value;
				}
				else {
					throw new IllegalEventException(
							"An additional literal metadata content event was encountered, although the previous content event was a terminating event.", 
							this, getParentEvent(), event);
				}
				clearCurrentLiteralValue();
			}
		}
	}


	@Override
	protected void handleMetaEndEvent(JPhyloIOEvent event) throws IOException, XMLStreamException {
		if (event.getType().getContentType().equals(EventContentType.LITERAL_META) && currentLiteralValue.length() > 0) {
			throw new InconsistentAdapterDataException("A literal meta end event was encounterd, although the last literal meta content "
					+ "event was marked to be continued in a subsequent event.");
		}
		//TODO The functionality of this method should be moved to AbstractEventReceiver.
	}


	@Override
	protected void handleComment(CommentEvent event) {
		commentEvents.add(event.asCommentEvent());
	}

	
	/**
	 * Determines whether this receiver currently stores metadata to be written into a <i>Newick</i>/<i>Nexus</i> hot comment.
	 * 
	 * @return {@code true} if any data can be written, {@code false} otherwise
	 */
	public boolean hasMetadataToWrite() {
		return !metadataList.isEmpty();
	}
	

	/**
	 * Write the metadata stored in this receiver into a <i>Newick</i>/<i>Nexus</i> hot comment.
	 * 
	 * @throws IOException
	 */
	public void writeMetadata() throws IOException {
		if (hasMetadataToWrite()) {
			getStreamDataProvider().getWriter().write(COMMENT_START);
			getStreamDataProvider().getWriter().write(HOT_COMMENT_START_SYMBOL);
			Iterator<Metadata> iterator = metadataList.iterator();
			while (iterator.hasNext()) {
				Metadata metadata = iterator.next();
				getStreamDataProvider().getWriter().write(metadata.key);
				getStreamDataProvider().getWriter().write(ALLOCATION_SYMBOL);
				if (metadata.value != null) {
					getStreamDataProvider().getWriter().write(metadata.value);  // Necessary string delimiters or array definitions have already been add.
				}
				if (iterator.hasNext()) {
					getStreamDataProvider().getWriter().write(ALLOCATION_SEPARATOR_SYMBOL);
					getStreamDataProvider().getWriter().write(' ');
				}
			}
			getStreamDataProvider().getWriter().write(COMMENT_END);
		}
	}

	
	public void writeComments() throws IOException {
		Iterator<CommentEvent> iterator = commentEvents.iterator();
		while (iterator.hasNext()) {
			getStreamDataProvider().getWriter().write(COMMENT_START);
			CommentEvent event = iterator.next();
			getStreamDataProvider().getWriter().write(event.getContent());
			
			while (event.isContinuedInNextEvent() && iterator.hasNext()) {
				event = iterator.next();
				getStreamDataProvider().getWriter().write(event.getContent());
			}
			getStreamDataProvider().getWriter().write(COMMENT_END);
		}
	}
}
