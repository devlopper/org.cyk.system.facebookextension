package org.cyk.system.facebookextension.tools.collector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.cyk.system.facebookextension.model.Post;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

public class Utils {

	public static final String ACCESS_TOKEN_URL_FORMAT = "https://graph.facebook.com/oauth/access_token?client_id=%s&client_secret=%s&grant_type=client_credentials";
	
	public static Application DEV_APPLICATION = new Application("317227825044284", "adcec35241e9398a8a5dc919235152a4", "317227825044284|dvARkqh7RCIF2zt8uqeJsUGyYYg");
	
	public static void createAccessToken(Application application) throws IOException{
		URL url = new URL(String.format(ACCESS_TOKEN_URL_FORMAT, application.getName(),application.getSecret()));
		InputStream inputStream = null;
		String line;
		try {
			inputStream = url.openStream();
			line = IOUtils.toString(inputStream);
		} finally {
			if(inputStream!=null) 
				try {
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		application.setAccessToken(line.substring(13));
	}
	
	public static FacebookClient createFacebookClient(Application application){
		return new DefaultFacebookClient(application.getAccessToken());
	}
	
	public static void clean(List<Post> posts){
		//System.out.println(posts.size()+" Posts before removing no message and media");
		for(int i=0;i<posts.size();)
			if( StringUtils.isEmpty(posts.get(i).getMessage()) && posts.get(i).getMedia()==null)
				posts.remove(i);
			else
				i++;
		//System.out.println(posts.size()+" Posts after removing no message and media");
		List<Post> temp = new LinkedList<Post>(posts);
		posts.clear();
		Set<String> messages = new HashSet<String>();
		//System.out.println(temp.size()+" Posts before removing duplicate");
		for(int i=0;i<temp.size();)
			if(messages.add(temp.get(i).getMessage().toLowerCase()))
				posts.add(temp.remove(i));
			else
				i++;
		//System.out.println(posts.size()+" Posts after removing duplicate");
	}

}
