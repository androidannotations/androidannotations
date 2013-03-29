/**
 * Copyright (C) 2010-2013 eBusiness Information, Excilys Group
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

public class AndroidManifestFinderTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void fails_if_no_manifest() {
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void finds_specified_manifest() {
		addManifestProcessorParameter(AndroidManifestFinderTest.class);
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void fails_if_cannot_find_specified_manifest() {
		addProcessorParameter("androidManifestFile", "/some/random/path/AndroidManifest.xml");
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void finds_manifest_in_generated_source_parent_folder() throws Exception {
		copyManifestToParentOfOutputDirectory();
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationSuccessful(result);
		deleteManifestFromParentOfOutputDirectory();
	}

	@Test
	public void fails_if_cannot_parse_manifest() {
		addManifestProcessorParameter(AndroidManifestFinderTest.class, "ParseErrorManifest.xml");
		CompileResult result = compileFiles(SomeClass.class);
		assertCompilationErrorWithNoSource(result);
		assertCompilationErrorCount(1, result);
	}

	private void deleteManifestFromParentOfOutputDirectory() {
		manifestFileInParentOfOutputDirectory().delete();
	}

	private void copyManifestToParentOfOutputDirectory() throws FileNotFoundException, IOException {
		InputSupplier<InputStream> from = new InputSupplier<InputStream>() {
			@Override
			public InputStream getInput() throws IOException {
				return AndroidManifestFinderTest.class.getResourceAsStream("AndroidManifest.xml");
			}
		};
		Files.copy(from, manifestFileInParentOfOutputDirectory());
	}

	private File manifestFileInParentOfOutputDirectory() {
		File outputDirectory = getOuputDirectory();
		File manifestFile = new File(outputDirectory.getParentFile(), "AndroidManifest.xml");
		return manifestFile;
	}

}
