package org.cyk.system.facebookextension.tools.collector;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.system.root.model.file.File;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;

@Getter @Setter
public class GroupPostCollector implements Runnable {
	
	private static final String SELECT_FORMAT = "SELECT post_id,created_time,message,attachment FROM stream WHERE source_id = %s "
			+ "AND created_time >= %d AND created_time <= %d ORDER BY created_time";
	
	private Group group;
	private Date startDate;
	private Date endDate;
	private Long limit;
	
	private FacebookClient facebookClient;
	private Throwable throwable;
	private String query;
	private List<PostFql> results;
	private List<Post> posts = new LinkedList<Post>(); 
	
	private Collection<GroupPostCollectorListener> groupPostCollectorListeners = new ArrayList<>();
	
	public GroupPostCollector(GroupPostCollectorListener listener,FacebookClient facebookClient, Group group,
			Date startDate, Date endDate, Long limit) {
		super();
		if(listener!=null)
			groupPostCollectorListeners.add(listener);
		this.facebookClient = facebookClient;
		this.group = group;
		this.startDate = startDate;
		this.endDate = endDate;
		this.limit = limit;
	}
	
	public void run() {		
		for(GroupPostCollectorListener groupPostCollectorListener : groupPostCollectorListeners)
			groupPostCollectorListener.createQuery(this);
		
		if(StringUtils.isBlank(query))
			query = String.format(SELECT_FORMAT, group.getId(),unixTime(startDate),unixTime(endDate))+(limit==null?"":(" LIMIT "+limit));
		
		try {
			results = facebookClient.executeFqlQuery(query,PostFql.class);
		} catch (Exception e) {
			throwable = e;
		}
		
		if(throwable==null){
			for(GroupPostCollectorListener groupPostCollectorListener : groupPostCollectorListeners)
				groupPostCollectorListener.processResults(this);
			
			group.setPostCount(results.size());
			
			for(PostFql postFql : results)
				posts.add(createPost(group,postFql));
			Utils.clean(posts);
			group.setCreatedPostCount(posts.size());
			
			for(GroupPostCollectorListener groupPostCollectorListener : groupPostCollectorListeners)
				groupPostCollectorListener.processPosts(this);
		}else{
			for(GroupPostCollectorListener groupPostCollectorListener : groupPostCollectorListeners)
				groupPostCollectorListener.exceptionWhileExecutingQuery(this);
		}

	}
	
	/**/
	
	private static long unixTime(Date date){
		return (long)date.getTime()/1000L;
	}
	
	private static Post createPost(Group group,PostFql postFql){
		Post post = new Post();
		post.setTags(group.getTags());
		post.setReference(postFql.post_id);
		post.setCreationDate(new Date(postFql.created_time*1000L));
		post.setMessage(postFql.message);
		if(postFql.attachment!=null){
			if(postFql.attachment.has("media")){
				JsonArray jsonArray = (JsonArray) postFql.attachment.get("media");	
				//System.out.println(jsonArray);
				for(int i=0;i<jsonArray.length();i++){
					JsonObject jsonObject = (JsonObject) jsonArray.get(i);
					if(jsonObject.has("src")){
						String uri = StringUtils.replace(jsonObject.getString("src"), "_s.jpg", "_n.jpg");
						if(StringUtils.isNotBlank(uri)){
							File file = new File();
							file.setUri(URI.create(uri));
							post.setMedia(file);
						}
						break;
					}
				}
			}
		}
		return post;
	}
	
	/**/
	
	public static void main(String[] args) throws ParseException {
		for(Group group : Group.SOURCE_SELL_BUY){
			GroupPostCollector collector = new GroupPostCollector(null,new DefaultFacebookClient(AbstractCollector.TEST_AT),group,DateUtils.parseDate("01/06/2014", "dd/MM/yyyy")
					,DateUtils.parseDate("01/07/2014", "dd/MM/yyyy"),5l);
			collector.run();
			System.out.println(collector.getPosts().size()+" post(s)");
		}
	}


	
}