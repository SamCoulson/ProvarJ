package com.provar.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Log implements Observer {
	
	PrintWriter outStream;
	StringBuffer strBuf;
	
	boolean isLogging;
	
	public Log(){
		
		// Buffer to hold all messages
		strBuf = new StringBuffer();
		
		// Switch to turn on/off logging
		isLogging = false;
	}
	
	/**
	 * Switch on logging
	 */
	public void startLogging(){
		isLogging = true;
	}
	
	/**
	 * Switch off logging
	 */
	public void stopLogging(){
		isLogging = false;
	}
	
	/**
	 * 
	 * @param filename name of file to write the log in
	 * @throws IOException
	 */
	public void writeLog(String filename)throws IOException{
		// Open the file and write the log to the file
		try{
			// Open a new file for writing log data
			outStream = new PrintWriter( new BufferedWriter( new FileWriter( filename ) ) );
			
			// Copy all log data to write buffer
			outStream.print(strBuf);
			
			// Write to file
			outStream.flush();
			
		}catch(IOException ex){
			throw ex;
		}finally{
			outStream.close();
		}
	}
	
	/**
	 * Receive messages and copy to log buffer
	 */
	@Override
	public void textMsg(String msg) {
		// If logging is switched on save the message
		if( isLogging ){
			strBuf.append( msg );
		}
	}
}
