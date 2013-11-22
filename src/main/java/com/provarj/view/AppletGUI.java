package com.provar.app;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.*;

public class AppletGUI extends JPanel implements ActionListener, FocusListener, Observer{

	private static final long serialVersionUID = 1L;

	private JFrame provarWin;
	
	private JPanel splitPanel;
	private JPanel topLeftPanel;
	private JPanel bottomLeftPanel;
	private JTextArea textLog;
	
	private JButton processButton;
	
	private JMenuItem menuItemExit;
	private JTextField runName;				// Run ID text field
	private JTextField baseDir;				// Root directory for run output
	private JTextField refPdbName;			// Name of PDB file that is the representative structure
	private JTextField refStructDir;		// Reference structure text field
	private JButton addButton;				// Add button for adding pocket programs
	private JButton removeButton;			// Remove button for removing pocket programs from the list
	private JScrollPane scrollPane;
	private JButton loadConfigButton;
	private JButton saveConfigButton;
	
	private JFileChooser baseDirSelector;	// File chooser to select the output directory for all data
	private JFileChooser refPdbNameSelector;
	private JFileChooser refStructSelector;
	private JFileChooser configFileSelector;
	private JFileChooser configSaveDirSelector;
	
	private File chosenBaseDir;				// User selected root path for output directory
	private File chosenRefPdb;				// 
	private File chosenRefStructDir;
	private String chosenConfigFile;
	
	private JLabel currentConfigFile;
	
	PocketDialog pocket;
	
	private JList<PocketProgram> pocketList;
	private DefaultListModel<PocketProgram> pocketListModel;
	
	Controller provarController;
	
	boolean isBaseDirFocus = true;
	boolean isRefPDBFocus = true;
	boolean isRefStructFocus = true;
	
	private ArrayList<String> config = new ArrayList<String>();
	
