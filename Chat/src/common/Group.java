package common;

import java.util.HashSet;
import java.util.Set;

public class Group {
	
	public Set<String> members;
	String name;
	
	public Group(String name) {
		this.name = name;
		this.members = new HashSet<String> ();
	}
	
	public void addMember(String member) {
		this.members.add(member);
	}

}
