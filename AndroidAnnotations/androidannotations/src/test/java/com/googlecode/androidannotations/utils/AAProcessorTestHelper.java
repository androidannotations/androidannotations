package com.googlecode.androidannotations.utils;

import java.io.File;

import com.googlecode.androidannotations.helper.ModelConstants;

public class AAProcessorTestHelper extends ProcessorTestHelper {

	public void addManifestProcessorParameter(Class<?> classOfPackagingContainingManifest) {
		String manifestPath = classOfPackagingContainingManifest.getResource("AndroidManifest.xml").getPath();
		addProcessorParameter("androidManifestFile", manifestPath);
	}

	public File toGeneratedFile(Class<?> compiledClass) {
		File output = new File(OUTPUT_DIRECTORY, toPath(compiledClass.getPackage()) + "/" + compiledClass.getSimpleName() + ModelConstants.GENERATION_SUFFIX + SOURCE_FILE_SUFFIX);
		return output;
	}

}
