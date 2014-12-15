package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.cyk.utility.common.RunnableListener;
import org.cyk.utility.common.ThreadPoolExecutor;
import org.cyk.utility.common.cdi.AbstractBean;

@Getter @Setter
public abstract class AbstractDirectoryProcessor<FILE_PROCESSOR extends AbstractFileProcessor,DIRECTORY_PROCESSOR_LISTENER extends DirectoryProcessorListener<FILE_PROCESSOR, AbstractDirectoryProcessor<FILE_PROCESSOR,?>>> extends AbstractBean implements Runnable, Serializable {

	private static final long serialVersionUID = 2689083520680885739L;

	protected Collection<RunnableListener<?>> runnableListeners = new ArrayList<>();
	
	protected File directory;
	protected File[] files;
	protected Boolean sequential;
	protected ThreadPoolExecutor threadPoolExecutor;
	protected Class<FILE_PROCESSOR> fileProcessorClass;
	protected Collection<DIRECTORY_PROCESSOR_LISTENER> directoryProcessorListeners = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	public AbstractDirectoryProcessor(File directory,Boolean sequential) {
		super();
		this.directory = directory;
		this.sequential = sequential;
		this.fileProcessorClass = (Class<FILE_PROCESSOR>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}	
	
	@Override
	public void run() {
		files = directory.listFiles(new PostFileNameFilter());
		if(Boolean.TRUE.equals(sequential)){
			for(File file : files)
				createFileProcessor(file).run();
		}else{
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(1000));
			for(File file : files)
				threadPoolExecutor.execute(createFileProcessor(file));
			threadPoolExecutor.waitTermination(10, TimeUnit.MINUTES);
		}
	}

	protected FILE_PROCESSOR createFileProcessor(File file){
		FILE_PROCESSOR fileProcessor = newInstance(fileProcessorClass);
		fileProcessor.setFile(file);
		for(DIRECTORY_PROCESSOR_LISTENER directoryProcessorListener : directoryProcessorListeners)
			directoryProcessorListener.fileProcessorCreated(this, fileProcessor);
		initFileProcessor(fileProcessor);
		return fileProcessor;
	}
	
	protected void initFileProcessor(FILE_PROCESSOR runnable){}
	
	/**/
	
	
	
}
