package server;

import java.util.List;
import common.*;

public interface ChatServerInterface {

	public void createAccount(String username);
	public void deleteAccount(String username);
	public List<String> listAccounts();
	public List<String> listAccounts(String pattern);
	public void newGroup(String name);
	public void addMember(Group g, String user);
	public List<Group> listGroups();
	public List<Group> listGroups(String pattern);
	public void sendMessage(Message m, Group g);
	public void sendMessage(Message m, String user);
	public List<Message> getUndelivered();
	
}
