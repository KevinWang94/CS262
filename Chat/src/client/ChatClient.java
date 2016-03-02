package client;

import java.util.List;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import common.Message;
import server.ChatServerInterface;

public class ChatClient implements ChatClientInterface {

	private Scanner scanner;
	private int sessionID;
	private String username;
	private String host;
	
	/**
	 * Basic constructor
	 * 
	 * @param host the host IP address
	 */
	public ChatClient(String host) {
		this.scanner = new Scanner(System.in);	
		this.host = host;
	}
	
	public String getName() throws RemoteException {
		return null;
	}

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
	 * Called by the server to send a message to this client
	 * 
	 * @param msg the message to be send
	 */
	public void send(Message msg) throws RemoteException {
		System.out.println(stringOfMessage(msg));

	}
	
	/**
	 * Once through the CMI loop
	 * 
	 * @param serverStub
	 * @return
	 * @throws RemoteException
	 */
	private boolean loopOnce(ChatServerInterface serverStub) throws RemoteException {
		System.out.println("Which command? Enter 'help' for command list, 'q' to Quit");
		String input = scanner.nextLine();
		switch (input) {
		case "LA": {
			System.out.println("If you want to use a pattern please input now, otherwise hit enter");
			String pattern = scanner.nextLine();
			List<String> accts;
			if(pattern.length() > 0) {
				accts = serverStub.listAccounts(pattern);
			} else {
				accts = serverStub.listAccounts();
			}
			System.out.println("Here are all the accounts you requested:");
			for(String acct : accts) {
				System.out.println(acct);
			}
			System.out.println("Accounts done!");
			return true;
		}
		case "LG": {
			System.out.println("If you want to use a pattern please input now, otherwise hit enter");
			String pattern = scanner.nextLine();
			List<String> groups;
			if(pattern.length() > 0) {
				groups = serverStub.listGroups(pattern);
			} else {
				groups = serverStub.listGroups(pattern);
			}
			System.out.println("Here are all the groups you requested:");
			for(String group : groups) {
				System.out.println(group);
			}
			System.out.println("Groups done!");
			return true;
		}
		case "SM": {
			System.out.println("Message text:");
			String message = scanner.nextLine();
			Message m = new Message(this.username, message);
			System.out.println("(G)roup or (I)ndividual message?");
			String choice = scanner.nextLine();
			if(choice.equals("G")) {
				System.out.println("Group name:");
				String gname = scanner.nextLine();
				m.setGroup(gname);
				serverStub.sendGroupMessage(this.sessionID, m, gname);
			} else if(choice.equals("I")) {
				System.out.println("Username of recipient:");
				String recipient = scanner.nextLine();
				serverStub.sendMessage(this.sessionID, m, recipient);
			} else {
				System.out.println("Invalid choice");
			}
			return true;
		}
		case "CG": {
			System.out.println("Group Name:");
			String gname = scanner.nextLine();
			serverStub.newGroup(gname);
			return true;
		}
		case "AG": {
			System.out.println("Group Name:");
			String gname = scanner.nextLine();
			System.out.println("Comma separated usernames (no spaces)");
			String usernamesString = scanner.nextLine();
			String[] usernames = usernamesString.split(",");
			for(String username : usernames) {
				serverStub.addMember(gname, username);
			}
			return true;
		}
		case "D": {
			List<Message> messages = serverStub.getUndelivered(this.sessionID);
			System.out.println("Here are your undelievered messages:");
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
			System.out.println("quitting");
			return false;
		}
		default:
			System.out.println("Invalid input please try again");
			return true;
		}
	}

	/**
	 * CMI loop
	 */
	private void loop() {
		boolean keepGoing = true;
		while(keepGoing) {
			try {
				Registry registry = LocateRegistry.getRegistry(this.host);
				System.out.println("located");
				ChatServerInterface serverStub;
				// TODO: run experiments without this?
				try {
					serverStub = (ChatServerInterface) registry.lookup("Server");
					keepGoing = loopOnce(serverStub);
				} catch (NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (RemoteException e) {
				System.out.println("Remote exception. Unable to contact server. Try restarting? :P");
			}
		}
	}
	
	/**
	 * Initialization, either logging in or creating account
	 * @param clientHost
	 * @throws RemoteException
	 */
	private void initial(String clientHost) throws RemoteException {
		ChatServerInterface serverStub = null;
		Registry registry = LocateRegistry.getRegistry(this.host);
		try {
			serverStub = (ChatServerInterface) registry.lookup("Server");
		} catch (NotBoundException e) {
			// todo
		}
		
		System.out.println("Type N for new account or L for login");
		String input = scanner.nextLine();
		switch(input) {
		case "N": {
			System.out.println("New username:");
			String username = scanner.nextLine();
			System.out.println("New password:");
			String password = scanner.nextLine();
			System.out.println("located");

			int sessionID = serverStub.createAccount(username, password, clientHost);
			if(sessionID > 0) {
				this.username = username;
				this.sessionID = sessionID;
				ChatClientInterface clientStub = (ChatClientInterface) UnicastRemoteObject.exportObject(this, 0);
				Registry clientRegistry = LocateRegistry.getRegistry();
				clientRegistry.rebind(username, clientStub);
			} else {
				System.out.println("Username is already taken, please try again");
				initial(clientHost);
			}
			break;
		}
		case "L": {
			System.out.println("Username:");
			String username = scanner.nextLine();
			System.out.println("Password:");
			String password = scanner.nextLine();

			int sessionID = serverStub.signIn(username, password, clientHost);
			if(sessionID > 0) {
				this.sessionID = sessionID;
				this.username = username;
				ChatClientInterface clientStub = (ChatClientInterface) UnicastRemoteObject.exportObject(this, 0);
				Registry clientRegistry = LocateRegistry.getRegistry();
				clientRegistry.rebind(username, clientStub);
			} else {
				System.out.println("Issue signing in, please try again");
				initial(clientHost);
			}
			break;
		}
		default:
			System.out.println("Invalid choice please try again");
		}
	}
	
	public void signOut(String message) {
		System.out.println(message);
	}
	
	public static void main(String[] args) {
		System.out.println(args[0]);
		System.out.println(args[1]);
		// args[0] = hostname of server
		// args[1] = hostname of self
		try {
			Registry registry = LocateRegistry.getRegistry(args[0]);
			System.out.println("located");
			ChatServerInterface serverStub = (ChatServerInterface) registry.lookup("Server");
			System.out.println("stub");
			ChatClient client = new ChatClient(args[0]);
			client.initial(args[1]);
			client.loop();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
