package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import common.*;

public interface ChatServerInterface extends Remote {
    /**
     * Creates an account given 
     * 
     * @param username: the username desired
     * @param password: the password desired
     * @param host: the host of the client
     * 
     * @return SessionID integer for new signin, -1 if username already exists
     */
	public int createAccount(String username, String password, String host) throws RemoteException;
	
	/**
	 * Sign in to the chat server
	 * 
	 * @param username: supplied username
	 * @param password: supplied password
	 * 
	 * @return SessionID integer for new session, -1 if account doesn't exist 
	 * or password is not correct
	 */
	public int signIn(String username, String password, String host) throws RemoteException;
	
	/**
	 * Delete an account while logged in
	 * 
	 * @param SessionID (this makes sure only you can delete your own account)
	 * @throws FailException if session ID is not valid
	 */
	public void deleteAccount(int sender) throws RemoteException;
	
	/**
	 * Lists all account names 
	 * 
	 * @param sender: the sessionID of the requester
	 * 
	 * @return the list of account names (never null)
	 * 
	 * @throws FailException if sessionID invalid
	 */
	public List<String> listAccounts(int sender) throws RemoteException;
	
	/**
	 * Lists all account names matching a certain pattern
	 * 
	 * @param sender: the sessionID of the requester
	 * @param pattern: the pattern to be matched
	 * 
	 * @return the list of account names (never null)
	 * 
	 * @throws FailException if sessionID is not valid
	 */
	public List<String> listAccounts(int sender, String pattern) throws RemoteException;

	/**
	 * Creates a new (empty) group. Overwrites any group that currently exists with this name.
	 * 
	 * @param sender: sessionID of the requester
	 * @param name: name of the new group
	 * 
	 * @throws FailException if sessionID is not valid
	 */
	public void newGroup(int sender, String name) throws RemoteException;
	
	/**
	 * Add a member to a group
	 * 
	 * @param sender: sessionID of the requester
	 * @param gname: name of the group in question
	 * @param user: username of the person to be added
	 * 
	 * @throws FailException if sessionID is not valid
	 * @throws ServerException if user or group does not exist
	 */
	public void addMember(int sender, String gname, String user) throws RemoteException;
	
	/**
	 * Lists all groupnames
	 * 
	 * @param sender: the sessionID of the requester
	 * 
	 * @return the list of group names (never null)
	 * 
	 * @throws FailException if sessionID is not valid
	 */
	public List<String> listGroups(int sender) throws RemoteException;

	/**
	 * Lists all groupnames matching a certain pattern
	 * 
	 * @param sender: the sessionID of the requester
	 * @param pattern: the pattern to be matched
	 * 
	 * @return the list of group names (never null)
	 * 
	 * @throws FailException if sessionID is not valid
	 */
	public List<String> listGroups(int sender, String pattern) throws RemoteException;
	
	/**
	 * Sends a group message
	 * 
	 * @param sender: the sessionID of the sender
	 * @param m: the message
	 * @param gname: the name of the intended group recipient
	 * 
	 * @throws FailException if sessionID is not valid
	 * @throws ServerException if group does not exist
	 */
	public void sendGroupMessage(int sender, Message m, String gname) throws RemoteException;
	
	/**
	 * Sends an individual message 
	 * 
	 * @param sender: the sessionID of the sender
	 * @param m: the message
	 * @param user: the username of the intended recipient
	 * 
	 * @throws ServerException if attempting to send a message to oneself or if recipient
	 * does not exist
	 * @throws FailException if sessionID is not valid
	 */
	public void sendMessage(int sender, Message m, String user) throws RemoteException;
	
	/**
	 * Get the undelivered messages for a user
	 * 
	 * @param sessionID: the sessionID of the client asking for their messages
	 * This ensures that only a particular user can get their undelivered messages
	 * 
	 * @return the desired list of messages, null if there are no messages
	 * @throws FailException if the sessionID is invalid
	 */
	public List<Message> getUndelivered(int sessionID) throws RemoteException;
}