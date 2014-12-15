package org.cyk.system.facebookextension.tools.api.post;

import java.util.Collection;

import org.cyk.system.facebookextension.model.Post;

public interface FilePublisherListener extends FileProcessorListener {

	void publish(FilePublisher filePublisher, Collection<Post> posts,
			Collection<Post> notPublished);

	
}
