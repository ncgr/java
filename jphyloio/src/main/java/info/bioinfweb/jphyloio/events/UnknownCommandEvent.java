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


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.NexusCommandEventReader;



/**
 * Event to be fired, if an unknown command was found (e.g. a <i>Nexus</i> command without an according
 * {@link NexusCommandEventReader} implementation). Events of this type are only fired, if the parameter 
 * {@link ReadWriteParameterMap#KEY_CREATE_UNKNOWN_COMMAND_EVENTS} is set to {@code true}. (Refer to the
 * individual reader documentations for details.) 
 * 
 * @author Ben St&ouml;ver
 * @see ReadWriteParameterMap#KEY_CREATE_UNKNOWN_COMMAND_EVENTS
 */
public class UnknownCommandEvent extends ConcreteJPhyloIOEvent {
	private String key;
	private String value;
	//TODO Refactor properties.
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param key the name or key of the unknown command 
	 * @param value the value of the unknown command
	 */
	public UnknownCommandEvent(String key, String value) {
		super(EventContentType.UNKNOWN_COMMAND, EventTopologyType.SOLE);
		
		this.key = key;
		if (value == null) {
			throw new NullPointerException("The parameter \"value\" cannot be null.");
		}
		else {
			this.value = value;
		}
	}


	/**
	 * Returns the name or key of the unknown command, if provided in the document. 
	 * 
	 * @return the name or {@code null} if no key or name is provided in the document
	 */
	public String getKey() {
		return key;
	}


	/**
	 * Returns the value of the unknown command.
	 * 
	 * @return the string representation of the unknown command (Will never be {@code null}.)
	 */
	public String getValue() {
		return value;
	}
}
