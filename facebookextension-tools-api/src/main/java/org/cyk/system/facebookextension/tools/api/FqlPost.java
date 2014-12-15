package org.cyk.system.facebookextension.tools.api;


public interface FqlPost<ATTACHMENT> {
	
	String getPost_id();

	int getCreated_time();

	String getMessage();

	ATTACHMENT getAttachment();
	
}