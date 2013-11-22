package com.provar.app;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class ProvarJApplet extends JApplet {

	private static final long serialVersionUID = 1L;
	
	Controller controller = new ProvarController(); 
	
	//Called when this applet is loaded into the browser.
	@Override
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
        	
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                	createGUI();
                }
            });
                  			
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
    }
	
	/*
	 * Create the main window 
	 */
	public void createGUI(){
		
		// Create a new view
		AppletGUI gui = new AppletGUI( controller );
		gui.setOpaque(true);
		setContentPane(gui);
		
		setSize( 950, 650 );
		
		// Set the location of the window on startup to the centre of the screen
		Dimension frameSize = getSize();
		Dimension screenSize = getSize();
		
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				
		// Set where on the screen the window will appear
		setLocation((screenSize.width - frameSize.width) / 2,
										(screenSize.height - frameSize.height) / 2);
		
		// Associate window with the controller
		controller.setView( gui );
	}

}
