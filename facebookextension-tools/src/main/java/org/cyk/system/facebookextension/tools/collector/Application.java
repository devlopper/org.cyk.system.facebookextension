package org.cyk.system.facebookextension.tools.collector;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.cyk.system.root.model.AbstractIdentifiable;

import com.restfb.FacebookClient.DebugTokenInfo;
 
@Getter @Setter
public class Application extends AbstractIdentifiable implements Serializable {

	private static final long serialVersionUID = -8020615837291427522L;

	private String name;
	
	private String secret;
	
	private String accessToken;
	
	private DebugTokenInfo accessTokenInfo;
	
	public Application() {}

	public Application(String name, String secret, String accessToken) {
		super();
		this.name = name;
		this.secret = secret;
		this.accessToken = accessToken;
	}
	
	
}
