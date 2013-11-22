package com.provar.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PDBWrite {
	
	public static final int ATOM = 0;
	public static final int AMINO = 1;
	public static final int AMINO_AVG = 2;
	
	private String pdbOutFilename;
	private String pdbRefFilename;
	private BufferedReader inPDBBuf;
	
	List<String> inPDBfileContentsBuf;
	List<String> outPDBfileContentsBuf;
	
	/**
	 * 
	 * @param pdbRefFilename name of PDB reference structure that the output PDB files will be based on
	 * @throws IOException
	 */
	public PDBWrite(String pdbRefFilename) throws IOException{
		this.pdbRefFilename = pdbRefFilename;
		
		// Attempt to open and associate a buffer with the reference file
		try{
			inPDBBuf = new BufferedReader( new FileReader( new File( this.pdbRefFilename ) ) );
		}catch( IOException ex ){
			throw ex;
		}
	}
	
	// Method to write the B-values into the individual lines inside the PDB file
	private void WriteBValues( List<Double> bValues, int type )throws Exception{
		
		// Track which bValue in the list is being written 
		int bValueIndex = 0;
		
		// Format the output number to 2.d.p
		DecimalFormat df = new DecimalFormat("0.##");
		
		// Hold the line number
		StringBuffer newLine = null;
		
		// Buffer to hold new PDB file contents
		outPDBfileContentsBuf = new ArrayList<String>();
		
		// Could try FileConvert in BioJava3 it might be easier to do it this way
		
		for( String line : inPDBfileContentsBuf ){
			// If an ATOM line, find the number of the line and try to match to an
			// entry in the array of b-values skip to position of B-values and 
			// insert the B-value probability from the array
			//String isAtom = line.substring(0, 3);
			
			if( line.contains("ATOM") ){
				
				// B-value/factor to be written
				Double bValue;
				
				// Hold the new line to be written to the new PDB file
				String nLine = null;
				 
				// Get line numbers
				newLine = new StringBuffer( line );
				
				// Need to do a 'replace' in on the 61 char padded by 6 spaces with a precision of 2 d.p. 
				// with fixed point notation
				
				// If Amino values are being written, find the amino number of this line and match it up with the one in the bvalue list and write this
				if( type == AMINO ){
					
					// Extract the section of the line that contains the current residue number
					String amino = line.substring(22, 26).trim();
					
					// Extract this number and store as an integer
					Integer resNum = Integer.parseInt(amino);
					
					// Find the corresponding residue number in the b-value list and multiply it by 100
					// Minus 1 to account for 0 index array but amino listing start at 1.
					bValue = Double.valueOf( df.format( 100*bValues.get( resNum - 1 ) ) );
					
					// Create the line again but with the added b-value
					nLine = newLine.replace(60, 66, " " + bValue.toString()).toString();
					
				}else{
					try{
						// Retrieve the b-value that correponds with the line  
						bValue = Double.valueOf( df.format( 100*bValues.get( bValueIndex ) ) );
						
						// Replace the specific point in the line with the new B-value, store this line to be added to the
						// list of lines that are written to the new PDB output file
						nLine = newLine.replace(60, 66, bValue.toString()).toString();
						
					}catch(Exception ex){
						throw ex;
					}
				}
				
				// Add the new line with the added B-value to an buffer that represent the file to output file to be written  
				outPDBfileContentsBuf.add(nLine);
				
				// Increment index for next b-value
				bValueIndex++;
			}
		}
	}
	
	// Method to read the individual lines inside the reference PDB file
	private void ReadPDBFileContents() throws IOException{
		
		String inPDBLine;
		
		// Buffer to store lines read from file
		inPDBfileContentsBuf = new ArrayList<String>();
		
		// Read the first line
		inPDBLine = inPDBBuf.readLine();
		
		try{
			while( inPDBLine != null ){
			
				// Write line in to an array that is set to the size of the expected amount of atoms whose
				// B-factors will be written
				if( !inPDBLine.contains("HEADER") ){
					inPDBfileContentsBuf.add(inPDBLine);
				}
				
				// Read next line
				inPDBLine = inPDBBuf.readLine();
			}
		}catch(IOException ex){
			throw ex;
		}finally{
				inPDBBuf.close();
		}
	}
	
	/**
	 * 
	 * @param outPDBFilename name of file to output the PDB data
	 * @param bValues list of probability values
	 * @param type specify that either atoms or residues b-values will be written
	 * @throws Exception
	 */
	public void WritePDBFile(String outPDBFilename, List<Double> bValues, int type)throws Exception{
		
		// Store the name of the file to write the data to
		pdbOutFilename = outPDBFilename;
		
		// Output buffer for data to be written to file 
		PrintWriter outBuf = null;
		
		try{
			// Read in the reference PDB, this is used to create the representative structure
			ReadPDBFileContents();
			
			// Write the probability values to the B-Factor columns
			WriteBValues( bValues,  type);
		
			// Create the output buffer for the representative structure PDB file 
			outBuf = new PrintWriter( new BufferedWriter( new FileWriter( pdbOutFilename ) ) );
		
			// Write a header field into the PDB file
			outBuf.println( "HEADER " + this.pdbRefFilename );
			
			// Populate the new PDB file with the ATOM data plus the B-factors 
			for( String outLine : outPDBfileContentsBuf ){
				outBuf.println( outLine );
				// Write the contents of the buffer to file
				outBuf.flush();
			}
		}catch(IOException ex){
			throw ex;
		}
		// Close the file
		outBuf.close();
		
	}
}
