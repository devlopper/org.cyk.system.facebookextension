package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.cyk.utility.common.RunnableAdapter;
import org.cyk.utility.common.cdi.BeanAdapter;

@Getter @Setter
public class RemoteResourceReader extends AbstractDirectoryProcessor<FileRemoteResourceReader,RemoteResourceReaderListener> implements Runnable,Serializable {

	private static final long serialVersionUID = -5906118319262741622L;
 
	public RemoteResourceReader(File directory) {
		super(directory, Boolean.TRUE);
	}
	
	public static void main(String[] args) { 
		RemoteResourceReader remoteResourceReader = new RemoteResourceReader(new File("H:/fbposts"));
		remoteResourceReader.getDirectoryProcessorListeners().add(new RemoteResourceReaderAdapter() {
			@Override
			public void fileProcessorCreated(
					AbstractDirectoryProcessor<FileRemoteResourceReader, ?> directoryProcessor,
					FileRemoteResourceReader fileProcessor) {
				fileProcessor.getBeanListeners().add(new BeanAdapter(){
					@Override
					public void info(String message) {
						System.out.println(message);
					}
				});
			}
		});
		remoteResourceReader.getRunnableListeners().add(new RunnableAdapter<Collector>(){
			@Override
			public void throwable(Collector aRunnable, Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		remoteResourceReader.getBeanListeners().add(new BeanAdapter(){
			@Override
			public void info(String message) {
				System.out.println(message);
			}
		});
		remoteResourceReader.run();
	}
	
}
