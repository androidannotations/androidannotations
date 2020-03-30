/**
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
package org.androidannotations.sharedprefs;

import java.io.File;
import java.io.IOException;

import org.androidannotations.internal.AndroidAnnotationProcessor;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class SharedPrefNamingTest extends AAProcessorTestHelper {

	@Before
	public void setUp() {
		addManifestProcessorParameter(ActivityInManifest.class);
		addProcessor(AndroidAnnotationProcessor.class);
		ensureOutputDirectoryIsEmpty();
	}

	@Test
	public void testNamedPrefGeneration() throws IOException {
		CompileResult result = compileFiles(PrefsCollection.class);
		File generatedFile = toGeneratedFile(PrefsCollection.class);

		assertCompilationSuccessful(result);

		// CHECKSTYLE:OFF
		String[] activityPref = { //
				"        public ActivityPrefs_(Context context) {", //
				"            super(context.getSharedPreferences((getLocalClassName(context)+\"_ActivityPrefs\"), 0));", //
				"        }" //
		};
		String[] namedActivityPref = { //
				"        public NamedActivityPrefs_(Context context) {", //
				"            super(context.getSharedPreferences((getLocalClassName(context)+\"_named_pref_activity\"), 0));", //
				"        }" //
		};
		String[] uniquePref = { //
				"        public UniquePrefs_(Context context) {", //
				"            super(context.getSharedPreferences(\"UniquePrefs\", 0));", //
				"        }" //
		};
		String[] namedUniquePref = { //
				"        public NamedUniquePrefs_(Context context) {", //
				"            super(context.getSharedPreferences(\"named_pref_unique\", 0));", //
				"        }" //
		};
		// CHECKSTYLE:ON
		assertGeneratedClassContains(generatedFile, activityPref);
		assertGeneratedClassContains(generatedFile, namedActivityPref);
		assertGeneratedClassContains(generatedFile, uniquePref);
		assertGeneratedClassContains(generatedFile, namedUniquePref);
	}

	@Test
	public void testNamedPrefGenerationErrors() throws IOException {
		CompileResult result = compileFiles(PrefsCollectionWithError.class);

		assertCompilationError(result);

		assertCompilationErrorOn(PrefsCollectionWithError.class, "@SharedPref(value = SharedPref.Scope.ACTIVITY_DEFAULT, name = \"named_pref_activity_default\")", result);
		assertCompilationErrorOn(PrefsCollectionWithError.class, "@SharedPref(value = SharedPref.Scope.APPLICATION_DEFAULT, name = \"named_pref_application_default\")", result);
	}
}
