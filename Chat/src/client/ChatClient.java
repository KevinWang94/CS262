package client;

import java.util.List;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import common.FailException;
import common.Message;
import server.ChatServerInterface;

/**
 * Chat client. 
 * @author Lucy
 *
 */
public class ChatClient implements ChatClientInterface {

	/**
	 * Scanner  to take user input.
	 */
	private Scanner scanner;
	
	/**
	 * Session ID for this client. When contact with the 
	 * server is first made, a session ID is assigned.
	 * The client keeps the session ID and uses it to 
	 * verify identity with the server for all future requests.
	 */
	private int sessionID;
	
	/**
	 * Username of the account logged onto this client.
	 */
	private String username;
	
	/**
	 * Basic constructor. Creates scanner object. All
	 * real initialization occurs on login.
	 */
	public ChatClient() {
		this.scanner = new Scanner(System.in);
	}
	
	/**
	 * Formatter for messages. Takes a received message object
	 * and formats the string to display to the user.
	 * 
	 * @param msg: the received message
	 * @return the formatted string
	 */
	private String stringOfMessage(Message msg) {
		String out = "Message from " + msg.getSender();
		if(msg.getGroup() != null) {
			out += " to " + msg.getGroup();
		}
		out += ": ";
		out += msg.getText();
		return out;
	}
	
	/**
	 * Function called by the server to send a message to this client
	 * Handles the printing of the message on the client side.
	 * 
	 * @param msg the message to be send
	 */
	public void send(Message msg) throws RemoteException {
		System.out.println();
		System.out.println(stringOfMessage(msg));
		System.out.print(">");
	}
	
	/**
	 * Handles one iteration through the CLI loop.
	 * Asks for a command, and handles it, throwing RemoteException
	 * on failures. Returns false if the client should terminate,
	 * and true otherwise. Terminate if user logged off.
	 * 
	 * @param serverStub
	 * @return
	 * @throws RemoteException
	 */
	private boolean loopOnce(ChatServerInterface serverStub) throws RemoteException {
		printPrompt("Which command? Enter 'help' for command list, 'q' to Quit");
		String input = scanner.nextLine();
		switch (input) {
		case "LA": {
			printPrompt("If you want to use a pattern please input now, otherwise hit enter");
			String pattern = scanner.nextLine();
			List<String> accts;
			if(pattern.length() > 0) {
				accts = serverStub.listAccounts(this.sessionID, pattern);
			} else {
				accts = serverStub.listAccounts(this.sessionID);
			}
			System.out.println("Here are all the accounts you requested:");
			for(String acct : accts) {
				System.out.println(acct);
			}
			System.out.println("Accounts done!");
			return true;
		}
		case "LG": {
			printPrompt("If you want to use a pattern please input now, otherwise hit enter");
			String pattern = scanner.nextLine();
			List<String> groups;
			if(pattern.length() > 0) {
				groups = serverStub.listGroups(this.sessionID, pattern);
			} else {
				groups = serverStub.listGroups(this.sessionID);
			}
			System.out.println("Here are all the groups you requested:");
			for(String group : groups) {
				System.out.println(group);
			}
			System.out.println("Groups done!");
			return true;
		}
		case "SM": {
			printPrompt("Message text:");
			String message = scanner.nextLine();
			Message m = new Message(this.username, message);
			printPrompt("(G)roup or (I)ndividual message?");
			String choice = scanner.nextLine();
			if(choice.equals("G")) {
				printPrompt("Group name:");
				String gname = scanner.nextLine();
				m.setGroup(gname);
				serverStub.sendGroupMessage(this.sessionID, m, gname);
			} else if(choice.equals("I")) {
				printPrompt("Username of recipient:");
				String recipient = scanner.nextLine();
				serverStub.sendMessage(this.sessionID, m, recipient);
			} else {
				printError("Invalid choice");
			}
			return true;
		}
		case "CG": {
			printPrompt("Group Name:");
			String gname = scanner.nextLine();
			serverStub.newGroup(this.sessionID, gname);
			return true;
		}
		case "AG": {
			printPrompt("Group Name:");
			String gname = scanner.nextLine();
			printPrompt("Comma separated usernames (no spaces)");
			String usernamesString = scanner.nextLine();
			String[] usernames = usernamesString.split(",");
			for(String username : usernames) {
				serverStub.addMember(this.sessionID, gname, username);
			}
			return true;
		}
		case "D": {
			List<Message> messages = serverStub.getUndelivered(this.sessionID);
			System.out.println("Here are your undelivered messages:");
			if (messages != null) {
				for(Message m : messages) {
					System.out.println(stringOfMessage(m));
				}
			}
			System.out.println("Messages done!");
			return true;
		}
		case "DA": {
			serverStub.deleteAccount(this.sessionID);
			this.sessionID = -1;
			this.username = null;
			System.out.println("Your account has been deleted");
			return false;
		}
		case "help": {
			System.out.println("LA for list accounts, LG for list groups, SM for send message");
			System.out.println("CG for create group, AG to add to group, D for undelivered, DA for delete acct");
			return true;
		}
		case "q": {
			System.out.println("Quitting");
			return false;
		}
		default:
			printError("Invalid input please try again");
			return true;
		}
	}

