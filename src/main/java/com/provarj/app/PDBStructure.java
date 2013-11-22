package com.provar.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.biojava.bio.structure.*;
import org.biojava.bio.structure.io.*;
import org.biojava.bio.structure.AtomIterator;

public class PDBStructure implements ProteinStructure {
		
		// All data concerning the PDB structure is primarily centred around the atoms
		// of the structure, each index in these lists represents an atom, from here
		// data concerning the atom such as which residue it belongs too are recorded
	
		// Note: Should only ever be concerned with reading in one chain
	
		// Atom data lists
		private List<double[]> 	atomXYZ;			// Atom relative coordinates in XYZ order
		private List<Integer> 	atomSerialNo;		// Atom serial number
		private List<String> 	atomName;			// Atom name 
		private List<Integer>	residueSeqNo;		// The sequence number of the residue the atom belongs too
		private List<String> 	residueName;		// Name of the residue the atom belongs too
		private List<String> 	chainID;			// Chain letter of chain the atom belongs too
		private List<String> 	atomElement;		// Atom element name
		private List<String>	aaSeq;				// Amino acid sequence in 3-letter format
		
		private List<String>	chains;
		private List<Integer>	chainStart;
		
		/**
		 * 
		 * @param structureName filename of the single structure to load
		 * @throws IllegalArgumentException
		 * @throws IOException
		 */
		public PDBStructure( String structureName ) throws IllegalArgumentException, IOException{
			
			// Arrays to store data for each atoms
			atomXYZ = new ArrayList<double[]>();
			atomSerialNo = new ArrayList<Integer>();
			atomName = new ArrayList<String>();
			residueSeqNo = new ArrayList<Integer>();
			residueName = new ArrayList<String>();
			chainID = new ArrayList<String>();
			atomElement = new ArrayList<String>();	
			aaSeq = new ArrayList<String>();	
			
			chains = new ArrayList<String>();
			chainStart = new ArrayList<Integer>();
			
			try{
				loadStructureData( verifyStructureFile( structureName ) );
			}catch(IllegalArgumentException ex){
				throw ex;
			}catch(IOException ex){
				throw ex;
			}
		}
		
		/**
		 * 
		 * @param multiParts filename of the structure to load that has multiple parts
		 * @throws IllegalArgumentException
		 * @throws IOException
		 */
		public PDBStructure(ArrayList<String> multiParts) throws IllegalArgumentException, IOException{
			
			// Arrays to store data for each atoms
			atomXYZ = new ArrayList<double[]>();
			atomSerialNo = new ArrayList<Integer>();
			atomName = new ArrayList<String>();
			residueSeqNo = new ArrayList<Integer>();
			residueName = new ArrayList<String>();
			chainID = new ArrayList<String>();
			atomElement = new ArrayList<String>();	
			aaSeq = new ArrayList<String>();	
			
			chains = new ArrayList<String>();
			chainStart = new ArrayList<Integer>();
			
			try{
				for(String part : multiParts){
					loadStructureData( verifyStructureFile( part ) );
				}
			}catch( IllegalArgumentException ex ){
				throw ex;
			}catch(IOException ex){
				throw ex;
			}
			
		}
		
		// Meta data concerning the list of atoms
		private int atomCount;						// Track the total amount of atoms in the whole structure
		private int maximumResidueNo;				// Track the total amount of residues in the structure
		
		/**
		 * 
		 * @return total number of residues in the structure
		 */
		public int getMaxResidue(){
			return maximumResidueNo;
		}
		
		/**
		 * 
		 * @return total number of atoms in the structure
		 */
		public int getAtomCount(){
			return atomCount;
		}
		
		/**
		 * 
		 * @return list of coordinates for all the atoms in the structure
		 */
		public List<double[]> getAtomXYZ(){
			return atomXYZ; 
		}
		
		/**
		 * 
		 * @return list with all the atom numbers in series
		 */
		public List<Integer> getAtomSerNos(){
			return atomSerialNo;
		}
		
		/**
		 * 
		 * @return list with all the names for each atom i.e. C, H, O in series 
		 */
		public List<String> getAtomNames(){
			return atomName;
		}
		
		/**
		 *  
		 * @return list containing the residues number that each atom belongs too
		 */
		public List<Integer> getResidueSeqNo(){
			return residueSeqNo;
		}
		
		/**
		 * 
		 * @return list containing the residues name (3 letter code) for each atom
		 */
		public List<String> getResidueName(){
			return residueName;
		}
		
		/**
		 * @return the ID of the chain for each atom in the structure
		 */
		public List<String> getChainID(){
			return chainID;
		}
		
		/**
		 * @return the element of each atom in the list
		 */
		public List<String> getAtomElement(){
			return atomElement;
		}
		
