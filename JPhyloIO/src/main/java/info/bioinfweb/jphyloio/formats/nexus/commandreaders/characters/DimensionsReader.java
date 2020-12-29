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
package info.bioinfweb.jphyloio.formats.nexus.commandreaders.characters;


import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.QName;

import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractKeyValueCommandReader;
import info.bioinfweb.jphyloio.formats.text.KeyValueInformation;



public class DimensionsReader extends AbstractKeyValueCommandReader implements NexusConstants, ReadWriteConstants {
	public static final String INFO_KEY_NTAX = "info.bioinfweb.jphyloio.nexus.ntax";
	public static final String INFO_KEY_CHAR = "info.bioinfweb.jphyloio.nexus.ntax";

	
	public DimensionsReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_DIMENSIONS, new String[]{BLOCK_NAME_CHARACTERS, BLOCK_NAME_UNALIGNED, BLOCK_NAME_DATA}, nexusDocument);
		//TODO In the UNALIGNED block NCHAR is invalid.
	}


	@Override
	protected boolean processSubcommand(KeyValueInformation info) throws IOException {
		long longValue = Long.MIN_VALUE;
		try {
			longValue = Long.parseLong(info.getValue());
		}
		catch (NumberFormatException e) {}  // Nothing to do.
		
		Collection<JPhyloIOEvent> events = getStreamDataProvider().getCurrentEventCollection();
		String key = info.getOriginalKey().toUpperCase();
		QName genericPredicate = new QName(NEXUS_PREDICATE_NAMESPACE, COMMAND_NAME_DIMENSIONS + PREDICATE_PART_SEPERATOR + key);  //TODO Should the predicate really be in upper case?
		if (longValue > 0) {
			QName predicate = genericPredicate;
			if (DIMENSIONS_SUBCOMMAND_NTAX.equals(key)) {
				getStreamDataProvider().getSharedInformationMap().put(INFO_KEY_NTAX, longValue);
				predicate = PREDICATE_SEQUENCE_COUNT;
			}
			else if (DIMENSIONS_SUBCOMMAND_NCHAR.equals(key)) {
				getStreamDataProvider().getSharedInformationMap().put(INFO_KEY_CHAR, longValue);
				predicate = PREDICATE_CHARACTER_COUNT;  // This predicate may need to be changed, if a DIMENSION command outside of alignment blocks is read here.
				
				getStreamDataProvider().getMatrixWidthsMap().put(  // Store matrix width for later use by set readers.
						(String)getStreamDataProvider().getSharedInformationMap().get(NexusReaderStreamDataProvider.INFO_KEY_CURRENT_BLOCK_ID), 
						longValue);
			}
			
			events.add(new LiteralMetadataEvent(DEFAULT_META_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID(), 
					info.getOriginalKey(), new URIOrStringIdentifier(info.getOriginalKey(), predicate), LiteralContentSequenceType.SIMPLE));
			events.add(new LiteralMetadataContentEvent(longValue, info.getValue()));
		}
		else if (DIMENSIONS_SUBCOMMAND_NTAX.equals(key) || DIMENSIONS_SUBCOMMAND_NCHAR.equals(key)) {
			throw new JPhyloIOReaderException("\"" + info.getValue() + "\" is not a valid positive integer. Only positive integer "
					+ "values are valid for NTAX or NCHAR in the Nexus DIMENSIONS command.", getStreamDataProvider().getDataReader());  //TODO Is the position of the reader too far behind?
		}
		else {  // Possible unknown subcommand with a non-long value
			events.add(new LiteralMetadataEvent(DEFAULT_META_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID(), 
					info.getOriginalKey(), new URIOrStringIdentifier(info.getOriginalKey(), genericPredicate), LiteralContentSequenceType.SIMPLE));
			events.add(new LiteralMetadataContentEvent(info.getValue(), false));
		}
		events.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.LITERAL_META));
		
		return true;  // An event is add to the queue in every case.
	}


	@Override
	protected boolean addStoredEvents() {
		return false;  // This reader does not store any events.
	}
}
