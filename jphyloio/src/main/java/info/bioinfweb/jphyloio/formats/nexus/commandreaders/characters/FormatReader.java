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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import info.bioinfweb.commons.bio.CharacterStateSetType;
import info.bioinfweb.commons.bio.CharacterSymbolMeaning;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.ConcreteJPhyloIOEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.SingleTokenDefinitionEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralContentSequenceType;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataContentEvent;
import info.bioinfweb.jphyloio.events.meta.LiteralMetadataEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.exception.JPhyloIOReaderException;
import info.bioinfweb.jphyloio.formats.BufferedEventInfo;
import info.bioinfweb.jphyloio.formats.nexus.NexusConstants;
import info.bioinfweb.jphyloio.formats.nexus.NexusReaderStreamDataProvider;
import info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractKeyValueCommandReader;
import info.bioinfweb.jphyloio.formats.text.KeyValueInformation;



/**
 * Parser for the {@code Format} command in a {@code Characters}, {@code Unaligned} or {@code Data} block.
 * 
 * @author Ben St&ouml;ver
 */
public class FormatReader extends AbstractKeyValueCommandReader implements NexusConstants, ReadWriteConstants {
	public static final String INFO_KEY_TOKENS_FORMAT = "info.bioinfweb.jphyloio.nexus.tokens";
	public static final String INFO_KEY_INTERLEAVE = "info.bioinfweb.jphyloio.nexus.interleave";
	public static final String INFO_KEY_LABELS = "info.bioinfweb.jphyloio.nexus.labels";
	public static final String INFO_KEY_TRANSPOSE = "info.bioinfweb.jphyloio.nexus.transpose";
	