	/**
	 * The CLI loop. Calls loopOnce until error or log off,
	 * as signaled by loopOnce. If an exception occurred, print
	 * the error for the user. If a fatal error (FailException or
	 * RemoteException), exit and return 1. 
	 * 
	 * @param serverStub: the stub of the chat server
	 * @return 1 on fatal exceptions and 0 otherwise.
	 */
	private int loop(ChatServerInterface serverStub) {
		boolean keepGoing = true;
		while(keepGoing) {
			try {
				keepGoing = loopOnce(serverStub);
			} catch(ServerException e) {
				printError(e.getLocalizedMessage());
			} catch(FailException e) {
				printError(e.getLocalizedMessage());
				return 1;
			} catch (RemoteException e) {
				printError("Remote exception. Unable to contact server. Restart and try again");
				return 1;
			}
		}
		return 0;
	}
	
	/** Prints the prompt for user input.
	 * 
	 * @param message: the specific prompt
	 */
	private void printPrompt(String message) {
		System.out.println(message);
		System.out.print(">");
	}
	
	/**
	 * Print an error.
	 * 
	 * @param message: the specific error
	 */
	private void printError(String message) {
		System.out.println("Error: " + message);
	}
	
	/**
	 * Initialization, either logging in or creating account.
	 * Asks user whether they want to log in or create a new account.
	 * Sends request to server, which either rejects log in or
	 * returns a new session ID. Store the session ID, and register
	 * a client stub to this machine's registry.
	 * 
	 * @param clientHost: ip of host for the client
	 * @throws RemoteException
	 */
	private void initial(ChatServerInterface serverStub, String clientHost) throws RemoteException {
		printPrompt("Type N for new account or L for login");
		String input = scanner.nextLine();
		switch(input) {
		case "N": {
			printPrompt("New username:");
			String username = scanner.nextLine();
			printPrompt("New password:");
			String password = scanner.nextLine();

			int sessionID = serverStub.createAccount(username, password, clientHost);
			if(sessionID > 0) {
				this.username = username;
				this.sessionID = sessionID;
				ChatClientInterface clientStub = (ChatClientInterface) UnicastRemoteObject.exportObject(this, 0);
				Registry clientRegistry = LocateRegistry.getRegistry();
				clientRegistry.rebind(username, clientStub);
			} else {
				printError("Username is already taken, please try again");
				initial(serverStub, clientHost);
			}
			break;
		}
		case "L": {
			printPrompt("Username:");
			String username = scanner.nextLine();
			printPrompt("Password:");
			String password = scanner.nextLine();

			int sessionID = serverStub.signIn(username, password, clientHost);
			if(sessionID > 0) {
				this.sessionID = sessionID;
				this.username = username;
				ChatClientInterface clientStub = (ChatClientInterface) UnicastRemoteObject.exportObject(this, 0);
				Registry clientRegistry = LocateRegistry.getRegistry();
				clientRegistry.rebind(username, clientStub);
			} else {
				printError("Issue signing in, please try again");
				initial(serverStub, clientHost);
			}
			break;
		}
		default:
			printError("Invalid choice please try again");
			initial(serverStub, clientHost);
		}
	}
	
	/**
	 * Sign out. Can also be called by server to force a sign
	 * out when this account is logged into from another machine.
	 * 
	 * @param message: message to print when signing out
	 */
	public void signOut(String message) {
		System.out.println();
		System.out.println(message);
		System.exit(0);
	}
	
	public static void main(String[] args) {
		String serverHost = args[0];
		String clientHost = args[1];
		try {
			Registry registry = LocateRegistry.getRegistry(serverHost);
			ChatServerInterface serverStub = (ChatServerInterface) registry.lookup("Server");
			ChatClient client = new ChatClient();
			client.initial(serverStub, clientHost);
			client.loop(serverStub);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		System.exit(0);
		return;
	}
}
