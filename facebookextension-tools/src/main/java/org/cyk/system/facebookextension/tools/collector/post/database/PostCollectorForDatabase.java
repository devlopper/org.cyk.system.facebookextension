package org.cyk.system.facebookextension.tools.collector.post.database;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.cyk.system.facebookextension.model.Post;
import org.cyk.system.facebookextension.tools.collector.Application;
import org.cyk.system.facebookextension.tools.collector.Group;
import org.cyk.system.facebookextension.tools.collector.post.PostReader;

@Getter @Setter
public class PostCollectorForDatabase extends PostReader implements Serializable {

	private static final long serialVersionUID = 1645631834292748898L;

	public PostCollectorForDatabase(Application application,Date fromCreationDate, Date toCreationDate,Group...source) {
		super(application, new HashSet<Group>(Arrays.asList(source)), fromCreationDate, toCreationDate);
	}

	public PostCollectorForDatabase(Application application) {
		super(application);
	}
	
	@Override
	public void consume(List<Post> posts,Date start,Date end) {
		verbose("Persisting...");
		//new PostDao().bulkPersistWithoutDuplicate(posts,false);
	}
	
	
}
