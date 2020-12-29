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

import java.util.Stack;

import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;



public class ParentEventInformation {
	private Stack<JPhyloIOEvent> parentEvents = new Stack<JPhyloIOEvent>();
	
	
	protected void add(JPhyloIOEvent event) {
		parentEvents.add(event);
	}
	
	
	protected void pop() {
		parentEvents.pop();
	}
	
	
	public int size() {
		return parentEvents.size();
	}
	
	
	public boolean isEmpty() {
		return parentEvents.isEmpty();
	}
	
	
	public JPhyloIOEvent getDirectParent() {
		if (parentEvents.isEmpty()) {
			return null;
		}
		else {
			return parentEvents.peek();
		}
	}
	
	
	public EventContentType getDirectParentContentType() {
		JPhyloIOEvent event = getDirectParent();
		if (event == null) {
			return null;
		}
		else {
			return event.getType().getContentType();
		}
	}
	
	
	public JPhyloIOEvent getParentFromBottom(int index) {
		return parentEvents.get(parentEvents.size() - index - 1);
	}
	
	
	public JPhyloIOEvent getParentFromTop(int index) {
		return parentEvents.get(index);
	}
	
	
	/**
	 * Checks whether the current parent events have the specified content type(s).
	 * 
	 * @param types the expected content types of the parent events (The first parameter must match the direct parent, 
	 *        the second the grandparent and so on.) 
	 * @return {@code true} the lowest elements in the sequence of parent events have the specified content types in the
	 *         specified order or {@code false} otherwise 
	 * @throws NullPointerException if at least one specified content type is {@code null} 
	 */
	public boolean isParentSequence(EventContentType... types) {
		boolean result = size() >= types.length;
		if (result) {
			for (int i = 0; i < types.length; i++) {
				if (!types[i].equals(getParentFromBottom(i).getType().getContentType())) {
					return false;
				}
			}
		}
		return result;
	}
}
