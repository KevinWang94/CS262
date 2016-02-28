package client;

import java.rmi.*;

import common.Message;

public interface ChatClientInterface extends Remote {
	
	public String getName() throws RemoteException;

	public void send(Message msg) throws RemoteException;	
}
