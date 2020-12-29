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


import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.ResourceMetadataEvent;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.events.type.EventType;



/**
 * Implements basic functionality for {@link JPhyloIOEvent}s.
 * 
 * @author Ben St&ouml;ver
 */
public class ConcreteJPhyloIOEvent implements JPhyloIOEvent, Cloneable {
	private EventType type;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param contentType the content type of the event
	 * @param topologyType the topology type of the event
	 * @throws NullPointerException if {@code contentType} or {@code topologyType} are {@code null}
	 */
	public ConcreteJPhyloIOEvent(EventContentType contentType, EventTopologyType topologyType) {
		super();
		this.type = new EventType(contentType, topologyType);
	}
	
	
	/**
	 * Creates a new instance of this class with the topology type {@link EventTopologyType#END}-
	 * 
	 * @param contentType the content type of the event
	 * @throws NullPointerException if {@code contentType} is {@code null}
	 * @return the new event instance
	 * @see PartEndEvent
	 */
	public static ConcreteJPhyloIOEvent createEndEvent(EventContentType contentType) {
		return new ConcreteJPhyloIOEvent(contentType, EventTopologyType.END);
	}


	@Override
	public EventType getType() {
		return type;
	}


	@Override
	public ConcreteJPhyloIOEvent clone() {
		try {
			return (ConcreteJPhyloIOEvent)super.clone();
		} 
		catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}


	@Override
	public ResourceMetadataEvent asResourceMetadataEvent() throws ClassCastException {
		return (ResourceMetadataEvent)this;
	}


	@Override
	public LiteralMetadataEvent asLiteralMetadataEvent() throws ClassCastException {
		return (LiteralMetadataEvent)this;
	}


	@Override
	public LiteralMetadataContentEvent asLiteralMetadataContentEvent() throws ClassCastException {
		return (LiteralMetadataContentEvent)this;
	}


	@Override
	public UnknownCommandEvent asUnknownCommandEvent() throws ClassCastException {
		return (UnknownCommandEvent)this;
	}


	@Override
	public CommentEvent asCommentEvent() throws ClassCastException {
		return (CommentEvent)this;
	}


	@Override
	public SetElementEvent asSetElementEvent() throws ClassCastException {
		return (SetElementEvent)this;
	}


	@Override
	public PartEndEvent asPartEndEvent() throws ClassCastException {
		return (PartEndEvent)this;
	}


	@Override
	public LabeledIDEvent asLabeledIDEvent() throws ClassCastException {
		return (LabeledIDEvent)this;
	}


	@Override
	public LinkedLabeledIDEvent asLinkedLabeledIDEvent() throws ClassCastException {
		return (LinkedLabeledIDEvent)this;
	}


	@Override
	public SingleSequenceTokenEvent asSingleSequenceTokenEvent() throws ClassCastException {
		return (SingleSequenceTokenEvent)this;
	}


	@Override
	public SequenceTokensEvent asSequenceTokensEvent() throws ClassCastException {
		return (SequenceTokensEvent)this;
	}


	@Override
	public CharacterSetIntervalEvent asCharacterSetIntervalEvent() throws ClassCastException {
		return (CharacterSetIntervalEvent)this;
	}


	@Override
	public CharacterDefinitionEvent asCharacterDefinitionEvent() throws ClassCastException {
		return (CharacterDefinitionEvent)this;
	}


	@Override
	public TokenSetDefinitionEvent asTokenSetDefinitionEvent() throws ClassCastException {
		return (TokenSetDefinitionEvent)this;
	}


	@Override
	public SingleTokenDefinitionEvent asSingleTokenDefinitionEvent() throws ClassCastException {
		return (SingleTokenDefinitionEvent)this;
	}


	@Override
	public EdgeEvent asEdgeEvent() throws ClassCastException {
		return (EdgeEvent)this;
	}


	@Override
	public NodeEvent asNodeEvent() throws ClassCastException {
		return (NodeEvent)this;
	}


	@Override
	public LinkedIDEvent asLinkedIDEvent() throws ClassCastException {
		return (LinkedIDEvent)this;
	}
}
