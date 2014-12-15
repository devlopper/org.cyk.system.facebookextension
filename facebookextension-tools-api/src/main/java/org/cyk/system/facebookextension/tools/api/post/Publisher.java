package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.cyk.system.facebookextension.model.Constants;
import org.cyk.system.facebookextension.tools.provider.restfb.RestFB;
import org.cyk.utility.common.RunnableSysout;
import org.cyk.utility.common.cdi.BeanSysout;

@Getter @Setter
public class Publisher extends AbstractDirectoryProcessor<FilePublisher,PublisherListener> implements Runnable, Serializable {

	private static final long serialVersionUID = 2689083520680885739L;

	private Group group;
	
	public Publisher(File directory) {
		super(directory,Boolean.TRUE);
	}	
	
	@Override
	protected void initFileProcessor(FilePublisher filePublisher) {
		filePublisher.setGroup(group);
	}
	
	public static void main(String[] args) {
		Group.loadGroups();
		final Publisher publisher = new Publisher(new File("H:/fbposts"));
		publisher.getRunnableListeners().add(new RunnableSysout<Publisher>());
		
		final RestFB restFB = new RestFB();
		restFB.getBeanListeners().add(new BeanSysout());
		
		publisher.setGroup(Group.collection(Constants.TAG_POST_SELL, Boolean.FALSE).iterator().next());
		publisher.getDirectoryProcessorListeners().add(new PublisherAdapter() {
			@Override
			public void fileProcessorCreated(AbstractDirectoryProcessor<FilePublisher, ?> directoryProcessor,FilePublisher fileProcessor) {
				fileProcessor.getFilePublisherListeners().add(restFB);
				fileProcessor.getBeanListeners().add(new BeanSysout());
			}
		});
		
		publisher.run();
	}
	
}
