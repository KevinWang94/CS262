package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;

import client.ChatClientInterface;
import common.*;

public class ChatServer implements ChatServerInterface {

	private Map<String, Account> users;
	private Map<Integer, String> sessionIDs;
	private Map<String, Group> groups;
//	private Map<String, ChatClientInterface> activeUsers;
	private Map<String, List<Message>> undelivered;

	/**
	 * Basic constructor - initialize all maps to empty
	 */
    public ChatServer() {
    	this.users = new HashMap<String, Account>();
    	this.sessionIDs = new HashMap<Integer, String>();
    	this.groups = new HashMap<String, Group>();
    	this.undelivered = new HashMap<String, List<Message>>();
    }

    /**
     * Create an account
     * 
     * @param client: the client being used to create this account
     * @param username
     * @param password
     * 
     * @return SessionID integer, -1 if username already exists
     */
	public int createAccount(String username,
			String password) {
		if (users.containsKey(username)) {
			return -1;
		}
		Account newAcct = new Account(username, password);
		this.users.put(username, newAcct);
		return signIn(username, password);
	}

	/**
	 * Sign in to the chat server
	 * 
	 * @param username
	 * @param password
	 * 
	 * @return SessionID integer, -1 if account doesn't exist or password is not correct
	 */
	public int signIn(String username, String password) {
		Account acct = users.get(username);
		if (acct == null || !acct.password.equals(password)) {
			return -1;
		}
		
		int sessionID;
		do {
			sessionID = (int) (Math.random() * Integer.MAX_VALUE);
		} while (sessionIDs.containsKey(sessionID));

		// Register this SessionID
		this.sessionIDs.put(sessionID, username);

		return sessionID;
	}
	
	/**
	 * Delete an account while logged in
	 * 
	 * @param SessionID (this makes sure only you can delete your own account)
	 */
	public void deleteAccount(int sender) {
		String username = this.sessionIDs.get(sender);
		this.sessionIDs.remove(sender);
		this.users.remove(username);
		this.undelivered.remove(username);
		
	}
	
	public List<String> listAccounts() {
		return new ArrayList<String>(users.keySet());
	}

	public List<String> listAccounts(String pattern) {
		ArrayList<String> accts = new ArrayList<String>();
		for(String a : users.keySet()) {
			if(a.matches(pattern)) {
				accts.add(a);
			}
		}
		return accts;
	}


	public void newGroup(String gname) {
		Group g = new Group(gname);
		this.groups.put(gname, g);
	}

	public void addMember(String gname, String user) {
		if (this.groups.containsKey(gname)) {
			// TODO: what if the user doesn't exist?
			this.groups.get(gname).addMember(user);
		} else {

		}

	}

	public void sendMessage(int sender, Message m, String user) {
		try {
			Registry registry = LocateRegistry.getRegistry();
			ChatClientInterface clientStub = (ChatClientInterface) registry.lookup(user);
			clientStub.send(m);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if(this.onlineUsers.contains(user) {
		//
		// } else {
		//
		// }

	}
	
	@Override
	public void sendGroupMessage(int sender, Message m, String gname) throws RemoteException {
		Group g = this.groups.get(gname);
		if(g != null) {
			for(String user : g.members) {
				sendMessage(sender, m, user);
			}
		} else {
			// TODO: exception?
		}
		
	}
	
	/**
	 * Get the undelivered messages for a user
	 * 
	 * @param sessionID ensures that only a particular user can get their undelivered messages
	 * 
	 * @return list of messages
	 */
	public List<Message> getUndelivered(int sessionID) {
		String username = sessionIDs.get(sessionID);
		if (this.undelivered.containsKey(username)) {
			List<Message> undelivered = this.undelivered.get(username);
			this.undelivered.remove(username);
			return undelivered;
		} else {
			return null;
		}
	}


	@Override
	public List<String> listActiveAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listActiveAccounts(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> listGroups() {
		return new ArrayList<String>(groups.keySet());
	}

	public List<String> listGroups(String pattern) {
		ArrayList<String> groups = new ArrayList<String>();
		for(String g : this.groups.keySet()) {
			if(g.matches(pattern)) {
				groups.add(g);
			}
		}
		return groups;
	}
	
	
	public static void main(String args[]) {
		try {
			ChatServer obj = new ChatServer();
			ChatServerInterface stub = (ChatServerInterface) UnicastRemoteObject.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Server", stub);

			System.err.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public String sayHello() {
		// TODO Auto-generated method stub
		return "Hello, world!";
	}
}
