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


import info.bioinfweb.commons.SystemUtils;
import info.bioinfweb.commons.log.ApplicationLogger;
import info.bioinfweb.jphyloio.dataadapters.DocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ElementDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.OTUListDataAdapter;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LabeledIDEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.SingleSequenceTokenEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.InconsistentAdapterDataException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;



/**
 * Implements shared functionality for <i>JPhyloIO</i> event writers.
 *
 * @author Ben St&ouml;ver
 */
public abstract class AbstractEventWriter<P extends WriterStreamDataProvider<? extends AbstractEventWriter<P>>>	implements JPhyloIOEventWriter {
	public static final String EDITED_LABEL_SEPARATOR = "_";


	public static interface UniqueLabelHandler {
		public boolean isUnique(String label);

		public String editLabel(String label);
	}


	public static abstract class NoEditUniqueLabelHandler implements UniqueLabelHandler {
		@Override
		public String editLabel(String label) {
			return label;
		}
	}


	private String indention = "";
	private P streamDataProvider;  // Must not be set to anything here.


	public AbstractEventWriter() {
		super();
	}


	/**
	 * This method is called in the constructor of {@link AbstractEventWriter} to initialize the stream
	 * data provider that will be returned by {@link #getStreamDataProvider()}. Inheriting classes that use
	 * their own stream data provider implementation should overwrite this method.
	 * <p>
	 * This default implementation creates a new instance of {@link WriterStreamDataProvider}.
	 *
	 * @return the stream data provider to be used with this instance
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected P createStreamDataProvider() {
		return (P)new WriterStreamDataProvider(this);  // Cannot be created generic, since this implementation is used by different inherited classes.
	}


	public P getStreamDataProvider() {
		return streamDataProvider;
	}


	/**
	 * Writes the line separator, as it is specified in the parameter map or the line separator
	 * of the current operating system, if the map contains no according entry.
	 *
	 * @param writer the writer to write the line break to
	 * @param parameters the parameter map possibly containing a line separator definition
	 * @throws IOException if an I/O error occurs while writing to the specified writer
	 */
	public static void writeLineBreak(Writer writer, ReadWriteParameterMap parameters) throws IOException {
		writer.write(parameters.getString(ReadWriteParameterNames.KEY_LINE_SEPARATOR, SystemUtils.LINE_SEPARATOR));
	}


	/**
	 * Outputs a warning message, if the specified document data adapter provides one or more OTU lists.
	 *
	 * @param document the document data adapter to be checked for OTU lists
	 * @param logger the logger to write the warning to
	 * @param formatName the name of the format to which OTU lists cannot be written (will be included in the message)
	 * @param labeledElements the name of data elements (e.g. sequences or nodes) that may reference the OTU lists
	 *        (The warning message will use this name for a hint that the OTU lists may still be used to label this
	 *        type of elements.)
	 */
	public static void logIngnoredOTULists(DocumentDataAdapter document, ApplicationLogger logger, ReadWriteParameterMap parameters,
			String formatName, String labeledElements) {

		if (document.getOTUListIterator(parameters).hasNext()) {
			logger.addWarning("The specified OTU list(s) will not be written, since the " + formatName
					+	" format does not support this. Referenced lists will though be used to try to label " + labeledElements
					+ " if necessary.");
		}
	}


	/**
	 * Returns a name for the specified event.
	 *
	 * @param event the event to get the name of
	 * @return either the label of the event or its ID, if it does not carry a label
	 */
	public static String getLabeledIDName(LabeledIDEvent event) {
		if (event.hasLabel()) {
			return event.getLabel();
		}
		else {
			return event.getID();
		}
	}


