package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
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
import org.cyk.utility.common.cdi.BeanAdapter;

@Getter @Setter
public class FileRemoteResourceReader extends AbstractFileProcessor implements PostRemoteResourceReaderListener, Serializable {

	private static final long serialVersionUID = 8424938402931143112L;

	private Collection<FileRemoteResourceReaderListener> fileRemoteResourceReaderListeners = new ArrayList<>();
	
	@Override
	protected void process(Collection<Post> posts) {
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	    HttpClient httpClient = new HttpClient(connectionManager);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(posts.size()));
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
			for(Post post : toBeRead){
				PostRemoteResourceReader postRemoteResourceReader = new PostRemoteResourceReader(post,httpClient);
				for(FileRemoteResourceReaderListener postFileRemoteResourceReaderListener : fileRemoteResourceReaderListeners)
					postFileRemoteResourceReaderListener.postRemoteResourceReaderCreated(this,postRemoteResourceReader);
				threadPoolExecutor.execute(postRemoteResourceReader);
			}
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
		FileRemoteResourceReader fileRemoteResourceReader = new FileRemoteResourceReader();
		fileRemoteResourceReader.setFile(new File("H:/fbposts/201401010000 201401020000.txt"));
		fileRemoteResourceReader.getBeanListeners().add(new BeanAdapter(){
			@Override
			public void info(String message) {
				System.out.println(message);
			}
		});
		fileRemoteResourceReader.run();
	}



}
