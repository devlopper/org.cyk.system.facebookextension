package org.cyk.system.facebookextension.tools.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.cyk.system.facebookextension.tools.api.post.Group;

@Getter @Setter
public class GroupBatchs implements Serializable {

	private static final long serialVersionUID = -1962076765745544663L;

	private Collection<GroupBatch> collection = new ArrayList<>();
	
	public void build(Collection<Group> groups,Integer batchSize){
		List<Group> list = new ArrayList<>(groups);
		collection.clear();
		GroupBatch batch = null;
		Integer count = 0;
		for(Integer index = 0;index < groups.size();index++){
			if(count==0)
				collection.add(batch = new GroupBatch());
			batch.getCollection().add(list.get(index));
			if(++count==batchSize)
				count=0;			
		}
	}
	
}
