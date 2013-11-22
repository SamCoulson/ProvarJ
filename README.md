ProvarJ is based on Provar (Probability of variation) (Ashford et al, 2012).  Provar provides a method for probabilistic scoring of pocket predictions across large sets of structures related to a protein of interest.  The scoring calculates the overall probability that atoms and residues  are pocket-lining.  Given a suitable set of protein conformations and pocket predictions for a single structure, Provar will output a PDB-format file with the probabilities[1] written into the B-factor fields for each atom or residue.  The probability variation can then be viewed using molecular visualisation software, which can allow (for example) colouring of the molecular surface according to the probability values. 
 
 
ProvarJ, written by Sam Coulson, is a re-implementation of Provar in Java; this version also adds a graphical front end to allow users to define the input files and monitor the progress of processing through a GUI output window.  The original Provar accepts families of homologous proteins alongside a suitable FASTA alignment file; ProvarJ does not currently support this feature, although it may be implemented in future revisions.          
 
 
So far ProvarJ has been tested using ensemble conformations from: 
 
CONCOORD 
 
And output file for pocket predictions from: 
 
PASS 
 
fPocket 
 
Ligsite 
 
 
Provar is written as a set of MATLAB modules that can be freely downloaded at:  http://people.cryst.bbk.ac.uk/~ubcg66a/provar_summary.html 
 
 
ProvarJ is compatible with Java version 1.6 and above and can be compiled with the JDK version 1.6 
 
 
ProvarJ requires the Biojava3 libraries: http://www.biojava.org/wiki/BioJava3_project 
 
So far ProvarJ has only been tested using the Windows Vista operating system. 
 
To compile at command line: 
 
On Windows: 
 
Open cmd 
 
cd /path of provarJ/

mkdir build
 
javac -d build - classpath "/path of biojava3/biojava3-structure/target/*" /src/main/java/com/provar/app/*.java src/main/java/com/provar/view/*.java 
 
To run from windows command line: 
/path of ProvarJ/ 
cd /build 
java -cp . com.provar.app.ProvarJ 
Note: Must use captials on P and J or ProvarJ 
 
 
Publication for the original Provar implementation: 
Ashford, P., Moss, D. S., Alex, A., Yeap, S. K., Povia, A., Nobeli, I., & Williams, M. A. (2012). Visualisation of variable binding pockets on protein surfaces by probabilistic analysis of related structure sets. BMC bioinformatics, 13, 39. doi:10.1186/1471-2105-13-39 

Footnotes:
[1] Probabilities are scaled to the range [0,100] to be in accordance with PDB file format for the “Temperature factor” field in ATOM records.
