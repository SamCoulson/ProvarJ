package com.provar.app;

import java.util.ArrayList;

// Interface provides a unified way for passing pocket prediction data into the pocket extractor, for both single and mutli-file pocket data
public interface StructureSet {
	
	/**
	 * 
	 * @return true is the pocket prediction data is split over multiple files
	 */
	public boolean isMultiPart();
	
	/**
	 * 
	 * @return number of files in the list
	 */
	public Integer getSize();
	
	/**
	 * 
	 * @param structNo number of specific structure to retrieve
	 * @return filename of structure
	 */
	public String getStructure( int structNo );
	
	/**
	 * 
	 * @param filename name of file to add
	 */
	public void add( String filename );
	
	/**
	 * 
	 * @param multiFiles list of filenames that comprise the all parts of a single set of pocket predictions for a single structure
	 */
	public void addMultiPartFiles( ArrayList<String> multiFiles );
	
	/**
	 * 
	 * @param structNo number of specific structure to retrieve
	 * @return list of filenames for all parts of a single set of pocket predictions for a single structure
	 */
	public ArrayList<String> getStructureMultiFiles( Integer structNo );
	
}
