package org.cyk.system.facebookextension.tools.api.post;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.utility.common.cdi.AbstractBean;

@Getter @Setter
public class PostRemoteResourceReader extends AbstractBean implements Runnable, Serializable {

	private static final long serialVersionUID = -3214220567058528121L;
	
	private Collection<PostRemoteResourceReaderListener> postRemoteResourceReaderListeners = new ArrayList<>();
	
	private Post post;
	private HttpClient httpClient;
	
	public PostRemoteResourceReader(Post post,HttpClient httpClient) {
		super();
		this.post = post;
		this.httpClient = httpClient;
	}
	
	@Override
	public void run() {
		if(post.getMedia()==null){
			;
		}else{
			GetMethod httpGet = new GetMethod(post.getMedia().getUri().toString());
			try {
				httpClient.executeMethod(httpGet);
				post.getMedia().setBytes(IOUtils.toByteArray(httpGet.getResponseBodyAsStream()));
				String[] contentType = StringUtils.split(httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue(), '/');
				if(contentType.length>0){
					post.getMedia().setMime(contentType[0]);
					post.getMedia().setExtension(contentType[1]);
				}
			} catch (Exception e) {
				__writeInfo__(e.toString());
			} finally{
				httpGet.releaseConnection();
			}
		}
	}

}
