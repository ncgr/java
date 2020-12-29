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
package info.bioinfweb.jphyloio.dataadapters.implementations;


import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.CharacterDefinitionEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.TokenSetDefinitionEvent;



/**
 * Abstract implementation of {@link MatrixDataAdapter}, which returns empty object list adapters
 * for {@link #getCharacterDefinitions(ReadWriteParameterMap)}, {@link #getTokenSets(ReadWriteParameterMap)},
 * {@link #getSequenceSets(ReadWriteParameterMap)}, {@link #getCharacterSets(ReadWriteParameterMap)}.
 * Additionally an empty implementation of
 * {@link #writeMetadata(ReadWriteParameterMap, info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver)} is inherited.
 * <p>
 * Application adapters that do not provide any token, sequence or character sets can be inherited from this class.
 *
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class NoCharDefsNoSetsMatrixDataAdapter extends EmptyAnnotatedDataAdapter<LinkedLabeledIDEvent> implements MatrixDataAdapter {
	/**
	 * Default implementation that always returns an empty object list adapter.
	 *
	 * @return a shared instance of {@link EmptyObjectListDataAdapter}
	 * @see info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter#getCharacterDefinitions(ReadWriteParameterMap)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ObjectListDataAdapter<CharacterDefinitionEvent> getCharacterDefinitions(ReadWriteParameterMap parameters) {
		return EmptyObjectListDataAdapter.SHARED_EMPTY_OBJECT_LIST_ADAPTER;
	}


	/**
	 * Default implementation that always returns an empty object list adapter.
	 *
	 * @return a shared instance of {@link EmptyObjectListDataAdapter}
	 * @see info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter#getTokenSets(ReadWriteParameterMap)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ObjectListDataAdapter<LinkedLabeledIDEvent> getSequenceSets(ReadWriteParameterMap parameters) {
		return EmptyObjectListDataAdapter.SHARED_EMPTY_OBJECT_LIST_ADAPTER;
	}


	/**
	 * Default implementation that always returns an empty object list adapter.
	 *
	 * @return a shared instance of {@link EmptyObjectListDataAdapter}
	 * @see info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter#getTokenSets(ReadWriteParameterMap)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ObjectListDataAdapter<TokenSetDefinitionEvent> getTokenSets(ReadWriteParameterMap parameters) {
		return EmptyObjectListDataAdapter.SHARED_EMPTY_OBJECT_LIST_ADAPTER;
	}


	/**
	 * Default implementation that always returns an empty object list adapter
	 *
	 * @return a shared instance of {@link EmptyObjectListDataAdapter}
	 * @see info.bioinfweb.jphyloio.dataadapters.MatrixDataAdapter#getCharacterSets(ReadWriteParameterMap)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ObjectListDataAdapter<LinkedLabeledIDEvent> getCharacterSets(ReadWriteParameterMap parameters) {
		return EmptyObjectListDataAdapter.SHARED_EMPTY_OBJECT_LIST_ADAPTER;
	}
}
