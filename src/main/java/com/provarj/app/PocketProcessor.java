package com.provar.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
//import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PocketProcessor implements Runnable, Observable, Observer {
	
	// General paths, directories and global parameters used to process the given pocket programs 
	
	private String 	runId;								// Provar Run ID, base directory takes this name, formerly 'pdfid'
	private File 	outRootDir;							// Specifies the root directory for all data and Provar output
	private File 	refPDB;								// Name of representative reference structure file
	private File 	structureDir;						// Directory containing the reference structures
	private Pattern refFileFormat;						// Format of the conformer filename specific to this class
	private ArrayList<PocketProgram> pocketPrograms;	// Store a list of pocket programs that will be used
	
	private File baseDir;								// Root directory for all pocket program output 
	
	private ArrayList<String> refStructs;				// Hold a list of path and filenames for the reference structures			
	
	private ArrayList<Observer> observers;				// List of observers to update with processing messages		
	
	private Double pocketRadius;						// Define the radius of how big the pocket to search will be
	
	private Log logfile;								// Log to store all output, this is the same output that the GUI log may see
	
	// Constructor for a run, pass in all needed details here
	public PocketProcessor( String runID, 
			File outputDir, 
			File refPdb, 
			File structDir,
			Pattern filenameFormat,
			ArrayList<PocketProgram> pocketProgs )throws IllegalArgumentException{
		
		// Distance to search from at for pocket binding atoms
		pocketRadius = 3.75;							
		
		// Take a copy of global parameters 
		this.runId = runID;
		this.outRootDir = outputDir;
		this.refPDB = refPdb;
		this.structureDir = structDir;
		
		// Initialise list to hold the pocket programs
		this.pocketPrograms = pocketProgs;
		
		// Store name format of expected structure files
		this.refFileFormat = filenameFormat;
		
		// Create a list to register and update observers
		this.observers = new ArrayList<Observer>();
		
		// Validate input parameters for the process to be run
		try{
			validateParameters();
		}catch( IllegalArgumentException ex ){
			throw ex;
		}
		
		// Determine how many structures should be processed by counting
		// the number of structures files passed in
		loadRefStructures();
		
		// Set up and begin logging messages.
		logfile = new Log();
		addObserver(logfile);
		logfile.startLogging();
	}
	
	// Override run() in Runnable to launch the processing code
	@Override
	public void run(){
		
		File fullPath = new File( outRootDir + File.separator + runId ) ;
		
		// Display message for this process to user
		// Print Provar title message
		updateObservers("****************************************************************************************");
		updateObservers("\t\tProvar ( " + ProvarJ.PROVAR_VERSION + " ) / Run ID : " + runId);
		updateObservers("****************************************************************************************");
		updateObservers("Using site point / atom cutt-off: " + pocketRadius);
		
		// Create a base directory in which all the output of all processed pocket programs will reside
		updateObservers("Creating Provar directory structure...\n");
		updateObservers("Creating Provar run-specific directory: " + fullPath);
		
		// Check that the intended path name for this run does not already exist
		if( fullPath.exists() ){
			// Send out message to user
			updateObservers("Directory: " + fullPath + " already exists, please try using a different run ID name");
		}else{
			// Check that the path is able to be created.
			if( !fullPath.mkdirs() ){
				updateObservers("Unable to create directory: " + fullPath + ", please check file permissions");
			}
			
			// Retain this path for when probability files and PDSB files are written
			baseDir = fullPath;
		
			// Display parameters information
			updateObservers("Type\t\t: CONF");
			updateObservers("Structure directory\t: " + structureDir);
			updateObservers("Reference PDB\t\t: " + refPDB);
			
			updateObservers("Running Provar in multi conformer mode");
			updateObservers("*****************************************************************");
			
			// Proceed to processing given list of pocket programs
			ProcessPocketProgs();
			
			// Update user when the processing is complete
			// Notify end of Program run
			updateObservers( "\nFinshed\n--------------------------------------------" );
		}
		
		// Stop logging and write the contents of the log to the run directory
		logfile.stopLogging();
		try{
			logfile.writeLog( outRootDir + File.separator + runId + File.separator + runId + "_logfile.txt" );
			updateObservers( "Writing log to " + outRootDir + File.separator + runId + File.separator + runId + "_logfile.txt");
		}catch(IOException ex){
			updateObservers("Unable to write log file: " + ex.getMessage() );
		}
		
	}
	
	/**
	 * Iterate through the pocket programs passed in and process them
	 */
	public void ProcessPocketProgs(){
		
		// Process each pocket program data in turn
		for(PocketProgram pocketProg : pocketPrograms ){
			
			// Hold a list of path and filenames for all of pocket structures
			StructureSet pocketStructs = new PDBStructureSet( pocketProg.isMultiFile() );
			
			// Attempt to build an pair of lists that have an equal amount of files for structure/pocket pairs
			for( String refFilename : refStructs ){
				
				// retrieve the sequence number form the filename
				int refFile = getSequenceFile( refFilename );
				
				// Attempt to retrieve the corresponding filename from the list of pocket files 
				String pocketFile = pocketProg.getPocketFileName( refFile );
				
				// Check a valid name was returned
				if ( pocketFile != null ){
					if( !pocketProg.isMultiFile() ){
						// Add single pocket prediction file to list to be processed
						pocketStructs.add(pocketFile);
					}else{
						// Add multiple pocket prediction files to list to be processed
						pocketStructs.add( pocketFile );
						// Pocket program stores pockets in separate PDB files, store all the names of these files
						pocketStructs.addMultiPartFiles( pocketProg.getMultiplePocketFiles( refFile ) );
					}	
				}
			}
			
			// Make sure there are the same number of reference structures as there are pocket structures 
			if( refStructs.size() != pocketStructs.getSize() ){
				updateObservers( "Number of structure files and pocket files do not match" );
				updateObservers( "There are " + refStructs.size() + " reference structures and " + pocketStructs.getSize() + " pocket structures" );
				
			}else{
				updateObservers( "Processing " + refStructs.size() + " structures" );
			}
				
			updateObservers("Extracting pocket data");
			
			// Attempt to extract all of the data from the structure and pocket data
			PocketExtractor extracter = null;
			try{
				extracter = new PocketExtractor( pocketStructs, refStructs, refPDB, pocketProg.isDirect(), pocketRadius, this );
			}catch(IOException ex){
				updateObservers( "Failed to extract pocket data: " + ex.getMessage() );
			}
			
			// Calculate and display quantile data
			updateObservers("Quantiles\t\t( 0.25, 0.5 ,0.75 ) for :" + pocketProg.getName() );

			ProbabilityCalculator atom = new ProbabilityCalculator( extracter.getAtomTotal(), refStructs.size() );
			double[] atomProb = atom.getQuantiles();
			updateObservers("Atom\t\t: " + atomProb[0] + " " + atomProb[1] + " " + atomProb[2] );
			
			ArrayList<Double> zeroRemoved = new ArrayList<Double>();
			for( Double num : extracter.getAtomTotal() ){
				if( num > 0.0 ){
					zeroRemoved.add(num);
				}
			}
			
			// Calculate the probabilities for the atoms
			ProbabilityCalculator atomAvg = new ProbabilityCalculator( zeroRemoved, refStructs.size() );
			double[] atomAvgProb = atomAvg.getQuantiles();
			updateObservers("Atom normalised\t: " + atomAvgProb[0] + " " + atomAvgProb[1] + " " + atomAvgProb[2] );
			
			// Calculate the probabilities for the amino acids
			ProbabilityCalculator amino = new ProbabilityCalculator( extracter.getAminoTotal(), refStructs.size() );
			double[] aminoProb = amino.getQuantiles();
			updateObservers("Amino\t\t: " + aminoProb[0] + " " + aminoProb[1] + " " + aminoProb[2] );
			
			// Calculate the probabilities for the averaged amino acids
			ProbabilityCalculator aminoAvg = new ProbabilityCalculator( extracter.getAminoAvgTotal() , refStructs.size() );
			double[] aminoAvgProb = aminoAvg.getQuantiles();
			updateObservers( "Amino average\t\t: " + aminoAvgProb[0] + " " + aminoAvgProb[1] + " " + aminoAvgProb[2] );
			
			updateObservers( "-----------------------------------------------------------------");
			updateObservers( "Writing pocket data" );
			
			// Create a sub-directory to write the probability and PDB files to
			StringBuilder pathAndFileName = new StringBuilder();
			pathAndFileName.append(baseDir.toString() + File.separator + pocketProg.getAbrevName() + File.separator );
			pathAndFileName.append(runId );
			pathAndFileName.append("_" + pocketProg.getName());
			
			updateObservers("Creating sub-directory for: " + pocketProg.getName() );
			updateObservers("Writing probability files..");
			
			try{
				File pocketDir = new File( baseDir + File.separator + pocketProg.getAbrevName() );
				if( !pocketDir.exists() ){
					pocketDir.mkdirs();
				}
			}catch(Exception ex){
				updateObservers("Unable to create base directory:" + ex.getMessage() );
			}
			
			updateObservers("OK");
			// Output the prediction file
			
			// Write probabilities to PB files for amino, atom, amino average
			try{
				// Amino
				new PBWrite(pathAndFileName + "_p_amino_out_" + pocketRadius + "A-Radial.txt", amino.getProbabilites() );
				
				// Atom
				new PBWrite(pathAndFileName + "_p_ATOM_out_" + pocketRadius + "A-Radial.txt", atom.getProbabilites() );
				
				// Amino average
				new PBWrite(pathAndFileName +"_p_amino_avg_by_atom_out_" + pocketRadius + "A-Radial.txt", aminoAvg.getProbabilites() );
			}catch( IOException ex){
				updateObservers("Could not write probability files: " + ex.getMessage() );
			}
			
			// Write PDB output files
			updateObservers("Writing PDB files...");
			
			try{
				// Atom
				PDBWrite outAtomFile = new PDBWrite( refPDB.toString() );
				outAtomFile.WritePDBFile(pathAndFileName + "_ATOM_out_" + pocketRadius + "A-Radial.pdb", atom.getProbabilites(), PDBWrite.ATOM );
				
				// Amino
				PDBWrite outAminoFile = new PDBWrite( refPDB.toString() );
				outAminoFile.WritePDBFile(pathAndFileName + "_amino_out_" + pocketRadius + "A-Radial.pdb", amino.getProbabilites(), PDBWrite.AMINO );
				
				// Amino
				PDBWrite outAminoAvgFile = new PDBWrite( refPDB.toString() );
				outAminoAvgFile.WritePDBFile(pathAndFileName + "_amino_avg_by_atom_out_" + pocketRadius + "A-Radial.pdb", aminoAvg.getProbabilites(), PDBWrite.AMINO );
				updateObservers("OK\n");
			}catch(Exception ex){
				updateObservers("Could not write PDB files: " + ex.getMessage() );
			}
			
		}
	}	
	
	// Get the file corresponding to a filename in the series of reference structure files
	private int getSequenceFile( String filename ) {
		
		// Scan the filename for a number
		Matcher match = refFileFormat.matcher( filename.toString() );
			
		// First match the filename layout to ensure is number.pdb file
		if( match.find() ){
			
			// Return the sequence number of this reference file
			if( match.group(1).length() > 0 ){
				return Integer.valueOf( String.valueOf( match.group(1) ) );
			}
		}
		
		return 0;
	}
	
	//Check that all the parameters passed in a valid
	private void validateParameters() throws IllegalArgumentException{
		
		if( ( runId == null ) || ( runId.length() == 0 ) ) {
			throw new IllegalArgumentException("Zero length RunID passed for processing");
		}
		
		if( ( !outRootDir.exists() ) || ( !outRootDir.isDirectory() ) ){
			throw new IllegalArgumentException("Output directory does not exist or is not a directory");
		}
		
		if( ( !refPDB.exists() ) || ( !refPDB.isFile() ) ){
			throw new IllegalArgumentException("Reference PDB file does not exist or is not a file");
		}
		
		if( ( !structureDir.exists() ) || ( !structureDir.isDirectory() ) ){
			throw new IllegalArgumentException("Reference structure directory does not exist or is not a directory");
		}
		
		if( ( ( refFileFormat == null ) || ( refFileFormat.equals("") ) ) ){
			throw new IllegalArgumentException("Null or zero length filename format pass for processing");
		}
		
		if( ( pocketPrograms == null ) || ( pocketPrograms.isEmpty() ) ){
			throw new IllegalArgumentException("Pocket program list is empty");
		}
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add( observer );
	}

	@Override
	public void updateObservers(String msg) {
		for(Observer obv : observers){
			obv.textMsg(msg + "\n");
		}
	}

	@Override
	public void textMsg(String msg) {
		// Receive update messages from the extractor and forward to Observers of this processor
		updateObservers(msg);
	}
	
	private void loadRefStructures(){
		
		refStructs = new ArrayList<String>();
		
		// Get all files within the reference structure directory
		String[] refFilenames = structureDir.list();
		
		// Iterate over the list and store those that belong to that have correct format
		// in the case of conformers it will contain a number
		for( String filename : refFilenames ){
			
			// First match the filename layout to ensure is number.pdb file
			Matcher match = refFileFormat.matcher( filename.toString() );
			
			// If a match is found add it to the list of reference structures
			if( match.find() ){
				if( match.group(1).length() > 0 ){
					refStructs.add( structureDir + File.separator + filename );
				}
			}
		}
	}

}
		

