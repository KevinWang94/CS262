package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import common.Message;

import server.ChatServer;
import server.ChatServerInterface;

public class ChatClient implements ChatClientInterface {

	public ChatClient(String username, String password) {}

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
