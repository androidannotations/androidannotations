/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.generation;

import java.io.File;
import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class GenerateFinalClassesFlagTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(ActivityInManifest.class);
		addProcessor(AndroidAnnotationProcessor.class);
		ensureOutputDirectoryIsEmpty();
	}

	@Test
	public void generateFinalClass() throws IOException {
		addProcessorParameter("generateFinalClasses", "true");

		CompileResult result = compileFiles(ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);

		assertGeneratedClassMatches(generatedFile, "public final class ActivityInManifest_");
	}

	@Test
	public void generateNonFinalClass() throws IOException {
		addProcessorParameter("generateFinalClasses", "false");

		CompileResult result = compileFiles(ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);

		assertGeneratedClassMatches(generatedFile, "public class ActivityInManifest_");
	}
}
