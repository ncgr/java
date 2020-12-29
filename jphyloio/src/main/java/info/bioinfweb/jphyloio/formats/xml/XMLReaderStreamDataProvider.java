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
package info.bioinfweb.jphyloio.formats.xml;


import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.NodeEdgeInfo;
import info.bioinfweb.jphyloio.formats.xml.elementreaders.XMLElementReader;

import java.util.Queue;
import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartDocument;



/**
 * Stores data that shall be shared among different implementations of {@link XMLElementReader}
 * reading from the same document.
 * 
 * @author Sarah Wiechers
 */
public class XMLReaderStreamDataProvider<R extends AbstractXMLEventReader<? extends XMLReaderStreamDataProvider<R>>> extends ReaderStreamDataProvider<R> {	
	private String parentName;
	private String elementName;
	
	private StartDocument startDocumentEvent;
	
	private Stack<String> nestedMetaNames = new Stack<String>();
	private boolean customXMLStartWritten = false;
	
	private CharacterStateSetType characterSetType;
	
	private String incompleteToken = null;
	
	private boolean isRootedPhylogeny;
	
	private Stack<NodeEdgeInfo> sourceNode = new Stack<NodeEdgeInfo>();
	private Stack<Queue<NodeEdgeInfo>> edgeInfos = new Stack<Queue<NodeEdgeInfo>>();
	
	private boolean createNodeStart;
	
	
	public XMLReaderStreamDataProvider(R eventReader) {
		super(eventReader);
	}
	
	
	public XMLEventReader getXMLReader() {
		return getEventReader().getXMLReader();
	}


	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	public String getElementName() {
		return elementName;
	}


	public void setElementName(String elementName) {
		this.elementName = elementName;
	}


	public StartDocument getStartDocumentEvent() {
		return startDocumentEvent;
	}


	public void setStartDocumentEvent(StartDocument startDocumentEvent) {
		this.startDocumentEvent = startDocumentEvent;
	}


	public Stack<String> getNestedMetaNames() {
		return nestedMetaNames;
	}
	
	
	public boolean isCustomXMLStartWritten() {
		return customXMLStartWritten;
	}


	public void setCustomXMLStartWritten(boolean isFirstCustomXMLElement) {
		this.customXMLStartWritten = isFirstCustomXMLElement;
	}


	public boolean hasIncompleteToken() {
		return incompleteToken != null;
	}


	public String getIncompleteToken() {
		return incompleteToken;
	}


	public void setIncompleteToken(String incompleteToken) {
		this.incompleteToken = incompleteToken;
	}


	public boolean isRootedPhylogeny() {
		return isRootedPhylogeny;
	}


	public void setRootedPhylogeny(boolean isRootedPhylogeny) {
		this.isRootedPhylogeny = isRootedPhylogeny;
	}

	
	public CharacterStateSetType getCharacterSetType() {
		return characterSetType;
	}


	public void setCharacterSetType(CharacterStateSetType characterSetType) {
		this.characterSetType = characterSetType;
	}
	

	public Stack<NodeEdgeInfo> getSourceNode() {
		return sourceNode;
	}


	public Stack<Queue<NodeEdgeInfo>> getEdgeInfos() {
		return edgeInfos;
	}


	public boolean isCreateNodeStart() {
		return createNodeStart;
	}


	public void setCreateNodeStart(boolean createNodeStart) {
		this.createNodeStart = createNodeStart;
	}
}