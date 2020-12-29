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
package info.bioinfweb.jphyloio.demo.tree;


import info.bioinfweb.commons.appversion.ApplicationType;
import info.bioinfweb.commons.appversion.ApplicationVersion;
import info.bioinfweb.commons.io.ContentExtensionFileFilter;
import info.bioinfweb.commons.io.ContentExtensionFileFilter.TestStrategy;
import info.bioinfweb.commons.io.ExtensionFileFilter;
import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.JPhyloIOEventWriter;
import info.bioinfweb.jphyloio.JPhyloIOFormatSpecificObject;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.dataadapters.implementations.ListBasedDocumentDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.implementations.store.StoreTreeNetworkGroupDataAdapter;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.factory.JPhyloIOContentExtensionFileFilter;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formatinfo.JPhyloIOFormatInfo;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.collections4.set.ListOrderedSet;



public class Application {
	protected JFrame mainFrame;
	protected JTree tree;
	private JFileChooser fileChooser;
	private List<FileFilter> readingFilters = new ArrayList<FileFilter>();
	private List<FileFilter> writingFilters = new ArrayList<FileFilter>();
	
	protected JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.mainFrame.setVisible(true);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Create the application.
	 */
	public Application() {
		initialize();  // Create GUI components.
	}
	
	
	public String getName() {
		return "JPhyloIO tree demo application";
	}
	
	
	public ApplicationVersion getVersion() {
		return new ApplicationVersion(1, 1, 0, 1368, ApplicationType.BETA);
	}
	
	
	public String getApplicationURL() {
		return "http://r.bioinfweb.info/JPhyloIODemoTree";
	}
	
	
	protected void readTree(String formatID, File file) throws Exception {
		ReadWriteParameterMap parameters = new ReadWriteParameterMap();
		parameters.put(ReadWriteParameterNames.KEY_USE_OTU_LABEL, true);  
				// Use OTU labels as node labels if no node label is present.
		parameters.put(ReadWriteParameterNames.KEY_PHYLOXML_CONSIDER_PHYLOGENY_AS_TREE, true);
				// This parameter defines if cross links between nodes (defined by the clade_relation tag of PhyloXML) should be
				// modeled as metadata attached to a node or if the whole phylogeny shall be interpreted as a phylogenetic network.
				// Since the network interpretation is the default, we need to set this parameter in order to receive tree events
				// and not network events.
		
		JPhyloIOEventReader eventReader = factory.getReader(formatID, file, parameters);  // Create JPhyloIO reader instance for the determined format.
		try {
			new TreeReader().read(eventReader, getTreeModel());  // Read tree into the data model of this application.
		}
		finally {
			eventReader.close();
		}
	}
	
	
	protected void writeTree(String formatID, File file) {
		// Create data adapters:
		ListBasedDocumentDataAdapter document = new ListBasedDocumentDataAdapter();
		StoreTreeNetworkGroupDataAdapter treeGroup = new StoreTreeNetworkGroupDataAdapter(
				new LinkedLabeledIDEvent(EventContentType.TREE_NETWORK_GROUP, "treeGroup", null, null), null);
		document.getTreeNetworkGroups().add(treeGroup);
		treeGroup.getTreesAndNetworks().add(new TreeNetworkDataAdapterImpl(getTreeModel()));
		
		// Define writer parameters:
		ReadWriteParameterMap parameters = new ReadWriteParameterMap();
		parameters.put(ReadWriteParameterNames.KEY_APPLICATION_NAME, getName());
		parameters.put(ReadWriteParameterNames.KEY_APPLICATION_VERSION, getVersion());
		parameters.put(ReadWriteParameterNames.KEY_APPLICATION_URL, getApplicationURL());
		
		// Write document:
		JPhyloIOEventWriter writer = factory.getWriter(formatID);
		try {
			writer.writeDocument(document, file, parameters);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainFrame, "The error \"" + ex.getLocalizedMessage() + "\" occurred.", 
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	/**
	 * Initialize the contents of the frame.
	 */
	protected void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle(getName());
		mainFrame.setBounds(100, 100, 600, 400);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		tree = new JTree(new DefaultTreeModel(null));
		JScrollPane scrollPane = new JScrollPane(tree);
		mainFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		mainFrame.setJMenuBar(createMenuBar());
	}
	
	
	private void setFileFilters(List<FileFilter> filters) {
		getFileChooser().resetChoosableFileFilters();
		for (FileFilter filter : filters) {
			getFileChooser().addChoosableFileFilter(filter);
		}
	}
	
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open...");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setFileFilters(readingFilters);
					if (getFileChooser().showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
						String formatID;
						if (getFileChooser().getFileFilter() instanceof JPhyloIOFormatSpecificObject) {
							formatID = ((JPhyloIOFormatSpecificObject)getFileChooser().getFileFilter()).getFormatID();  // Use the user defined format.
						}
						else {  // In this case the "All supported formats" filter was used and the format needs to be guessed.
							formatID = factory.guessFormat(getFileChooser().getSelectedFile());  // Guess the format, since the user did not explicitly specify one.
						}
						
						if (formatID != null) {
							readTree(formatID, getFileChooser().getSelectedFile());
						}
						else {  // If the format had to be guessed and none was found.
							JOptionPane.showMessageDialog(mainFrame, "The format of the file \"" + getFileChooser().getSelectedFile() + 
									"\" is not supported.", "Unsupported format", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				catch (Exception ex) {  // If an error occurred while trying to load a tree.
					ex.printStackTrace();
					JOptionPane.showMessageDialog(mainFrame, "The error \"" + ex.getLocalizedMessage() + "\" occurred.", 
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as...");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setFileFilters(writingFilters);
				if (getFileChooser().showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
					writeTree(((JPhyloIOFormatSpecificObject)getFileChooser().getFileFilter()).getFormatID(), getFileChooser().getSelectedFile());
				}
			}
		});
		mnFile.add(mntmSaveAs);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.setVisible(false);
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(getApplicationURL()));
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		mnHelp.add(mntmAbout);
		
		return menuBar;
	}
	
	
	private JTree getTree() {
		return tree;
	}
	
	
	protected DefaultTreeModel getTreeModel() {
		return (DefaultTreeModel)getTree().getModel();
	}
	
	
	/**
	 * Returns a file chooser with file filters for all tree formats supported by <i>JPhyloIO</i> and a filter filter
	 * accepting valid extensions of all tree formats.
	 * <p>
	 * The goal of this dialog is on the one hand to filter all supported tree files using the "All supported formats" 
	 * filter. {@link JPhyloIOReaderWriterFactory#guessReader(File, ReadWriteParameterMap)} will be used to determine
	 * an appropriate reader for files selected this way later. On the other hand, single filters for all supported
	 * formats are offered, so the user can manually define the format of a file. In that case the format will not be
	 * guessed but directly determined using {@link JPhyloIOContentExtensionFileFilter#getFormatID()}.
	 * 
	 * @return the file chooser instance for opening files in this application
	 */
	private JFileChooser getFileChooser() {
		if (fileChooser == null) {  // if fileChooser was not initialized yet
			// Create file chooser:
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);  // Do not allow to select more than one file.
			fileChooser.setAcceptAllFileFilterUsed(false);  // Do not include predefined "All files (*.*)" filter, since we will create a special filter later.
			
			// Add file filters for supported formats and collect extensions for "All supported formats" filter:
			ListOrderedSet<String> validExtensions = new ListOrderedSet<String>();  // This set is used to collect valid extensions of all formats to create the "All supported formats" filter later.
			for (String formatID : factory.getFormatIDsSet()) {
				JPhyloIOFormatInfo info = factory.getFormatInfo(formatID);
				ContentExtensionFileFilter filter = info.createFileFilter(TestStrategy.CONTENT);  // Create a filter filter instance for the current format.
				if (info.isElementModeled(EventContentType.TREE, true)) {  // Check if the current format can contain trees and can be read.
					validExtensions.addAll(filter.getExtensions());  // Add the file extensions of this filter to the set of all supported extensions.
					readingFilters.add(filter);
				}
				if (info.isElementModeled(EventContentType.TREE, false)) {  // The same for writing. (Not all formats in JPhyloIO can be read and written.)
					writingFilters.add(filter);
				}
			}
			
			// Add "All supported formats" filter:
			readingFilters.add(0, new ExtensionFileFilter("All supported formats", false, validExtensions.asList()));
					// Create a file filter accepting extensions of all supported formats at the same time. 
		}
		return fileChooser;
	}
}
