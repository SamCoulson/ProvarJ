package com.provar.app;

import java.io.File;
import java.util.regex.Pattern;

final public class PocketProgramFactory {
	
	/*
	 * Returns a pocket program with settings for the specific pocket type of pocket data
	 */
	public static PocketProgram CreatePocketProgram( String name, File pocketDir, String runType ){
		
		PocketProgram newProg = null;
		
		// Determine the reference structure types
		if( runType.equals( "CONF" ) ){
			// Determine the type of pocket program and create a pocket program filled out with the appropriate settings
			if(name.equals( "PASS" ) ){
				newProg = new singleFilePocketProgram("PASS", "PA", runType, pocketDir, Pattern.compile("([0-9]*)_probes.pdb"), false, false );
			}else if(name == "fPocket"){
				newProg = new multiFilePocketProgram("fPocket", "FD", runType, pocketDir, "pockets", Pattern.compile("([0-9]*)_out"), true, true );
			}else if(name == "Ligsite"){
				newProg = new singleFilePocketProgram("LIGSITE", "LC", runType, pocketDir, Pattern.compile("([0-9]*).pdb_pocket_r.pdb"), false, false );
			}
		}
		
		return newProg;
	}
}
