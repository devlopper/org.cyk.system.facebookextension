package org.cyk.system.facebookextension.tools.api.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.io.FileUtils;
import org.cyk.system.facebookextension.model.Post;

@Getter @Setter
public class FilePublisher extends AbstractFileProcessor implements Runnable, Serializable {

	private static final long serialVersionUID = 2689083520680885739L;

	private Group group;
	private Collection<FilePublisherListener> filePublisherListeners = new ArrayList<>();
	
	@Override
	protected void process(Collection<Post> posts) {
		Collection<Post> notPublished = new ArrayList<>();
		for(FilePublisherListener listener : filePublisherListeners)
			try{
				listener.publish(this,posts,notPublished);
			}catch(Exception e){
				__writeInfo__(e.toString());
			}
		
		if(notPublished.isEmpty()){
			__writeInfo__(file+" fully published");
			FileUtils.deleteQuietly(file);
		}else{
			__writeInfo__(file+" "+notPublished.size()+" not yet published");
			Utils.writePosts(file, notPublished, beanListeners);
		}
	}
	
	public void notPublished(Collection<Post> posts){
		Utils.writePosts(file, posts, beanListeners);
	}

}
