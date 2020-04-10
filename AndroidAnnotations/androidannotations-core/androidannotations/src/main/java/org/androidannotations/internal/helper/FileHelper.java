/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2020 the AndroidAnnotations project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.internal.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public final class FileHelper {

	private FileHelper() {
	}

	public static File findRootProject(ProcessingEnvironment processingEnv) throws FileNotFoundException {
		FileHolder rootProjectHolder = findRootProjectHolder(processingEnv);
		return rootProjectHolder.projectRoot;
	}

	/**
	 * We use a dirty trick to find the AndroidManifest.xml file, since it's not
	 * available in the classpath. The idea is quite simple : create a fake class
	 * file, retrieve its URI, and start going up in parent folders to find the
	 * AndroidManifest.xml file. Any better solution will be appreciated.
	 */
	public static FileHolder findRootProjectHolder(ProcessingEnvironment processingEnv) throws FileNotFoundException {
		FileHolder rootProjectHolder = findKaptRootProjectHolder(processingEnv);

		if (rootProjectHolder != null) {
			return rootProjectHolder;
		}

		Filer filer = processingEnv.getFiler();

		FileObject dummySourceFile;
		try {
			dummySourceFile = filer.createResource(StandardLocation.SOURCE_OUTPUT, "", "dummy" + System.currentTimeMillis());
		} catch (IOException ignored) {
			throw new FileNotFoundException();
		}

		return createFileHolder(dummySourceFile.toUri().toString());
	}

	private static FileHolder createFileHolder(String dummySourceFilePath) throws FileNotFoundException {
		if (dummySourceFilePath.startsWith("file:")) {
			if (!dummySourceFilePath.startsWith("file://")) {
				dummySourceFilePath = "file://" + dummySourceFilePath.substring("file:".length());
			}
		} else {
			dummySourceFilePath = "file://" + dummySourceFilePath;
		}

		URI cleanURI;
		try {
			cleanURI = new URI(dummySourceFilePath);
		} catch (URISyntaxException e) {
			throw new FileNotFoundException();
		}

		File dummyFile = new File(cleanURI);
		File sourcesGenerationFolder = dummyFile.getParentFile();
		File projectRoot = sourcesGenerationFolder.getParentFile();

		return new FileHolder(dummySourceFilePath, sourcesGenerationFolder, projectRoot);
	}

	private static FileHolder findKaptRootProjectHolder(ProcessingEnvironment processingEnv) throws FileNotFoundException {
		String kaptFolderOption = processingEnv.getOptions().get("kapt.kotlin.generated");

		if (kaptFolderOption == null) {
			return null;
		}

		String dummySourceFile = kaptFolderOption.replace("kaptKotlin", "kapt") + File.separator + "dummy";
		String dummySourceFilePath = new File(dummySourceFile).toURI().toString();

		return createFileHolder(dummySourceFilePath);
	}

	public static File resolveOutputDirectory(ProcessingEnvironment processingEnv) throws FileNotFoundException {
		File rootProject = FileHelper.findRootProject(processingEnv);

		// Target folder - Maven
		File targetFolder = new File(rootProject, "target");
		if (targetFolder.isDirectory() && targetFolder.canWrite()) {
			return targetFolder;
		}

		// Build folder - Gradle
		File buildFolder = new File(rootProject, "build");
		if (buildFolder.isDirectory() && buildFolder.canWrite()) {
			return buildFolder;
		}

		// Bin folder - Eclipse
		File binFolder = new File(rootProject, "bin");
		if (binFolder.isDirectory() && binFolder.canWrite()) {
			return binFolder;
		}

		// Fallback to projet root folder
		return rootProject;
	}

	static class FileHolder {
		String dummySourceFilePath;
		File sourcesGenerationFolder;
		File projectRoot;

		FileHolder(String dummySourceFilePath, File sourcesGenerationFolder, File projectRoot) {
			this.dummySourceFilePath = dummySourceFilePath;
			this.sourcesGenerationFolder = sourcesGenerationFolder;
			this.projectRoot = projectRoot;
		}
	}

}
