package com.provar.app;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public interface Controller {
		
	/**
	 * 
	 * @param prog pocket program to add
	 */
	public void addPocketProgram(PocketProgram prog);
	/**
	 * 
	 * @param prog pocket program to add remove
	 */
	public void removePocketProgram( PocketProgram prog );
	
	/**
	 * 
	 * @param gui view to associate with the controller
	 */
	public void setView(AppletGUI gui);
	
	/**
	 * 
	 * @return handle to the view
	 */
	public AppletGUI getView();
	
	/**
	 * 
	 * @param configFile name of configuration file to load
	 * @return list containing the configuration data
	 */
	public List<String> loadConfigFile( String configFile ); 
	
	/**
	 * 
	 * @param runID name of run ID
	 * @param outputDir name of base directory
	 * @param refPdb name of reference structure
	 * @param structDir name of the directory where the reference structures are stored
	 * @param fileFormat file naming convention for the reference structure files
	 * @param saveDir name of the directory to save the configuration file in
	 */
	public void saveConfigFile( String runID, 
			File outputDir, 
			File refPdb, 
			File structDir,
			Pattern fileFormat, 
			File saveDir );
	
	/**
	 * Starts the processing of data running 
	 */
	public void runProvar();
		
}
