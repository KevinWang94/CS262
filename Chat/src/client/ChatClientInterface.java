package client;

import java.rmi.*;

import common.Message;

public interface ChatClientInterface extends Remote {
	
	public void send(Message msg) throws RemoteException;	
	
	public void signOut(String message) throws RemoteException;
}