package org.cyk.system.facebookextension.tools.collector.post.wall;

import java.io.Serializable;

import org.cyk.system.facebookextension.tools.collector.Application;
import org.cyk.system.facebookextension.tools.collector.Group;
import org.cyk.system.facebookextension.tools.collector.post.PostWorker;

public class SellBuyWorker extends PostWorker implements Serializable {

	private static final long serialVersionUID = -1795270653256817728L;

	public SellBuyWorker(Application application) {
		super(application, Group.SOURCE_SELL_BUY, Group.SELL_BUY);
	}

	
	
}
