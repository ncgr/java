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
package info.bioinfweb.jphyloio.events;


import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Event indicating an edge in a tree or network. Edges are directed and connect a source node with a target node, 
 * which are references by their IDs. If an edge represents a root of a tree or network, its source node ID 
 * reference is {@code null}. All referenced nodes must have been defined in the event stream before this event
 * is fired.
 * <p>
 * This event is a start event, which is followed by an end event of the same content type. Comment
 * and metainformation events maybe nested between this and its according end event. (See the description
 * of {@link JPhyloIOEventReader} for the complete grammar definition of JPhyloIO event streams.)
 * 
 * @author Ben St&ouml;ver
 */
public class EdgeEvent extends LabeledIDEvent {
	private String sourceID;
	private String targetID;
	private double length;
	
	
	/**
	 * Creates a new instance of this class that represents an internal edge or a root edge. If {@code sourceID}
	 * is {@code null} the content type will be {@link EventContentType#ROOT_EDGE} and otherwise it will be
	 * {@link EventContentType#EDGE}.
	 * 
	 * @param id the unique ID associated with the represented edge (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label an optional label associated with this edge (Maybe {@code null}.)
	 * @param sourceID the ID of the source node of this edge (Maybe {@code null} if a root edge shall be created.)
	 * @param targetID the ID of the target node of this edge
	 * @param length an optional lengths of this edge (Maybe {@link Double#NaN} if no length is given.)
	 * @throws NullPointerException if {@code id}, {@code sourceID} or {@code targetID} are {@code null}
	 * @throws IllegalArgumentException if {@code id}, {@code sourceID} or {@code targetID} are not a valid 
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCNames</a>
	 */
	public EdgeEvent(String id, String label,	String sourceID, String targetID, double length) {
		super(sourceID == null ? EventContentType.ROOT_EDGE : EventContentType.EDGE, id, label);

		checkID(targetID, "target ID");
		if (sourceID != null) {
			checkID(sourceID, "source ID");
		}
		this.sourceID = sourceID;
		this.targetID = targetID;
		this.length = length;
	}


	/**
	 * Creates a new instance of this class that represents a root edge.
	 * 
	 * @param id the unique ID associated with the represented edge (Must be a valid
	 *        <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCName</a>.)
	 * @param label an optional label associated with this edge (Maybe {@code null}.)
	 * @param targetID the ID of the target node of this edge
	 * @param length an optional lengths of this edge (Maybe {@link Double#NaN} if no length is given.)
	 * @throws NullPointerException if {@code id} or {@code targetID} are {@code null}
	 * @throws IllegalArgumentException if {@code id} or {@code targetID} are not a valid 
	 *         <a href="https://www.w3.org/TR/1999/REC-xml-names-19990114/#NT-NCName">NCNames</a>
	 */
	public EdgeEvent(String id, String label,	String targetID, double length) {
		super(EventContentType.ROOT_EDGE, id, label);
		
		checkID(targetID, "target ID");
		this.targetID = targetID;
		this.length = length;
	}
	
	
	/**
	 * Returns the ID of the source node linked by this edge.
	 * 
	 * @return the ID of the source node or {@code null} if this event represents a root edge
	 * @see #hasSource()
	 */
	public String getSourceID() {
		return sourceID;
	}

	
	/**
	 * Indicates whether this edge has a source node and can therefore be considered a root edge.
	 * <p>
	 * If this method returns {@code true}, {@link #getSourceID()} will return {@code null}.
	 * 
	 * @return {@code true} if this edge has a source node or {@code false} otherwise
	 */
	public boolean hasSource() {
		return getSourceID() == null;
	}
	
	
	/**
	 * Returns the ID of the target node linked by this edge.
	 * 
	 * @return the ID of the target node (never {@code null})
	 */
	public String getTargetID() {
		return targetID;
	}


	/**
	 * Returns the length of this edge.
	 * <p>
	 * Additional information (e.g. on length confidence intervals) maybe given in upcoming nested metaevents.
	 * 
	 * @return the length of this edge or {@link Double#NaN} if this edge does not have a defined length
	 */
	public double getLength() {
		return length;
	}
	
	
	/**
	 * Indicates whether this edge has a defined length.
	 * <p>
	 * If this method returns {@code false}, {@link #getLength()} will return {@code Double#NaN}.
	 * 
	 * @return {@code true} if this edge has a defined length or {@code false} otherwise
	 */
	public boolean hasLength() {
		return !Double.isNaN(getLength());
	}


	@Override
	public String toString() {
		return getType() + " (" + getSourceID() + " -> " + getTargetID() + "):" + getLength();
	}
	
	
	public EdgeEvent cloneWithNewIDs(String newEventID, String newSourceID, String newTargetID) {
		return new EdgeEvent(newEventID, getLabel(), newSourceID, newTargetID, getLength());
	}
}
