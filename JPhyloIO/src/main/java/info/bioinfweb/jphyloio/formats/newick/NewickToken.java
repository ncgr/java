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


import info.bioinfweb.commons.io.StreamLocation;
import info.bioinfweb.commons.io.StreamLocationProvider;



public class NewickToken {
	private NewickTokenType type;
	private String text = "";
	private double length = 0;
	private boolean delimited = false;
	private StreamLocation location;
	
	
	public NewickToken(NewickTokenType type, StreamLocationProvider location) {
		this.type = type;
		this.location = new StreamLocation(location);
	}

	
	public NewickToken(StreamLocationProvider location, String text, boolean delimited) {
		this.type = NewickTokenType.NAME;
		this.location = new StreamLocation(location);;
		this.text = text;
		this.delimited = delimited;
	}
	
	
	public NewickToken(StreamLocationProvider location, double length) {
		this.type = NewickTokenType.LENGTH;
		this.location = new StreamLocation(location);;
		this.length = length;
	}
	
	
	public double getLength() {
		return length;
	}


	public String getText() {
		return text;
	}


	public NewickTokenType getType() {
		return type;
	}


	public void setLength(double length) {
		this.length = length;
	}


	public void setText(String text) {
		this.text = text;
	}


	public StreamLocation getLocation() {
		return location;
	}


	public boolean wasDelimited() {
		return delimited;
	}


	public void setDelimited(boolean delimited) {
		this.delimited = delimited;
	}
}