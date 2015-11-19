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

public class SystemServiceHandlerContextTest extends AAProcessorTestHelper {

	private static final String WIFI_MANAGER_SIGNATURE = ".*wifiManager = \\(\\(WifiManager\\) this\\.getApplicationContext\\(\\)\\.getSystemService\\(Context\\.WIFI_SERVICE\\)\\);.*";

	@Before
	public void setUp() {
		addProcessor(AndroidAnnotationProcessor.class);
	}

	@Test
	public void activityRetrievesWifiManagerViaApplicationContext() {
		addManifestProcessorParameter(SystemServiceHandlerContextTest.class, "AndroidManifestForApplicationContextSystemServices.xml");

		CompileResult result = compileFiles(ActivityWithApplicationContextSystemServices.class);
		File generatedFile = toGeneratedFile(ActivityWithApplicationContextSystemServices.class);

		assertCompilationSuccessful(result);
		assertGeneratedClassMatches(generatedFile, WIFI_MANAGER_SIGNATURE);
	}
}
