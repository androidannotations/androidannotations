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
package org.androidannotations.otto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class OttoGenerationTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(OttoGenerationTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void enhancedClassCompilesSuccessfully() {
		assertCompilationSuccessful(compileFiles(EnhancedBean.class));
	}

	@Test
	public void nonEnhancedClassCompilesSuccessfully() {
		assertCompilationSuccessful(compileFiles(NonEnhancedBean.class));
	}

	@Test
	public void enhancedClassGeneratesCode() {
		CompileResult result = compileFiles(EnhancedBean.class);
		File generatedFile = toGeneratedFile(EnhancedBean.class);

		assertCompilationSuccessful(result);

		assertTrue(generatedFile.exists());
	}

	@Test
	public void nonEnhancedClassDoesNotGenerateCode() {
		CompileResult result = compileFiles(NonEnhancedBean.class);
		File generatedFile = toGeneratedFile(NonEnhancedBean.class);

		assertCompilationSuccessful(result);

		assertFalse(generatedFile.exists());
	}
}
