package com.provar.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.biojava.bio.structure.Atom;
import org.biojava.bio.structure.AtomImpl;
import org.biojava.bio.structure.Calc;
import org.biojava.bio.structure.StructureException;

public class PocketFinder {

	private ArrayList<Boolean> pocketAminos;
	private ArrayList<Boolean> pocketAtoms;
	private ArrayList<Double> pocketAminoAverage;

	/**
	 * 
	 * @return a list containing true or false values depending on whether or not the residue is pocket lining for each residue in the structure
	 */
	public ArrayList<Boolean> getPocketLiningAminoList() {
		return pocketAminos;
	}

	/**
	 * 
	 * @return a list containing true or false values depending on whether or not the atom is pocket lining for each atoms in the structure
	 */
	public ArrayList<Boolean> getPocketLiningAtomList() {
		return pocketAtoms;
	}

	/**
	 * 
	 * @return a list containing the averaged number of atoms per residue for each residue in the structure
	 */
	public ArrayList<Double> getPocketLiningAminoAverageList() {
		return pocketAminoAverage;
	}

	/**
	 * 
	 * @param refData protein structure
	 * @param pocketData pocket predictions for the protein structure
	 * @param pocketRadius distance from the protein surface atom  
	 * @param isDirect specifies is the pocket lining atoms have already been determined
	 * @throws StructureException
	 */
	public void FindPockets(ProteinStructure refData, ProteinStructure pocketData, double pocketRadius, boolean isDirect)throws StructureException {
		
		// Load in all data locally to determine how many times to do the loop and save calling
		// get methods on every loop
		
		double radius = pocketRadius;
		
		// Retrieve list of atom coordinates for both reference structure and pocket structure
		List<double[]> refAtomXYZ = refData.getAtomXYZ();
		List<double[]> pocketAtomXYZ = pocketData.getAtomXYZ();
		
		// Find number of atoms in the reference structure
		int noOfAtoms = refAtomXYZ.size();
		
		// Retrieve array of residue sequence numbers for each atom, this will be the same size as noOfAtoms
		List<Integer> residueNo = refData.getResidueSeqNo();
		
		// Get the total number of residues in the structure
		int maxRes = refData.getMaxResidue() + 1;
		
		// Initialise arrays to false each amino and atom will be set to true or false
		// depending on weather or not they are pocket lining 
		pocketAminos = new ArrayList<Boolean>( Collections.nCopies( maxRes, false ) ); 
		pocketAtoms = new ArrayList<Boolean>( Collections.nCopies( noOfAtoms, false) );
		pocketAminoAverage = new ArrayList<Double>( Collections.nCopies( maxRes, 0.0 ) ); 
		
		// Res tally for each residue in the sequence keep a tally of how many times it is flagged as
		// pocket lining.
		List<Double> resTally = new ArrayList<Double>( Collections.nCopies( maxRes, 0.0 ) );
		
		// Retrieve atom serial numbers
		List<Integer> atomSerNo = refData.getAtomSerNos();
		
		// Handle for atom id numbers
		List<Integer> pocketAtomSorted;
		
		// Check for direct pocket predictions
		if( isDirect ){
			
			// Get the atoms serial numbers for the pockets
			pocketAtomSorted = pocketData.getAtomSerNos();
			
			// Sort the serial numbers into ascending order
			Collections.sort( pocketAtomSorted );
			
		}else{
			// Proceed with unordered list of atom serial numbers
			pocketAtomSorted = pocketData.getAtomSerNos();
		}
		
		// Flag to signal that atom is pocket lining
		boolean isPocketLining;
		
		for(int i = 0; i < noOfAtoms; i++){
			
			// Get the residue number that the current atom belongs to
			int resNo = residueNo.get(i);
			
			// Reset the flag
			isPocketLining = false;
			
			// Catch oddly names resides, some start with 500
			if(resNo > 0 && resNo < maxRes ){
				
				// Some structures may homologues skip if the residues has been counted already
				if( /*( pocketAminos.get(resNo) == true ) && */ pocketAtoms.get(i) == true  ){
					// Flagged as already scanned skip, used for multiple chains
				}else{
					if( isDirect ){
						// Direct mode - Pocket program directly outputs pocket atoms or residues
						isPocketLining = pocketLiningDirect(pocketAtomSorted, atomSerNo.get(i) );
					}else{
						// Calculate pocket-lining based on nearest atoms/residues to protein structure atom coordinates
						
						// Apply the search space limits based on pocket radius
						List<double[]> searchSpace = ApplyLimits(refAtomXYZ.get(i), pocketAtomXYZ, radius);
						
						// Determine whether or not it is pocket lining
						try{
							isPocketLining = PocketLining(refAtomXYZ.get(i), searchSpace, radius);
						}catch(StructureException ex){
							throw ex;
						}
					}
					
					if( isPocketLining ){
						
						// Keep a count of how pocket lining residues and atoms
						if( pocketAminos.get(resNo -1) == false ){
							
							// Minus 1 on resNo to place elements correctly into 0 based array
							pocketAminos.set(resNo -1, true);
						}
						
						// Flag this atom as pocket lining
						pocketAtoms.set(i, true);
						
						// Add one to the tally of atoms that are pocket lining for this particular residue
						resTally.set( resNo -1, ( resTally.get( resNo -1 ) ) + 1 );
					}
				}
			}
		}
		
		// Loop through the residues for amino averages
		for( int i = 0; i < maxRes; i++ ){
			
			// Find the total number of atoms for this reside
			int thisResCount = 0;
			
			// Iterate over the list of atoms residue numbers and count how many atoms there are that that residue
			for(int resCount : residueNo ){
				if( resCount == i+1 ){
					thisResCount++;
				}
			}
			
			// Control for divide by 0 cases
			if( resTally.get(i) > 0.0 ){
				
				// For each residue take the tally count of pocket binding atoms in resTally and divide by total 
				// number of atoms for that particular residue.
				Double resAverage = resTally.get(i) / thisResCount;
				
				// Enter the residue average for this residue in the list of averages
				pocketAminoAverage.set(i, resAverage);
			}else{
				
				// If there where no pocket lining atoms, set the value to 0.0, each entry must have a value
				pocketAminoAverage.set(i, 0.0);
			}
		}
	}

