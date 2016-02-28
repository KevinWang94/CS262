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

    public ChatServer() {
    	users = new HashMap<String, Account>();
    	sessionIDs = new HashMap<Integer, String>();
    }

	public int createAccount(ChatClientInterface client, String username,
			String password) {
		if (users.containsKey(username)) {
			return -1;
		}
		Account newAcct = new Account(username, password);
		users.put(username, newAcct);
		return signIn(username, password);
	}

	@Override
	public void deleteAccount(int sender) {
		// TODO Auto-generated method stub
		
	}


	public int signIn(String username, String password) {
		Account acct = users.get(username);
		if (acct == null || !acct.password.equals(password)) {
			return -1;
		}
		int sessionID;

		do {
			sessionID = (int) (Math.random() * Integer.MAX_VALUE);
		} while (sessionIDs.containsKey(sessionID));

		sessionIDs.put(sessionID, username);

		return sessionID;
	}

	public void deleteAccount(Account account) {

	}

	public List<String> listAccounts() {
		// return new
		// ArrayList<String>(Arrays.asList(users.keySet().toArray()));
		return null;
	}

	public List<String> listAccounts(String pattern) {
		// String key = null;
		// if(key.matches(pattern))
		return null;
	}

	public void newGroup(String gname) {
		Group g = new Group(gname);
		this.groups.put(gname, g);
	}

	public void addMember(String gname, String user) {
		if (this.groups.containsKey(gname)) {
			this.groups.get(gname).addMember(user);
		} else {

		}

	}

	public void sendMessage(int sender, Message m, Group g) {
		for (String user : g.members) {
			sendMessage(sender, m, user);
		}
	}

	public void sendMessage(int sender, Message m, String user) {
		System.out.println("sending message");
		String name = sessionIDs.get(sender);
		System.out.println(name);
		try {
			Registry registry = LocateRegistry.getRegistry();
			ChatClientInterface clientStub = (ChatClientInterface) registry.lookup(name);
			System.out.println("sending message try/catch");
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

	@Override
	public void addMember(Group g, String user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Group> listGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Group> listGroups(String pattern) {
		// TODO Auto-generated method stub
		return null;
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
