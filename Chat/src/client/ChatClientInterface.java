package client;

import java.rmi.*;

public interface ChatClientInterface {
	public String getName() throws RemoteException;

	public void send(String msg) throws RemoteException;	
}
