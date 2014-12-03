package org.cyk.system.facebookextension.tools.collector;

public interface PostCollectorListener {

	void processPosts(PostCollector postCollector);

	void authenticationException(PostCollector postCollector);

	void stopped(PostCollector postCollector);

	void started(PostCollector postCollector);
	
}
