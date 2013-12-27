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
package org.androidannotations.generation;

import java.io.IOException;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.api.SdkVersionHelper;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class OnBackPressedApiDependenciesTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(OnBackPressedApiDependenciesTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
		ensureOutputDirectoryIsEmpty();
	}

	@Test
	public void activity_with_on_back_pressed_generate_api_dependency() throws IOException {
		CompileResult result = compileFiles(ActivityWithOnBackPressedMethod.class);
		assertClassSourcesGeneratedToOutput(SdkVersionHelper.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void activity_without_on_back_pressed_do_not_generate_api_dependency() throws IOException {
		CompileResult result = compileFiles(ActivityWithBackgroundMethod.class);
		assertClassSourcesNotGeneratedToOutput(SdkVersionHelper.class);
		assertCompilationSuccessful(result);
	}

}
