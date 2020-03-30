/**
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
package org.androidannotations.ebean;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class RootFragmentTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(RootFragmentTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void eBeanOnFragmentCompiles() {
		assertCompilationSuccessful(compileFiles(SomeBeanWithRootFragment.class));
	}

	@Test
	public void eBeanOnCustomFragmentCompiles() {
		assertCompilationSuccessful(compileFiles(SomeBeanWithRootFragmentWithCustomFragment.class));
	}

	@Test
	public void eBeanOnCustomSupportFragmentsCompiles() {
		assertCompilationSuccessful(compileFiles(defPath("support/Fragment.java", "CustomSupportFragment.java", "SomeBeanWithRootFragmentWithCustomSupportFragment.java")));
	}

	@Test
	public void eBeanOnSupportFragmentsCompiles() {
		assertCompilationSuccessful(compileFiles(defPath("support/Fragment.java", "SomeBeanWithRootFragmentWithSupportFragment.java")));
	}

	@Test
	public void eBeanOnObjectDoesNotCompile() {
		assertCompilationError(compileFiles(SomeBeanWithRootFragmentWithObject.class));
	}

	@Test
	public void eBeanOnPrivateFieldDoesNotCompile() {
		assertCompilationError(compileFiles(SomeBeanWithRootFragmentWithPrivateField.class));
	}

	@Test
	public void eBeanOnSomeInterfaceDoesNotCompile() {
		assertCompilationError(compileFiles(SomeBeanWithRootFragmentWithSomeInterface.class));
	}

}
