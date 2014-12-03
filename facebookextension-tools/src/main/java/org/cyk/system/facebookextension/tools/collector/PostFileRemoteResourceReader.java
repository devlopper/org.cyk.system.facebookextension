package org.cyk.system.facebookextension.tools.collector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang3.SerializationUtils;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.utility.common.ThreadPoolExecutor;
import org.cyk.utility.common.cdi.AbstractBean;
import org.cyk.utility.common.cdi.BeanAdapter;

@Getter @Setter
public class PostFileRemoteResourceReader extends AbstractBean implements Runnable,PostRemoteResourceReaderListener, Serializable {

	private static final long serialVersionUID = 8424938402931143112L;

	private Collection<PostFileRemoteResourceReaderListener> postFileRemoteResourceReaderListeners = new ArrayList<>();
	
	private File file;
	private String me;
	
	public PostFileRemoteResourceReader(File file, String me) {
		super();
		this.file = file;
		this.me = me;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		Collection<Post> posts;
		try {
			__writeInfo__(file+" : Reading ");
			posts = (Collection<Post>) SerializationUtils.deserialize(new FileInputStream(file));
		} catch (Exception e) {
			__writeInfo__(e.toString());
			return;
		}
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	    HttpClient httpClient = new HttpClient(connectionManager);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(150));
		Collection<Post> gotMedia = new ArrayList<>();
		for(Post post : posts)
			if(post.getMedia()!=null)
				gotMedia.add(post);
		
		Collection<Post> toBeRead = new ArrayList<>();
		for(Post post : gotMedia)
			if(post.getMedia()!=null && post.getMedia().getBytes()==null)
				toBeRead.add(post);
		if(toBeRead.isEmpty())
			__writeInfo__(file+" : Completed!");
		else{
			__writeInfo__(file+" : "+gotMedia.size()+"/"+posts.size()+" got media");
			__writeInfo__(file+" : "+toBeRead.size()+"/"+gotMedia.size()+" to be read");
			for(Post post : toBeRead)
				threadPoolExecutor.execute(new PostRemoteResourceReader(post,me,httpClient));
			threadPoolExecutor.waitTermination(10, TimeUnit.MINUTES);
		
			int c = 0;
			for(Post post : toBeRead){
				if(post.getMedia()!=null && post.getMedia().getBytes()==null)
					c++;
			}
			if(c>0)
				__writeInfo__(file+" : "+c+"/"+toBeRead.size()+" media(s) cannot be read");
			
			__writeInfo__(file+" : Updating ");
			try {	
				SerializationUtils.serialize((Serializable)posts, new FileOutputStream(file));
			} catch (Exception e) {
				__writeInfo__(e.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		PostFileRemoteResourceReader postFileRemoteResourceReader = new PostFileRemoteResourceReader(new File("H:/fbposts/201401030000 201401040000.txt"),"");
		postFileRemoteResourceReader.getBeanListeners().add(new BeanAdapter(){
			@Override
			public void info(String message) {
				System.out.println(message);
			}
		});
		postFileRemoteResourceReader.run();
	}



}
