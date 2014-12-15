package org.cyk.system.facebookextension.tools.provider.restfb;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cyk.system.facebookextension.tools.api.AbstractFacebookClient;
import org.cyk.system.facebookextension.tools.api.post.Collector;
import org.cyk.system.facebookextension.tools.api.post.FilePublisher;
import org.cyk.system.facebookextension.tools.api.post.Group;
import org.cyk.system.root.model.file.File;
import org.cyk.system.root.model.file.Tag;

import com.restfb.BinaryAttachment;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchRequest.BatchRequestBuilder;
import com.restfb.batch.BatchResponse;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Post;

public class RestFB extends AbstractFacebookClient<FacebookClient,JsonObject,RestFBFqlPost> implements Serializable {

	private static final long serialVersionUID = -8764604461153940309L;

	private static final Integer BATCH_REQUEST_SIZE = 50;
	
	public RestFB() {
		super(RestFBFqlPost.class);
	}
	
	@Override
	protected FacebookClient createClient(String accessToken) {
		return new DefaultFacebookClient(accessToken);
	}
	
	@Override
	protected void fetch(Collector collector, FacebookClient facebookClient) {
		if(apiType==null)
			apiType = ApiType.FQL;
		switch(apiType){
		case FQL:
			fql(collector, facebookClient);
			break;
		case GRAPH:
			graph(collector, facebookClient);
			break;
		}
	}
	
	@Override
	protected void publish(FilePublisher filePublisher,Collection<org.cyk.system.facebookextension.model.Post> posts,Collection<org.cyk.system.facebookextension.model.Post> notPublished,FacebookClient facebookClient) {
		List<BatchRequest> requests = new ArrayList<>();
		List<BinaryAttachment> binaryAttachments = new ArrayList<>();
	
		String location = filePublisher.getGroup().getId()+"/";
		List<org.cyk.system.facebookextension.model.Post> postList = new ArrayList<>(posts);
		int postBatchSize = 0,postBatchCount=0;
		for(int postIndex = 0;postIndex < posts.size();postIndex++){
			org.cyk.system.facebookextension.model.Post post = postList.get(postIndex);
			BatchRequestBuilder requestBuilder;
			if(post.getMedia()==null)
				requestBuilder = new BatchRequestBuilder(location+"feed");
			else{
				requestBuilder = new BatchRequestBuilder(location+(post.getMedia().getMime().equals("image")?"photos":"video"));
				String mediaName = "media"+RandomStringUtils.randomNumeric(5)+System.currentTimeMillis();
				requestBuilder.attachedFiles(mediaName);
				binaryAttachments.add(BinaryAttachment.with(mediaName+"."+post.getMedia().getExtension(), post.getMedia().getBytes()));
			}
			requestBuilder.method("POST").body(Parameter.with(MESSAGE, post.getMessage()));
			requests.add(requestBuilder.build());

			if(++postBatchSize<BATCH_REQUEST_SIZE && postIndex<posts.size())
				continue;
			
			List<BatchResponse> responses;
			try {
				System.out.println("Posting!!!");
				//Only One Call - Reduce network traffic
				responses = facebookClient.executeBatch(requests,binaryAttachments);
				requests.clear();
			} catch (Exception e) {
				e.printStackTrace();
				__writeInfo__(e.toString());
				notPublished.addAll(postList);
				return;
			}
			int i = 0;
			for(BatchResponse response : responses){
				if(response.getBody().startsWith("{\"error\"")){
					__writeInfo__(response.getBody());
					notPublished.add(postList.get(postBatchCount*postBatchSize+ i));
				}
				i++;
			}
			filePublisher.notPublished(notPublished);
			postBatchSize = 0;
			postBatchCount++;
			if(postIndex<postList.size()-1){
				//need to wait some time to avoid to be blocked
				__writeInfo__("Waiting before posting again");
				pause(DateUtils.MILLIS_PER_MINUTE * 3);
			}
		}
	}
	
	/**/
	
