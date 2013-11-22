package com.provar.app;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProbabilityCalculator {
	
	ArrayList<Double> totalProb;
	/**
	 * 
	 * @return probability for each value in the list of atom, amino acid or averaged amino acid residues
	 */
	public ProbabilityCalculator( List<Double> counts, int noStructs ){
		
		// Calculate averages
		totalProb = new ArrayList<Double>( Collections.nCopies(counts.size(), 0.0));
		
		// Format to 7 decimal places
		DecimalFormat df = new DecimalFormat("0.#######");
		
		// Divide the total number of atoms, or residues by the number of structures, put the values into an array to pass back to caller
		for( int atomIndex = 0; atomIndex < counts.size(); atomIndex++){
			totalProb.set(atomIndex, Double.valueOf( df.format( counts.get(atomIndex) / noStructs ) ) );
		}
	}

	public ArrayList<Double> getProbabilites(){
		return totalProb;
	}
	
	/**
	 * 
	 * @return quartile values for 0.25, 0.50, 0.75 for the atom, amino acid or averaged amino acid residues
	 */
	public double[] getQuantiles(){
		// Code adapted from http://code.hammerpig.com/simple-compute-median-java.html
		
		// Take a copy of the probabilities in order to not modify the originals
		ArrayList<Double> probabilities = (ArrayList<Double>) totalProb.clone();
		
		// Determine the 0.50 median value
		double median = median( probabilities );
		
		// Split the median from above and get the median values for 0.25 and 0.75
		ArrayList<Double> lowerHalf = GetValuesLessThan(probabilities, median, true);
		ArrayList<Double> upperHalf = GetValuesGreaterThan(probabilities, median, true);
		
		// Format the number to the 3rd decimal place
		DecimalFormat df = new DecimalFormat("0.###");
		
		// Store them in an array and return it
		double[] quantiles = new double[3];
		quantiles[0] = Double.valueOf( df.format( median( lowerHalf ) ) );
		quantiles[1] = Double.valueOf( df.format( median ) );
		quantiles[2] = Double.valueOf( df.format( median(upperHalf) ) );
		return quantiles;
	}
	
	private double median( ArrayList<Double> list ){
		// Code used from http://code.hammerpig.com/simple-compute-median-java.html
		
		// Take a copy of the numbers
		ArrayList<Double> numberList = list;
		// Sort the list of number from small to large
		Collections.sort(numberList);
		
		// Does the set divide by 2 exactly, if not 
		if( numberList.size() % 2 == 1 ){
			return numberList.get( (numberList.size()+1)/2-1);
		}else{
			double upper = numberList.get(numberList.size() / 2-1);
			double lower = numberList.get(numberList.size() / 2);
			
			return (lower + upper) / 2.0;
		}
	}
	
	private ArrayList<Double> GetValuesGreaterThan(ArrayList<Double> values, double limit, boolean orEqualTo){
		// Code used from http://code.hammerpig.com/simple-compute-median-java.html
		ArrayList<Double> modValues = new ArrayList<Double>();
		
		// Filter the values which are greater than the mid point
		for(double value : values){
			if( value > limit || (value == limit && orEqualTo) ){
				modValues.add(value);
			}
		}
		
		return modValues;
	}
	
	private ArrayList<Double> GetValuesLessThan(ArrayList<Double> values, double limit, boolean orEqualTo){
		// Code used from http://code.hammerpig.com/simple-compute-median-java.html
		ArrayList<Double> modValues = new ArrayList<Double>();
		
		// Filter the values which fall below the mid point
		for(double value : values){
			if( value < limit || (value == limit && orEqualTo) ){
				modValues.add(value);
			}
		}
		
		return modValues;
	}
}