	public AppletGUI(Controller controller ){
	
		this.provarController = controller;
		
		// Top-level window objects
		
		splitPanel = new JPanel();						// Split left hand column in to 2 rows
		topLeftPanel = new JPanel();					//  
		bottomLeftPanel = new JPanel();					//
		textLog = new JTextArea(5, 100);				// Text area for log output right hand column
		
		runName = new JTextField();
		baseDir = new JTextField();
		addButton = new JButton("Add");
		removeButton = new JButton("Remove");
		processButton = new JButton("Process");
		refPdbName = new JTextField();
		refStructDir = new JTextField();
		scrollPane = new JScrollPane(textLog);
		loadConfigButton = new JButton("Load configuration");
		saveConfigButton = new JButton("Save configuration");
		
		baseDirSelector = new JFileChooser
				( System.getProperty("user.home") );		// Directory selection Dialog box for root of output
		refPdbNameSelector = new JFileChooser();
		refStructSelector = new JFileChooser();
		configFileSelector = new JFileChooser();
		configSaveDirSelector = new JFileChooser();
		
		currentConfigFile = new JLabel("Using configuration file : None");
		
		// Pocket list box
		pocketListModel = new DefaultListModel<PocketProgram>();
		pocketList = new JList<PocketProgram>(pocketListModel);
		pocketList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pocketList.setLayoutOrientation(JList.VERTICAL_WRAP);
		pocketList.setVisibleRowCount(-1);
		
		JScrollPane pocketPane = new JScrollPane(pocketList);
		pocketPane.setPreferredSize(new Dimension(400, 150));
		
		// Top-level window item parameters
		
		// Base Content pane simple two column pane
		setLayout(new GridLayout(0,2));
		
		// Split panel for left hand side top level controls, 2 rows
		splitPanel.setLayout(new GridLayout(2,0));
		//splitPanel.setBorder(new BorderFactory.)
		
		// Text log panel on right hand side
		textLog.setBorder(BorderFactory.createTitledBorder("Output Log"));
		
		// Top left panel with rid layout
		topLeftPanel.setLayout(new GridLayout(7, 2, 10, 10));
		topLeftPanel.setBorder(BorderFactory.createTitledBorder("General"));
		
		// Bottom left panel with grid layout
		//bottomLeftPanel.setLayout(new GridLayout(0,2, 10, 10));
		bottomLeftPanel.setBorder(BorderFactory.createTitledBorder("Pocket Programs"));
		
		// Top level menu items
		
		// Text area for log
		textLog.setEditable(false);
		
		runName.setToolTipText("Enter a name to identify the run");
		
		// Put text pane inside a scroll pane
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// The root output directory can have some defaults, set to the text box to user home directory
		baseDir.setText(System.getProperty("user.home") + "\\ProvarOut");
		// Store the user home directory
		chosenBaseDir = new File(System.getProperty("user.home") + "\\ProvarOut");
		// Initially set to nothing
		chosenConfigFile = "";
		
		// Build the window
		
		// Associate the content pane with the window
		add(splitPanel);					// Add split panel to left hand side
		add(scrollPane);					// Add text log to right hand side
		splitPanel.add(topLeftPanel);		// Split the left panel into two, one top 
		splitPanel.add(bottomLeftPanel);	// one bottom
		
		topLeftPanel.add( new JLabel("Run ID : ", JLabel.LEFT ));
		topLeftPanel.add( runName );
		topLeftPanel.add( new JLabel( "Output directory : ", JLabel.LEFT ));
		topLeftPanel.add( baseDir );
		topLeftPanel.add( new JLabel( "Reference PDB structure : " ));
		topLeftPanel.add( refPdbName );
		topLeftPanel.add( new JLabel( "Reference structure directory : " ));
		topLeftPanel.add( refStructDir );
		topLeftPanel.add( loadConfigButton );
		topLeftPanel.add( saveConfigButton );
		topLeftPanel.add( currentConfigFile ); 
		
		
		bottomLeftPanel.add(pocketPane);
		bottomLeftPanel.add(processButton);
		bottomLeftPanel.add(addButton);
		bottomLeftPanel.add(removeButton);
		
		// Associate action items with events 
		
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		processButton.addActionListener(this);
		loadConfigButton.addActionListener(this);
		saveConfigButton.addActionListener(this);
		
		// Associate focus item with events
		baseDir.addFocusListener(this);
		refPdbName.addFocusListener(this);
		refStructDir.addFocusListener(this);
		//provarWin.pack();
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Event handler for all top level actions
		if( e.getSource() == menuItemExit ){
			System.exit(0);
		}else if(e.getSource() == addButton){
			pocket = new PocketDialog(this);
		}else if(e.getSource() == removeButton){
			removePocketProgram( (PocketProgram)pocketList.getSelectedValue() );
		}else if(e.getSource() == processButton){
		
			provarController.runProvar();
			//addButton.setEnabled(false);
			//removeButton.setEnabled(false);
			
		}else if(e.getSource() == loadConfigButton){
			//  Shows open file dialog box but only allow files to be chosen and not directories 
			configFileSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int status = configFileSelector.showOpenDialog(provarWin);
			
			// If a file is chosen proceed to load in the contents
			if(status == JFileChooser.APPROVE_OPTION){
				
				// Get name of file to load
				chosenConfigFile = configFileSelector.getSelectedFile().toString();
				
				// Attempt to load the file and get the data in a list
				config = (ArrayList<String>) provarController.loadConfigFile( chosenConfigFile );
				
				// Set the configuration in the panel according to the list, each config element has a set place at the start of the list.
				runName.setText( config.get(0) );
				baseDir.setText( config.get(1) );
				refPdbName.setText( config.get(2) );
				refStructDir.setText( config.get(3) );
				
				// Store the configuration options 
				chosenBaseDir = new File( config.get(1) );
				chosenRefPdb = new File (config.get(2) );
				chosenRefStructDir = new File( config.get(3) );
				
				// Iterator to iterate over list for pocket program configuration settings
				Iterator<String> it = config.iterator();
				
				while( it.hasNext() ){
					// Get an entry
					String entry = it.next();
					
					// Check to see if it is the name of a pocket program 
					if(entry.contains("POCKETNAME") ){
						// Store the name of the program 
						String progName = entry.substring(11);
						
						// Move to the next line and get the type i.e. conf, hom, etc.
						String progTypeEntry = it.next();
						
						// Move one again to get the directory for the pocket files
						String progDirEntry = it.next();
						
						// Add the pocket program
						addPocketProgram( PocketProgramFactory.CreatePocketProgram( progName, new File(progDirEntry), progTypeEntry ) );
						
						// Up the GUI log and label that shows which configuration file is being used
						currentConfigFile.setText("Using configuration file : " + chosenConfigFile);
						
					}
				}
				
			}
		}else if(e.getSource() == saveConfigButton){
			
			configSaveDirSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			// Show save file dialog box
			int status = configSaveDirSelector.showSaveDialog(provarWin);
			
			// If a filename was entered then proceed to save the file
			if(status == JFileChooser.APPROVE_OPTION){
				provarController.saveConfigFile( 
						runName.getText(), 
						chosenBaseDir, 
						chosenRefPdb, 
						chosenRefStructDir, 
						Pattern.compile("([0-9]*).pdb"),
						configSaveDirSelector.getSelectedFile() );
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		// Track text area boxes for focus events
		if( e.getSource() == baseDir ){
			isBaseDirFocus = true;
		}else if( e.getSource() == refPdbName ){
			isRefPDBFocus = true;
		}else if( e.getSource() == refStructDir ){
			isRefStructFocus = true;
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		
		// User clicked on one of the text fields for one of the directories, 
		// open the directory/file selector to let them choose the directory/file  
		if( e.getSource() == baseDir ){
			if( isBaseDirFocus == true){
				baseDirSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = baseDirSelector.showOpenDialog(provarWin);
				
				// User has chosen the directory, change the text and store the selected directory 
				if(status == JFileChooser.APPROVE_OPTION){
					chosenBaseDir = baseDirSelector.getSelectedFile();
					baseDir.setText( chosenBaseDir.getPath().toString() );
				}
				isBaseDirFocus = false;
			}
		}else if( e.getSource() == refPdbName ){
			if( isRefPDBFocus == true ){
				refPdbNameSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int status = refPdbNameSelector.showOpenDialog(provarWin);
				
				// User has chosen the file, change the text and store the selected file
				if(status == JFileChooser.APPROVE_OPTION){
					chosenRefPdb = refPdbNameSelector.getSelectedFile();
					refPdbName.setText( chosenRefPdb.toString() );
				}
				isRefPDBFocus = false;
			}
		}else if( e.getSource() == refStructDir ){
			if( isRefStructFocus == true ){
				refStructSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = refStructSelector.showOpenDialog(provarWin);
				
				// User has chosen the directory, change the text and store the selected directory 
				if(status == JFileChooser.APPROVE_OPTION){
					chosenRefStructDir = refStructSelector.getSelectedFile();
					refStructDir.setText( chosenRefStructDir.getPath().toString() );
				}
				isRefStructFocus = false;
			}
		}
		
	}
	
	/**
	 * 
	 * @return the name of the run id
	 */
	public String getRunID(){
		return runName.getText();
	}
	
	/**
	 * 
	 * @return the directory that will hold all other directories created for the pocket program output
	 */
	public File getBaseDir(){
		return chosenBaseDir;
	}
	
	/**
	 * 
	 * @return the filename of the PDB structure that the output strcutures will be based on
	 */
	public File getPdbRefName(){
		return chosenRefPdb;
	}
	
	/**
	 * 
	 * @return the die
	 */
	public File getRefStructDir(){
		return chosenRefStructDir;
	}
	
	public JFrame getFrame(){
		return this.provarWin;
	}
	
	/**
	 * 
	 * @param pocket pocket program to add from the GUI list
	 */
	public void addPocketProgram( PocketProgram pocket ){
		// Add pocket program so that it appears in the list of pocket data to process
		pocketListModel.addElement( pocket );
		provarController.addPocketProgram( (PocketProgram)pocket );
	}
	
	/**
	 * 
	 * @param pocket pocket program to remove from the GUI list
	 */
	public void removePocketProgram( PocketProgram pocket ){
		// Remove pocket program so that it disappears in the list of pocket data to process
		pocketListModel.removeElement( pocket );
		provarController.removePocketProgram( pocket );
	}

	@Override
	public void textMsg(final String msg) {
		// To avoid GUI hanging from events sent from other threads
		// create a new runnable object and push it on to the EDT
		try{
			SwingUtilities.invokeAndWait(new Runnable(){
				@Override
				public void run(){
					textLog.append(msg);
				}
			});
		}catch( InterruptedException ex){
			ex.printStackTrace();
		}catch(InvocationTargetException ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param msg message to display to the user through the GUI log output window
	 */
	public void updateLogMsg( String msg ){
		textLog.append(msg);
	}
	
}
