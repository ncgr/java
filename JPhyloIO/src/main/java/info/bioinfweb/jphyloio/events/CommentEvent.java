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


import info.bioinfweb.jphyloio.events.type.EventContentType;



/**
 * Event that indicates that a comment has been parsed at the current position or within the data the last
 * non-comment event has been parsed from.
 * <p>
 * Nested comments as they are possible e.g. in the MEGA format do not produce separate events.   
 * 
 * @author Ben St&ouml;ver
 */
public class CommentEvent extends ContinuedEvent {
	private String content;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param content the content of the represented comment
	 * @param continuedInNextEvent Specify {@code true} here if this event does not contain the final part of 
	 *        this comment and more events are ahead or {@code false otherwise}.
	 */
	public CommentEvent(String content, boolean continuedInNextEvent) {
		super(EventContentType.COMMENT, continuedInNextEvent);
		this.content = content;
	}


	/**
	 * Creates a new instance of this class that is not continued in the next event.
	 * 
	 * @param content the content of the represented comment
	 */
	public CommentEvent(String content) {
		this(content, false);
	}
	
	
	/**
	 * Returns the content of the comment.
	 * 
	 * @return the content of the comment
	 */
	public String getContent() {
		return content;
	}
}
