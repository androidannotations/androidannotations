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

import java.io.IOException;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class AndroidManifestErrorsTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(AndroidManifestErrorsTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activity_subclass_in_manifest_compiles() {
		CompileResult result = compileFiles(ActivitySubclassInManifest.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void activity_in_manifest_does_not_compile() throws IOException {
		CompileResult result = compileFiles(ActivityInManifest.class);
		assertCompilationErrorOn(ActivityInManifest.class, "@EActivity", result);
	}

	@Test
	public void activity_not_in_manifest_compiles_with_warning() throws IOException {
		CompileResult result = compileFiles(ActivityNotInManifest.class);
		assertCompilationWarningOn(ActivityNotInManifest.class, "@EActivity", result);
	}
}
