package com.provar.app;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class multiFilePocketProgram extends PocketProgram {

	// Store the path containing the multiple pocket files
	String multiFileDir;
	
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
	public multiFilePocketProgram(  String name, 
									String abvName, 
									String runType, 
									File pocketDir,
									String multiFileDir,
									Pattern filenameFormat, 
									boolean isDirect, 
									boolean isMultiFile  ){
		super( name, abvName, runType, pocketDir, filenameFormat, isDirect, isMultiFile );
		
		this.multiFileDir = multiFileDir;
	}
	
	/**
	 * Return list of files that make the multi-part pocket prediction data for a single structure
	 */
	@Override
	public ArrayList<String> getMultiplePocketFiles(int structNo) {
		
		// List to hold filenames
		ArrayList<String> files = new ArrayList<String>();
		
		// Retrieve the directory of the specified file
		String dir = getPocketFileName( structNo );
		
		// Load in all the multi-part filenames
		File fileloc = new File(dir + "\\" + multiFileDir + "\\");
		
		// Create a sting list
		String[] fileparts = fileloc.list();
		
		// Iterate through the list and add only the file that have .pdb extensions 
		for(String filename : fileparts ){
			if( filename.contains(".pdb") ){
				files.add( dir + "\\" + multiFileDir + "\\" + filename );
			}
		}
		
		// Return the list of multi-part files
		return files;
	}
	
}
