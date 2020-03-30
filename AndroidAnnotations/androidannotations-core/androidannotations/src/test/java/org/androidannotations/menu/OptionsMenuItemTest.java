/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
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
package org.androidannotations.menu;

import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class OptionsMenuItemTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(OptionsMenuItemTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void testWhenFieldNameCouldConflictWithParameterName() throws IOException {
		CompileResult result = compileFiles(OptionsMenuItemActivity.class);
		assertCompilationSuccessful(result);
	}

	@Test
	public void testDeeplyInheritedMenuNotBreakOrder() throws IOException {
		CompileResult result = compileFiles(OptionsMenuItemSubSubSubActivity.class);
		assertCompilationSuccessful(result);
		// CHECKSTYLE:OFF
		String[] codeFragment = { //
				"    @Override", //
				"    public boolean onCreateOptionsMenu(Menu menu) {", //
				"        MenuInflater menuInflater = getMenuInflater();", //
				"        this.menu = menu.findItem(R.id.menu);", //
				"        this.menu1 = menu.findItem(R.id.menu1);", //
				"        this.menu2 = menu.findItem(R.id.menu2);", //
				"        this.menu3 = menu.findItem(R.id.menu3);", //
				"        return super.onCreateOptionsMenu(menu);", //
				"    }", };
		// CHECKSTYLE:ON
		assertGeneratedClassContains(toGeneratedFile(OptionsMenuItemSubSubSubActivity.class), codeFragment);
	}
}
