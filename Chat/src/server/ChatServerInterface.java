package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import common.*;

public interface ChatServerInterface extends Remote {
	public int createAccount(String username, String password, String host) throws RemoteException;
	public int signIn(String username, String password, String host) throws RemoteException;
	public void deleteAccount(int sender) throws RemoteException;
	public List<String> listAccounts(int sender) throws RemoteException;
	public List<String> listAccounts(int sender, String pattern) throws RemoteException;
	public void newGroup(int sender, String name) throws RemoteException;
	public void addMember(int sender, String gname, String user) throws RemoteException;
	public List<String> listGroups(int sender) throws RemoteException;
	public List<String> listGroups(int sender, String pattern) throws RemoteException;
	public void sendGroupMessage(int sender, Message m, String gname) throws RemoteException;
	public void sendMessage(int sender, Message m, String user) throws RemoteException;
	public List<Message> getUndelivered(int sender) throws RemoteException;
}
