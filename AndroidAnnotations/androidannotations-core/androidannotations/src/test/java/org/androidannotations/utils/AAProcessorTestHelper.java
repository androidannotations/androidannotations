/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
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
package org.androidannotations.utils;

import static org.androidannotations.helper.ModelConstants.classSuffix;

import java.io.File;

public class AAProcessorTestHelper extends ProcessorTestHelper {

	public void addManifestProcessorParameter(Class<?> classOfPackagingContainingManifest) {
		addManifestProcessorParameter(classOfPackagingContainingManifest, "AndroidManifest.xml");
	}

	public void addManifestProcessorParameter(Class<?> classOfPackagingContainingManifest, String manifestFileName) {
		String manifestPath = toPath(classOfPackagingContainingManifest, manifestFileName);
		addProcessorParameter("androidManifestFile", manifestPath);
	}

	public File toGeneratedFile(Class<?> compiledClass) {
		File output = new File(OUTPUT_DIRECTORY, toPath(compiledClass.getPackage()) + "/" + compiledClass.getSimpleName() + classSuffix() + SOURCE_FILE_SUFFIX);
		return output;
	}

	public String toPath(Class<?> classOfPackagingContainingFile, String filename) {
		return classOfPackagingContainingFile.getResource(filename).getPath();
	}

}
