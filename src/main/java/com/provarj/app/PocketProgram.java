package com.provar.app;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PocketProgram {
	
	protected String name;
	protected String abvName;
	protected File pocketStructDir;
	protected String runType;
	protected String[] pocketFilenames;
	protected Pattern filenameFormat;
	protected boolean isDirect;
	protected boolean isMultiFile;
	
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
	public PocketProgram( String name, 
			String abvName, 
			String runType, 
			File pocketDir, 
			Pattern filenameFormat, 
			boolean isDirect, 
			boolean isMultiFile ){
		this.name = name;
		this.abvName = abvName;
		this.pocketStructDir = pocketDir;
		this.runType = runType;
		this.filenameFormat = filenameFormat;
		this.pocketFilenames = pocketStructDir.list();
		this.isDirect = isDirect;
		this.isMultiFile = isMultiFile;
	}
	
	/**
	 * 
	 * @return the name of the pocket program
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return the abbreviated name of the pocket program
	 */
	public String getAbrevName(){
		return abvName;
	}
	
	/**
	 * 
	 * @return the name of the directory containing the pocket files
	 */
	public String getPocketDir(){
		return pocketStructDir.toString();
	}
	
	@Override
	public String toString(){
		// Return the name of the pocket program and the pocket data directory
		return name + " : " + pocketStructDir;
	}
	
	/**
	 * 
	 * @return type of pocket program e.g. Ligsite, fPocket, PASS.
	 */
	public String getRunType() {
		// Return the run type
		return runType;
	}
	
	/**
	 * 
	 * @return true if the pocket lining atoms have already been determined by the pocket program
	 */ 
	public boolean isDirect(){
		return isDirect;
	}
	
	/**
	 * 
	 * @return true is the pocket prediction data is split over multiple files
	 */
	public boolean isMultiFile(){
		return isMultiFile;
	}
	
	/**
	 * 
	 * @param seqNo number of pocket prediction file
	 * @return a single pocket file name or directory of the pocket probe information for a given structure 
	 */
	public String getPocketFileName(int seqNo) {
		// Build file name if run type conf
		for( String filename : pocketFilenames ){
			Matcher match = filenameFormat.matcher( filename.toString() );
			
			// First match the filename layout to ensure is number.pdb file
			if( match.find() ){
				if( match.group(1).length() > 0 ){
					// Compare the number 
					if( Integer.valueOf( String.valueOf( match.group(1) ) ).equals( seqNo ) ){
						return pocketStructDir + "\\" + filename;
					}
				}
			}
		}
		return null;
	}

	// Returns a list of files that comprise all of the pockets for a pocket program, only used with pocket
	// programs that split the pocket information over multiple files e.g. fPocketDirect

	abstract public ArrayList<String> getMultiplePocketFiles( int structNo );
	
}