		// Method to load the structure given a filename
		private void loadStructureData( File pdbFilename ) throws IOException{
			
			// BioJava structure to hold PDB structure data
			// Attempt to lead structure data from file reference
			Structure pdbStruct;
			try{
				pdbStruct = loadStructure( pdbFilename );
			}catch(IOException ex){
				throw ex;
			}
			
			// Need to count the residue numbers in the chains and workout the actual amount of residues.
			
			// Use a BioJava iterator to cycle through all the atom groups in the extracted data
			AtomIterator atomIt = new AtomIterator( pdbStruct );
			
			String lastChain = atomIt.getCurrentChain().getChainID();
			
			// Get and store the chain ID for the first chain
			if( atomIt.getCurrentChain() != null ){
				chains.add( lastChain );
			}
			
			// Get and store the first residue number for the first chain
			ResidueNumber lastResNo = atomIt.getCurrentChain().getAtomGroup(0).getResidueNumber();
			chainStart.add( lastResNo.getSeqNum() );
			
			int aminoCount = 1;
			// Store the highest residue in the structure
			int maxRes = 0;
			
			while( atomIt.hasNext() ){
				
				Atom atom = atomIt.next();
				
				if( !atom.getGroup().getPDBName().equals("HOH") ){
				
					// Add XYZ array to a list
					atomXYZ.add( atom.getCoords() );
					
					// Retrieve the serial number for this atom ( every atom has one, usually occur in sequence )
					atomSerialNo.add( atom.getPDBserial() );
					
					// Retrieve the atom name
					atomName.add( atom.getName() );
					
					// Retrieve the element type of this atom
					ElementType elemType;
					elemType = atom.getElement().getElementType();
					atomElement.add( elemType.name() );
					
					// Add sequence number to index of current atom
					// These do not necessarily always start from 0 and can be non-consecutive because of indels
					ResidueNumber thisResNo = atom.getGroup().getResidueNumber();
					residueSeqNo.add( thisResNo.getSeqNum() );
					
					// Retrieve the name of the residue this atom belongs too
					residueName.add( atom.getGroup().getPDBName() );
					
					// Add 3 letter aaSeq to list  
					aaSeq.add( atom.getGroup().getPDBName() );
					
					// Get the chain ID of the current atom 
					String thisChain = atom.getGroup().getChainId();
					chainID.add( thisChain );
					
					if( !thisChain.equals( lastChain ) ){
						// Add the new chain ID to the list
						chains.add( thisChain );
						
						// Add the first residue number of the chain
						chainStart.add( thisResNo.getSeqNum() );
						
						// Store this chain so that the comparison can be made for the next chain change
						lastChain = thisChain;
						
						// reset the residue counter for the next chain 
						aminoCount = 1;
						
					}
					
					// If the number of residues counted for this chain is larger than the largest counted
					// Also stop HOH being counted, some PDB's may have these
					if( ( thisResNo.getSeqNum() > maxRes ) && (!atom.getGroup().getPDBName().equals("HOH") ) ){
						maxRes = thisResNo.getSeqNum();
					}
					
					// Detect a change from one amino acid to the next and count how many changes happen over the whole list of atoms
					if( !lastResNo.equals( atom.getGroup().getResidueNumber() )  ){
						
						// Set the last residue to this one in order to be able to detect the next residue change
						lastResNo = thisResNo;
						
						// Increment the amino counter
						aminoCount++;
					}
				}
			}
			
			// Store the number of atoms counted
			atomCount = atomSerialNo.size();
			
			// Get the highest residue number found in the list
			maximumResidueNo = maxRes;
		}
		
		// Method to read the actual PDB file data.
		private Structure loadStructure( File pdbFileName ) throws IOException{
			
			Structure structureData = null;
			
			try{
				// Create a PDBreader to read in the atom records
				PDBFileReader pdbReader = new PDBFileReader();
			
				// Read in the structural data
				structureData = pdbReader.getStructure( pdbFileName );
					
				}catch( IOException ex ){
					// Throws exception on bad header which conformers
					// this can be ignored though execution will continue. The stack trace gets called within
					// the pdb_HEADER_Handler method inside BIoJava PDBFileParser.
					throw ex;
				}
			
			return structureData;
		}
		
		// Check that the filename passed is readable
		private File verifyStructureFile(String filename) throws IllegalArgumentException{
			// Check for valid filename string 
			if( ( filename == null ) || ( filename.length() == 0 ) ){
				throw new IllegalArgumentException("PDB Filename is null or zero length");
			}
			
			// Check that file exists
			File pdbFilename;
			pdbFilename = new File( filename );
			
			// If the file cannot be opened exit
			if( !pdbFilename.exists() ){
				throw new IllegalArgumentException("PDB File " + filename +" does not exist or cannot be found");
			}
			// Return name of pdb file ready to be read
			return pdbFilename;
		}
		
		// This method is supposed to be a work around for the Exception that is thrown within the BioJava PDBReader function.
		// It loads a structure, copies the content to a temporary file, then delete the header and passes back the temporary file to be loaded
		// by the PDBReader, however the temporary file never gets deleted or cannot be deleted because the PDBReader function keeps it open,
		// and when a file is open it cannot be closed or deleted outside of the function that opened it.  Have kept this function in just in case  
		// someone in the future knows how to fix it.
		
		private File trimHeader(String pdbFileName) throws IOException{
			
			File tempPDB = null;
			PrintWriter outStream = null;
			BufferedReader inStream = null;
			
			try{
				// Create a temporary PDB file
				tempPDB = File.createTempFile("tempPDBfile", ".pdb");
			
				// Open the file ready for writing
				outStream = new PrintWriter( new BufferedWriter( new FileWriter( tempPDB ) ) );
				
				// Open the input PDB file
				inStream = new BufferedReader( new FileReader( pdbFileName ) );
				
				// Read the contents, but skips header data, and write it to the output buffer
				String line;
				line = inStream.readLine();
				
				while( line != null ){
					if(!line.contains("HEADER")){
						outStream.println(line + "\n");
					}
					line = inStream.readLine();
				}
				
				// Copy output buffer to temporary file
				outStream.flush();
				
			}catch( IOException ex ){
				throw ex;
			}finally{
				inStream.close();
				outStream.close();
			}
			
			return tempPDB;
		}
}
