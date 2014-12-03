package org.cyk.system.facebookextension.tools.collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.utility.common.cdi.AbstractBean;

@Getter @Setter
public class PostRemoteResourceReader extends AbstractBean implements Runnable, Serializable {

	private static final long serialVersionUID = -3214220567058528121L;
	
	private Collection<PostRemoteResourceReaderListener> postRemoteResourceReaderListeners = new ArrayList<>();
	
	private Post post;
	private String me;
	private HttpClient httpClient;
	
	public PostRemoteResourceReader(Post post,String me,HttpClient httpClient) {
		super();
		this.post = post;
		this.me = me;
		this.httpClient = httpClient;
	}
	
	@Override
	public void run() {
		String facebookConnection = me+"/";
		if(post.getMedia()==null){
			facebookConnection += "feed";
		}else{
			GetMethod httpGet = new GetMethod(post.getMedia().getUri().toString());
			try {
				httpClient.executeMethod(httpGet);
				if(httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE)!=null && httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue()!=null){
					post.getMedia().setExtension(httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue());
					if(post.getMedia().getExtension()==null)
						__writeInfo__("No MimeType Found for : "+post.getMedia().getUri());
					else if(post.getMedia().getExtension().startsWith("image/"))
						facebookConnection += "photos";
					else if(post.getMedia().getExtension().startsWith("video/"))
						facebookConnection += "video";
					else 
						__writeInfo__("MimeType may not be handled properly. - "+post.getMedia().getUri()+" | "+post.getMedia().getExtension());
				}else
					__writeInfo__("No Content Type can be retrieved for Http Response");
				post.getMedia().setBytes(IOUtils.toByteArray(httpGet.getResponseBodyAsStream()));
			} catch (Exception e) {
				__writeInfo__(e.toString());
			} finally{
				httpGet.releaseConnection();
			}
		}
	}

}
