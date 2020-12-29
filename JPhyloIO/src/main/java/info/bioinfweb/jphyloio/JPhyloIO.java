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


import info.bioinfweb.commons.appversion.ApplicationType;
import info.bioinfweb.commons.appversion.ApplicationVersion;

import java.net.MalformedURLException;
import java.net.URL;



/**
 * Singleton main class that provides information about this library at runtime.
 * 
 * @author Ben St&ouml;ver
 */
public class JPhyloIO {
	private static final String NAME = "JPhyloIO";
	private static final ApplicationVersion VERSION = new ApplicationVersion(1, 0, 0, 1681, ApplicationType.BETA);

	private static JPhyloIO firstInstance = null;
	
	
	private URL projectURL;
	
	
	private JPhyloIO() {
		super();
		try {
			projectURL = new URL("http://bioinfweb.info/JPhyloIO/");
		}
		catch (MalformedURLException e) {
			throw new InternalError(e.getMessage());  // Should not happen.
		}
	}
	
	
	public static JPhyloIO getInstance() {
		if (firstInstance == null) {
			firstInstance = new JPhyloIO();
		}
		return firstInstance;
	}


	public ApplicationVersion getVersion() {
		return VERSION;
	}
	
	
	public String getLibraryName() {
		return NAME;
	}
	
	
	public String getLibraryNameAndVersion() {
		return getLibraryName() + " " + getVersion().toString();
	}
	
	
	public URL getProjectURL() {
		return projectURL;
	}
}