	/**
	 * Determines the name (label) to be used for the specified event when writing its data to a file. The name is
	 * tried to be set by the following properties in decreasing priority:
	 * <ol>
	 *   <li>the label of {@code linkedOTUEvent} if present</li>
	 *   <li>the label of the linked OTU event, if any is linked and carries a label</li>
	 *   <li>the ID of {@code linkedOTUEvent}</li>
	 * </ol>
	 *
	 * @param linkedOTUEvent the event defining the name
	 * @param otuList the data adapter providing the OTU data
	 * @return a string representing the specified event as described
	 * @see #getLinkedOTUNameOTUFirst(LinkedLabeledIDEvent, OTUListDataAdapter)
	 */
	public static String getLinkedOTUNameOwnFirst(LinkedLabeledIDEvent linkedOTUEvent, OTUListDataAdapter otuList,
			ReadWriteParameterMap parameters) {

		String result = linkedOTUEvent.getLabel();
		if (result == null) {
			if (linkedOTUEvent.hasLink() && (otuList != null)) {
				result = otuList.getObjectStartEvent(parameters, linkedOTUEvent.getLinkedID()).getLabel();
			}
			if (result == null) {
				result = linkedOTUEvent.getID();
			}
		}
		return result;
	}
	//TODO Remove this method, if it us not used in the future.


	/**
	 * Determines the name (label) to be used for the specified event when writing its data to a file. The name is
	 * tried to be set by the following properties in decreasing priority:
	 * <ol>
	 *   <li>the label of the linked OTU event, if any is linked and carries a label</li>
	 *   <li>the ID of the linked OTU event, if any is linked</li>
	 *   <li>the label of {@code linkedOTUEvent} if present</li>
	 *   <li>the ID of {@code linkedOTUEvent}</li>
	 * </ol>
	 *
	 * @param linkedOTUEvent the event defining the name
	 * @param otuList the data adapter providing the OTU data
	 * @return a string representing the specified event as described
	 * @see #getLinkedOTUNameOwnFirst(LinkedLabeledIDEvent, OTUListDataAdapter)
	 */
	public static String getLinkedOTUNameOTUFirst(LinkedLabeledIDEvent linkedOTUEvent, OTUListDataAdapter otuList,
			ReadWriteParameterMap parameters) {

		if (linkedOTUEvent.hasLink() && (otuList != null)) {
			return getLabeledIDName(otuList.getObjectStartEvent(parameters, linkedOTUEvent.getLinkedID()));
		}
		else {
			return getLabeledIDName(linkedOTUEvent);
		}
	}


	/**
	 * Returns the OTU list found in {@code document} which is referenced by the specified event.
	 *
	 * @param document the document data adapter providing the OTU lists.
	 * @param source the event referencing the OTU list
	 * @return the referenced list or {@code null}, if the specified event does not reference any OTU
	 * @throws IllegalArgumentException if no OTU list with the specified ID is available in {@code document}
	 */
	public static OTUListDataAdapter getReferencedOTUList(DocumentDataAdapter document, ElementDataAdapter<LinkedLabeledIDEvent> source,
			ReadWriteParameterMap parameters) {

		OTUListDataAdapter result = null;
		String otuListID = source.getStartEvent(parameters).getLinkedID();
		if (otuListID != null) {
			result = document.getOTUList(parameters, otuListID);
		}
		return result;
	}


	/**
	 * Calculates the maximum sequence length in matrix with unequal lengths. This method iterates over all sequences
	 * if {@link MatrixDataAdapter#getColumnCount(ReadWriteParameterMap)} returns -1, otherwise it will directly return the specified column count.
	 *
	 * @param matrix the matrix data adapter containing the sequences
	 * @return the maximal sequence length or -1, if the specified matrix data adapter does not declare any sequences
	 */
	public static long determineMaxSequenceLength(MatrixDataAdapter matrix, ReadWriteParameterMap parameters) {
		long result = matrix.getColumnCount(parameters);
		if (result == -1) {
			Iterator<String> iterator = matrix.getSequenceIDIterator(parameters);
			while (iterator.hasNext()) {
				result = Math.max(result, matrix.getSequenceLength(parameters, iterator.next()));
			}
		}
		return result;
	}


	protected String getIndention() {
		return indention;
	}


	protected void writeLineStart(Writer writer, String text) throws IOException {
		if (indention.length() > 0) {
			writer.write(indention);
		}
		writer.write(text);
	}


	protected void increaseIndention() {
		indention += "\t";
	}


	protected void decreaseIndention() {
		if (indention.length() > 0) {
			indention = indention.substring(1);
		}
	}


