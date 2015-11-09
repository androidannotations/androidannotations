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

import java.io.File;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class ContextCompatColorStateListTest extends AAProcessorTestHelper {

	private static final String COLOR_STATE_LIST_SIGNATURE = ".*myColorStateList = resources_\\.getColorStateList\\(R\\.color\\.myColorStateList\\);.*";
	private static final String COLOR_STATE_LIST_VIA_SUPPORT_SIGNATURE = ".*myColorStateList = ContextCompat\\.getColorStateList\\(this, R\\.color\\.myColorStateList\\);.*";
	private static final String COLOR_STATE_LIST_VIA_CONTEXT_ON_MARSHMALLOW = ".*myColorStateList = this\\.getColorStateList\\(R\\.color\\.myColorStateList\\);.*";
	private static final String[] COLOR_STATE_LIST_CONDITIONAL_WITHOUT_CONTEXT_COMPAT =  new String[] {
		"        if (VERSION.SDK_INT >= VERSION_CODES.M) {",
		"            myColorStateList = this.getColorStateList(R.color.myColorStateList);",
		"        } else {",
		"            myColorStateList = resources_.getColorStateList(R.color.myColorStateList);",
		"        }",
	};

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activityCompilesWithRegularColorStateList() {
		addManifestProcessorParameter(ContextCompatColorStateListTest.class, "AndroidManifestForColorStateList.xml");

		CompileResult result = compileFiles(ActivityWithColorStateList.class);
		File generatedFile = toGeneratedFile(ActivityWithColorStateList.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, COLOR_STATE_LIST_SIGNATURE);
	}

	@Test
	public void activityCompilesWithContextCompatColorStateList() {
		// To simulate android support v4 in classpath, we add
		// android.support.v4.content.ContextCompat
		// in classpath
		addManifestProcessorParameter(ContextCompatColorStateListTest.class, "AndroidManifestForColorStateList.xml");
		CompileResult result = compileFiles(toPath(ContextCompatColorStateListTest.class, "ContextCompat.java"), ActivityWithColorStateList.class);
		File generatedFile = toGeneratedFile(ActivityWithColorStateList.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, COLOR_STATE_LIST_VIA_SUPPORT_SIGNATURE);
	}

	@Test
	public void activityCompilesOnMinSdk23WithoutContextCompat() throws Exception {
		addManifestProcessorParameter(ContextCompatColorStateListTest.class, "AndroidManifestForColorStateListMinSdk23.xml");

		CompileResult result = compileFiles(ActivityWithGetColorStateListMethod.class);
		File generatedFile = toGeneratedFile(ActivityWithGetColorStateListMethod.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, COLOR_STATE_LIST_VIA_CONTEXT_ON_MARSHMALLOW);
	}

	@Test
	public void activityCompilesOnMinSdkLower23CompileSdkHigher22WithoutContextCompat() throws Exception {
		addManifestProcessorParameter(ContextCompatColorStateListTest.class, "AndroidManifestForColorStateListMinSdk22.xml");

		CompileResult result = compileFiles(toPath(ContextCompatColorStateListTest.class, "Context.java"),
				toPath(ContextCompatColorStateListTest.class, "Build.java"),
				ActivityWithGetColorStateListMethod.class);
		File generatedFile = toGeneratedFile(ActivityWithGetColorStateListMethod.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassContains(generatedFile, COLOR_STATE_LIST_CONDITIONAL_WITHOUT_CONTEXT_COMPAT);
	}

}
