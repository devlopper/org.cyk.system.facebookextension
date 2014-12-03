package org.cyk.system.facebookextension.tools.collector.post.filesystem;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.cyk.system.facebookextension.model.Post;

import com.restfb.BinaryAttachment;

@Getter @Setter
@AllArgsConstructor
public class PostFileRecord implements Serializable {

	private static final long serialVersionUID = 2542309693893472816L;

	private Post post;
	private String connection;
	private byte[] mediaContent;
	
	public PostFileRecord() {}

	public BinaryAttachment getBinaryAttachment(){
		if(mediaContent==null)
			return null;
		return BinaryAttachment.with("media", new ByteArrayInputStream(mediaContent));
	}
	
}
