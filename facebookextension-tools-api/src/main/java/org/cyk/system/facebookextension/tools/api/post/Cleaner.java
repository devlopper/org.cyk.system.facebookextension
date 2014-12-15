package org.cyk.system.facebookextension.tools.api.post;

import java.io.Serializable;
import java.util.List;

import org.cyk.utility.common.cdi.AbstractBean;
import org.cyk.utility.common.cdi.BeanAdapter;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

public class Cleaner extends AbstractBean implements Serializable {

	private static final long serialVersionUID = 1853872656403462572L;
	
	public void clean(Group group,String accessToken){
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
		Connection<Post> myFeed;
		int count = 0;
		do{
			myFeed = facebookClient.fetchConnection(group.getId()+"/feed", Post.class);
			boolean delete = false;
			for (List<Post> myFeedConnectionPage : myFeed){
				__writeInfo__(myFeedConnectionPage.size()+" post(s) found");
				for (Post post : myFeedConnectionPage){
					facebookClient.deleteObject(post.getId());
					count++;
					delete = true;
				}
			}
			if(!delete)
				break;
		}while(true);
		if(count>0)
			__writeInfo__(count+" post(s) deleted");
	}
	
	public static void main(String[] args) {
		Cleaner cleaner =  new Cleaner();
		cleaner.getBeanListeners().add(new BeanAdapter(){
			@Override
			public void info(String message) {
				System.out.println(message);
			}
		});
		//cleaner.clean(Group.SELL_BUY, "CAAEgSWBi5WYBAOJMKs8FfFRG4d7tmzPReAE521kYWlvkUhZA0iJ0i5XTPd8JJ9MVaOkuPPnuF2ON5zl52PJHq3dzhePfqxnvRUDXPmpc42ZCDkG4rmdqBusybZB6CeZCdC0iezSoYvRjDfDKExsZAqsvx7Yi6Df1cLe0qhbu0ueMZAw7C7U15A7irHPv5dB4bqEZAoOQcHkTHPTjkMW9zko7omy9YGjZAgsZD");
	}

}
