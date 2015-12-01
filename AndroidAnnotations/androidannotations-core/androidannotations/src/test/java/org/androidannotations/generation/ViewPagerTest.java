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

public class ViewPagerTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void compileActivityWithViewPager() {
		addManifestProcessorParameter(ViewPagerTest.class, "AndroidManifestForDrawable.xml");
		assertCompilationSuccessful(compileFiles(ActivityWithViewPager.class));
	}

	@Test
	public void compileFailForRegisterDuplicatePageScrolled() {
		addManifestProcessorParameter(ViewPagerTest.class, "AndroidManifestForDrawable.xml");
		CompileResult result = compileFiles(ActivityWithViewPagerDuplicatePageScrolled.class);
		assertCompilationError(result);
	}

	@Test
	public void compileFailForRegisterDuplicatePageSelected() {
		addManifestProcessorParameter(ViewPagerTest.class, "AndroidManifestForDrawable.xml");
		CompileResult result = compileFiles(ActivityWithViewPagerDuplicatePageSelected.class);
		assertCompilationError(result);
	}

	@Test
	public void compileFailForRegisterDuplicatePageScrollStateChanged() {
		addManifestProcessorParameter(ViewPagerTest.class, "AndroidManifestForDrawable.xml");
		CompileResult result = compileFiles(ActivityWithViewPagerDuplicatePageScrollStateChanged.class);
		assertCompilationError(result);
	}

	@Test
	public void compileFailForNonExistParameter() throws IOException {
		addManifestProcessorParameter(ViewPagerTest.class, "AndroidManifestForDrawable.xml");
		CompileResult result = compileFiles(ActivityWithViewPagerWrongParams.class);
		assertCompilationErrorOn(ActivityWithViewPagerWrongParams.class, "@PageSelected(R.id.myViewPager)", result);
		assertCompilationErrorOn(ActivityWithViewPagerWrongParams.class, "@PageScrolled(R.id.myViewPager)", result);
		assertCompilationErrorOn(ActivityWithViewPagerWrongParams.class, "@PageScrollStateChanged(R.id.myViewPager)", result);
		assertCompilationErrorOn(ActivityWithViewPagerWrongParams.class, "@PageScrolled(R.id.myViewPager2)", result);
		assertCompilationError(result);
	}

}
