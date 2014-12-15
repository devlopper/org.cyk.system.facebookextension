package org.cyk.system.facebookextension.tools.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.cyk.system.facebookextension.model.Post;
import org.cyk.system.facebookextension.tools.api.post.Collector;
import org.cyk.system.facebookextension.tools.api.post.CollectorListener;
import org.cyk.system.facebookextension.tools.api.post.FilePublisher;
import org.cyk.system.facebookextension.tools.api.post.FilePublisherListener;
import org.cyk.system.facebookextension.tools.api.post.Group;
import org.cyk.system.root.model.file.Tag;
import org.cyk.utility.common.cdi.AbstractBean;


public abstract class AbstractFacebookClient<CLIENT,ATTACHMENT,FQL_POST extends FqlPost<ATTACHMENT>> extends AbstractBean implements CollectorListener,FilePublisherListener,Serializable {

	private static final long serialVersionUID = -8764604461153940309L;

	protected static final String SELECT_FORMAT = "SELECT post_id,created_time,message,attachment FROM stream WHERE source_id = %s "
			+ "AND created_time >= %d AND created_time <= %d LIMIT %d"; //ORDER BY created_time";
	
	protected static final String MESSAGE = "message";
	
	public enum ApiType{GRAPH,FQL}
	
	/**/
	
	protected ApiType apiType = ApiType.FQL;
	protected Class<FQL_POST> fqlPostClass;
	/**/
	

	public AbstractFacebookClient(Class<FQL_POST> fqlPostClass) {
		this.fqlPostClass = fqlPostClass;
	}
	
	protected String fqlAccessToken(){
		return "CAACEdEose0cBAGTFtkpdnIwSjGuuzihIy52oNC6cM3PZBZAMLPigOjBc9tN60A1mwn858484v7tpTdTXmK9pyNWxEKoLUtxnpidfL4JTdoqrAJpDk6kvQxvZCYRxoaQmqFrFZCZCNEMjOwggtj9DIEAotMz0vFNGIqqyzjyTTong69baXvQrZAAwv7qcD75NU7uAVUflW0Byy1QgbydoNI9dhrJCrXNi0ZD";
	}
	
	protected String noFlqlAccessToken(){
		return "CAAEgSWBi5WYBAHQBPPXrM4DH6LM8dyuPyIAKTTIdgOGPeiTGvZCPZCQDe44T10VPxExsTZBNKZAcnom42VWTI102xbUccaNsredEUnZA5d4Mw8Idruyv7jXjMWoqfOoUHkESZCQgceANRCtuhUE0ZANWjd8KKl9ZByTLBwa1zU5aWXrEjRroGWEkGH6pDHLss4XWOmE343hX7AZCq0OGrYz2aZBvBwClMcAY0ZD";
	}
	
	@Override
	public final void fetch(Collector collector) {
		fetch(collector,createClient(fqlAccessToken()));
	}
	
	@Override
	public final void publish(FilePublisher filePublisher, Collection<Post> posts,Collection<Post> notPublished) {
		publish(filePublisher,posts,notPublished,createClient(noFlqlAccessToken()));
	}
	
	/**/
	
	protected abstract void fetch(Collector collector, CLIENT client);
	
	protected abstract CLIENT createClient(String accessToken);
	
	protected abstract void publish(FilePublisher filePublisher, Collection<Post> posts,Collection<Post> notPublished, CLIENT createClient);

	protected void createPost(Collector collector,Group group,Collection<FQL_POST> postFqls){
		if(postFqls==null)
			return;
		for(FQL_POST postFql : postFqls){
			org.cyk.system.facebookextension.model.Post post = new org.cyk.system.facebookextension.model.Post();
			for(String tagId : group.getTagsId()){
				Tag tag = new Tag();
				tag.setCode(tagId);
				post.getTags().add(tag);
			}
			post.setReference(postFql.getPost_id());
			post.setCreationDate(new Date(postFql.getCreated_time()*1000L));
			post.setMessage(postFql.getMessage());
			if(postFql.getAttachment()!=null)
				processAttachment(post,postFql.getAttachment());
			collector.getPosts().add(post);
		}
		//System.out.println(group.getName()+" : "+postFqls.size()+" : "+collector.getPosts().size());
	}
	
	protected abstract void processAttachment(Post post,ATTACHMENT attachement);
	
	/**/
	
	protected static Long unixTime(Date date){
		return (long)date.getTime()/1000L;
	}
	
}
