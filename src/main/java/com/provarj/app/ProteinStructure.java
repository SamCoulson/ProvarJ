package com.provar.app;

import java.util.List;
// Interface for a protein structure, this may be used to implement PDB or perhaps mmcif file readers
public interface ProteinStructure {

	/**
	 * 
	 * @return total number of atoms in the structure
	 */
	public int getAtomCount();
	
	/**
	 * 
	 * @return list of coordinates for all the atoms in the structure
	 */
	public List<double[]> getAtomXYZ();
	
	/**
	 * 
	 * @return list with all the atom numbers in series
	 */
	public List<Integer> getAtomSerNos();
	
	/**
	 * 
	 * @return list with all the names for each atom i.e. C, H, O in series 
	 */
	public List<String> getAtomNames();
	
	/**
	 *  
	 * @return list containing the residues number that each atom belongs too
	 */
	public List<Integer> getResidueSeqNo();
	
	/**
	 * 
	 * @return list containing the residues name (3 letter code) for each atom
	 */
	public List<String> getResidueName();
	
	/**
	 * 
	 * @return the ID of the chain for each atom in the structure
	 */
	public List<String> getChainID();
	
	/**
	 * 
	 * @return the element of each atom in the list
	 */
	public List<String> getAtomElement();
	
	/**
	 * 
	 * @return total number of residues in the structure
	 */
	public int getMaxResidue();
}