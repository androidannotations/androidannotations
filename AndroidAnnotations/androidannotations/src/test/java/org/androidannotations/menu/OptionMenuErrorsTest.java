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
package org.androidannotations.menu;

import java.io.IOException;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class OptionMenuErrorsTest extends AAProcessorTestHelper {

	@Before
	public void setup() {
		addManifestProcessorParameter(OptionMenuErrorsTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void android_menuitem_in_sherlock_activity() throws IOException {
		CompileResult result = compileFiles(SherlockActivityWithAndroidMenu.class);
		assertCompilationErrorOn(SherlockActivityWithAndroidMenu.class, "@OptionsMenuItem", result);
	}

	@Test
	public void sherlock_menuitem_in_android_activity() throws IOException {
		CompileResult result = compileFiles(ActivityWithSherlockMenu.class);
		assertCompilationErrorOn(ActivityWithSherlockMenu.class, "@OptionsMenuItem", result);
	}

}
