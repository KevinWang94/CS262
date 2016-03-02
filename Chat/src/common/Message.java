package common;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String sender;
	String text;
	String group;

	public Message(String sender, String text) {
		this.sender = sender;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
}
