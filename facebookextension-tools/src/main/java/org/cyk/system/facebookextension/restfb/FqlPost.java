package org.cyk.system.facebookextension.restfb;

import java.io.Serializable;

import com.restfb.Facebook;
import com.restfb.json.JsonObject;

public class FqlPost implements Serializable{
	
	private static final long serialVersionUID = 8919195125852733641L;

	@Facebook
	public String post_id;

	@Facebook
	public int created_time;
	
	@Facebook
	public String message;
	
	@Facebook
	public JsonObject attachment;
	
	@Override
	public String toString() {
		return message;
	}
	
	
}