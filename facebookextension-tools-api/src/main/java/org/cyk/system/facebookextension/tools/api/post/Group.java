package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.cyk.system.facebookextension.model.Constants;

@Getter @Setter
public class Group implements Serializable {
	
	private static final long serialVersionUID = -6376789573256524964L;
	
	private static final Collection<Group> COLLECTION = new LinkedHashSet<>();
	
	public enum Type{
		//these are the groups where I fetch data
		SOURCE,
		//these are the groups where I publish data
		DESTINATION
	}
	
	private Long id;
	private String name;
	private Set<String> tagsId = new HashSet<String>();
	private Type type;
	
	private Integer createdPostCount;
	private Integer postCount;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**/
	
	public static Set<Group> collection(String tagId,Boolean source){
		Set<Group> collection = new LinkedHashSet<>();
		for(Group group : COLLECTION)
			if(group.getTagsId().contains(tagId))
				if(source==null || (Boolean.TRUE.equals(source) && Type.SOURCE.equals(group.getType())) || (Boolean.FALSE.equals(source) && Type.DESTINATION.equals(group.getType())) )
					collection.add(group);
		return collection;
	}
	
	@SuppressWarnings("unchecked")
	public static void loadGroups(){
		String userDir = System.getProperty("user.dir");
		List<String> groups = null;
		try {
			groups = FileUtils.readLines(new File(userDir,"groups.txt"));
			for(String groupLine : groups){
				Group group = new Group();
				String[] parts = StringUtils.split(groupLine, ',');
				group.setId(Long.parseLong(parts[0]));
				group.setName(parts[1]);
				String tagId = parts[2];
				if("SELL".equals(tagId))
					tagId = Constants.TAG_POST_SELL;
				group.getTagsId().add(tagId);
				group.setType(Type.valueOf(parts[3]));
				COLLECTION.add(group);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/*-------------------------------------*/
	
	public static void main(String[] args) {
		loadGroups();
		System.out.println(COLLECTION);
		System.out.println(collection(Constants.TAG_POST_SELL, null));
		System.out.println(collection(Constants.TAG_POST_SELL, Boolean.TRUE));
		System.out.println(collection(Constants.TAG_POST_SELL, Boolean.FALSE));
	}
	

}