	private List<double[]> ApplyLimits(double[] originAtomCoords, List<double[]> pocketCoords, double pocketRadius) {
		
		List<double[]> searchSpaceVectors = new ArrayList<double[]>(); 
		
		// Ensure that the edges of the search space included
		double radius = pocketRadius*1.01;
	
		// Search for atoms that falls within the radius along the XYZ planes in both positive and negative direction
		// If they do add them to a list of potential pocket lining atoms
		for( double[] XYZcoords : pocketCoords ){
			
			// Calculate distances between the location of the atom on the protein and all the pocket lining atoms
			if(( XYZcoords[0] < ( originAtomCoords[0] + radius ) ) &
				( XYZcoords[1] < ( originAtomCoords[1] + radius ) ) &
				( XYZcoords[2] < ( originAtomCoords[2] + radius ) )) {
				searchSpaceVectors.add(XYZcoords);
			}
			
			if(( XYZcoords[0] > ( originAtomCoords[0] - radius ) ) &
				( XYZcoords[1] > ( originAtomCoords[1] - radius ) ) &
				( XYZcoords[2] > ( originAtomCoords[2] - radius ) )) {
				searchSpaceVectors.add(XYZcoords);
			}
		}
		// Return the list that represents all the atoms that fall within the pocket radius
		return searchSpaceVectors;
	}

	private boolean PocketLining(double[] originAtomCoords, List<double[]> searchSpace, double pocketRadius)throws StructureException {
		
		// Create two atoms to hold the origin atom (on protein) and one for the search space atom
		// These atom objects are needed for the Calc.getDistance method.
		Atom pocketAtom = new AtomImpl();
		Atom proteinAtom = new AtomImpl();
		
		double dist = 0.0;
		
		// Take each pocket atom found to be within the search space radius and calculate the distance between it
		// and the atom on the surface of the protein
		for( double[] atomXYZcoords : searchSpace ){
			
			pocketAtom.setCoords( atomXYZcoords );
			proteinAtom.setCoords( originAtomCoords );
			
			try{
				dist = Calc.getDistance( pocketAtom, proteinAtom );
			}catch(StructureException ex){
				throw ex;
			}
			
			// At the first occurrence of an atom that falls within the pocket radius
			// flag as pocket lining
			if( dist < pocketRadius ){
				return true;
			}
		}
		// No atoms where found to be pocket lining, flag as non-pocket lining
		return false;
	}
	
	private boolean pocketLiningDirect( List<Integer>sortedPocketAtoms, Integer structAtom ){
		
		// Iterate over the list of sorted ( direct ) pocket atoms
		for( Integer pocketAtom : sortedPocketAtoms ){
			
			// If the current atom matches any in the list then flag it as pocket lining
			if( pocketAtom.equals( structAtom ) ){
				return true;
			}
		}
		
		// No matches atom is not pocket lining
		return false;
	}
}