package com.provar.app;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JApplet;
import javax.swing.JFrame;

/**
 * @author Sam Coulson 
 */

public class ProvarJ {

	// Provar version number
	static public final String PROVAR_VERSION = "4.84";
	
	public static void main(String[] args) throws Exception {
		
		// Create the main application
		JApplet applet = new ProvarJApplet();
		
		// Initiate the main application
		applet.init();
		
		// Create a window for the application
		final JFrame frame = new JFrame("ProvarJ");
		
		// Set the application window to use the components inside the applet 
		frame.setContentPane( applet.getRootPane() );
		
		// Set the size
		frame.setSize( 950, 650 );
		
		// Set the location of the window on startup to the centre of the screen
		Dimension frameSize = frame.getSize();
		Dimension screenSize = frame.getSize();
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				
		// Set where on the screen the window will appear
		frame.setLocation((screenSize.width - frameSize.width) / 2,
										(screenSize.height - frameSize.height) / 2);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Show the windows
		frame.setVisible(true);
		
		// Start the applet running
		applet.start();
	}

}
