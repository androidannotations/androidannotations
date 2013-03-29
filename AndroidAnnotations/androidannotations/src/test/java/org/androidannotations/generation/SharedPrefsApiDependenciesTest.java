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
package org.androidannotations.generation;

import java.io.IOException;

import org.androidannotations.AndroidAnnotationProcessor;
import org.androidannotations.api.sharedpreferences.AbstractPrefEditorField;
import org.androidannotations.api.sharedpreferences.AbstractPrefField;
import org.androidannotations.api.sharedpreferences.BooleanPrefEditorField;
import org.androidannotations.api.sharedpreferences.BooleanPrefField;
import org.androidannotations.api.sharedpreferences.EditorHelper;
import org.androidannotations.api.sharedpreferences.FloatPrefEditorField;
import org.androidannotations.api.sharedpreferences.FloatPrefField;
import org.androidannotations.api.sharedpreferences.IntPrefEditorField;
import org.androidannotations.api.sharedpreferences.IntPrefField;
import org.androidannotations.api.sharedpreferences.LongPrefEditorField;
import org.androidannotations.api.sharedpreferences.LongPrefField;
import org.androidannotations.api.sharedpreferences.SharedPreferencesCompat;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;
import org.androidannotations.api.sharedpreferences.StringPrefEditorField;
import org.androidannotations.api.sharedpreferences.StringPrefField;
import org.androidannotations.manifest.SomeClass;
import org.androidannotations.utils.AAProcessorTestHelper;
import org.junit.Before;
import org.junit.Test;

public class SharedPrefsApiDependenciesTest extends AAProcessorTestHelper {

	private static final Class<?>[] SHARED_PREF_API_DEPENDENCIES = new Class<?>[] { AbstractPrefEditorField.class,//
			AbstractPrefField.class,//
			BooleanPrefEditorField.class,//
			BooleanPrefField.class,//
			EditorHelper.class,//
			FloatPrefEditorField.class,//
			FloatPrefField.class,//
			IntPrefEditorField.class,//
			IntPrefField.class,//
			LongPrefEditorField.class,//
			LongPrefField.class,//
			SharedPreferencesCompat.class,//
			SharedPreferencesHelper.class,//
			StringPrefEditorField.class,//
			StringPrefField.class //
	};

	@Before
	public void setup() {
		addManifestProcessorParameter(SharedPrefsApiDependenciesTest.class);
		addProcessor(AndroidAnnotationProcessor.class);
		ensureOutputDirectoryIsEmpty();
	}

	@Test
	public void shared_prefs_generate_api_dependencies() throws IOException {
		compileFiles(SharedPrefs.class);
		for (Class<?> apiDependency : SHARED_PREF_API_DEPENDENCIES) {
			assertClassSourcesGeneratedToOutput(apiDependency);
		}
	}

	@Test
	public void class_without_prefs_do_not_generate_api_dependencies() throws IOException {
		compileFiles(SomeClass.class);
		for (Class<?> apiDependency : SHARED_PREF_API_DEPENDENCIES) {
			assertClassSourcesNotGeneratedToOutput(apiDependency);
		}
	}

}
