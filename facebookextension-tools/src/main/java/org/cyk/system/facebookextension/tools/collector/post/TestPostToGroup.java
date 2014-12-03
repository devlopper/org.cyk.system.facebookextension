package org.cyk.system.facebookextension.tools.collector.post;

import org.cyk.system.facebookextension.tools.collector.AbstractCollector;
import org.cyk.system.facebookextension.tools.collector.Application;

import com.restfb.Parameter;
import com.restfb.types.FacebookType;

public class TestPostToGroup extends AbstractCollector {

	private static final long serialVersionUID = -1578924383751834439L;

	public TestPostToGroup(Application application) {
		super(application);
		
	}
	
	public void postToGroup(){
		createFacebookClient().publish("132346010261919/feed", FacebookType.class,Parameter.with("message","My 1st Message on Wall."));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application application = new Application("317227825044284", "adcec35241e9398a8a5dc919235152a4",TEST_AT);
		new TestPostToGroup(application).postToGroup();
		
	}

}
