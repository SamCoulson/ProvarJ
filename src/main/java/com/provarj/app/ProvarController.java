package com.provar.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ProvarController implements Controller {
	
	// Handle to model i.e. Process
	PocketProcessor processData;
	
	ProvarConfigIO configuration = new ProvarConfigIO();
	
	// List of pocket programs to be fed in to the processor
	ArrayList<PocketProgram> pocketProgs; 
	
	// Handle to GUI
	AppletGUI gui;
	
	// Log to store all output, this is the same output that the GUI log may see
	private Log logfile;

	private final Logger logger = Logger.getLogger( ProvarController.class.getName() );
	
	public ProvarController() {
		// Initialise list to hold pocket programs to process
		pocketProgs = new ArrayList<PocketProgram>();
							
		logfile = new Log();
	}
	
	// Set up a handle to the view
	public void setView(AppletGUI gui){
		this.gui = gui;
	}
	
	// Return the handle to the view
	public AppletGUI getView(){
		return this.gui;
	}
	
	// Method called from GUI to load the configuration data
	public List<String> loadConfigFile( String configFile ){
		
		try{
			configuration.loadConfigFile( configFile );
			gui.updateLogMsg("Loading configuration from: " + configFile + "\n");
		}catch(Exception e){
			gui.updateLogMsg("Unable to load configuration file: " + e.getMessage() );
		}
		
		// Retrieve the configuration data
		List<String> config = configuration.getConfiguration();
		
		// Return the configuration data to the caller
		return config;
	}
	
	// Method called from GUI to save the configuration data
	public void saveConfigFile( 
			String runID, 
			File outputDir, 
			File refPdb, 
			File structDir,
			Pattern fileFormat,
			File saveDir){
		
		try{
			configuration.saveConfigFile( 
					runID, 
					outputDir, 
					refPdb, 
					structDir, 
					fileFormat, 
					pocketProgs, 
					saveDir );
			gui.updateLogMsg("Saving configuration to: " + saveDir + "\n");
		}catch(Exception e){
			gui.updateLogMsg("Unable to save configuration file: " + e.getMessage() + "\n" );
		}
	}
	
	// Will start the processing of the data input into ProvarJ
	public void runProvar() {
		
		PocketProcessor newProcess = null;
		
		// Set up and begin logging messages.
		logfile.startLogging();
		
		// Process the data given the configuration
		try{
			
			// Create a new PocketProcessor with global parameters for the process i.e. run ID, output path, a reference structures
			newProcess = new PocketProcessor( 	gui.getRunID(), 
												gui.getBaseDir(), 
												gui.getPdbRefName(),
												gui.getRefStructDir(),
												Pattern.compile( "([0-9]*).pdb" ),
												pocketProgs );
			// Set up 'subscribers' to listen for messages
			newProcess.addObserver( this.gui );
			newProcess.addObserver( logfile );
			
		}catch(IllegalArgumentException e){
			logger.severe("Invalid Run parameter(s) " + e.getMessage() + "\n" );
			gui.updateLogMsg( "Invalid Run parameter(s) " + e.getMessage() + "\n" );
		}
		
		try{
			// This process should run in a separate thread to allow continued use of the GUI
			new Thread(newProcess).start();
		}catch( Exception ex ){
			gui.updateLogMsg( "Error in processing thread: " + ex.getMessage() + "\n" );
		}
	}
	
	// Add a pocket program
	@Override
	public void addPocketProgram( PocketProgram program ){
		// Add a procket program to the list
		if( program != null ){
			pocketProgs.add( program );
		}
	}

	@Override
	public void removePocketProgram(PocketProgram prog) {
		// Check the list and remove the pocket program entry if it exists
		if(pocketProgs.contains( prog )){
			pocketProgs.remove( prog );
		}
	}

}
