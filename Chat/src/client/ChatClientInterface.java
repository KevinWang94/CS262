package client;

import java.rmi.*;

import common.Message;

public interface ChatClientInterface extends Remote {
	
	/**
	 * Function called by the server to send a message to this client
	 * Handles the printing of the message on the client side.
	 * 
	 * @param msg the message to be send
	 */
	public void send(Message msg) throws RemoteException;	
	
	/**
	 * Sign out. Can also be called by server to force a sign
	 * out when this account is logged into from another machine.
	 * 
	 * @param message: message to print when signing out
	 */
	public void signOut(String message) throws RemoteException;
}