	public static final Pattern MIXED_DATA_TYPE_VALUE_PATTERN = Pattern.compile(FORMAT_VALUE_MIXED_DATA_TYPE + "\\((.+)\\)", 
			Pattern.DOTALL + Pattern.CASE_INSENSITIVE);
	public static final Pattern MIXED_DATA_TYPE_SINGLE_SET_PATTERN = Pattern.compile("(.+)\\:([0-9]+)\\-([0-9]+)");
	
	
	private boolean continuousData = false;
	private List<BufferedEventInfo<TokenSetDefinitionEvent>> tokenSetInfos = new ArrayList<BufferedEventInfo<TokenSetDefinitionEvent>>();  // The list of nested events is primarily meant for {@link CharacterSetIntervalEvent}s, since {@link SingleTokenDefinitionEvent}s are the same for all sets and are stored in a separate list.
	private List<SingleTokenDefinitionEvent> singleTokenDefinitionEvents = new ArrayList<SingleTokenDefinitionEvent>();
	
	
	public FormatReader(NexusReaderStreamDataProvider nexusDocument) {
		super(COMMAND_NAME_FORMAT, new String[]{BLOCK_NAME_CHARACTERS, BLOCK_NAME_UNALIGNED, BLOCK_NAME_DATA}, nexusDocument);
	}
	
	
	private CharacterStateSetType getTokenSetType(String parsedName) {
		if (parsedName.equals(FORMAT_VALUE_STANDARD_DATA_TYPE)) {
			return CharacterStateSetType.DISCRETE;
		}
		else if (parsedName.equals(FORMAT_VALUE_NUCLEOTIDE_DATA_TYPE)) {
			return CharacterStateSetType.NUCLEOTIDE;
		}
		else if (parsedName.equals(FORMAT_VALUE_DNA_DATA_TYPE)) {
			return CharacterStateSetType.DNA;
		}
		else if (parsedName.equals(FORMAT_VALUE_RNA_DATA_TYPE)) {
			return CharacterStateSetType.RNA;
		}
		else if (parsedName.equals(FORMAT_VALUE_PROTEIN_DATA_TYPE)) {
			return CharacterStateSetType.AMINO_ACID;
		}
		else if (parsedName.equals(FORMAT_VALUE_CONTINUOUS_DATA_TYPE)) {
			continuousData = true;
			return CharacterStateSetType.CONTINUOUS;
		}
		else {
			return CharacterStateSetType.UNKNOWN;
		}
	}
	
	
	private boolean parseMixedDataType(String content) {
		String[] parts = content.split("\\,");
		List<BufferedEventInfo<TokenSetDefinitionEvent>> tokenSetInfoBuffer = new ArrayList<BufferedEventInfo<TokenSetDefinitionEvent>>(2 * parts.length);
		for (int i = 0; i < parts.length; i++) {
			Matcher matcher = MIXED_DATA_TYPE_SINGLE_SET_PATTERN.matcher(parts[i]);
			if (matcher.matches()) {
				BufferedEventInfo<TokenSetDefinitionEvent> info = new BufferedEventInfo<TokenSetDefinitionEvent>(new TokenSetDefinitionEvent(
						getTokenSetType(matcher.group(1).toUpperCase()), DEFAULT_TOKEN_SET_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID(),	matcher.group(1)));
				try {
					info.getNestedEvents().add(new CharacterSetIntervalEvent(Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)) + 1));
				}
				catch (NumberFormatException e) {
					return false;  // Abort parsing and treat the whole string as a regular data type name.  //TODO Give warning or throw exception?
				}
				tokenSetInfoBuffer.add(info);
			}
			else {
				return false;  // Abort parsing and treat the whole string as a regular data type name.  //TODO Give warning or throw exception?
			}
		}
		tokenSetInfos.addAll(tokenSetInfoBuffer);  // Save to nest single token symbol definitions later.
		return true;
	}
	
	
	private void addSingleTokenDefinitionEvent(String tokenName, CharacterSymbolMeaning meaning) {
		singleTokenDefinitionEvents.add(new SingleTokenDefinitionEvent(DEFAULT_TOKEN_DEFINITION_ID_PREFIX + 
				getStreamDataProvider().getIDManager().createNewID(), null, tokenName, meaning, null));
	}
	
	
	@Override
	protected boolean processSubcommand(KeyValueInformation info) throws IOException {
		boolean eventCreated = false;
		boolean eventAddToQueue = false;
		
		String key = info.getOriginalKey().toUpperCase();
		String upperCaseValue = info.getValue().toUpperCase();
		if (FORMAT_SUBCOMMAND_TOKENS.equals(key) || 
				(FORMAT_SUBCOMMAND_DATA_TYPE.equals(key) && FORMAT_VALUE_CONTINUOUS_DATA_TYPE.equals(upperCaseValue))) {
			
			getStreamDataProvider().getSharedInformationMap().put(INFO_KEY_TOKENS_FORMAT, true);
		}
		
		if (FORMAT_SUBCOMMAND_DATA_TYPE.equals(key)) {
			Matcher matcher = MIXED_DATA_TYPE_VALUE_PATTERN.matcher(info.getValue());  // Does case insensitive matching.
			if (matcher.matches()) {  // Parse MrBayes extension
				if (parseMixedDataType(matcher.group(1))) {  // Otherwise the previously constructed meta event for datatype will be returned.
					eventCreated = true;
					eventAddToQueue = true;
				}
			}
			else {
				if (tokenSetInfos.isEmpty()) {  // Only MrBayes extension allows to specify more than one token set.
					tokenSetInfos.add(new BufferedEventInfo<TokenSetDefinitionEvent>(new TokenSetDefinitionEvent(getTokenSetType(upperCaseValue), 
							DEFAULT_TOKEN_SET_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID(),
							info.getValue())));  // Since this token set shall be valid for the whole alignment, no interval events need to be created here.
					eventCreated = true;
				}
				else {
					throw new JPhyloIOReaderException("Duplicate token set definition in Nexus FORMAT command.", 
							getStreamDataProvider().getDataReader());
				}
			}
		}
		else if (FORMAT_SUBCOMMAND_INTERLEAVE.equals(key)) {
			getStreamDataProvider().getSharedInformationMap().put(INFO_KEY_INTERLEAVE, true);
		}
		else if (FORMAT_SUBCOMMAND_NO_LABELS.equals(key)) {
			getStreamDataProvider().getSharedInformationMap().put(INFO_KEY_LABELS, false);
		}
		else if (FORMAT_SUBCOMMAND_TRANSPOSE.equals(key)) {
			getStreamDataProvider().getSharedInformationMap().put(INFO_KEY_TRANSPOSE, true);
		}
		else if (FORMAT_SUBCOMMAND_MATCH_CHAR.equals(key)) {
			getStreamDataProvider().getSequenceTokensEventManager().setMatchToken(info.getValue());
			addSingleTokenDefinitionEvent(info.getValue(), CharacterSymbolMeaning.MATCH);
			eventCreated = true;
		}
		else if (FORMAT_SUBCOMMAND_GAP_CHAR.equals(key)) {
			addSingleTokenDefinitionEvent(info.getValue(), CharacterSymbolMeaning.GAP);
			eventCreated = true;
		}
		else if (FORMAT_SUBCOMMAND_MISSING_CHAR.equals(key)) {
			addSingleTokenDefinitionEvent(info.getValue(), CharacterSymbolMeaning.MISSING);
			eventCreated = true;
		}
		else if (FORMAT_SUBCOMMAND_SYMBOLS.equals(key)) {
			if (continuousData) {
				throw new JPhyloIOReaderException("The subcommand " + FORMAT_SUBCOMMAND_SYMBOLS + " of " + getCommandName() + 
						" is not allowed if " +	FORMAT_SUBCOMMAND_DATA_TYPE + "=" + FORMAT_VALUE_CONTINUOUS_DATA_TYPE + " was specified.",
						getStreamDataProvider().getDataReader());
			}
			else {
				for (int i = 0; i < info.getValue().length(); i++) {
					char c = info.getValue().charAt(i);
					if (!Character.isWhitespace(c)) {
						addSingleTokenDefinitionEvent(Character.toString(c), CharacterSymbolMeaning.CHARACTER_STATE);
						eventCreated = true;  // Otherwise the previously constructed meta event will be returned, indicating an empty symbols subcommand.
					}
				} 
			}
		}
		
		if (!eventCreated) {
			Collection<JPhyloIOEvent> events = getStreamDataProvider().getCurrentEventCollection();
			events.add(new LiteralMetadataEvent(DEFAULT_META_ID_PREFIX + getStreamDataProvider().getIDManager().createNewID(), 
					info.getOriginalKey(), new URIOrStringIdentifier(info.getOriginalKey(), 
					new QName(NEXUS_PREDICATE_NAMESPACE, COMMAND_NAME_FORMAT + PREDICATE_PART_SEPERATOR + key)),  //TODO Should the predicate really be in upper case? 
					LiteralContentSequenceType.SIMPLE));
			events.add(new LiteralMetadataContentEvent(info.getValue(), false));
			events.add(ConcreteJPhyloIOEvent.createEndEvent(EventContentType.LITERAL_META));
			eventAddToQueue = true;
		}
		return eventAddToQueue;
	}
	
	
	private void removeWaitingCharacterStateEvents() {
		Iterator<SingleTokenDefinitionEvent> iterator = singleTokenDefinitionEvents.iterator();
		while (iterator.hasNext()) {
			if (CharacterSymbolMeaning.CHARACTER_STATE.equals(iterator.next().getMeaning())) {
				iterator.remove();
			}
		}
	}


	/**
	 * Adds the stored token set event with nested single token definition events to the queue, if present.
	 * <p>
	 * In the case of MrBayes {@code MIXED} data type more than one token set definition event may be added. The 
	 * stored single token definition events are nested into each of these token set definition event, except for 
	 * events with the meaning {@link CharacterSymbolMeaning#CHARACTER_STATE}, which are removed in this case, 
	 * because they may not fit to all defined token sets.
	 * <p>
	 * If only single token definition events could be created from the format command and no token set, no
	 * events will be added.
	 * 
	 * @see info.bioinfweb.jphyloio.formats.nexus.commandreaders.AbstractKeyValueCommandReader#addStoredEvents()
	 */
	@Override
	protected boolean addStoredEvents() {
		boolean result = !tokenSetInfos.isEmpty();
		if (result) {
			Collection<JPhyloIOEvent> queue = getStreamDataProvider().getCurrentEventCollection();
			if (tokenSetInfos.size() > 1) {  // DATATYPE = MIXED
				removeWaitingCharacterStateEvents();  // Possibly such events would not fit to all token sets.
			}
			
			for (BufferedEventInfo<TokenSetDefinitionEvent> info : tokenSetInfos) {
				queue.add(info.getStartEvent());
				for (SingleTokenDefinitionEvent singleTokenDefinitionEvent : singleTokenDefinitionEvents) {
					queue.add(singleTokenDefinitionEvent);
					queue.add(new ConcreteJPhyloIOEvent(EventContentType.SINGLE_TOKEN_DEFINITION, EventTopologyType.END));
				}
				queue.addAll(info.getNestedEvents());
				queue.add(new ConcreteJPhyloIOEvent(EventContentType.TOKEN_SET_DEFINITION, EventTopologyType.END));
			}
	
			tokenSetInfos.clear();
			singleTokenDefinitionEvents.clear();
		}
		return result;
	}
}
