package client;

import java.util.List;
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

	public ChatClient(String username, String password) {
		this.scanner = new Scanner(System.in);
		
	}
	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send(Message msg) throws RemoteException {
		System.out.println("testtest");
		System.out.println(msg.getText());
		// TODO Auto-generated method stub

	}
	
	private void loop(ChatServerInterface serverStub) throws RemoteException {
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
			break;
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
			break;
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
				serverStub.sendGroupMessage(this.sessionID, m, gname);
			} else if(choice.equals("I")) {
				System.out.println("Username of recipient:");
				String username = scanner.nextLine();
				serverStub.sendMessage(this.sessionID, m, username);
			} else {
				System.out.println("Invalid choice");
			}
			break;
		}
		case "CG": {
			System.out.println("Group Name:");
			String gname = scanner.nextLine();
			serverStub.newGroup(gname);
			break;
		}
		case "AG": {
			System.out.println("Group Name:");
			String gname = scanner.nextLine();
			System.out.println("Comma separated usernames");
			String usernamesString = scanner.nextLine();
			String[] usernames = usernamesString.split(",");
			for(String username : usernames) {
				serverStub.addMember(gname, username);
			}
			break;
		}
		case "D": {
			List<Message> messages = serverStub.getUndelivered(this.sessionID);
			for(Message m : messages) {
				System.out.println("Blah");
				System.out.println(m.getText());
			}
			break;
		}
		case "DA": {
			serverStub.deleteAccount(this.sessionID);
			this.sessionID = -1;
			this.username = null;
			System.out.println("Your account has been deleted");
			break;
		}
		case "help": {
			System.out.println("HELP TEXT HERE");
			break;
		}
		default:
			System.out.println("Invalid input please try again");
		}
	}

	private void initial(ChatServerInterface serverStub) throws RemoteException {
		System.out.println("Type N for new account or L for login");
		String input = scanner.nextLine();
		switch(input) {
		case "N": {
			System.out.println("New username:");
			String username = scanner.nextLine();
			System.out.println("New password:");
			String password = scanner.nextLine();
			int sessionID = serverStub.createAccount(this, username, password);
			if(sessionID > 0) {
				this.username = username;
				this.sessionID = sessionID;
			} else {
				System.out.println("Username is already taken, please try again");
				initial(serverStub);
			}
			break;
		}
		case "L": {
			System.out.println("Username:");
			String username = scanner.nextLine();
			System.out.println("Password:");
			String password = scanner.nextLine();
			int sessionID = serverStub.signIn(username, password);
			if(sessionID > 0) {
				this.sessionID = sessionID;
				this.username = username;
			} else {
				System.out.println("Issue signing in, please try again");
				initial(serverStub);
			}
			break;
		}
		default:
			System.out.println("Invalid choice please try again");
		}

	}
	public static void main(String[] args) {

		try {
			String username = "test";
			String password = "test";
			ChatClient client = new ChatClient(username, password);
			ChatClientInterface clientStub = (ChatClientInterface) UnicastRemoteObject.exportObject(client, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("test", clientStub);

			String host = (args.length < 1) ? null : args[0];

			ChatServerInterface serverStub = (ChatServerInterface) registry.lookup("Server");
			int response = serverStub.createAccount(client, username, password);
			serverStub.sendMessage(response, new Message("", "hi"), "test");
			System.out.println("response: " + response);
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
		}
	}
}
