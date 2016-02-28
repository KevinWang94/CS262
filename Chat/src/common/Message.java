package common;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String sender;
	String text;

	public Message(String sender, String text) {
		this.sender = sender;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}