	private static String createLabel(String prefix, String suffix, int maxLength, UniqueLabelHandler handler) {
		prefix = handler.editLabel(prefix);
		// Suffix is not edited, since it can only be "" or an integer value. (Problems would arise in formats which do not allow integers in names.)
		if (suffix.length() > maxLength) {
			throw new IllegalArgumentException("The label suffix \"" + suffix +
					"\" is longer than the specified maximum length (" + maxLength + ").");  //TODO Throw some kind of writer exception instead?
		}
		else {
			int lengthDif = (prefix.length() + suffix.length()) - maxLength;
			if (lengthDif > 0) {
				return prefix.substring(0, prefix.length() - lengthDif) + suffix;
			}
			else {
				return prefix + suffix;
			}
		}
	}


	public static String createUniqueLabel(ReadWriteParameterMap parameters, UniqueLabelHandler handler, String label1,
			String id1, String label2, String id2) {

		int maxLength = parameters.getInteger(ReadWriteParameterNames.KEY_MAXIMUM_NAME_LENGTH, Integer.MAX_VALUE);
		String result;
		if (label1 != null) {
			result = createLabel(label1, "", maxLength, handler);
		}
		else if (label2 != null) {
			result = createLabel(label2, "", maxLength, handler);
		}
		else {
			result = id1;
		}
		String initialResult = result;

		if (!handler.isUnique(result)) {
			if (label1 != null) {
				if (label2 != null) {
					result = label1 + EDITED_LABEL_SEPARATOR + label2;
				}

				if (!handler.isUnique(result)) {
					result = id1 + EDITED_LABEL_SEPARATOR + label1;
				}

				if (!handler.isUnique(result) && (label2 != null)) {
					result = id1 + EDITED_LABEL_SEPARATOR + label1 + EDITED_LABEL_SEPARATOR + label2;
				}
			}
		}

		if (!handler.isUnique(result)) {
			result = initialResult;  // Do not append index on combined labels.
			long suffix = 2;
			String editedResult;
			do {
				editedResult = createLabel(result, EDITED_LABEL_SEPARATOR + suffix, maxLength, handler);
				suffix++;
			}	while (!handler.isUnique(editedResult));
			result = editedResult;
		}

		return result;
	}


	public static String createUniqueLabel(ReadWriteParameterMap parameters, UniqueLabelHandler handler, LabeledIDEvent event) {
		String result = createUniqueLabel(parameters, handler, event.getLabel(), event.getID(), null, null);
		parameters.getLabelEditingReporter().addEdit(event, result);
		return result;
	}


	public static String createUniqueLabel(final ReadWriteParameterMap parameters, final LabeledIDEvent event) {
		return createUniqueLabel(parameters,
				new NoEditUniqueLabelHandler() {
					@Override
					public boolean isUnique(String label) {
						return !parameters.getLabelEditingReporter().isLabelUsed(event.getType().getContentType(), label);
					}
				},
				event);
	}


	public static String createUniqueLinkedOTULabel(ReadWriteParameterMap parameters, UniqueLabelHandler handler,
			LinkedLabeledIDEvent event, OTUListDataAdapter otuList, boolean otuFirst) {

		if (event.hasLink() && (otuList != null)) {
			try {
				String result;
				if (otuFirst) {
					result = createUniqueLabel(parameters, handler, otuList.getObjectStartEvent(parameters, event.getLinkedID()).getLabel(),
							event.getLinkedID(), event.getLabel(), event.getID());
				}
				else {
					result = createUniqueLabel(parameters, handler, event.getLabel(), event.getID(),
							otuList.getObjectStartEvent(parameters, event.getLinkedID()).getLabel(), event.getLinkedID());
				}
				parameters.getLabelEditingReporter().addEdit(event, result);
				return result;
			}
			catch (IllegalArgumentException e) {  // from otuList.getOTUStartEvent()
				throw new InconsistentAdapterDataException("The OTU with the ID " + event.getLinkedID() +
						" referenced by the data element with the ID " + event.getID() +
						" was not found in the OTU list with the ID " +	otuList.getStartEvent(parameters).getID());
			}
		}
		else {
			return createUniqueLabel(parameters, handler, event);
		}
	}


//	protected String createUniqueLabel(ReadWriteParameterMap parameters, UniqueLabelTester tester, LabeledIDEvent event) {
//		int maxLength = parameters.getInteger(ReadWriteParameterMap.KEY_MAXIMUM_NAME_LENGTH, Integer.MAX_VALUE);
//		String result = createLabel(getLabeledIDName(event), "", maxLength);  //TODO Consider OTU
//		if (!tester.isUnique(result)) {
//			if (event.hasLabel()) {
//				String alternative = createLabel(event.getID() + EDITED_LABEL_SEPARATOR + event.getLabel(), "", maxLength);  //TODO Use OTU instead in some cases.
//				if (tester.isUnique(alternative)) {  // If the alternative is not unique, keep sole label as the basis for upcoming operations.
//					result = alternative;
//				}
//			}
//
//			if (!tester.isUnique(result)) {
//				long suffix = 2;
//				String editedResult;
//				do {
//					editedResult = createLabel(result, EDITED_LABEL_SEPARATOR + suffix, maxLength);
//					suffix++;
//				}	while (!tester.isUnique(editedResult));
//				result = editedResult;
//			}
//		}
//
//		parameters.getLabelEditingReporter().addEdit(event, result);
//		return result;
//	}


