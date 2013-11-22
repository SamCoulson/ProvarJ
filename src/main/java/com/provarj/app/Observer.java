package com.provar.app;

//Interface for classes that receive updates from observable/broadcaster
public interface Observer {
	/**
	 * 
	 * @param msg the message that the observer will receive
	 */
	public void textMsg(String msg);
}
