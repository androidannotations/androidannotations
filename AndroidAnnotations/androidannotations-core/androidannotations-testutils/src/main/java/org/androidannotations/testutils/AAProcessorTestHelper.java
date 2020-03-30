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
package org.androidannotations.testutils;

import java.io.File;
import java.lang.reflect.Method;

public class AAProcessorTestHelper extends ProcessorTestHelper {

	public void addManifestProcessorParameter(Class<?> classOfPackagingContainingManifest) {
		addManifestProcessorParameter(classOfPackagingContainingManifest, "AndroidManifest.xml");
	}

	public void addManifestProcessorParameter(Class<?> classOfPackagingContainingManifest, String manifestFileName) {
		String manifestPath = toPath(classOfPackagingContainingManifest, manifestFileName);
		addProcessorParameter("androidManifestFile", manifestPath);
	}

	public File toGeneratedFile(Class<?> compiledClass) {
		File output = new File(OUTPUT_DIRECTORY, toPath(compiledClass.getPackage()) + "/" + compiledClass.getSimpleName() + getAndroidAnnotationsClassSuffix() + SOURCE_FILE_SUFFIX);
		return output;
	}

	public File toGeneratedFile(Class<?> classOfPackagingContainingFile, String compiledClassSimpleName) {
		return new File(OUTPUT_DIRECTORY, toPath(classOfPackagingContainingFile.getPackage()) + "/" + compiledClassSimpleName + getAndroidAnnotationsClassSuffix() + SOURCE_FILE_SUFFIX);
	}

	public String toPath(Class<?> classOfPackagingContainingFile, String filename) {
		return classOfPackagingContainingFile.getResource(filename).getPath();
	}

	public String[] defPath(String... filesName) {
		String[] paths = new String[filesName.length];
		for (int i = 0; i < filesName.length; i++) {
			paths[i] = toPath(this.getClass(), filesName[i]);
		}
		return paths;
	}

	/**
	 * This module cannot depend on androidannotations module, because that would
	 * introduce a cycle in the dependency graph. That is why we cannot directly
	 * reference the <code>classSuffix</code> method. We still have to use this
	 * method, so we call it reflectively.
	 *
	 * @return the result of
	 *         <code>org.androidannotations.helper.ModelConstants.classSuffix()</code>
	 */
	private static String getAndroidAnnotationsClassSuffix() {
		try {
			Class<?> modelConstantsClazz = Class.forName("org.androidannotations.helper.ModelConstants");
			Method classSuffixMethod = modelConstantsClazz.getMethod("classSuffix");
			return (String) classSuffixMethod.invoke(null);
		} catch (ReflectiveOperationException e) {
			return "_";
		}
	}
}
