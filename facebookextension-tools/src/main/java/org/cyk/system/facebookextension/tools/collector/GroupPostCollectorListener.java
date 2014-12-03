package org.cyk.system.facebookextension.tools.collector;

public interface GroupPostCollectorListener {

	void createQuery(GroupPostCollector groupPostCollector);

	void processResults(GroupPostCollector groupPostCollector);

	void processPosts(GroupPostCollector groupPostCollector);

	void exceptionWhileExecutingQuery(GroupPostCollector groupPostCollector);

	
	
}
