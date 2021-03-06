package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import client.ChatClientInterface;
import common.*;

public class ChatServer implements ChatServerInterface {

	/**
	 * Map from username to account objects.
	 */
	private Map<String, Account> users;
	
	/**
	 * Map from session ID to username.
	 */
	private Map<Integer, String> sessionIDs;
	
	/**
	 * Map from group name to group objects.
	 */
	private Map<String, Group> groups;
	
	/**
	 * Map from username to hosting server.
	 */
	private Map<String, String> hosts;
	
	/**
	 * Messages that have yet to be delivered
	 */
	private Map<String, List<Message>> undelivered;

	/**
	 * Basic constructor - initialize all maps to empty
	 */
    public ChatServer() {
    	this.users = new HashMap<String, Account>();
    	this.sessionIDs = new HashMap<Integer, String>();
    	this.groups = new HashMap<String, Group>();
    	this.undelivered = new HashMap<String, List<Message>>();
    	this.hosts = new HashMap<String, String>();
    }

    /**
     * Validate a session given a sessionID
     * 
     * @param sender's sessionID
     * @throws FailException
     */
	public void validateSession(int sender) throws FailException {
		if (!sessionIDs.containsKey(sender)) {
			throw new FailException("Invalid session ID");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#createAccount(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int createAccount(String username,
			String password, String host) {
		if (users.containsKey(username)) {
			return -1;
		}
		Account newAcct = new Account(username, password);
		this.users.put(username, newAcct);
		this.hosts.put(username, host);
		return signIn(username, password, host);
	}

	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#signIn(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int signIn(String username, String password, String host) {
		Account acct = users.get(username);
		if (acct == null || !acct.password.equals(password)) {
			return -1;
		}
		
		int sessionID;
		do {
			sessionID = (int) (Math.random() * Integer.MAX_VALUE);
		} while (sessionIDs.containsKey(sessionID));

		// Register this SessionID
		if (this.hosts.containsKey(username)) {
			Registry registry;
			try {
				registry = LocateRegistry.getRegistry(this.hosts.get(username));
				ChatClientInterface clientStub = (ChatClientInterface) registry.lookup(username);
				clientStub.signOut("This account has signed in on another machine.");	
			} catch (RemoteException e) {
				System.out.println("Error signing out duplicate account, problems may ensue");
			} catch (NotBoundException e) {
			}
		}
		
		this.sessionIDs.put(sessionID, username);
		this.hosts.put(username, host);

		return sessionID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#deleteAccount(int)
	 */
	public void deleteAccount(int sender) throws FailException {
		validateSession(sender);
		String username = this.sessionIDs.get(sender);
		this.sessionIDs.remove(sender);
		this.users.remove(username);
		this.undelivered.remove(username);
	}
	
	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#listAccounts(int)
	 */
	public List<String> listAccounts(int sender) throws FailException {
		validateSession(sender);
		return new ArrayList<String>(users.keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#listAccounts(int, java.lang.String)
	 */
	public List<String> listAccounts(int sender, String pattern) throws FailException {
		validateSession(sender);
		ArrayList<String> accts = new ArrayList<String>();
		for(String a : users.keySet()) {
			if(a.matches(pattern)) {
				accts.add(a);
			}
		}
		return accts;
	}

	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#newGroup(int, java.lang.String)
	 */
	public void newGroup(int sender, String gname) throws FailException {
		validateSession(sender);
		Group g = new Group(gname);
		this.groups.put(gname, g);
	}

	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#addMember(int, java.lang.String, java.lang.String)
	 */
	public void addMember(int sender, String gname, String user) throws ServerException, FailException {
		validateSession(sender);
		if(this.groups.containsKey(gname)) {
			if(this.users.containsKey(user)) {
				this.groups.get(gname).addMember(user);
			} else {
				throw new ServerException("User does not exist.");
			}
		} else {
			throw new ServerException("Group does not exist.");
		}

	}

	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#sendMessage(int, common.Message, java.lang.String)
	 */
	public void sendMessage(int sender, Message m, String user) throws RemoteException {
		validateSession(sender);
		if (sessionIDs.get(sender).equals(user)) {
			throw new ServerException("Sending message to self.");
		}
		if (users.get(user) == null) {
			throw new ServerException("User does not exist.");
		}

		String host = hosts.get(user);
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			ChatClientInterface clientStub = (ChatClientInterface) registry.lookup(user);
			clientStub.send(m);
		} catch (RemoteException e) {
			System.out.println("Remote exception trying to send message to: " + user);
			if(this.undelivered.get(user) != null) {
				this.undelivered.get(user).add(m);
			} else {
				List<Message> messages = new ArrayList<Message>();
				messages.add(m);
				this.undelivered.put(user, messages);
			}
		} catch (NotBoundException e) {
			if(this.undelivered.get(user) != null) {
				List<Message> messages = new ArrayList<Message>();
				messages.add(m);
				this.undelivered.put(user, messages);
			} else {
				this.undelivered.get(user).add(m);
			}	
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#sendGroupMessage(int, common.Message, java.lang.String)
	 */
	public void sendGroupMessage(int sender, Message m, String gname) throws RemoteException {
		validateSession(sender);
		Group g = this.groups.get(gname);
		if(g != null) {
			for(String user : g.members) {
				if (!user.equals(sessionIDs.get(sender)))
					sendMessage(sender, m, user);
			}
		} else {
			throw new ServerException("Group does not exist.");
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#getUndelivered(int)
	 */
	public List<Message> getUndelivered(int sessionID) throws FailException {
		validateSession(sessionID);
		String username = sessionIDs.get(sessionID);
		if (this.undelivered.containsKey(username)) {
			List<Message> undelivered = this.undelivered.get(username);
			this.undelivered.remove(username);
			return undelivered;
		} else {
			return null;
		}
	}


	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#listGroups(int)
	 */
	public List<String> listGroups(int sender) throws FailException {
		validateSession(sender);
		return new ArrayList<String>(groups.keySet());
	}

	/*
	 * (non-Javadoc)
	 * @see server.ChatServerInterface#listGroups(int, java.lang.String)
	 */
	public List<String> listGroups(int sender, String pattern) throws FailException {
		validateSession(sender);
		ArrayList<String> groups = new ArrayList<String>();
		for(String g : this.groups.keySet()) {
			if(g.matches(pattern)) {
				groups.add(g);
			}
		}
		return groups;
	}
		
	/**
	 * Main method. Exports the stub, binding it in the registry.
	 * @param args
	 */
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
}
