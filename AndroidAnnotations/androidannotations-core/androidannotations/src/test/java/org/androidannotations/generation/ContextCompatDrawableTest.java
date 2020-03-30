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
package org.androidannotations.generation;

import java.io.File;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class ContextCompatDrawableTest extends AAProcessorTestHelper {

	private static final String DRAWABLE_SIGNATURE = ".*myDrawable = resources_\\.getDrawable\\(R\\.drawable\\.myDrawable\\);.*";
	private static final String DRAWABLE_VIA_SUPPORT_SIGNATURE = ".*myDrawable = ContextCompat\\.getDrawable\\(this, R\\.drawable\\.myDrawable\\);.*";
	private static final String DRAWABLE_VIA_CONTEXT_ON_LOLLIPOP = ".*myDrawable = this\\.getDrawable\\(R\\.drawable\\.myDrawable\\);.*";
	// CHECKSTYLE:OFF
	private static final String[] DRAWABLE_CONDITIONAL_WITHOUT_CONTEXT_COMPAT = new String[] { "        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {",
			"            this.myDrawable = this.getDrawable(R.drawable.myDrawable);", "        } else {", "            this.myDrawable = resources_.getDrawable(R.drawable.myDrawable);",
			"        }", };
	// CHECKSTYLE:ON

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activityCompilesWithRegularDrawable() {
		addManifestProcessorParameter(ContextCompatDrawableTest.class, "AndroidManifestForDrawable.xml");

		CompileResult result = compileFiles(ActivityWithDrawable.class);
		File generatedFile = toGeneratedFile(ActivityWithDrawable.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, DRAWABLE_SIGNATURE);
	}

	@Test
	public void activityCompilesWithContextCompatDrawable() {
		// To simulate android support v4 in classpath, we add
		// android.support.v4.content.ContextCompat
		// in classpath
		addManifestProcessorParameter(ContextCompatDrawableTest.class, "AndroidManifestForDrawable.xml");
		CompileResult result = compileFiles(toPath(ContextCompatDrawableTest.class, "ContextCompat.java"), ActivityWithDrawable.class);
		File generatedFile = toGeneratedFile(ActivityWithDrawable.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, DRAWABLE_VIA_SUPPORT_SIGNATURE);
	}

	@Test
	public void activityCompilesOnMinSdk21WithoutContextCompat() throws Exception {
		addManifestProcessorParameter(ContextCompatDrawableTest.class, "AndroidManifestForDrawableMinSdk21.xml");

		CompileResult result = compileFiles(ActivityWithGetDrawableMethod.class);
		File generatedFile = toGeneratedFile(ActivityWithGetDrawableMethod.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, DRAWABLE_VIA_CONTEXT_ON_LOLLIPOP);
	}

	@Test
	public void activityCompilesOnMinSdkLower21CompileSdkHigher21WithoutContextCompat() throws Exception {
		addManifestProcessorParameter(ContextCompatDrawableTest.class, "AndroidManifestForDrawableMinSdk20.xml");

		CompileResult result = compileFiles(toPath(ContextCompatDrawableTest.class, "Context.java"), toPath(ContextCompatDrawableTest.class, "Build.java"), ActivityWithGetDrawableMethod.class);
		File generatedFile = toGeneratedFile(ActivityWithGetDrawableMethod.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassContains(generatedFile, DRAWABLE_CONDITIONAL_WITHOUT_CONTEXT_COMPAT);
	}

}
