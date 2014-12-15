package org.cyk.system.facebookextension.tools.api.post;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;

public class TestPostToGroup {

	public void postToGroup(){
		String accessToken = "CAAEgSWBi5WYBAOaFSAS0YWOEeRdnf2wdpiCfljdUOMBJJAtiHmWXdms5QJhYJJxPlQgOUut0drIihfwyZCthNXGMjO17DOEDVvsZAGy5vd13mk3uKbvOuJNZByzgooSdXmHEtWGHm1OKNJqUp3TAFbU8zoF9SYuhdNmfVbCe4f7HQUVvKvw0M8Sx7QsZAU2lgufYY9JSUevCCVomd3sVpi7eJecvEGkZD";
		new DefaultFacebookClient(accessToken)
		.publish("224490197721426/feed", Post.class,Parameter.with("message","My 2nd Message on Wall OHH 2."));
		System.out.println("done");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestPostToGroup().postToGroup();
		
	}

}
