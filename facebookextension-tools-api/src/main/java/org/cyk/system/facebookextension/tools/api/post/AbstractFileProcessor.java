package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import org.cyk.system.facebookextension.model.Post;
import org.cyk.utility.common.cdi.AbstractBean;

@Getter @Setter
public abstract class AbstractFileProcessor extends AbstractBean implements Runnable {

	private static final long serialVersionUID = -6439986081362005897L;
	
	protected File file;

	@Override
	public void run() {
		Collection<Post> posts = Utils.readPosts(file, beanListeners);
		process(posts);
	}

	protected abstract void process(Collection<Post> posts);
	
	/**/
	
	
}