	protected void extendSequence(MatrixDataAdapter matrix, ReadWriteParameterMap parameters, String sequenceID, long targetLength,
			String extensionToken, JPhyloIOEventReceiver receiver) throws IOException {

		if (extensionToken != null) {
			long additionalLength = targetLength - matrix.getSequenceLength(parameters, sequenceID);
			SingleSequenceTokenEvent startEvent = new SingleSequenceTokenEvent(null, extensionToken);
			ConcreteJPhyloIOEvent endEvent = ConcreteJPhyloIOEvent.createEndEvent(EventContentType.SINGLE_SEQUENCE_TOKEN);
			for (long i = 0; i < additionalLength; i++) {
				receiver.add(startEvent);
				receiver.add(endEvent);
			}
		}
	}


	protected String getFileStartInfo(ReadWriteParameterMap parameters) {
		StringBuffer applicationInfo = new StringBuffer();
		applicationInfo.append("This file was generated by ");

		if (parameters.getString(ReadWriteParameterMap.KEY_APPLICATION_NAME) != null) {
			applicationInfo.append(parameters.getString(ReadWriteParameterMap.KEY_APPLICATION_NAME));
			applicationInfo.append(" ");

			if (parameters.getObject(ReadWriteParameterMap.KEY_APPLICATION_VERSION, null) != null) {
				applicationInfo.append(parameters.getObject(ReadWriteParameterMap.KEY_APPLICATION_VERSION, null).toString());
				applicationInfo.append(" ");
			}

			if (parameters.getString(ReadWriteParameterMap.KEY_APPLICATION_URL) != null) {
				applicationInfo.append("<");
				applicationInfo.append(parameters.getString(ReadWriteParameterMap.KEY_APPLICATION_URL));
				applicationInfo.append("> ");
			}
		}
		else {
			applicationInfo.append("an application ");
		}

		applicationInfo.append("using ");
		applicationInfo.append(JPhyloIO.getInstance().getLibraryNameAndVersion());
		applicationInfo.append(" <");
		applicationInfo.append(JPhyloIO.getInstance().getProjectURL().toString());
		applicationInfo.append(">.");

		return applicationInfo.toString();
	}


	@Override
	public void writeDocument(DocumentDataAdapter document, File file, ReadWriteParameterMap parameters) throws IOException {
		Writer writer = new BufferedWriter(new FileWriter(file));
		try {
			writeDocument(document, writer, parameters);
		}
		finally {
			writer.close();
		}
	}


	@Override
	public void writeDocument(DocumentDataAdapter document, OutputStream stream, ReadWriteParameterMap parameters) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		try {
			writeDocument(document, writer, parameters);
		}
		finally {
			writer.close();  //TODO This is necessary but will probably prevent applications from writing further in the child stream. How can this be solved?
		}
	}


	@Override
	public void writeDocument(DocumentDataAdapter document, Writer writer, ReadWriteParameterMap parameters) throws IOException {
		streamDataProvider = createStreamDataProvider();  // Must be done here instead of in the constructor to make sure that no data stored in the provider is still present when writing the next document.
		parameters.put(ReadWriteParameterNames.KEY_WRITER_INSTANCE, this);
		doWriteDocument(document, writer, parameters);
	}


	protected abstract void doWriteDocument(DocumentDataAdapter document, Writer writer, ReadWriteParameterMap parameters) throws IOException;
}
