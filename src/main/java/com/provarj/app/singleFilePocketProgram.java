package com.provar.app;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class singleFilePocketProgram extends PocketProgram {

	/**
	 * 
	 * @param name full name of the pocket program
	 * @param abvName abbreviated name of the pocket program
	 * @param runType type conformer structure
	 * @param pocketDir type of pocket data
	 * @param filenameFormat format of the naming convention (in a regex) for the pocket files
	 * @param isDirect specify if pocket lining atoms have already been predicated
	 * @param isMultiFile specify if the pocket predictions have been split among multiple files
	 */
	public singleFilePocketProgram( String name, 
			String abvName, 
			String runType, 
			File pocketDir, 
			Pattern filenameFormat, 
			boolean isDirect, 
			boolean isMultiFile  ){
		super( name, abvName, runType, pocketDir, filenameFormat, isDirect, isMultiFile );
	}
	
	@Override
	public ArrayList<String> getMultiplePocketFiles(int structNo){
		return null;
	}

}
