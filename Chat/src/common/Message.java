package common;

/**
 * This is the serializable message that gets passed between the server and
 * client using RMI. It's pretty lightweight, having just a couple of simple
 * fields.
 */
import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String sender;
	
	/**
	 * The text of the message.
	 */
	String text;
	
	/**
	 * The name of the group if the message is being sent to a group.
	 * If not being sent to a group, null
	 */
	String group;

	/**
	 * Constructs a message object.
	 * 
	 * @param sender: the username of the sender
	 * @param text: the text of the message
	 */
	public Message(String sender, String text) {
		this.sender = sender;
		this.text = text;
	}
	
	/**
	 * Gets the text of a message. 
	 * 
	 * @return the text of a message
	 */
	public String getText() {
		return text;
	}
	
	/** 
	 * Gets the sender of a message.
	 * 
	 * @return the sender of a message
	 */
	public String getSender() {
		return this.sender;
	}
	
	/** 
	 * Gets the group the message is being sent to.
	 * 
	 * @return: null if no group.
	 */
	public String getGroup() {
		return group;
	}
	
	/**
	 * Sets the group the message is being sent to.
	 * 
	 * @param group: name of the group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
}