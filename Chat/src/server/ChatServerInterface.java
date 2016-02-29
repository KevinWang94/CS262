package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.ChatClientInterface;
import common.*;

public interface ChatServerInterface extends Remote {

	public String sayHello() throws RemoteException;
	public int createAccount(ChatClientInterface client, String username, String password) throws RemoteException;
	public void deleteAccount(int sender) throws RemoteException;
	public int signIn(String username, String password) throws RemoteException;
	public List<String> listAccounts() throws RemoteException;
	public List<String> listAccounts(String pattern) throws RemoteException;
	public List<String> listActiveAccounts() throws RemoteException;
	public List<String> listActiveAccounts(String pattern) throws RemoteException;
	public void newGroup(String name) throws RemoteException;
	public void addMember(String gname, String user) throws RemoteException;
	public List<String> listGroups() throws RemoteException;
	public List<String> listGroups(String pattern) throws RemoteException;
	public void sendGroupMessage(int sender, Message m, String gname) throws RemoteException;
	public void sendMessage(int sender, Message m, String user) throws RemoteException;
	public List<Message> getUndelivered(int sender) throws RemoteException;
}
