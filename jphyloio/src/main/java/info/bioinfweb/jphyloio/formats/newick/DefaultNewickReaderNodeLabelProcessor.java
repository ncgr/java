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
package info.bioinfweb.jphyloio.formats.newick;



/**
 * Default implementation of {@link NewickReaderNodeLabelProcessor} that returns the original name unchanged.
 * 
 * @author Ben St&ouml;ver
 */
public class DefaultNewickReaderNodeLabelProcessor implements NewickReaderNodeLabelProcessor {
	@Override
	public String processLabel(String originalLabel, boolean isInternal) {
		return originalLabel;
	}

	
	@Override
	public String getLinkedOTUID(String processedLabel) {
		return null;
	}
}
