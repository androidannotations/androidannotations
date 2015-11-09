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

public class ContextCompatColorTest extends AAProcessorTestHelper {

	private static final String COLOR_SIGNATURE = ".*myColor = resources_\\.getColor\\(R\\.color\\.myColor\\);.*";
	private static final String COLOR_VIA_SUPPORT_SIGNATURE = ".*myColor = ContextCompat\\.getColor\\(this, R\\.color\\.myColor\\);.*";
	private static final String COLOR_VIA_CONTEXT_ON_MARSHMALLOW = ".*myColor = this\\.getColor\\(R\\.color\\.myColor\\);.*";
	private static final String[] COLOR_CONDITIONAL_WITHOUT_CONTEXT_COMPAT =  new String[] {
		"        if (VERSION.SDK_INT >= VERSION_CODES.M) {",
		"            myColor = this.getColor(R.color.myColor);",
		"        } else {",
		"            myColor = resources_.getColor(R.color.myColor);",
		"        }",
	};

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activityCompilesWithRegularColor() {
		addManifestProcessorParameter(ContextCompatColorTest.class, "AndroidManifestForColor.xml");

		CompileResult result = compileFiles(ActivityWithColor.class);
		File generatedFile = toGeneratedFile(ActivityWithColor.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, COLOR_SIGNATURE);
	}

	@Test
	public void activityCompilesWithContextCompatColor() {
		// To simulate android support v4 in classpath, we add
		// android.support.v4.content.ContextCompat
		// in classpath
		addManifestProcessorParameter(ContextCompatColorTest.class, "AndroidManifestForColor.xml");
		CompileResult result = compileFiles(toPath(ContextCompatColorTest.class, "ContextCompat.java"), ActivityWithColor.class);
		File generatedFile = toGeneratedFile(ActivityWithColor.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, COLOR_VIA_SUPPORT_SIGNATURE);
	}
	
	@Test
	public void activityCompilesOnMinSdk23WithoutContextCompat() throws Exception {
		addManifestProcessorParameter(ContextCompatColorTest.class, "AndroidManifestForColorMinSdk23.xml");

		CompileResult result = compileFiles(ActivityWithGetColorMethod.class);
		File generatedFile = toGeneratedFile(ActivityWithGetColorMethod.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, COLOR_VIA_CONTEXT_ON_MARSHMALLOW);
	}
	
	@Test
	public void activityCompilesOnMinSdkLower23CompileSdkHigher22WithoutContextCompat() throws Exception {
		addManifestProcessorParameter(ContextCompatColorTest.class, "AndroidManifestForColorMinSdk22.xml");

		CompileResult result = compileFiles(toPath(ContextCompatColorTest.class, "Context.java"),
				toPath(ContextCompatColorTest.class, "Build.java"),
				ActivityWithGetColorMethod.class);
		File generatedFile = toGeneratedFile(ActivityWithGetColorMethod.class);
		
		assertCompilationSuccessful(result);
		assertGeneratedClassContains(generatedFile, COLOR_CONDITIONAL_WITHOUT_CONTEXT_COMPAT);
	}
	
}
