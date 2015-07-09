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
package org.androidannotations.manifest;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AndroidManifestFinderTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void failsIfNoManifest() {
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void findsSpecifiedManifest() {
		addManifestProcessorParameter(AndroidManifestFinderTest.class);
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void failsIfCannotFindSpecifiedManifest() {
		addProcessorParameter("androidManifestFile", "/some/random/path/AndroidManifest.xml");
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void findsManifestInGeneratedSourceParentFolder() throws Exception {
		copyManifestToParentOfOutputDirectory();
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationSuccessful(result);
		deleteManifestFromParentOfOutputDirectory();
	}

	@Test
	public void failsIfCannotParseManifest() {
		addManifestProcessorParameter(AndroidManifestFinderTest.class, "ParseErrorManifest.xml");
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

	private void deleteManifestFromParentOfOutputDirectory() {
		manifestFileInParentOfOutputDirectory().delete();
	}

	private void copyManifestToParentOfOutputDirectory() throws IOException {
		Files.copy(AndroidManifestFinderTest.class.getResourceAsStream("AndroidManifest.xml"), manifestFileInParentOfOutputDirectory().toPath());
	}

	private File manifestFileInParentOfOutputDirectory() {
		File outputDirectory = getOuputDirectory();
		File manifestFile = new File(outputDirectory.getParentFile(), "AndroidManifest.xml");
		return manifestFile;
	}

}
