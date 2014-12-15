package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.cyk.system.facebookextension.model.Post;
import org.cyk.utility.common.cdi.BeanListener;

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
	
	
	 
	@SuppressWarnings("unchecked")
	public static Collection<Post> readPosts(File file,Collection<BeanListener> beanListeners){
		try {
			//beanListener.info(file+" : Reading ");
			return (Collection<Post>) SerializationUtils.deserialize(new FileInputStream(file));
		} catch (Exception e) {
			for(BeanListener beanListener : beanListeners)
				beanListener.info(e.toString());
			return null;
		}
	}
	
	public static void writePosts(File file,Collection<Post> posts,Collection<BeanListener> beanListeners){
		try {
			SerializationUtils.serialize((Serializable) posts,new FileOutputStream(file));
		} catch (Exception e) {
			for(BeanListener beanListener : beanListeners)
				beanListener.info(e.toString());
		}
	}
	
}
