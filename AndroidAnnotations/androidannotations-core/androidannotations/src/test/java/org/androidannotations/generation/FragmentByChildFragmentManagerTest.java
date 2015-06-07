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
package org.androidannotations.generation;

import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class FragmentByChildFragmentManagerTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void nativeFragmentByIdChildFragmentDoesNotCompileWithWithFroyo() throws IOException {
		addManifestProcessorParameter(FragmentByChildFragmentManagerTest.class, "AndroidManifestMinFroyo.xml");

		CompileResult result = compileFiles(toPath(FragmentByChildFragmentManagerTest.class, "Fragment.java"), NativeFragmentWithChild.class);

		assertCompilationErrorOn(NativeFragmentWithChild.class, "@FragmentByTag(childFragment = true)", result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void nativeFragmentByIdChildFragmentCompilesWithWithJB() {
		addManifestProcessorParameter(FragmentByChildFragmentManagerTest.class, "AndroidManifestMinJB.xml");

		CompileResult result = compileFiles(toPath(FragmentByChildFragmentManagerTest.class, "Fragment.java"), NativeFragmentWithChild.class);

		assertCompilationSuccessful(result);
	}

	@Test
	public void nativeFragmentByIdChildFragmentDoesNotCompileInActivity() throws IOException {
		addManifestProcessorParameter(FragmentByChildFragmentManagerTest.class, "AndroidManifestMinJB.xml");

		CompileResult result = compileFiles(toPath(FragmentByChildFragmentManagerTest.class, "Fragment.java"), ActivityWithChildFragment.class);

		assertCompilationErrorOn(ActivityWithChildFragment.class, "@FragmentByTag(childFragment = true)", result);
		assertCompilationErrorCount(1, result);
	}

	@Test
	public void supportFragmentByIdChildFragmentCompilesWhenFragmentHasGetChildFragmentManager() {
		addManifestProcessorParameter(FragmentByChildFragmentManagerTest.class, "AndroidManifestMinJB.xml");

		CompileResult result = compileFiles(toPath(FragmentByChildFragmentManagerTest.class, "support/Fragment.java"), //
				toPath(FragmentByChildFragmentManagerTest.class, "SupportFragmentWithChild.java"));

		assertCompilationSuccessful(result);
	}

	@Test
	public void supportFragmentByIdChildFragmentDoesNotCompileWhenFragmentDoesNotHaveGetChildFragmentManager() throws ClassNotFoundException, IOException {
		addManifestProcessorParameter(FragmentByChildFragmentManagerTest.class, "AndroidManifestMinJB.xml");

		CompileResult result = compileFiles(toPath(FragmentByChildFragmentManagerTest.class, "support/old/Fragment.java"), //
				toPath(FragmentByChildFragmentManagerTest.class, "SupportFragmentWithChild.java"));

		assertCompilationErrorOn("SupportFragmentWithChild", "@FragmentByTag", result);
		assertCompilationErrorCount(1, result);
	}
}
