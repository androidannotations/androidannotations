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

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class ActivityIntentFragmentTest extends AAProcessorTestHelper {

	// Couldn't find better than this for now
	private static final String INTENT_FRAGMENT_SIGNATURE = ".*public static ActivityInManifest_\\.IntentBuilder_ intent\\((android\\.app\\.)?Fragment fragment\\).*";
	private static final String INTENT_FRAGMENT_SUPPORT_SIGNATURE = ".*public static ActivityInManifest_\\.IntentBuilder_ intent\\((android\\.support\\.v4\\.app\\.)?Fragment supportFragment\\).*";

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activityIntentFragmentMinSdkFroyoCompilesWithFroyo() {
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinFroyo.xml");
		// we need android.os.Build in the classpath
		CompileResult result = compileFiles(ActivityInManifest.class /*
		 * ,toPath(
		 * ActivityIntentFragmentTest
		 * .class,
		 * "Build.java"
		 * )
		 */);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassDoesntMatches(generatedFile, INTENT_FRAGMENT_SIGNATURE);
	}

	@Test
	public void activityIntentFragmentMinSdkFroyoCompilesWithJB() {
		// To simulate compilation with SDK > 11, we add Fragment in classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinFroyo.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "Fragment.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassDoesntMatches(generatedFile, INTENT_FRAGMENT_SIGNATURE);
	}

	@Test
	public void activityIntentFragmentMinSdkJBCompileWithJB() {
		// To simulate compilation with SDK > 11, we add Fragment in classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinJB.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "Fragment.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, INTENT_FRAGMENT_SIGNATURE);
	}

	@Test
	public void activityIntentFragmentCompilesWithSupport() {
		// To simulate android support v4 in classpath, we add
		// android.support.v4.Fragment and android.support.v4.app.ActivityCompat
		// in classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinFroyo.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "support/Fragment.java"), //
				toPath(ActivityIntentFragmentTest.class, "support/ActivityCompat.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, INTENT_FRAGMENT_SUPPORT_SIGNATURE);
	}

	@Test
	public void activityIntentFragmentCompilesWithSupportContainingBundleOptions() {
		// To simulate android support v4 in classpath, we add
		// android.support.v4.Fragment and android.support.v4.app.ActivityCompat
		// in classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinFroyo.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "support/Fragment.java"), //
				toPath(ActivityIntentFragmentTest.class, "ActivityCompat.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, INTENT_FRAGMENT_SUPPORT_SIGNATURE);
	}

	@Test
	public void activityIntentFragmentMinSdkJBCompilesWithJBAndSupport() {
		// To simulate compilation with SDK > 11, we add Fragment in classpath
		addManifestProcessorParameter(ActivityIntentFragmentTest.class, "AndroidManifestMinJB.xml");
		CompileResult result = compileFiles(toPath(ActivityIntentFragmentTest.class, "Fragment.java"), toPath(ActivityIntentFragmentTest.class, "support/Fragment.java"), ActivityInManifest.class);
		File generatedFile = toGeneratedFile(ActivityInManifest.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, INTENT_FRAGMENT_SIGNATURE);
		assertGeneratedClassMatches(generatedFile, INTENT_FRAGMENT_SUPPORT_SIGNATURE);
	}

}
