package org.cyk.system.facebookextension.tools.provider.restfb;

import java.io.Serializable;

import lombok.Getter;

import org.cyk.system.facebookextension.tools.api.FqlPost;

import com.restfb.Facebook;
import com.restfb.json.JsonObject;

@Getter
public class RestFBFqlPost implements FqlPost<JsonObject>,Serializable {

	private static final long serialVersionUID = 3329595504914830606L;

	@Facebook public String post_id;
	
	@Facebook public int created_time;
	
	@Facebook public String message;
	
	@Facebook public JsonObject attachment;
	
}
