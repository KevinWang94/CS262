package server;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;

import common.*;

public class ChatServer {

	Map<String, Account> users;
	Map<String, Group> groups;
	Map<String, List<Message>> undelivered;
	Set<String> onlineUsers;
	
	public void createAccount(String username) {
		
	}
	public void deleteAccount(String username) {
		
	}
	public List<String> listAccounts() {
		
	}
	
	public List<String> listAccounts(String pattern) {
		String key = null;
		if(key.matches(pattern))
		
	}
	
	public void newGroup(String gname) {
		Group g = new Group(gname);
		this.groups.put(gname, g);
	}
	
	public void addMember(String gname, String user) {
		if(this.groups.containsKey(gname)) {
			this.groups.get(gname).addMember(user);
		} else {
			
		}
		
	}
	
	public Collection<Group> listGroups() {
		return this.groups.values();
	}
	
	public Collection<Group> listGroups(String pattern) {
	}
	
	public void sendMessage(Message m, Group g) {
		for(String user : g.members) {
			sendMessage(m, user);
		}
	}
	
	public void sendMessage(Message m, String user) {
		if(this.onlineUsers.contains(user) {
			
		} else {
			
		}
		
	}
	
	public List<Message> getUndelivered(String username) {
		if(this.undelivered.containsKey(username)) {
			List<Message> undelivered = this.undelivered.get(username);
			this.undelivered.remove(username);
			return undelivered;
		} else {
			return null;
		}
		
	}
}
