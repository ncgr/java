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
import info.bioinfweb.jphyloio.events.type.EventTopologyType;



/**
 * Event that indicates that new information on a character set has been read.
 * <p>
 * This event defines an interval belonging to the current character set. All events of this
 * type must be enclosed between a start and an end event with content type
 * {@link EventContentType#CHARACTER_SET}. The start event will define the ID and the name
 * of the set.
 * <p>
 * Note that in contrast to e.g. many alignment editors or the character set definitions in Nexus, column indices in
 * <i>JPhyloIO</i> start with 0 and not with 1. The indices used by instances of this class must follow this convention
 * and readers and writers will convert indices according to the conventions of their format.
 *
 * @author Ben St&ouml;ver
 */
public class CharacterSetIntervalEvent extends ConcreteJPhyloIOEvent {
	private long start;
	private long end;


	/**
	 * Creates a new instance of this class.
	 * <p>
	 * A segment ranges from {@link #getStart()} to {@link #getEnd()} {@code - 1}.
	 *
	 * @param start the index of the first position of the sequence segment to be add to the specified character set
	 * @param end the first index after the end of the sequence segment to be add to the specified character set
	 * @throws IndexOutOfBoundsException if {@code start} or {@code end} are below 0
	 * @throws IllegalArgumentException if {@code end} is not greater than {@code start} (If a character set shall have
	 *         no length, just omit nested interval event.)
	 */
	public CharacterSetIntervalEvent(long start, long end) {
		super(EventContentType.CHARACTER_SET_INTERVAL, EventTopologyType.SOLE);
		if (start < 0) {
			throw new IndexOutOfBoundsException("\"start\" (" + start + ") of an interval must not be below 0.");
		}
		else if (end < 0) {
			throw new IndexOutOfBoundsException("\"end\" (" + end + ") must not be below 0.");
		}
		else if (end <= start) {
			throw new IllegalArgumentException("\"end\" (" + end + ") must be greater than \"start\" (" + start +
			        "). (If a character set shall have no length, just omit nested interval events.)");
		}
		else {
			this.start = start;
			this.end = end;
		}
	}


	/**
	 * The first position of the new segment to be add to the character set with the specified name.
	 * <p>
	 * A segment ranges from {@link #getStart()} to {@code getEnd() - 1}.
	 *
	 * @return the first position to be included in the character set
	 */
	public long getStart() {
		return start;
	}


	/**
	 * The first index after the new segment to be add to the character set with the specified name.
	 * <p>
	 * A segment ranges from {@link #getStart()} to {@code getEnd() - 1}.
	 *
	 * @return the first index after the segment to be add
	 */
	public long getEnd() {
		return end;
	}
}
