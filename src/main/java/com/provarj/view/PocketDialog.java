package com.provar.app;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;

public class PocketDialog extends JDialog implements ActionListener, FocusListener {
	
	private static final long serialVersionUID = 1L;

	// Handle to parent window
	private AppletGUI parent;
	
	// Dialog box for selecting the pocket program data directory
	private final JFileChooser pocketDirSelector;
	private JTextField pocketDir;
	
	// Set size of dialog box
	private Dimension frameSize;
	private Dimension screenSize;
	
	// Add buttons to confirm/cancel
	private JButton applyButton;
	private JButton cancelButton;
	
	// Add a list of pocket program names
	JRadioButtonMenuItem PASS;
	JRadioButtonMenuItem fPocket;
	JRadioButtonMenuItem generic;
	JRadioButtonMenuItem ligsite;
	JRadioButtonMenuItem sitemap;
	JRadioButtonMenuItem gold;
	
	// Create group so that only one may be selected at a time
	private ButtonGroup pocketGroup;
	
	private boolean isPocketDirFocus = false;
	
	File chosenPocketDir;
	
	// Pocket factory to create pocket programs
	PocketProgramFactory pocketFactory;
	
	JRadioButtonMenuItem selectedPocket;
	
	/**
	 * 
	 * @param parent handle to parent window
	 */
	PocketDialog(AppletGUI parent){
		
		this.parent = parent;
		
		// Dialog directory chooser
		pocketDirSelector = new JFileChooser();
		
		frameSize = parent.getSize();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// The dialog box dimensions
		setPreferredSize(new Dimension(400, 200));
		setLocation((screenSize.width - frameSize.width) / 2,
								(screenSize.height - frameSize.height) / 2);
		
		Container pockContainer = getContentPane();
		
		GridLayout layout = new GridLayout(0,2);
		
		pockContainer.setLayout(layout);
		
		// Build panel for menu for pocket programs and buttons
		pocketGroup = new ButtonGroup();
		
		PASS = new JRadioButtonMenuItem("PASS");
		fPocket = new JRadioButtonMenuItem("fPocket");
		ligsite = new JRadioButtonMenuItem("LIGSITE");
		
		pocketDir = new JTextField("");
		applyButton = new JButton("Apply");
		cancelButton = new JButton("Cancel");
		
		// Set a default
		PASS.setSelected(true);
		selectedPocket = PASS;
		
		// Add buttons and labels to the dialog box
		pocketGroup.add(PASS);
		pocketGroup.add(fPocket);
		pocketGroup.add(ligsite);
		
		pockContainer.add( PASS );
		pockContainer.add( fPocket );
		pockContainer.add( ligsite );
		pockContainer.add( new JLabel() );
		pockContainer.add( new JLabel() );
		pockContainer.add( new JLabel() );
		
		pockContainer.add( new JLabel("Pocket directory: ") );
		pockContainer.add( pocketDir );
		
		pockContainer.add(cancelButton);
		pockContainer.add(applyButton);
		
		// Add event listeners
		pocketDir.addFocusListener(this);
		applyButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		PASS.addActionListener(this);
		fPocket.addActionListener(this);
		ligsite.addActionListener(this);
		
		cancelButton.requestFocus();
		
		chosenPocketDir = new File("");
		
		pack();
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Create the pocket program according to what the user has selected from the list of programs
		if( e.getActionCommand().contentEquals("Apply") ){
			{
				if( selectedPocket == PASS ){
					parent.addPocketProgram( PocketProgramFactory.CreatePocketProgram( "PASS", chosenPocketDir, "CONF" ) );
				}else if(selectedPocket == fPocket){
					parent.addPocketProgram( PocketProgramFactory.CreatePocketProgram( "fPocket", chosenPocketDir, "CONF" ) );
				}else if(selectedPocket == ligsite){
					parent.addPocketProgram( PocketProgramFactory.CreatePocketProgram( "Ligsite", chosenPocketDir, "CONF" ) );
				}
				
				dispose();
			}
		}else if( e.getActionCommand().contentEquals("Cancel") ){
			{
				dispose();
			}
		}else if( e.getSource() == PASS ){
			selectedPocket = PASS;
		}else if( e.getSource() == fPocket ){
			selectedPocket =  fPocket;
		}else if( e.getSource() == generic ){
			selectedPocket = generic;
		}else if( e.getSource() == ligsite ){
			selectedPocket = ligsite;
		}
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		// User clicked on the text box
		if( e.getSource() == pocketDir ){
			if( isPocketDirFocus == true ){
				pocketDirSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int status = pocketDirSelector.showOpenDialog(this);
				if(status == JFileChooser.APPROVE_OPTION){
					chosenPocketDir = pocketDirSelector.getSelectedFile();
					pocketDir.setText( chosenPocketDir.toString() );
				}
				isPocketDirFocus = false;
			}
		}
		applyButton.requestFocus();
	}

	@Override
	public void focusLost(FocusEvent e) {
		// 
		if( e.getSource() == pocketDir ){
			isPocketDirFocus = true;
		}
	}

}
