package common;

/**
 * Represent a chatting group. Groups are relatively straightforward, with 
 * an immutable name and a set of members, which are stored as a set of 
 * username strings. Note that we only support adding to a group, and not
 * removing from it.
 */
import java.util.HashSet;
import java.util.Set;

public class Group {
	
	/**
	 * The groups members, stored as a set of their usernames (strings)
	 */
	public Set<String> members;

	/**
	 * The immutable name of the group
	 */
	final String name;
	
	/**
	 * Create a group, given its name. The returned group will have no 
	 * members.
	 * 
	 * @param name: the name of the group
	 */
	public Group(String name) {
		this.name = name;
		this.members = new HashSet<String>();
	}
	
	/**
	 * Add a member to a group. Does a set add, so does not assume that the member is 
	 * not already in the group.
	 * 
	 * @param member: the member to be added to the group. 
	 */
	public void addMember(String member) {
		this.members.add(member);
	}
}