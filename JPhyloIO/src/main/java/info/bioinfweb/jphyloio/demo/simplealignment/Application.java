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
package info.bioinfweb.jphyloio.demo.simplealignment;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.implementations.ListBasedDocumentDataAdapter;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formats.JPhyloIOFormatIDs;

import java.io.File;
import java.io.IOException;



/**
 * This is the main class of this demo application. It offers three methods for reading, displaying and writing alignment data.
 * <p>
 * It identifies sequences using their unique <i>JPhyloIO</i> sequence IDs. Real world applications would usually also want to
 * store the human-readable sequence names stored in the input files. This is left out here for simplification.
 * 
 * @author Ben St&ouml;ver
 */
public class Application {
	/** Simple example business model of this application that models a multiple sequence alignment. */
	private ApplicationModel model = new ApplicationModel();

	/** Factory instance to be used, to create format specific <i>JPhyloIO</i> readers and writers. */
	private JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory(); 
	
	
	/**
	 * Reads a multiple sequence alignment from the specified file into the model.
	 * 
	 * @param file the input file in an alignment formal supported by <i>JPhyloIO</i>
	 */
	public void read(File file) {
		try {
			JPhyloIOEventReader eventReader = factory.guessReader(file, new ReadWriteParameterMap());
			if (eventReader != null) {
				try {
					new AlignmentReader().read(eventReader, model);
				}
				finally {
					eventReader.close();
				}
			}
			else {
				System.out.println("The format of the file \"" + file.getAbsolutePath() + "\" is not supported.");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Displays the contents of the current alignment model on the command line.
	 */
	public void display() {
		if (model.isEmpty()) {
			System.out.println("No data loaded.");
		}
		else {
			// Print title:
			if (model.getLabel() != null) {
				System.out.println(model.getLabel() + ":");
			}
			else {
				System.out.println("Unnamed alignment:");
			}
			
			// Print sequences:
			for (int i = 0; i < model.size(); i++) {
				System.out.print(model.getSequenceLabel(i) + ":\t");
				for (String token : model.getSequenceTokens(i)) {
					System.out.print(token + " ");
				}
				System.out.println();
			}
		}
	}
	
	
	/**
	 * Writes the multiple sequence alignment from the model to the specified file.
	 * 
	 * @param file the output file for the alignment
	 * @param formatID the <i>JPhyloIO</i> format ID that determines the format of the output file
	 */
	public void write(File file, String formatID) {
		// Prepare data adapters:
		ListBasedDocumentDataAdapter document = new ListBasedDocumentDataAdapter();
		document.getMatrices().add(new MatrixDataAdapterImpl(model));
		
		// Write data:
		JPhyloIOEventWriter writer = factory.getWriter(formatID);
		try {
			writer.writeDocument(document, file, new ReadWriteParameterMap());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		final File file = new File("data/input/Alignment.fasta");  // Specify another file name here to test another file.
		
		Application application = new Application();
		application.read(file);
		application.display();
		application.write(new File("data/output/Test.xml"), JPhyloIOFormatIDs.NEXML_FORMAT_ID);  // Specify another file name or format ID here to test other outputs.
	}
}
