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
package org.androidannotations.eview;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class EViewTest extends AAProcessorTestHelper {
	@Before
	public void setUp() {
		addManifestProcessorParameter(EViewTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void someViewDoesNotGenerateOnViewChangedNotifier() {
		// CHECKSTYLE:OFF
		String[] methodSignature = { //
				"    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();" //
		};
		// CHECKSTYLE:ON

		CompileResult result = compileFiles(SomeView.class);
		assertCompilationSuccessful(result);
		assertGeneratedClassDoesNotContain(toGeneratedFile(SomeView.class), methodSignature);
	}

	@Test
	public void someViewWithAfterViewGenerateOnViewChangedNotifier() {
		// CHECKSTYLE:OFF
		String[] methodSignature = { //
				"    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();" //
		};
		// CHECKSTYLE:ON

		CompileResult result = compileFiles(SomeViewWithAfterView.class);
		assertCompilationSuccessful(result);
		assertGeneratedClassContains(toGeneratedFile(SomeViewWithAfterView.class), methodSignature);
	}

}
