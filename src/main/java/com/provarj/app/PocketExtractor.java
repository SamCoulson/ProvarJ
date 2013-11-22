package com.provar.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.biojava.bio.structure.StructureException;

public class PocketExtractor implements Observable{
	
	// Store totals for all data from all processed structures
	private List<Double> aminoTotal;
	private List<Double> atomTotal;
	private List<Double> aminoAvgTotal;
	
	final protected Integer refStructAtomCount;
	final protected Integer noOfRes;
	
	final protected PocketFinder pocketFinder;
	final protected ArrayList<String> refStructs;
	final protected File refPdb;
	
	final double pocketRadius;
	
	boolean isDirect;

	protected ArrayList<Observer> observers;
	
	PocketProgram pocketProg;
	
	final protected StructureSet pocketStructures;
	
	/**
	 * 
	 * @param pocketStructs structure set containing all the pocket prediction data
	 * @param refStructs list of all the reference structures
	 * @param refPDB reference PDB file 
	 * @param isDirect specify if pocket lining atoms have already been predicted
	 * @param pocketRadius specify the distance the algorithm will search from each atom
	 * @param observer associate an observer to catch messages
	 * @throws IOException
	 */
	PocketExtractor(StructureSet pocketStructs, 
			ArrayList<String> refStructs, 
			File refPDB, boolean isDirect, 
			double pocketRadius, 
			Observer observer) throws IOException{
		
		// Store a copy of the parameters passed to the pocket extractor
		this.refStructs = refStructs;
		this.refPdb = refPDB;
		this.pocketStructures = pocketStructs;
		this.isDirect = isDirect;
		this.pocketRadius = pocketRadius;
		
		// Create a pocket finder object, this will work out which atoms and residues are pocket lining
		pocketFinder = new PocketFinder();
		
		// Retrieve the PDB data in the reference structure
		ProteinStructure refStruct = null;
		try{
			refStruct = new PDBStructure( refPdb.toString() );
		}catch(IOException ex){
			throw ex;
		}
		
		// Retrieve the number of atoms in the reference structure
		refStructAtomCount = refStruct.getAtomCount();
		
		// Retrieve the residue with the highest number in the reference structure
		noOfRes = refStruct.getMaxResidue();
		
		// Assume same number of atoms and residues in each structure and but not number of structures
		// Initialise the totals array with the number of atoms in the first structure
		
		// Pre-allocate space based on first reference structure, check that subsequent structures have the same number of atoms
		atomTotal = new ArrayList<Double>( Collections.nCopies( refStructAtomCount, 0.0 ) );
		
		aminoTotal = new ArrayList<Double>( Collections.nCopies( noOfRes, 0.0) );
	
		aminoAvgTotal = new ArrayList<Double>( Collections.nCopies( noOfRes, 0.0) );
		
		observers = new ArrayList<Observer>();
		
		if( observer != null ){
			addObserver(observer);
		}
		try{
			extractPocketData();
		}catch(IOException ex){
			throw ex;
		}
	}
	
	private void extractPocketData() throws IOException{
		
		// For each file in the structure directory attempt to match up the file to the corresponding pocket structure directory.
		for(int i = 0; i < refStructs.size(); i++){
			
			// Arrays to hold which atoms and residues are pocket lining and the average atoms per residue for this structure
			List<Boolean> aminoCount;
			List<Boolean> atomCount;
			List<Double> aminoAverage;
			
			// Structure to hold the PDB data 
			ProteinStructure pocketStruct;
			ProteinStructure pdbStruct = null;
			
			// Load in a PDB structure for the reference and pocket predictions
			try{
				pdbStruct = new PDBStructure( refStructs.get(i) );
				
				if( !pocketStructures.isMultiPart() ){
					pocketStruct = new PDBStructure( pocketStructures.getStructure(i) );
				}else{
					pocketStruct = new PDBStructure( pocketStructures.getStructureMultiFiles(i) );
				}
			}catch(IOException ex){
				throw ex;
			}
			
			// Lookout for reference structures that have an atom count that deviates from the original structure
			if( !refStructAtomCount.equals( pdbStruct.getAtomCount() ) ){
				updateObservers("Structure " + refStructs.get(i) + " has a different numbers of atoms to the reference structure" );
			}else{
				updateObservers("Reading structure " + refStructs.get(i) );
				updateObservers("Reading pocket structure " + pocketStructures.getStructure(i) );
			}
			
			updateObservers("Testing pocket-lining atoms and residues...");
			
			// Find pocket data for this structure
			try{
				pocketFinder.FindPockets(pdbStruct, pocketStruct, pocketRadius, isDirect);
			}catch(StructureException ex){
				updateObservers("Unable to process structure: " + ex.getMessage() );
			}
			updateObservers("OK");
			updateObservers("=======================================================");
			
			// Retrieve for this structure which atoms are pocket lining, which residues are pocket lining, and
			// the average atoms per residue for each atom.
			aminoCount = pocketFinder.getPocketLiningAminoList();
			
			atomCount = pocketFinder.getPocketLiningAtomList();
			
			aminoAverage = pocketFinder.getPocketLiningAminoAverageList();
			
			// Counts per atom, amino and amino average.
			
			// Total up how many times each atoms is detected as being pocket lining
			for( int atomIndex = 0; atomIndex < atomCount.size(); atomIndex++){
				if( atomCount.get( atomIndex ) == true ){ 
					atomTotal.set( atomIndex, atomTotal.get( atomIndex ) + 1 );
				}
			}
			
			// Total up how many times each residue is detected as being pocket lining
			for( int aminoIndex = 0; aminoIndex < aminoCount.size(); aminoIndex++ ){
				if( aminoCount.get( aminoIndex ) == true ){
					aminoTotal.set( aminoIndex, aminoTotal.get( aminoIndex ) + 1 );
				}	
			}
			
			// Total up the (atoms per residue ) averages for each residue 
			for( int aminoAvgIndex = 0; aminoAvgIndex < aminoAverage.size() - 1; aminoAvgIndex++ ){
				aminoAvgTotal.set(aminoAvgIndex, aminoAvgTotal.get(aminoAvgIndex) + aminoAverage.get(aminoAvgIndex));
			}
		}
	}
	 /**
	  * 
	  * @return a list containing the number of times each atom in the structure is pocket lining
	  */
	public List<Double> getAtomTotal(){
		return atomTotal;
	}
	
	/**
	 * 
	 * @return a list containing the number of times each residue in the structure is pocket lining
	 */
	public List<Double> getAminoTotal(){
		return aminoTotal;
	}
	
	/**
	 * 
	 * @return a list containing the average number of pocket lining atoms per residue each residue in the structure
	 */
	public List<Double> getAminoAvgTotal(){
		return aminoAvgTotal;
	}
	
	@Override
	public void addObserver(Observer obv){
		observers.add(obv);
	}
	
	@Override
	public void updateObservers( String msg ){
		if( observers != null ){
			for(Observer obv : observers){
				obv.textMsg(msg);
			}
		}
	}
		
}
