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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;
import info.bioinfweb.jphyloio.formats.text.AbstractTextEventReader;
import info.bioinfweb.jphyloio.formats.text.TextReaderStreamDataProvider;



/**
 * Reads tree files in <i>Newick</i> format. <i>Newick</i> files are considered as text files, that contain trees as 
 * <i>Newick</i> strings which are separated by {@code ';'}. Any whitespace, as well as comments contained in {@code '['} 
 * and {@code ']'} is allowed between all tokens.
 * <p>
 * Additionally this reader is able to parse hot comments associated with nodes or edges and the <i>eNexus</i> network formats 
 * as described in the documentation of {@link NewickStringReader}.
 * 
 * <h3><a id="parameters"></a>Recognized parameters</h3>
 * <ul>
 *   <li>{@link ReadWriteParameterNames#KEY_EXPECT_E_NEWICK}</li>
 * </ul>
 * 
 * @author Ben St&ouml;ver
 * @see <a href="http://r.bioinfweb.info/JPhyloIODemoMetadata">Metadata demo application</a>
 */
public class NewickEventReader extends AbstractTextEventReader<TextReaderStreamDataProvider<NewickEventReader>> 
		implements NewickConstants {
	
	private static enum State {
		START,
		IN_DOCUMENT,
		END;
	}
	
	
	private State state = State.START;
	private NewickStringReader newickStringReader;
	
	
	private void init() {
		newickStringReader = new NewickStringReader(getStreamDataProvider(), null, null, new DefaultNewickReaderNodeLabelProcessor(),
				getStreamDataProvider().getParameters().getBoolean(ReadWriteParameterNames.KEY_EXPECT_E_NEWICK, false));
	}
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param reader the reader providing the Newick data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NewickEventReader(BufferedReader reader, ReadWriteParameterMap parameters) throws IOException {
		super(reader, parameters, null);
		init();
	}

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param file the Newick file to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NewickEventReader(File file, ReadWriteParameterMap parameters) throws IOException {
		super(file, parameters, null);
		init();
	}

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param stream the stream providing the Newick data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NewickEventReader(InputStream stream, ReadWriteParameterMap parameters) throws IOException {
		super(stream, parameters, null);
		init();
	}

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param reader the reader providing the Newick data to be read 
	 * @param parameters the parameter map for this reader instance 
	 * @throws IOException if an I/O exception occurs while parsing the first event
	 */
	public NewickEventReader(Reader reader, ReadWriteParameterMap parameters) throws IOException {
		super(reader, parameters, null);
		init();
	}
	
	
	@Override
	public String getFormatID() {
		return JPhyloIOFormatIDs.NEWICK_FORMAT_ID;
	}
	
	
	@Override
	protected void readNextEvent() throws IOException {
		switch (state) {
			case START:
				state = State.IN_DOCUMENT;
				getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.DOCUMENT, EventTopologyType.START));
				getCurrentEventCollection().add(new LinkedLabeledIDEvent(EventContentType.TREE_NETWORK_GROUP, DEFAULT_TREE_NETWORK_GROUP_ID_PREFIX + 
						getStreamDataProvider().getIDManager().createNewID(), null, null));
				//TODO Empty files define a tree group as well this way. This could be avoided, if the first event produced by NewickStringReader would be buffered in a different event queue. Then the group start event could be fired depending on the first return value of addNextEvents() below.
				break;
			case IN_DOCUMENT:
				if (!newickStringReader.addNextEvents()) {
					state = State.END;
					getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.TREE_NETWORK_GROUP, EventTopologyType.END));
					getCurrentEventCollection().add(new ConcreteJPhyloIOEvent(EventContentType.DOCUMENT, EventTopologyType.END));
				}
				break;
			case END:
				break;
			default:
				throw new InternalError("Unsupported state " + state + ".");
		}
	}
}
