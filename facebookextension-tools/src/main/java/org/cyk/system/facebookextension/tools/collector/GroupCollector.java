package org.cyk.system.facebookextension.tools.collector;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import com.restfb.FacebookClient;
import com.restfb.types.Group;

@Getter @Setter @Log
public class GroupCollector extends AbstractCollector implements Serializable {

	private static final long serialVersionUID = -2417318220707684831L;

	private List<Group> groups;
	
	public GroupCollector(Application application) {
		super(application);	
		FacebookClient facebookClient = createFacebookClient();
		if(facebookClient==null){
			log.severe("NULL Facebook Client Object");
			return;
		}
		groups = facebookClient.fetchConnection("me/groups", Group.class).getData();
	}
	
	public Set<Long> getIds(){
		Set<Long> ids = new LinkedHashSet<Long>();
		for(Group group : groups)
			ids.add(Long.parseLong(group.getId()));
		return ids;
	}
	
	static final String CODE_FORMAT = "createGroup(s, %sL,\"%s\", Tag.SELL_BUY);";
	
	public static void main(String[] args) {
		GroupCollector groupCollector = new GroupCollector(Utils.DEV_APPLICATION);
		System.out.println(groupCollector.getGroups().size());
		for(Group group : groupCollector.getGroups())
			System.out.println(String.format(CODE_FORMAT, group.getId(),group.getName()));
		
	}
}
