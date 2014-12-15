package org.cyk.system.facebookextension.tools.api.post;


public interface DirectoryProcessorListener<FILE_PROCESSOR extends AbstractFileProcessor,DIRECTORY_PROCESSOR extends AbstractDirectoryProcessor<FILE_PROCESSOR,?>> {

	void fileProcessorCreated(DIRECTORY_PROCESSOR directoryProcessor,FILE_PROCESSOR fileProcessor);
	
}
