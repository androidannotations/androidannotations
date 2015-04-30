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
package org.androidannotations.menu;

import java.io.IOException;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class OptionMenuErrorsTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(OptionMenuErrorsTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void androidMenuitemInSherlockActivity() throws IOException {
		CompileResult result = compileFiles(SherlockActivityWithAndroidMenu.class);
		assertCompilationErrorOn(SherlockActivityWithAndroidMenu.class, "@OptionsMenuItem", result);
	}

	@Test
	public void sherlockMenuitemInAndroidActivity() throws IOException {
		CompileResult result = compileFiles(ActivityWithSherlockMenu.class);
		assertCompilationErrorOn(ActivityWithSherlockMenu.class, "@OptionsMenuItem", result);
	}

}
