package com.provar.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class ConfigIO {
	/**
	 * 
	 * @param filename name of the config file to load 
	 * @throws IOException
	 */
	abstract void loadConfigFile( String filename ) throws IOException;
	
	/**
	 * 
	 * @param runID name of the run id
	 * @param outputDir name of the directory where the output is stored
	 * @param refPdb name of the reference PDB file
	 * @param structDir name of the directory where the reference structures are stored
	 * @param fileFormat the naming convention of the output files for the reference structures
	 * @param pocketProgs a list of pocket programs to be written to the configuration file
	 * @param saveDir the name of the directory to save the configuration file in
	 * @throws IOException
	 */
	abstract void saveConfigFile(String runID, 
			File outputDir, 
			File refPdb, 
			File structDir,
			Pattern fileFormat,
			ArrayList<PocketProgram> pocketProgs, 
			File saveDir) throws IOException;
	abstract List<String> getConfiguration();
}
