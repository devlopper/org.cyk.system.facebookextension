package org.cyk.system.facebookextension.tools.api.post;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;

public class PostFileNameFilter implements FilenameFilter {

	public static final String EXTENSION = "post";
	
	@Override
	public boolean accept(File dir, String name) {
		return FilenameUtils.isExtension(name, EXTENSION);
	}

}
