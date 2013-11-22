package com.provar.app;

public interface Observable {
	// Methods to add and update observers/subscribers
	/**
	 * 
	 * @param observer the name of the object that will receive messages
	 */
	public void addObserver( Observer observer);
	
	/**
	 * 
	 * @param msg the message to send to all observers
	 */
	public void updateObservers( String msg);
}
