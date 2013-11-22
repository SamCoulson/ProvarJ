package com.provar.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProvarConfigIO extends ConfigIO {

	BufferedReader inputBuf;
	PrintWriter outputBuf;
	
	List<String> configuration = new ArrayList<String>(5);
	
	/**
	 * @params name of the config file
	 * @throws IOExcpetion
	 */
	@Override
	public void loadConfigFile( String filename) throws IOException{
		// Current line being read
		String configLine;
		// Scan for equals inside each line of the file
		Matcher matched;
		
		try{
			// Open config file for reading line by line
			inputBuf = new BufferedReader( new FileReader( new File( filename ) ) );
			
			// Prepare pattern to scan for in each line.  Looking for 'setting = value' pattern
			Pattern validLine = Pattern.compile( "(.*)=(.*)" );
			
			// Read in the first line
			configLine = inputBuf.readLine();
			
			// Read in lines until there no more
			while( configLine != null ){
				
				// Check for a match, line should have an = sign
				matched = validLine.matcher( configLine );
		
				// Read configuration line determine the which configuration it is and enter it into the array of settings with their values
				if( matched.find() ){
					
					//System.out.println( "Matched Line = " + matched.group(1) + "=" + matched.group(2) );
					if( matched.group(1).equals( "RUNID" ) ){
						configuration.add(0, matched.group(2) );
					}else if( matched.group(1).equals( "OUTPUTDIR" ) ){
						configuration.add(1, matched.group(2) );
					}else if( matched.group(1).equals( "REFPDB" ) ){
						configuration.add(2, matched.group(2) );
					}else if( matched.group(1).equals( "STRUCTUREDIR" ) ){
						configuration.add(3, matched.group(2) );
					}else if( matched.group(1).equals( "FILEFORMATPATTERN" ) ){
						configuration.add(4, matched.group(2) );
					}else if( matched.group(1).equals( "POCKETNAME" ) ){
						configuration.add( "POCKETNAME=" + matched.group(2) );
					}else if( matched.group(1).equals( "POCKETRUNTYPE" ) ){
						configuration.add( matched.group(2) );
					}else if( matched.group(1).equals( "POCKETSTRUCTDIR" ) ){
						configuration.add( matched.group(2) );
					}
					
				}
				
				// Read next line
				configLine = inputBuf.readLine();
			}
			
			// Debug output
			/*
			System.out.println( configuration.get(0) );
			System.out.println( configuration.get(1) );
			System.out.println( configuration.get(2) );
			System.out.println( configuration.get(3) );

			System.out.println( configuration.get(5) );
			System.out.println( configuration.get(6) );
			System.out.println( configuration.get(7) );
			*/
			
		}catch(IOException ex){
			throw ex;
		}finally{
			inputBuf.close();
		}
	}
	
	
	@Override
	public void saveConfigFile(String runID, 
			File outputDir, 
			File refPdb, 
			File structDir,
			Pattern fileFormat,
			ArrayList<PocketProgram> pocketProgs, 
			File saveDir) throws IOException {
			
		try{
			
			// Create the output buffer for config file 
			outputBuf = new PrintWriter( new BufferedWriter( new FileWriter( saveDir.getAbsoluteFile() + ".txt" ) )  );
			
			// Append new line character to end of each line
			String newLine = "\n";
			
			// Write the setting and value to the buffer
			outputBuf.println( "RUNID=" + runID + newLine );
			outputBuf.println( "OUTPUTDIR=" + outputDir.toString() + newLine );
			outputBuf.println( "REFPDB=" + refPdb.toString() + newLine );
			outputBuf.println( "STRUCTUREDIR=" + structDir.toString() + newLine );
			outputBuf.println( "FILEFORMATPATTERN=" + fileFormat.toString() + newLine );
			outputBuf.println( newLine );
			
			// Iterate over pocket programs and write settings into the buffer
			for(PocketProgram prog : pocketProgs){
				outputBuf.println( "POCKETNAME=" + prog.getName() + newLine );
				outputBuf.println( "POCKETRUNTYPE=" + prog.getRunType() + newLine );
				outputBuf.println( "POCKETSTRUCTDIR=" + prog.getPocketDir() + newLine ); // pocket directory
				outputBuf.println( newLine );
			}
			// Send the contents of the buffer to be written to file
			outputBuf.flush();
			
			}catch(IOException ex){
				throw ex;
			}finally{
				outputBuf.close();
			}
		}
	
	@Override
	public List<String> getConfiguration() {
		// Return array containing all configuration data
		return configuration;
	}
}
