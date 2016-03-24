package common;

/**
 * Represents an account created by a client of the ChatServer.
 * Accounts here are relatively lightweight, consisting of only 
 * a username and a password, both immutable and represented 
 * simply as strings (system security was not a design priority as 
 * this was written as an RMI-related exercise).
 * 
 * @author kwang01
 *
 */

public class Account {
	
	/** 
	 * The immutable username associated with the account
	 */
	public final String username;
	/**
	 * The immutable password associated with the account
	 */
	public final String password;
	
	/**
	 * Constructs an Account object
	 * @param username: the user's username
	 * @param password: the user's password
	 */
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}
}
