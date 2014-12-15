package org.cyk.system.facebookextension.tools.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import lombok.Setter;
import lombok.Getter;

import org.cyk.system.facebookextension.tools.api.post.Group;

@Getter @Setter
public class GroupBatch implements Serializable {

	private static final long serialVersionUID = -1962076765745544663L;

	private Collection<Group> collection = new LinkedHashSet<>();
	
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(Group group : collection)
			s.append(group.getName());
		return s.toString();
	}
}
