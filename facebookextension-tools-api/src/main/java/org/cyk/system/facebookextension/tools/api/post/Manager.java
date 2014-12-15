package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import org.cyk.utility.common.cdi.AbstractBean;

import com.restfb.FacebookClient;

@Getter @Setter
public class Manager extends AbstractBean implements Runnable, Serializable {

	private static final long serialVersionUID = 4466363181966156223L;

	private static final String COLLECTOR_DIRECTORY_NAME = "collected";
	private static final String PUBLISHER_DIRECTORY_NAME = "publishing";
	private static final String BACKUP_DIRECTORY_NAME = "backup";
	
	private FacebookClient facebookClient;
	private File directory,backupDirectory;
	private Collector collector;
	private RemoteResourceReader remoteResourceReader;
	private Publisher publisher;
	private Collection<ManagerListener> managerListeners = new ArrayList<>();
	
	public Manager(File directory) {
		this.directory = directory;
		backupDirectory = new File(directory, BACKUP_DIRECTORY_NAME);
		collector = new Collector(new File(directory, COLLECTOR_DIRECTORY_NAME));
		
		remoteResourceReader = new RemoteResourceReader(collector.getDirectory());
		
		publisher = new Publisher(new File(directory, PUBLISHER_DIRECTORY_NAME));
	}
	
	@Override
	public void run() {
		new Thread(collector).start();
		//new Thread(remoteResourceReader).start();
		//new Thread(publisher).start();
	}

}
