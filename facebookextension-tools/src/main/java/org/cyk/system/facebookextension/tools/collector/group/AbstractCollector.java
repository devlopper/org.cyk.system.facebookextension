package org.cyk.system.facebookextension.tools.collector.group;

import java.io.Serializable;
import java.util.List;

import org.cyk.system.facebookextension.tools.api.post.Application;
import org.cyk.system.facebookextension.tools.api.post.Group;

import lombok.Getter;
import lombok.Setter;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;

@Getter @Setter @Deprecated
public abstract class AbstractCollector extends Thread implements Serializable {

	private static final long serialVersionUID = -2159250811649761801L;

	public  static final String TEST_AT ="CAACEdEose0cBANhpBQXcWKopXqoRrAyKbo0ZCxm2CYDEpoElUazkqb7UGGzX4ljUi4v4EpwD1K0s56nVZA3dXCZCP577ZB4TW6ule4TilJceJwZCL08SLt34ekO8V6hDpOeejVv8ChRc9lV54ogkO0AUnkl25e5AyqxFt4BcQHnCCn8W8WRFv0unbhTpdkmutCuSfZCuoRWHpf5TZCJwbSP6ZARB8nR7QDAZD";
	
	protected Application application;
	
	protected boolean verbose=false;
	
	public AbstractCollector(Application application) {
		this.application = application;
	}
	
	protected FacebookClient createFacebookClient(){
		return createFacebookClient(application);
	}
		
	protected static FacebookClient createFacebookClient(Application application){
		//AccessToken accessToken= new DefaultFacebookClient().obtainExtendedAccessToken(application.getName(),application.getSecret(),application.getAccessToken());
		//System.out.println("Access Token : "+accessToken);
		//return new DefaultFacebookClient(accessToken.getAccessToken());
		return new DefaultFacebookClient(application.getAccessToken());
	}
	
	protected void verbose(String message){
		if(verbose)
			System.out.println(message);
	}
	
	public static void clean(Application application,Group group){
		FacebookClient facebookClient = createFacebookClient(application);
		Connection<Post> myFeed;
		int count = 0;
		do{
			myFeed = facebookClient.fetchConnection(group.getId()+"/feed", Post.class);
			boolean delete = false;
			for (List<Post> myFeedConnectionPage : myFeed){
				System.out.println(myFeedConnectionPage.size()+" found");
				for (Post post : myFeedConnectionPage){
					facebookClient.deleteObject(post.getId());
					count++;
					delete = true;
				}
			}
			if(!delete)
				break;
		}while(true);
		System.out.println(count+" deleted");

	}

}
