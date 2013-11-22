package com.provar.app;

import java.util.ArrayList;

public class PDBStructureSet implements StructureSet {
	
	// Store a list of filenames for all the structure in the series
	ArrayList<String> structures = new ArrayList<String>();
	
	// Store a list of filenames comprising predictions for a single structure
	ArrayList<ArrayList<String>> multiFiles = new ArrayList<ArrayList<String>>();
	
	/**
	 * 
	 * @param multiPart specify that the pocket data is split into mutliple file
	 */
	PDBStructureSet(boolean multiPart){
		this.isMultiPart = multiPart;
	}
	
	private boolean isMultiPart = false;
	
	/**
	 * @return the number of structures in the list
	 */
	public Integer getSize(){
		return structures.size();
	}
	
	/**
	 * @param number of the structure to retrieve
	 * @return the filename of the structure specified by the number
	 */
	public String getStructure(int structNo){
		return structures.get(structNo);
	}
	
	/**
	 * @param filename to add to the list
	 * 
	 */
	public void add( String filename ){
		structures.add(filename);
	}

	@Override
	public void addMultiPartFiles(ArrayList<String> multiFiles) {
		this.multiFiles.add(multiFiles);
	}
	
	@Override
	public ArrayList<String> getStructureMultiFiles(Integer structNo){
		return multiFiles.get(structNo);
	}
	
	@Override
	public boolean isMultiPart(){
		return isMultiPart;
	}
}
