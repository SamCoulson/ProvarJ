package com.provar.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PBWrite {

	private PrintWriter outStream;
	
	/**
	 * 
	 * @param filename	name of the file to write the probability values to
	 * @param probValues  a list of probability values to write
	 * @throws IOException
	 */
	public PBWrite(String filename, List<Double> probValues)throws IOException{
	
		try{
			// Open an output file to write values too
			outStream = new PrintWriter( new BufferedWriter( new FileWriter( filename ) ) );
			
			// Write the probability values from the array to the file
			WriteStringToFile(probValues);
		}catch(IOException e){
			throw e;
		}
	}
	
	// Write the probability values to the file
	private void WriteStringToFile(List<Double> probValues){
		
		// Begin at 1 not 0 because amino and atom listings are not zero based.
		int i = 1;
		for(Double value : probValues){
			// write atom or residue number and the probability value, comma separated
			outStream.println(i + "," + value + "\n");
			outStream.flush();
			i++;
		}
		outStream.close();
	}
	
}
