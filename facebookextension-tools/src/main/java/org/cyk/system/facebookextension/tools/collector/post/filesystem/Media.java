package org.cyk.system.facebookextension.tools.collector.post.filesystem;

import java.io.IOException;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

@Getter @Setter
public class Media implements Serializable {

	private static final long serialVersionUID = 5795221469471257633L;

	private String url;
	private String mimeType,facebookConnection;
	private byte[] content;
	
	public Media(String url,String me,HttpClient httpClient) throws IllegalStateException, IOException {
		super();
		this.url = url;
		facebookConnection = me+"/";
		if(StringUtils.isEmpty(url))
			facebookConnection += "feed";
		else
			fetchContent(httpClient);
	}
	
	private void fetchContent(HttpClient httpClient) throws IllegalStateException, IOException{
		GetMethod httpGet = new GetMethod(url);
		try {
			httpClient.executeMethod(httpGet);
			if(httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE)!=null && httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue()!=null){
				mimeType = httpGet.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue();
				if(mimeType==null)
					System.out.println("No MimeType Found for : "+url);
				else if(mimeType.startsWith("image/"))
					facebookConnection += "photos";
				else if(mimeType.startsWith("video/"))
					facebookConnection += "video";
				else 
					System.out.println("MimeType may not be handled properly. - "+url+" | "+mimeType);
			}else
				System.out.println("No Content Type can be retrieved for Http Response");
			content = IOUtils.toByteArray(httpGet.getResponseBodyAsStream());
		} finally{
			httpGet.releaseConnection();
		}
	}
	
}