	private static void graph(Collector collector,FacebookClient facebookClient){
		
		List<BatchRequest> requests = new ArrayList<>();
		for(Group group : collector.getGroups()){
			BatchRequest request = new BatchRequestBuilder(group.getId()+"/feed").method("GET").
					body(Parameter.with("limit", collector.getLimit()), Parameter.with("since", unixTime(collector.getStartCreationDate())),
							Parameter.with("until", unixTime(collector.getEndCreationDate()))).build();	
			requests.add(request);
		}
		
		//Only One API Call
		System.out.println("Call"); long t = System.currentTimeMillis();
		List<BatchResponse> responses =  facebookClient.executeBatch(requests);
		System.out.println((System.currentTimeMillis()-t)/1000);
		int i = 0;
		for(Group group : collector.getGroups()){
			Connection<Post> connection = new Connection<Post>(facebookClient, responses.get(i++).getBody(), Post.class);
			createPosts(collector, group, connection.getData());
		}
		
	}
	
	private void fql(Collector collector,FacebookClient facebookClient){
		Integer groupBatchSize = collector.getGroups().size();
		Long startDate = unixTime(collector.getStartCreationDate()),endDate = unixTime(collector.getEndCreationDate()),
				limit = (collector.getLimit()==null?1000L:collector.getLimit())*groupBatchSize;
		
		for(Group group : collector.getGroups()){
			String query = String.format(SELECT_FORMAT, group.getId(),startDate,endDate,limit);
			try {
				createPost(collector,group, facebookClient.executeFqlQuery(query,fqlPostClass));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		/*
		GroupBatchs batchs = new GroupBatchs();
		Map<String, String> queries = new HashMap<String, String>();
		Integer i = 0,groupBatchCount,groupBatchSize = 2;//collector.getGroups().size();
		
		Boolean stop = null;
		System.out.println("Limit : "+limit);
		do{
			stop = Boolean.FALSE;
			groupBatchCount = 0;
			batchs.build(collector.getGroups(), groupBatchSize);
			System.out.println("Batchs : "+batchs.getCollection().size());
			for(GroupBatch batch : batchs.getCollection()){
				for(Group group : batch.getCollection())
					queries.put("list"+(i++), String.format(SELECT_FORMAT, group.getId(),startDate,endDate,limit));
				
				FqlPosts fqlPosts;
				try {
					//if(groupBatchCount>0)
					//	pause(10 * 1000);
					fqlPosts = facebookClient.executeFqlMultiquery(queries, FqlPosts.class);
					
				} catch (FacebookNetworkException exception) {
					System.out.println(exception);
					if(groupBatchSize>1){
						System.out.println("The group batch size ("+groupBatchSize+") might be too high. It will be reduced to "+(groupBatchSize=(groupBatchSize/2)));
						break;
					}else
						return;
				}
				
				i = 0;
				for(Group group : batch.getCollection()){
					try {
						@SuppressWarnings("unchecked")
						List<FqlPost> list = (List<FqlPost>) FieldUtils.readField(fqlPosts, "list"+(i++));
						createPost(collector,group, list);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
				if(++groupBatchCount==batchs.getCollection().size())
					stop = Boolean.TRUE;
			}
		}while(!Boolean.TRUE.equals(stop));
		*/
		
		
	}
	
	@Override
	protected void processAttachment(org.cyk.system.facebookextension.model.Post post,JsonObject attachment) {
		if(attachment.has("media")){
			JsonArray jsonArray = (JsonArray) attachment.get("media");	
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
	
	private static void createPosts(Collector collector,Group group,Collection<Post> fposts){
		if(fposts==null)
			return;
		for(Post fpost : fposts){
			org.cyk.system.facebookextension.model.Post post = new org.cyk.system.facebookextension.model.Post();
			for(String tagId : group.getTagsId()){
				Tag tag = new Tag();
				tag.setCode(tagId);
				post.getTags().add(tag);
			}
			post.setReference(fpost.getId());
			post.setCreationDate(fpost.getCreatedTime());
			post.setMessage(fpost.getMessage());
			if(StringUtils.isNotBlank(fpost.getPicture())){
				File file = new File();
				file.setUri(URI.create(fpost.getPicture()));
				post.setMedia(file);
			}
			collector.getPosts().add(post);
		}
		
		for(org.cyk.system.facebookextension.model.Post post : collector.getPosts())
			if(post.getCreationDate().before(collector.getStartCreationDate()) || post.getCreationDate().after(collector.getEndCreationDate()))
				System.out.println(post.getCreationDate());
	}

}
